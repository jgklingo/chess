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

import java.util.UUID;

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
//        Spark.post("/game", this::createGame);
//        Spark.put("/game", this::joinGame);

        Spark.awaitInitialization();
        return Spark.port();
    }

    private Object register(Request req, Response res) throws DataAccessException {
        var userData = new Gson().fromJson(req.body(), UserData.class);
        userData = registrationService.register(userData);
        AuthData authData = authService.createAuth(userData);
        return new Gson().toJson(authData);
    }

    private Object login(Request req, Response res) throws DataAccessException {
        var userData = new Gson().fromJson(req.body(), UserData.class);
        AuthData authData = authService.login(userData);
        return new Gson().toJson(authData);
    }

    private Object logout(Request req, Response res) throws DataAccessException {
        String authToken = req.headers("authorization");
        authService.logout(authToken);
        return 200;
    }

    private Object listGames(Request req, Response res) throws DataAccessException {
        String authToken = req.headers("authorization");
        AuthData authData = authService.checkAuth(authToken);
        var games = gameService.listGames(authData.username());
        return new Gson().toJson(games);
    }

    private Object clear(Request req, Response res) throws DataAccessException {
        clearService.deleteDB();
        res.status(200);
        return 200; // TODO: fix this
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
