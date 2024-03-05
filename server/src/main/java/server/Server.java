package server;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import model.*;
import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::register);

        Spark.awaitInitialization();
        return Spark.port();
    }

    private Object register(Request req, Response res) throws DataAccessException {
        var userData = new Gson().fromJson(req.body(), UserData.class);
        return "";
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
