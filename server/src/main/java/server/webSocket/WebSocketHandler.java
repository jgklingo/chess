package server.webSocket;

import com.google.gson.Gson;
import webSocketMessages.userCommands.UserGameCommand;

import javax.websocket.OnMessage;
import javax.websocket.ClientEndpoint;
import javax.websocket.Session;
import java.io.IOException;

@ClientEndpoint  // TODO: check annotations
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();

    @OnMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        switch (userGameCommand.getCommandType()) {
            // TODO: implement actions
        }
    }
}
