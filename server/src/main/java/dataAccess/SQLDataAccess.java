package dataAccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.sql.SQLException;
import java.util.HashMap;

public class SQLDataAccess implements DataAccess {

    public SQLDataAccess() throws DataAccessException {
        configureDatabase();
    }
    public UserData createUser(UserData userData) throws DataAccessException {
        return null;
    }

    public AuthData createAuth(AuthData authData) throws DataAccessException {
        return null;
    }

    public boolean checkUser(UserData userData) throws DataAccessException {
        return false;
    }

    public void deleteAuth(String authToken) throws DataAccessException {

    }

    public AuthData checkAuth(String authToken) throws DataAccessException {
        return null;
    }

    public HashMap<Integer, GameData> getGames() throws DataAccessException {
        return null;
    }

    public GameData newGame(String name) throws DataAccessException {
        return null;
    }

    public void addPlayer(String username, String playerColor, Integer gameID) throws DataAccessException {

    }

    public void deleteDB() throws DataAccessException {
        final String[] deleteStatements = {
                "TRUNCATE user;", "TRUNCATE game;", "TRUNCATE auth;"
        };

        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : deleteStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("SQL Error: %s", ex.getMessage()), 500);
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  auth (
              token varchar(256) NOT NULL,
              username varchar(256) NOT NULL,
              PRIMARY KEY (token)
            );
            """,
            """
            CREATE TABLE IF NOT EXISTS  game (
                ID int NOT NULL,
                whiteUsername varchar(256),
                blackUsername varchar(256),
                gameName varchar(256) NOT NULL,
                json longtext NOT NULL,
                PRIMARY KEY (ID)
            );
            """,
            """
            CREATE TABLE IF NOT EXISTS  user (
                username varchar(256) NOT NULL,
                password varchar(256) NOT NULL,
                email varchar(256) NOT NULL,
                PRIMARY KEY (username)
            );
            """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            conn.setCatalog("chess");

            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()), 500);
        }
    }
}
