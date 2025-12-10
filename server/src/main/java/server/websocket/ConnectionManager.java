package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {

    //TO DO: fix hash map to map users/game data to game ID
    public final ConcurrentHashMap<Integer, Set<Session>> connections = new ConcurrentHashMap<>();

    public void add(Session session, UserGameCommand command) {
        if (!connections.containsKey(command.getGameID())) {
            Set<Session> seshList = new HashSet<>();
            seshList.add(session);
            connections.put(command.getGameID(), seshList);
        } else {
            var seshList = connections.get(command.getGameID());
            seshList.add(session);
        }
    }

    public void remove(Session session, int gameID) {
        var sessionsList = connections.get(gameID);
        if (sessionsList != null) {
            sessionsList.remove(session);
        }
    }

    public void selfBroadcast(Session session, UserGameCommand command, ServerMessage notification) {
        try {
            var sessionsList = connections.get(command.getGameID());
            for (Session sesh : sessionsList) {
                if (sesh == session) {
                    String msg = new Gson().toJson(notification);
                    sesh.getRemote().sendString(msg);
                    return;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void otherPeopleBroadcast(Session session, UserGameCommand command, ServerMessage notification) {
        try {
            var sessionsList = connections.get(command.getGameID());
            for (Session sesh : sessionsList) {

                if (sesh == session) {
                    continue;
                }
                String msg = new Gson().toJson(notification);
                sesh.getRemote().sendString(msg);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void everybodyBroadcast(UserGameCommand command, ServerMessage notification) {
        try {
            var sessionsList = connections.get(command.getGameID());
            for (Session sesh : sessionsList) {
                String msg = new Gson().toJson(notification);
                sesh.getRemote().sendString(msg);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
