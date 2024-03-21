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
        if (userData.username() == null || userData.password() == null || userData.email() == null) {
            throw new DataAccessException("Error: bad request", 400);
        }
        try (var conn = DatabaseManager.getConnection()) {
            // see if user already exists
            var preparedStatement = conn.prepareStatement("SELECT * FROM user WHERE username=?");
            preparedStatement.setString(1, userData.username());
            try (var rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    throw new DataAccessException("Error: already taken", 403);
                }
            }
            // insert user
            try (var preparedStatement2 = conn.prepareStatement(
                    "INSERT INTO user (username, password, email) VALUES(?, ?, ?)")) {
                preparedStatement2.setString(1, userData.username());
                preparedStatement2.setString(2, userData.password());
                preparedStatement2.setString(3, userData.email());

                preparedStatement2.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage(), 500);
        }

        return userData;
    }

    public AuthData createAuth(AuthData authData) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement2 = conn.prepareStatement(
                    "INSERT INTO auth (token, username) VALUES(?, ?)")) {
                preparedStatement2.setString(1, authData.authToken());
                preparedStatement2.setString(2, authData.username());
                preparedStatement2.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage(), 500);
        }
        return authData;
    }

    public boolean checkUser(UserData userData) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var preparedStatement = conn.prepareStatement("SELECT * FROM user WHERE (username, password)=(?,?)");
            preparedStatement.setString(1, userData.username());
            preparedStatement.setString(2, userData.password());
            try (var rs = preparedStatement.executeQuery()) {
                if (!rs.next()) {
                    throw new DataAccessException("Error: unauthorized", 401);
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage(), 500);
        }
        return true;
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var preparedStatement = conn.prepareStatement("DELETE FROM auth WHERE token=?");
            preparedStatement.setString(1, authToken);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage(), 500);
        }
    }

    public AuthData checkAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var preparedStatement = conn.prepareStatement("SELECT * FROM auth WHERE token=?");
            preparedStatement.setString(1, authToken);
            try (var rs = preparedStatement.executeQuery()) {
                if (!rs.next()) {
                    throw new DataAccessException("Error: unauthorized", 401);
                } else {
                    return new AuthData(rs.getString("token"), rs.getString("username"));
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage(), 500);
        }
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
