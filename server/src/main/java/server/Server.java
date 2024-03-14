package server;

import com.google.gson.Gson;
import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import model.*;
import service.AuthService;
import service.ClearService;
import service.GameService;
import service.RegistrationService;
import spark.*;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.HashMap;

public class Server {
    private final AuthService authService;
    private final ClearService clearService;
    private final GameService gameService;
    private final RegistrationService registrationService;

    public Server() {
        DataAccess dataAccess = new MemoryDataAccess();
        this.authService = new AuthService(dataAccess);
        this.clearService = new ClearService(dataAccess);
        this.gameService = new GameService(dataAccess);
        this.registrationService = new RegistrationService(dataAccess);
    }
    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", this::clear);
        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
        Spark.exception(DataAccessException.class, this::exceptionHandler);

        Spark.awaitInitialization();
        return Spark.port();
    }

    private void exceptionHandler(DataAccessException ex, Request req, Response res) {
        res.status(ex.StatusCode());
    }

    private Object register(Request req, Response res) throws DataAccessException {
        try {
            var userData = new Gson().fromJson(req.body(), UserData.class);
            userData = registrationService.register(userData);
            AuthData authData = authService.createAuth(userData);
            return new Gson().toJson(authData);
        } catch (DataAccessException e) {
            return exceptionParser(e, res);
        }
    }

    private Object login(Request req, Response res) throws DataAccessException {
        try {
            var userData = new Gson().fromJson(req.body(), UserData.class);
            AuthData authData = authService.login(userData);
            return new Gson().toJson(authData);
        } catch (DataAccessException e) {
            return exceptionParser(e, res);
        }
    }

    private Object logout(Request req, Response res) throws DataAccessException {
        try {
            String authToken = req.headers("authorization");
            authService.checkAuth(authToken);
            authService.logout(authToken);
            return "";
        } catch (DataAccessException e) {
            return exceptionParser(e, res);
        }
    }

    private Object listGames(Request req, Response res) throws DataAccessException {
        try {
            String authToken = req.headers("authorization");
            authService.checkAuth(authToken);
            var games = gameService.listGames();

            var gameListJSON = new HashMap<String, ArrayList<GameData>>();
            gameListJSON.put("games", new ArrayList<>());
            gameListJSON.get("games").addAll(games.values());

            return new Gson().toJson(gameListJSON);
//            return "{\"test\": \"foo bar\"}";
        } catch (DataAccessException e) {
            return exceptionParser(e, res);
        }
    }

    private Object createGame(Request req, Response res) throws DataAccessException {
        try {
            var name = (String) new Gson().fromJson(req.body(), HashMap.class).get("gameName");
            String authToken = req.headers("authorization");
            AuthData authData = authService.checkAuth(authToken);
            GameData gameData = gameService.createGame(name);
            return new Gson().toJson(gameData);
        } catch (DataAccessException e) {
            return exceptionParser(e, res);
        }
    }

    private Object joinGame(Request req, Response res) throws DataAccessException {
        try {
            String authToken = req.headers("authorization");
            var requestBody = new Gson().fromJson(req.body(), HashMap.class);
            var playerColor = (String) requestBody.get("playerColor");
            var gameID = (Double) requestBody.get("gameID");
            AuthData authData = authService.checkAuth(authToken);
            gameService.joinGame(authData, playerColor, gameID.intValue());
//        var name = new Gson().fromJson(req.body(), String.class);
        } catch (DataAccessException e) {
            return exceptionParser(e, res);
        }
        return "";
    }

    private Object clear(Request req, Response res) throws DataAccessException {
        try {
            clearService.deleteDB();
            return "";
        } catch (DataAccessException e) {
            return exceptionParser(e, res);
        }
    }

    private String exceptionParser(DataAccessException e, Response res) {
        res.status(e.StatusCode());
        return new Gson().toJson(new jsonResponse(e.getMessage()));
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
