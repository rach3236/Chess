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
//        System.out.println("got to connect method:)");
//        String player1 = "fake_player1";
        var playerName = userService.getUsername(command.getAuthToken());
        var message = String.format("%s joined the game", playerName);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(session, notification);
    }

    private void makeMove(Session session, MakeMoveGameCommand command) {}

    private void leave(Session session, UserGameCommand command) {
    }

    private void resign(Session session, UserGameCommand command) {}


//    private void enter(String visitorName, Session session) throws IOException {
//        connections.add(session);
//        var message = String.format("%s is in the shop", visitorName);
//        var notification = new Notification(Notification.Type.ARRIVAL, message);
//        connections.broadcast(session, notification);
//    }

//    private void leave() throws Exception {
//        var message = "smth";
//        var notification = new Notification(Notification.Type.DEPARTURE, message);
//        connections.broadcast(game, notification);
//        connections.remove(session);
//    }
//
//    private void resign() throws Exception {
//        var mensaje = "smth";
//
//        //call win method
//        win(resign);
//        connections.broadcast(game, notification);
//        connections.remove(session);
//    }

//    public void makeNoise(String petName, String sound) throws ResponseException {
//        try {
//            var message = String.format("%s says %s", petName, sound);
//            var notification = new Notification(Notification.Type.NOISE, message);
//            connections.broadcast(null, notification);
//        } catch (Exception ex) {
//            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
//        }








}

