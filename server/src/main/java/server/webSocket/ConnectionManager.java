package server.webSocket;

import webSocketMessages.serverMessages.ServerMessage;

import javax.websocket.Session;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();
    public void add(String username, Session session) {
        var connection = new Connection(username, session);
        connections.put(username, connection);
    }
    public void remove(String username) {
        connections.remove(username);
    }
    public void broadcast(String excludeVisitorName, ServerMessage serverMessage) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (!c.username.equals(excludeVisitorName)) {
                    c.send(serverMessage.toString());
                }
            } else {
                removeList.add(c);
            }
        }

        for (var c : removeList) {
            connections.remove(c.username);
        }
    }
}
