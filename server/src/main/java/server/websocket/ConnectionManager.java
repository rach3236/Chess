package server.websocket;

import datamodel.GameID;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import jakarta.websocket.*;



import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {

    //TO DO: fix hash map to map users/game data to game ID
    public final ConcurrentHashMap<Session, Session> connections = new ConcurrentHashMap<>();

    public void add(Session session, UserGameCommand command) {
        connections.put(session, session);
    }

    public void remove(Session session) {
        connections.remove(session);
    }

    public void broadcast(Session session, UserGameCommand command, ServerMessage notification) {
        //compare w/ what the server terminal prints out
        //TO DO
        System.out.println(notification.getServerMessage());

        try {
            //??? TO DO
            session.getBasicRemote().sendText(notification.getServerMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }








}
