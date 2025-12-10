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
                case LEAVE -> leave(ctx.session, command);
                case RESIGN -> resign(ctx.session, command);
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

        var game = userService.getGameState(command.getGameID());
        if (game == null) {
            ServerMessage errorNotification = new ServerMessage((ServerMessage.ServerMessageType.ERROR), "Game is null", null, null);
            connections.broadcast(session, command, errorNotification, false);
        }
        gameID = command.getGameID();

        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, playerName + " has connected " +
                (command.observerStatus() ? "as observer" : "as " + command.getPOV()), null, command.getPOV());
        connections.broadcast(session, command, notification, false);

        assert game != null;
        ServerMessage loadGameNotification = new ServerMessage
                (ServerMessage.ServerMessageType.LOAD_GAME, "", game.gameObject(), command.getPOV());
        connections.broadcast(session, command, loadGameNotification, true);
    }

    private void makeMove(Session session, UserGameCommand command) {
        var gameState = userService.getGameState(command.getGameID());

        if (gameState == null) {
            ServerMessage errorNotification = new ServerMessage((ServerMessage.ServerMessageType.ERROR), null, null, null);
            connections.broadcast(session, command, errorNotification, false);
        }

        if (!gameState.gameObject().getActiveGame()) {
            ServerMessage notActiveNotification = new ServerMessage((ServerMessage.ServerMessageType.ERROR), "No more moves can be made; the game is complete!", null, null);
            connections.broadcast(session, command, notActiveNotification, true);
            return;
        }
        var validMove = userService.checkValidMove(command.getMove(), command.getGameID(), command.getPOV());


        if (validMove) {
            try {
                gameState.gameObject().makeMove(command.getMove());
                userService.updateGameInfo(gameState);

                ServerMessage loadGameNotification = new ServerMessage
                        (ServerMessage.ServerMessageType.LOAD_GAME, "", gameState.gameObject(), command.getPOV());
                connections.broadcast(session, command, loadGameNotification, true);

                //broadcast move
                var startPos = command.getMove().getStartPosition();
                var endPos = command.getMove().getEndPosition();

                String startPosLetter = translatorHelper(startPos);
                String endPosLetter = translatorHelper(endPos);

                var playerName = userService.getUsername(command.getAuthToken());

                ServerMessage moveNotification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                        (playerName + " moved from " + startPosLetter + startPos.getRow() + " to " + endPosLetter + endPos.getRow()),
                        gameState.gameObject(), command.getPOV());

                connections.broadcast(session, command, moveNotification, false);

                var oppTeamColor = ChessGame.TeamColor.WHITE;
                var oppTeamName = gameState.whiteUsername();
                //TO DO move color check into wsHandler
                if (command.getPOV().equals("WHITE")) {
                    oppTeamColor = ChessGame.TeamColor.BLACK;
                    oppTeamName = gameState.blackUsername();
                }

                if (gameState.gameObject().isInCheck(oppTeamColor)) {

                    ServerMessage checkNotification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, oppTeamName + " is in check!", null, command.getPOV());
                    connections.broadcast(session, command, checkNotification, true);
                } else if (gameState.gameObject().isInCheckmate(oppTeamColor)) {
                    gameState.gameObject().setActiveGame(false);
                    userService.updateGameInfo(gameState);
                    ServerMessage checkMateNotification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, oppTeamName + " is in checkmate!", null, command.getPOV());
                    connections.broadcast(session, command, checkMateNotification, true);
                } else if (gameState.gameObject().isInStalemate(oppTeamColor)) {
                    gameState.gameObject().setActiveGame(false);
                    userService.updateGameInfo(gameState);
                    ServerMessage staleMateNotification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, oppTeamName + " is in stalemate!", null, command.getPOV());
                    connections.broadcast(session, command, staleMateNotification, true);
                }
            } catch (Exception e) {
                ServerMessage errorMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Error: " + e.getMessage(), null, command.getPOV());
                connections.broadcast(session, command, errorMessage, false);
            }
        } else {
            ServerMessage errorMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Error: Invalid move", null, command.getPOV());
            connections.broadcast(session, command, errorMessage, false);
        }

        //TO DO
        // server end deals with the logic for incorrect, gives the client an error message
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
                ServerMessage errorNotification = new ServerMessage((ServerMessage.ServerMessageType.ERROR), null, null, null);
                connections.broadcast(session, command, errorNotification, false);
            }
            String whiteUser = gameInfo.whiteUsername();
            String blackUser = gameInfo.blackUsername();

            if (command.getPOV().equals("WHITE") && gameInfo.whiteUsername() != null && gameInfo.whiteUsername().equals(playerName)) {
                whiteUser = null;
            }
            if (command.getPOV().equals("BLACK") && gameInfo.blackUsername() != null && gameInfo.blackUsername().equals(playerName)) {
                blackUser = null;
            }
            GameData gameAfterPlayerLeaves = new GameData(gameInfo.gameID(), whiteUser, blackUser, gameInfo.gameName(), gameInfo.gameObject());
            userService.updateGameInfo(gameAfterPlayerLeaves);
        }

        ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, playerName + " left the game", null, null);
        connections.remove(session, gameID);
        connections.broadcast(session, command, notification, false);
    }


    private void resign(Session session, UserGameCommand command) {

        if (command.observerStatus()) {
            return;
        }
        var game = userService.getGameState(command.getGameID());
        if (game == null) {
            ServerMessage errorNotification = new ServerMessage((ServerMessage.ServerMessageType.ERROR), null, null, null);
            connections.broadcast(session, command, errorNotification, false);
        }
        game.gameObject().setActiveGame(false);
        userService.updateGameInfo(game);

        var playerName = userService.getUsername(command.getAuthToken());

        ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, playerName + " has resigned. ", null, null);
        connections.broadcast(session, command, notification, false);
    }
}

