package ui;

import exception.ResponseException;
import model.AuthData;
import model.UserData;
import server.ServerFacade;

import java.util.Arrays;

public class Client {
    private final String serverUrl;
    private final Repl repl;
    private final ServerFacade server;
    private boolean signedIn = false;
    private String authToken = null;
    public Client(String serverUrl, Repl repl) {
        this.serverUrl = serverUrl;
        server = new ServerFacade(serverUrl);
        this.repl = repl;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            if (!signedIn) {
                return switch (cmd) {
                    case "login" -> login();
                    case "register" -> register();
                    case "quit" -> "";
                    default -> help();
                };
            } else {
                return switch (cmd) {
                    case "logout" -> logout();
                    case "creategame" -> createGame(params);
                    case "quit" -> "";
                    default -> help();
                };
            }
        } catch (Throwable ex) {
            return ex.getMessage();
        }
    }
    private String register() throws ResponseException {
        String username = repl.prompt("Username: ");
        String password = repl.prompt("Password: ");
        String email = repl.prompt("Email: ");
        server.register(new UserData(username, password, email));
        return "User registered.\n";
    }
    private String login() throws ResponseException {
        String username = repl.prompt("Username: ");
        String password = repl.prompt("Password: ");
        AuthData authData = server.login(new UserData(username, password, null));
        authToken = authData.authToken();
        signedIn = true;
        return "Logged in.\n";
    }
    private String logout() throws ResponseException {
        server.logout(authToken);
        signedIn = false;
        return "Logged out.\n";
    }
    private String createGame(String[] params) throws ResponseException {
        String gameName = params[0];
        server.createGame(authToken, gameName);
        return "Game created.\n";
    }

    public String help() {
        if (!signedIn) {
            return """
                    - help (see help text)
                    - login (create new session)
                    - register (create new user)
                    - quit (close the client)
                    """;
        } else {
            return """
                    - help (see help text)
                    - logout (end session)
                    - createGame (create a new game)
                    - listGames (list all games on the server)
                    - joinGame (join an existing game as a player)
                    - joinObserver (join an existing game as an observer)
                    - quit (close the client)
                    """;
        }
    }
}
