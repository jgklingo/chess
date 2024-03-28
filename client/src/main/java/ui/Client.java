package ui;

import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import server.ServerFacade;

import java.util.ArrayList;
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

    public String eval(String input) throws ResponseException {
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
                case "listgames" -> listGames();
                case "joingame" -> joinGame(params);
                case "joinobserver" -> joinObserver(params);
                case "quit" -> "";
                default -> help();
            };
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
    private String listGames() throws ResponseException {
        return server.listGames(authToken).toString() + "\n";
    }
    private String joinGame(String[] params) throws ResponseException {
        String playerColor = params[0];
        String gameID = params[1];
        server.joinGame(authToken, playerColor, Integer.parseInt(gameID));
        return "Successful join as player.\n";
    }
    private String joinObserver(String[] params) throws ResponseException {
        String gameID = params[0];
        server.joinGame(authToken, null, Integer.parseInt(gameID));
        return "Successful join as observer.\n";
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
                    - createGame <gameName> (create a new game)
                    - listGames (list all games on the server)
                    - joinGame <playerColor> <gameID> (join an existing game as a player)
                    - joinObserver <gameID> (join an existing game as an observer)
                    - quit (close the client)
                    """;
        }
    }
}
