package server.webSocket;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.AuthService;
import service.ClearService;
import service.GameService;
import service.UserService;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.userCommands.JoinPlayerCommand;
import webSocketMessages.userCommands.UserGameCommand;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    private final AuthService authService;
    private final ClearService clearService;
    private final GameService gameService;
    private final UserService userService;
    private final ConnectionManager connections = new ConnectionManager();

    public WebSocketHandler(AuthService authService, ClearService clearService, GameService gameService, UserService userService) {
        this.authService = authService;
        this.clearService = clearService;
        this.gameService = gameService;
        this.userService = userService;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        switch (userGameCommand.getCommandType()) {
            // TODO: implement actions
            case JOIN_PLAYER -> joinPlayer(session, message);
            case JOIN_OBSERVER -> {}
        }
    }
    public void joinPlayer(Session session, String message) throws IOException {
        JoinPlayerCommand joinPlayerCommand = new Gson().fromJson(message, JoinPlayerCommand.class);
        try {
            AuthData authData = authService.checkAuth(joinPlayerCommand.getAuthString());
            String username = authData.username();
            connections.add(username, session);

            GameData gameData = gameService.listGames().get(joinPlayerCommand.gameID);
            LoadGameMessage loadGameMessage = new LoadGameMessage(gameData.game());
            connections.whisper(username, new Gson().toJson(loadGameMessage));

            NotificationMessage notificationMessage = new NotificationMessage(
                    "%s has joined the game as the %s player".formatted(username, joinPlayerCommand.teamColor));
            connections.broadcast(username, notificationMessage);
        } catch (DataAccessException e) {
            exceptionParser(e, session);
        }
    }

    private void exceptionParser(DataAccessException e, Session session) throws IOException {
        ErrorMessage errorMessage = new ErrorMessage(e.getMessage());
        session.getRemote().sendString(new Gson().toJson(errorMessage));
    }
}
