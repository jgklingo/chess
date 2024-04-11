package server.webSocket;

import chess.ChessGame;
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
import webSocketMessages.userCommands.*;

import java.io.IOException;
import java.util.Objects;

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
            case JOIN_OBSERVER -> joinObserver(session, message);
            case LEAVE -> leave(session, message);
            case RESIGN -> resign(session, message);
        }
    }
    public void joinPlayer(Session session, String message) throws IOException {
        JoinPlayerCommand joinPlayerCommand = new Gson().fromJson(message, JoinPlayerCommand.class);
        try {
            AuthData authData = authService.checkAuth(joinPlayerCommand.getAuthString());
            String username = authData.username();
            GameData gameData = gameService.listGames().get(joinPlayerCommand.gameID);
            if ((joinPlayerCommand.playerColor == ChessGame.TeamColor.WHITE && gameData.whiteUsername() != null)
                    || (joinPlayerCommand.playerColor == ChessGame.TeamColor.BLACK && gameData.blackUsername() != null)) {
                throw new DataAccessException("Error: Spot is already taken.");
            }
            connections.add(username, session);

            LoadGameMessage loadGameMessage = new LoadGameMessage(gameData.game());
            connections.whisper(username, loadGameMessage);

            NotificationMessage notificationMessage = new NotificationMessage(
                    "%s has joined the game as the %s player.".formatted(username, joinPlayerCommand.playerColor));
            connections.broadcast(username, notificationMessage);
        } catch (DataAccessException e) {
            exceptionParser(e, session);
        }
    }
    public void joinObserver(Session session, String message) throws IOException {
        JoinObserverCommand joinObserverCommand = new Gson().fromJson(message, JoinObserverCommand.class);
        try {
            AuthData authData = authService.checkAuth(joinObserverCommand.getAuthString());
            String username = authData.username();
            connections.add(username, session);

            GameData gameData = gameService.listGames().get(joinObserverCommand.gameID);
            LoadGameMessage loadGameMessage = new LoadGameMessage(gameData.game());
            connections.whisper(username, loadGameMessage);

            NotificationMessage notificationMessage = new NotificationMessage(
                    "%s has joined the game as an observer.".formatted(username));
            connections.broadcast(username, notificationMessage);
        } catch (DataAccessException e) {
            exceptionParser(e, session);
        }
    }
    public void leave(Session session, String message) throws IOException {
        try {
            LeaveCommand leaveCommand = new Gson().fromJson(message, LeaveCommand.class);
            AuthData authData = authService.checkAuth(leaveCommand.getAuthString());
            String username = authData.username();
            NotificationMessage notificationMessage = new NotificationMessage(
                    "%s has left the game.".formatted(username));
            connections.broadcast(username, notificationMessage);
            connections.remove(username);
        } catch (DataAccessException e) {
            exceptionParser(e, session);
        }
    }
    public void resign(Session session, String message) throws IOException {
        try {
            ResignCommand resignCommand = new Gson().fromJson(message, ResignCommand.class);
            AuthData authData = authService.checkAuth(resignCommand.getAuthString());
            String username = authData.username();
            gameService.updateGame(resignCommand.gameID(), resignCommand.game());
            connections.broadcast(authData.username(), new LoadGameMessage(new Gson().fromJson(resignCommand.game(), ChessGame.class)));
            NotificationMessage notificationMessage = new NotificationMessage(
                    "%s has resigned the game.".formatted(username));
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
