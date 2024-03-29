package ui;

import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.GameDataList;
import model.UserData;
import server.ServerFacade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Client {
    private final String serverUrl;
    private final Repl repl;
    private final ServerFacade server;
    private boolean signedIn = false;
    private String authToken = null;
    private HashMap<Integer, Integer> gameListMapping;

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
            gameListMapping = mapGames();
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
        AuthData authData = server.register(new UserData(username, password, email));
        authToken = authData.authToken();
        signedIn = true;
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
        ArrayList<GameData> games = server.listGames(authToken);
        StringBuilder gameListString = new StringBuilder();
        for (GameData game : games) {
            gameListString.append(gameListMapping.get(game.gameID())).append(". ");
            gameListString.append("Game Name: %s, ".formatted(game.gameName()));
            gameListString.append("White Player: %s, ".formatted(game.whiteUsername()));
            gameListString.append("Black Player: %s".formatted(game.blackUsername()));
            gameListString.append("\n");
        }
        return gameListString.toString();
    }
    private String joinGame(String[] params) throws ResponseException {
        String playerColor = params[0];
        String gameNumber = params[1];
        Integer gameID = -1;
        if (gameListMapping.get(Integer.parseInt(gameNumber)) != null) {
            gameID = gameListMapping.get(Integer.parseInt(gameNumber));
        }
        server.joinGame(authToken, playerColor, gameID);
        return "Successful join as player.\n";
    }
    private String joinObserver(String[] params) throws ResponseException {
        String gameNumber = params[0];
        Integer gameID = -1;
        if (gameListMapping.get(Integer.parseInt(gameNumber)) != null) {
            gameID = gameListMapping.get(Integer.parseInt(gameNumber));
        }
        server.joinGame(authToken, null, gameID);
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
                    - joinGame <playerColor> <gameNumber> (join an existing game as a player)
                    - joinObserver <gameNumber> (join an existing game as an observer)
                    - quit (close the client)
                    """;
        }
    }
    private HashMap<Integer, Integer> mapGames() throws ResponseException {
        HashMap<Integer, Integer> mapping = new HashMap<>();
        ArrayList<GameData> games = server.listGames(authToken);
        int num = 1;
        for (GameData game : games) {
            mapping.put(num++, game.gameID());
        }
        return mapping;
    }
}
