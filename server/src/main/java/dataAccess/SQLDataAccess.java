package dataAccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Objects;

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
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                preparedStatement2.setString(1, userData.username());
                preparedStatement2.setString(2, encoder.encode(userData.password()));
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

    public void checkUser(UserData userData) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var preparedStatement = conn.prepareStatement("SELECT * FROM user WHERE username=? LIMIT 1");
            preparedStatement.setString(1, userData.username());
            try (var rs = preparedStatement.executeQuery()) {
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                if (!rs.next()) {
                    throw new DataAccessException("Error: unauthorized", 401);
                }
                var password = rs.getString("password");
                if (!encoder.matches(userData.password(), password)) {
                    throw new DataAccessException("Error: unauthorized", 401);
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage(), 500);
        }
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
        HashMap<Integer, GameData> games = new HashMap<>();
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT * FROM game")) {
                try (var rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        var id = rs.getInt("ID");
                        var whiteUsername = rs.getString("whiteUsername");
                        var blackUsername = rs.getString("blackUsername");
                        var gameName = rs.getString("gameName");
                        var game = rs.getString("json");
                        games.put(id, new GameData(id, whiteUsername, blackUsername, gameName, new Gson().fromJson(game, ChessGame.class)));  // TODO: fix this?
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage(), 500);
        }
        return games;
    }

    public GameData newGame(String name) throws DataAccessException {
        if (name == null || name.isEmpty()) {
            throw new DataAccessException("Error: bad request", 400);
        }
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("INSERT INTO game (gameName) VALUES(?)", Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, name);
                preparedStatement.executeUpdate();

                var resultSet = preparedStatement.getGeneratedKeys();
                var ID = 0;
                if (resultSet.next()) {
                    ID = resultSet.getInt(1);
                }
                return new GameData(ID, null, null, name, null);
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage(), 500);
        }
    }

    public void addPlayer(String username, String playerColor, Integer gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            // check if game exists
            var preparedStatement = conn.prepareStatement("SELECT * FROM game WHERE ID=?");
            preparedStatement.setInt(1, gameID);
            try (var rs = preparedStatement.executeQuery()) {
                if (!rs.next()) {
                    throw new DataAccessException("Error: bad request", 400);
                } else {
                    // check if spot is taken
                    var whiteUsername = rs.getString("whiteUsername");
                    var blackUsername = rs.getString("blackUsername");
                    if ((Objects.equals(playerColor, "BLACK") && blackUsername != null)
                            || (Objects.equals(playerColor, "WHITE") && whiteUsername != null)) {
                        throw new DataAccessException("Error: already taken", 403);
                    }
                }
            }
            // add user
            PreparedStatement preparedStatement2;
            if (Objects.equals(playerColor, "WHITE")) {
                preparedStatement2 = conn.prepareStatement("UPDATE game SET whiteUsername=? WHERE ID=?");
            } else if (Objects.equals(playerColor, "BLACK")) {
                preparedStatement2 = conn.prepareStatement("UPDATE game SET blackUsername=? WHERE ID=?");
            } else {
                // add as observer
                return;
            }
            preparedStatement2.setString(1, username);
            preparedStatement2.setInt(2, gameID);
            preparedStatement2.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage(), 500);
        }
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
                ID int NOT NULL AUTO_INCREMENT,
                whiteUsername varchar(256),
                blackUsername varchar(256),
                gameName varchar(256) NOT NULL,
                json longtext,
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
