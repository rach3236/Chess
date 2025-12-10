package server.websocket;

import chess.ChessGame;
import chess.ChessPosition;
import com.google.gson.Gson;
import datamodel.GameData;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;

import org.eclipse.jetty.websocket.api.Session;

import service.UserService;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import static chess.ChessGame.TeamColor.BLACK;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private static UserService userService;
    private int gameID;
    public WebSocketHandler(UserService uService) {
        this.userService = uService;
    }

    private final ConnectionManager connections = new ConnectionManager();

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) throws Exception {
        try {
            UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);

            switch (command.getCommandType()) {
                case CONNECT -> connect(ctx.session,  new Gson().fromJson(ctx.message(), UserGameCommand.class));
                case MAKE_MOVE -> makeMove(ctx.session,  new Gson().fromJson(ctx.message(), UserGameCommand.class));
                case LEAVE -> leave(ctx.session, new Gson().fromJson(ctx.message(), UserGameCommand.class));
                case RESIGN -> resign(ctx.session,new Gson().fromJson(ctx.message(), UserGameCommand.class));
            }
        } catch (Exception ex) {
            System.out.println("System exception " + ex.getMessage());
            throw new Exception(ex.getMessage());
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
        //TO DO
        connections.remove(ctx.session, gameID);
    }

    private void connect(Session session, UserGameCommand command) throws Exception {
        connections.add(session, command);
        var playerName = userService.getUsername(command.getAuthToken());
        if (playerName == null) {
            ServerMessage errorNotification = new ServerMessage((ServerMessage.ServerMessageType.ERROR),
                    null, "user doesn't exist",  null, null);
            connections.selfBroadcast(session, command, errorNotification);
            return;
        }

        var game = userService.getGameState(command.getGameID());
        if (game == null) {
            ServerMessage errorNotification = new ServerMessage((ServerMessage.ServerMessageType.ERROR),
                    null, "Game is null", null, null);
            connections.selfBroadcast(session, command, errorNotification);
            return;
        }
        gameID = command.getGameID();

        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, playerName + " has connected " +
                (command.observerStatus() ? "as observer" : "as " + command.getPOV()), null, null, command.getPOV());
        connections.otherPeopleBroadcast(session, command, notification);

        assert game != null;
        ServerMessage loadGameNotification = new ServerMessage
                (ServerMessage.ServerMessageType.LOAD_GAME, null, null, game.gameObject(), command.getPOV());
        connections.selfBroadcast(session, command, loadGameNotification);
    }

    private void makeMove(Session session, UserGameCommand command) {
        var gameState = userService.getGameState(command.getGameID());

        if (gameState == null) {
            ServerMessage errorNotification = new ServerMessage((ServerMessage.ServerMessageType.ERROR),
                    null, "Error: invalid game ID", null, null);
            connections.selfBroadcast(session, command, errorNotification);
            return;
        }

        if (!gameState.gameObject().getActiveGame()) {
            ServerMessage notActiveNotification = new ServerMessage((ServerMessage.ServerMessageType.ERROR),
                    null, "No more moves can be made; the game is complete!",   null, null);
            connections.selfBroadcast(session, command, notActiveNotification);
            return;
        }
        String pov = "WHITE";
        var playerName = userService.getUsername(command.getAuthToken());
        if (playerName == null) {
            ServerMessage badAuthNotification = new ServerMessage((ServerMessage.ServerMessageType.ERROR),
                    null, "Bad request for username", null, null);
            connections.selfBroadcast(session, command, badAuthNotification);
            return;
        }
        var observerStatus = false;
        ChessGame.TeamColor oppTeamColor;
        String oppTeamName;

        if (playerName.equals(gameState.whiteUsername())) {
            oppTeamColor = BLACK;
            oppTeamName = gameState.blackUsername();
        } else if (playerName.equals(gameState.blackUsername())) {
            pov = "BLACK";
            oppTeamColor = ChessGame.TeamColor.WHITE;
            oppTeamName = gameState.whiteUsername();
        } else {
            pov = "WHITE";
            observerStatus = true;
            oppTeamColor = ChessGame.TeamColor.WHITE;
            oppTeamName = gameState.whiteUsername();
        }
        if (observerStatus) {
            ServerMessage badAuthNotification = new ServerMessage((ServerMessage.ServerMessageType.ERROR),
                    null, "Observers can't make moves, silly:)", null, null);
            connections.selfBroadcast(session, command, badAuthNotification);
            return;
        }

        var validMove = userService.checkValidMove(command.getMove(), command.getGameID(), pov);

        if (validMove) {
            try {
                gameState.gameObject().makeMove(command.getMove());
                userService.updateGameInfo(gameState);

                ServerMessage loadGameNotification = new ServerMessage
                        (ServerMessage.ServerMessageType.LOAD_GAME, null, null, gameState.gameObject(), pov);
                connections.everybodyBroadcast(command, loadGameNotification);

                //broadcast move
                var startPos = command.getMove().getStartPosition();
                var endPos = command.getMove().getEndPosition();

                String startPosLetter = translatorHelper(startPos);
                String endPosLetter = translatorHelper(endPos);

                ServerMessage moveNotification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                        (playerName + " moved from " + startPosLetter + startPos.getRow() + " to " + endPosLetter + endPos.getRow()), null,
                        null, pov);
                connections.otherPeopleBroadcast(session, command, moveNotification);

                if (gameState.gameObject().isInCheck(oppTeamColor)) {
                    ServerMessage checkNotification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                            oppTeamName + " is in check!", null, null, pov);
                    connections.everybodyBroadcast(command, checkNotification);
                } else if (gameState.gameObject().isInCheckmate(oppTeamColor)) {
                    gameState.gameObject().setActiveGame(false);
                    userService.updateGameInfo(gameState);
                    ServerMessage checkMateNotification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                            oppTeamName + " is in checkmate!", null, null, pov);
                    connections.everybodyBroadcast(command, checkMateNotification);
                } else if (gameState.gameObject().isInStalemate(oppTeamColor)) {
                    gameState.gameObject().setActiveGame(false);
                    userService.updateGameInfo(gameState);
                    ServerMessage staleMateNotification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                            oppTeamName + " is in stalemate!", null, null, pov);
                    connections.everybodyBroadcast(command, staleMateNotification);
                }
            } catch (Exception e) {
                ServerMessage errorMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR, null,
                        "Error: " + e.getMessage(), null, pov);
                connections.selfBroadcast(session, command, errorMessage);
            }
        } else {
            ServerMessage errorMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR, null,
                    "Error: Invalid move", null, pov);
            connections.selfBroadcast(session, command, errorMessage);
        }
    }

    private String translatorHelper(ChessPosition startPos) {
        String startPosLetter = "h";
        if (startPos.getColumn() == 1) {
            startPosLetter = "a";
        } else if (startPos.getColumn() == 2) {
            startPosLetter = "b";
        } else if (startPos.getColumn() == 3) {
            startPosLetter = "c";
        } else if (startPos.getColumn() == 4) {
            startPosLetter = "d";
        } else if (startPos.getColumn() == 5) {
            startPosLetter = "e";
        } else if (startPos.getColumn() == 6) {
            startPosLetter = "f";
        } else if (startPos.getColumn() == 7) {
            startPosLetter = "g";
        }
        return startPosLetter;
    }

    private void leave(Session session, UserGameCommand command) {

        var playerName = userService.getUsername(command.getAuthToken());
        if (!command.observerStatus()) {
            var gameInfo = userService.getGameState(command.getGameID());
            if (gameInfo == null) {
                ServerMessage errorNotification = new ServerMessage((ServerMessage.ServerMessageType.ERROR),
                        null, "Invalid Game ID", null, null);
                connections.selfBroadcast(session, command, errorNotification);
                return;
            }
            String whiteUser = gameInfo.whiteUsername();
            String blackUser = gameInfo.blackUsername();
//
            if (playerName.equals(whiteUser)) {
                whiteUser = null;
            }
            if (playerName.equals(blackUser)) {
                blackUser = null;
            }

            GameData gameAfterPlayerLeaves = new GameData(gameInfo.gameID(), whiteUser, blackUser,
                    gameInfo.gameName(), gameInfo.gameObject());
            userService.updateGameInfo(gameAfterPlayerLeaves);
        }

        ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                playerName + " left the game", null, null, null);
        connections.remove(session, gameID);
        connections.otherPeopleBroadcast(session, command, notification);
    }


    private void resign(Session session, UserGameCommand command) {
        System.out.println(command.observerStatus());

        var playerName = userService.getUsername(command.getAuthToken());
        var gameState = userService.getGameState(command.getGameID());

        var observerStatus = true;
        if (playerName.equals(gameState.whiteUsername()) || playerName.equals(gameState.blackUsername())) {
            observerStatus = false;
        }

        if (observerStatus) {
            ServerMessage errorMessage = new ServerMessage((ServerMessage.ServerMessageType.ERROR),
                    null, "Observers cannot resign", null, null);
            connections.selfBroadcast(session, command, errorMessage);
            return;
        }

        if (!gameState.gameObject().getActiveGame()) {
            ServerMessage notActiveNotification = new ServerMessage((ServerMessage.ServerMessageType.ERROR),
                    null, "No more moves can be made; the game is complete!", null, null);
            connections.selfBroadcast(session, command, notActiveNotification);
            return;
        }

        if (playerName == null) {
            ServerMessage badAuthNotification = new ServerMessage((ServerMessage.ServerMessageType.ERROR),
                    null, "Bad request for username", null, null);
            connections.selfBroadcast(session, command, badAuthNotification);
            return;
        }
        gameState.gameObject().setActiveGame(false);
        userService.updateGameInfo(gameState);

        ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                playerName + " has resigned. ", null, null, null);
        connections.everybodyBroadcast(command, notification);
    }
}

