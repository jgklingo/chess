package webSocket;

import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.ServerMessage;

public interface ServerMessageHandler {
    void notify(ServerMessage serverMessage);
    void updateGame(LoadGameMessage loadGameMessage);
}
