package dataAccess;

import model.AuthData;
import model.GameData;
import model.UserData;
import service.AuthService;

import java.util.HashMap;

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

    public void deleteDB() throws DataAccessException {
        users.clear();
        games.clear();
        authTokens.clear();
    }
}
