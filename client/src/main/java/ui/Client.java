package ui;

import chess.ChessBoard;
import chess.ChessGame;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import server.ServerFacade;
import webSocket.ServerMessageHandler;
import webSocket.WebSocketFacade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class Client {
    private final Repl repl;  // This IS the ServerMessageHandler/NotificationHandler
    private final ServerFacade server;
    private WebSocketFacade ws;
    private ClientState clientState = ClientState.SIGNED_OUT;
    private String authToken = null;
    private HashMap<Integer, Integer> gameListMapping;
    protected ChessBoard currentBoard;

    private enum ClientState {
        SIGNED_IN, SIGNED_OUT, IN_GAME
    }

    public Client(String serverUrl, Repl repl) throws ResponseException {
        server = new ServerFacade(serverUrl);
        this.repl = repl;
        ws = new WebSocketFacade(serverUrl, repl);
    }

    public String eval(String input) throws ResponseException {
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        switch (this.clientState) {
            case SIGNED_OUT -> {
                return switch (cmd) {
                    case "login" -> login();
                    case "register" -> register();
                    case "quit" -> "";
                    default -> help();
                };
            }
            case SIGNED_IN -> {
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
            case IN_GAME -> {
                /*
                    - help (see help text)
                    - redrawBoard (redraw the chess board)
                    - leave (leave the game)
                    - makeMove (make a move)
                    - resign (resign the game)
                    - highlightMoves (see all legal moves for a piece)
                */
                return switch (cmd) {
                    case "redrawboard" -> redrawBoard();
                    default -> help();
                };
            }
            case null -> throw new RuntimeException("Bad client state.");
        }
    }
    private String register() throws ResponseException {
        String username = repl.prompt("Username: ");
        String password = repl.prompt("Password: ");
        String email = repl.prompt("Email: ");
        AuthData authData = server.register(new UserData(username, password, email));
        authToken = authData.authToken();
        clientState = ClientState.SIGNED_IN;
        return "User registered.\n";
    }
    private String login() throws ResponseException {
        String username = repl.prompt("Username: ");
        String password = repl.prompt("Password: ");
        AuthData authData = server.login(new UserData(username, password, null));
        authToken = authData.authToken();
        clientState = ClientState.SIGNED_IN;
        return "Logged in.\n";
    }
    private String logout() throws ResponseException {
        server.logout(authToken);
        clientState = ClientState.SIGNED_OUT;
        return "Logged out.\n";
    }
    private String createGame(String[] params) throws ResponseException {
        if (params.length != 1) {
            return help();
        }
        String gameName = params[0];
        server.createGame(authToken, gameName);
        return "Game created.\n";
    }
    private String listGames() throws ResponseException {
        ArrayList<GameData> games = server.listGames(authToken);
        if (games.isEmpty()) {
            return "No games found.\n";
        }
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
        if (params.length != 2) {
            return help();
        }
        String playerColor = params[0];
        String gameNumber = params[1];
        server.joinGame(authToken, playerColor, getGameID(gameNumber));

        ChessGame.TeamColor teamColor = null;
        if (Objects.equals(playerColor, "black")) {
            teamColor = ChessGame.TeamColor.BLACK;
        } else if (Objects.equals(playerColor, "white")) {
            teamColor = ChessGame.TeamColor.WHITE;
        }
        ws.joinPlayer(authToken, getGameID(gameNumber), teamColor);
        clientState = ClientState.IN_GAME;
        return "Successful join as player.\n";
    }
    private String joinObserver(String[] params) throws ResponseException {
        if (params.length != 1) {
            return help();
        }
        String gameNumber = params[0];
        server.joinGame(authToken, null, getGameID(gameNumber));
        ws.joinObserver(authToken, getGameID(gameNumber));
        clientState = ClientState.IN_GAME;
        return "Successful join as observer.\n" + printBoard(currentBoard);
    }
    public String redrawBoard() {
        // TODO: print only the board from the perspective of the user
        return printBoard(currentBoard);
    }
    public String help() {
        return switch (clientState) {
            case SIGNED_OUT -> """
                    - help (see help text)
                    - login (create new session)
                    - register (create new user)
                    - quit (close the client)
                    """;

            case SIGNED_IN -> """
                    - help (see help text)
                    - logout (end session)
                    - createGame <gameName> (create a new game)
                    - listGames (list all games on the server)
                    - joinGame [white|black] <gameNumber> (join an existing game as a player)
                    - joinObserver <gameNumber> (join an existing game as an observer)
                    - quit (close the client)
                    """;

            case IN_GAME -> """
                    - help (see help text)
                    - redrawBoard (redraw the chess board)
                    - leave (leave the game)
                    - makeMove (make a move)
                    - resign (resign the game)
                    - highlightMoves (see all legal moves for a piece)
                    """;
        };
    }
    private String printBoard(ChessBoard chessBoard) {
        BoardArtist boardArtist = new BoardArtist(chessBoard);
        return boardArtist.drawReverseBoard() + "\n" + boardArtist.drawBoard();
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
    private Integer getGameID(String gameNumber) {
        Integer gameID = -1;
        if (gameListMapping.get(Integer.parseInt(gameNumber)) != null) {
            gameID = gameListMapping.get(Integer.parseInt(gameNumber));
        }
        return gameID;
    }
}
