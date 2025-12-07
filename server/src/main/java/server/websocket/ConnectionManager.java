package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import javax.management.Notification;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {

    //TO DO: fix hash map to map users/game data to game ID
    public final ConcurrentHashMap<Session, Session> connections = new ConcurrentHashMap<>();

    public void add(Session session) {
        connections.put(session, session);
    }

    public void remove(Session session) {
        connections.remove(session);
    }

    //FIX THIS to only broadcast to those in the game
    public void broadcast(Session excludeSession, ServerMessage notification) throws IOException {
        System.out.println("we got to the broadcast function");
        String msg = notification.toString();
        for (Session c : connections.values()) {
            System.out.println("is c open? " + c.isOpen());
            if (c.isOpen()) {
                System.out.println("is it excluded? ");
//                if (!c.equals(excludeSession)) {
                    c.getRemote().sendString(msg);
                    System.out.println("feast your eyesss");
//                }
            }
        }
    }






}
