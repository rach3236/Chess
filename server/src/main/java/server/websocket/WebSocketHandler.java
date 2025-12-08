package server.websocket;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import jakarta.websocket.*;
import service.UserService;
import websocket.commands.ConnectGameCommand;
import websocket.commands.UserGameCommand;
import websocket.commands.MakeMoveGameCommand;
import websocket.messages.ServerMessage;

import javax.management.Notification;
import java.io.IOException;

import static websocket.commands.UserGameCommand.CommandType.*;

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

    //ASK TA: jakart vs. jetty? wassup w/ that? how to do broadcast
    @Override
    public void handleMessage(WsMessageContext ctx) throws Exception {
        try {
//            USER COMMAND Action action = new Gson().fromJson(ctx.message(), Action.class);
            UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            System.out.println("blah: " + command.getCommandType());
            switch (command.getCommandType()) {
                case CONNECT -> connect(ctx.session,  new Gson().fromJson(ctx.message(), ConnectGameCommand.class));
                case MAKE_MOVE -> makeMove(ctx.session,  new Gson().fromJson(ctx.message(), MakeMoveGameCommand.class));
                case LEAVE -> leave(ctx.session, command);
                case RESIGN -> resign(ctx.session, command);
            }
        } catch (Exception ex) {
            //TO DO
            throw new Exception(ex.getMessage());
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
        connections.remove(ctx.session);
    }

    private void empty() {
    }

    private void connect(Session session, ConnectGameCommand command) throws Exception {
        connections.add(session, command);
        var playerName = userService.getUsername(command.getAuthToken());

        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);

        if (command.observerStatus()) {
            // connections.broadcast("OBserver joined the game");

        } else {
            // connections.broadcast("%s joined the game as " + PlayerColor  + "");
            //serverMessage.mesageType
        }
        //connections.broadcast(Load_game_server_message);

    }

    private void makeMove(Session session, MakeMoveGameCommand command) {
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

        // QUESTION FOR TAs to fight about: horrible instruction my friend,
        // what to do when player makes an incorrect move?

    }

    private void leave(Session session, UserGameCommand command) {
        // userService.leaveGame(curr_player_color, null)  w/ new gameState
        // connections.remove(session);
        // connections.broadcast(notification that player left);
    }

    private void resign(Session session, UserGameCommand command) {
        //marks the game as over
        // no more moves possible

        // Questions TA: bro, what do you do w/ the game state when a player resigns?

        // userService.updateGameState
        // connections.broadcast(notification that opposite player of resignation wins)
        // leave(command)
    }











}

