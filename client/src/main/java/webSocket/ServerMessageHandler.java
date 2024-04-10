package webSocket;

import webSocketMessages.serverMessages.ServerMessage;

public interface ServerMessageHandler {
    void notify(ServerMessage serverMessage);
}
