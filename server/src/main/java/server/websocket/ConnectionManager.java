package server.websocket;

import com.google.gson.Gson;
import datamodel.GameID;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;


import java.util.Map;
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
        System.out.println("broadcast example");

        try {
            //??? TO DO filter who we broadcast to.
            //TO DO loop through connections
            if ((command.getCommandType()== UserGameCommand.CommandType.CONNECT) || command.getCommandType() == UserGameCommand.CommandType.LEAVE) {
                for (Map.Entry<Session, Session> c : connections.entrySet()) {
                    if (c.getValue()==session) {
                        continue;
                    }
                    String msg = new Gson().toJson(notification);
                    session.getRemote().sendString(msg);
                }
            }

            String msg = new Gson().toJson(notification);
            session.getRemote().sendString(msg);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }








}
