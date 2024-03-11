package dataAccess;

import model.AuthData;
import model.GameData;
import model.UserData;
import service.AuthService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class MemoryDataAccess implements DataAccess{
    private int gameID = 1;
    final private HashMap<String, UserData> users = new HashMap<>();
    final private HashMap<Integer, GameData> games = new HashMap<>();
    final private HashMap<String, AuthData> authTokens = new HashMap<>();

    public UserData createUser(UserData userData) throws DataAccessException {
        userData = new UserData(userData.username(), userData.password(), userData.email());
        users.put(userData.username(), userData);
        return userData;
    }

    public AuthData createAuth(AuthData authData) throws DataAccessException {
        authData = new AuthData(authData.authToken(), authData.username());
        authTokens.put(authData.authToken(), authData);
        return authData;
    }

    public boolean checkUser(UserData userData) throws DataAccessException {
        UserData record = users.get(userData.username());
        return Objects.equals(userData.password(), record.password());
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        authTokens.remove(authToken);
    }

    public AuthData checkAuth(String authToken) throws DataAccessException {
        return authTokens.get(authToken);
    }

    public ArrayList<GameData> getGames(String username) throws DataAccessException {
        ArrayList<GameData> gameList = new ArrayList<>();
        for (Integer id : games.keySet()) {
            GameData gameData = games.get(id);
            if (gameData.blackUsername().equals(username) || gameData.whiteUsername().equals(username)) {
                gameList.add(gameData);
            }
        }
        return gameList;
    }

    public void deleteDB() throws DataAccessException {
        users.clear();
        games.clear();
        authTokens.clear();
    }
}
