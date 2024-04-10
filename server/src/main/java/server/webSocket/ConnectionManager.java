package server.webSocket;

import org.eclipse.jetty.util.IO;
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
    public void broadcast(String excludeUsername, ServerMessage serverMessage) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (!c.username.equals(excludeUsername)) {
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
    public void whisper(String username, ServerMessage serverMessage) throws IOException {
        connections.get(username).send(serverMessage.toString());
    }
}
