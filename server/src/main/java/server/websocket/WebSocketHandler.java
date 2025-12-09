package server.websocket;

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
import websocket.commands.ConnectGameCommand;
import websocket.commands.UserGameCommand;
import websocket.commands.MakeMoveGameCommand;
import websocket.messages.ServerMessage;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private static UserService userService;
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
            System.out.println("blah: " + command.getCommandType());

            switch (command.getCommandType()) {
                case CONNECT -> connect(ctx.session,  new Gson().fromJson(ctx.message(), ConnectGameCommand.class));
                case MAKE_MOVE -> makeMove(ctx.session,  new Gson().fromJson(ctx.message(), MakeMoveGameCommand.class));
                case LEAVE -> leave(ctx.session, command);
                case RESIGN -> resign(ctx.session, command);
            }
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
        //TO DO
//        connections.remove(ctx.session);
    }

    private void connect(Session session, ConnectGameCommand command) throws Exception {
        connections.add(session, command);
        var playerName = userService.getUsername(command.getAuthToken());

        var game = userService.getGameState(command.getGameID());

        ServerMessage loadGameNotification = new ServerMessage
                (ServerMessage.ServerMessageType.LOAD_GAME, "", game.gameObject(), command.getPOV());
        connections.broadcast(session, command, loadGameNotification);

        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, playerName + " has connected " +
                (command.observerStatus() ? "as observer" : "as " +  command.getPOV()), null, null);
        connections.broadcast(session, command, notification);
    }

    private void makeMove(Session session, MakeMoveGameCommand command) {
        //TO DO
    // userService.validateMove(gameID, playerColor, move);
        // if (validMove) {
        //      updateGameData w/ new game state
        //      connections.broadcast(loadGame)
        //      connections.broadcast(notification: playerColor "moved from " + startPosition + " to " + endPosition)
        //      if (move -> check/checkmate) {
        //          connections.broadcast(CHECK)
//              } else if (move -> checkmate) {
//                    connections.broadcast(CHECKMATE);
//                } else if (move -> stalemate) {
//                  connections.broadcast(STALEMATE);
//                }
        //
        // } else {
        //    connections.broadcast(Error: bad move sonny)
        // }
        //

        // QUESTION FOR TAs to fight about:
        // what to do when player makes an incorrect move?
        // server end deals with the logic, gives the client an error message

    }

    private void leave(Session session, UserGameCommand command) {
        //TO DO

        var playerName = userService.getUsername(command.getAuthToken());
        if (!command.observerStatus()) {
            var gameInfo = userService.getGameState(command.getGameID());

            String whiteUser = gameInfo.whiteUsername();
            String blackUser = gameInfo.blackUsername();

            if (command.getPOV().equals("WHITE") && gameInfo.whiteUsername() != null && gameInfo.whiteUsername().equals(playerName)) {
                whiteUser = null;
            }
            if (command.getPOV().equals("BLACK") && gameInfo.blackUsername() != null && gameInfo.blackUsername().equals(playerName)) {
                blackUser = null;
            }
            GameData gameAfterPlayerLeaves = new GameData(gameInfo.gameID(), whiteUser, blackUser, gameInfo.gameName(), gameInfo.gameObject());
            userService.updatePlayerLeave(gameAfterPlayerLeaves);
        }

        ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, playerName + " left the game", null, null);
        connections.remove(session);
        connections.broadcast(session, command, notification);
    }

    private void resign(Session session, UserGameCommand command) {
        //TO DO
        // marks the game as over
        // no more moves possible

        // Questions TA: bro, what do you do w/ the game state when a player resigns?

        // userService.updateGameState
        // connections.broadcast(notification that opposite player of resignation wins)
        // leave(command)
    }
}

