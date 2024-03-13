package dataAccess;

import model.AuthData;
import model.GameData;
import model.UserData;
import service.GameService;

import javax.xml.crypto.Data;
import java.util.ArrayList;

public interface DataAccess {
    UserData createUser(UserData userData) throws DataAccessException;
    AuthData createAuth(AuthData authData) throws DataAccessException;
    boolean checkUser(UserData userData) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
    AuthData checkAuth(String authToken) throws DataAccessException;
    ArrayList<GameData> getGames() throws DataAccessException;
    GameData newGame(String name) throws DataAccessException;
    void addPlayer(String Username, String playerColor, Integer gameID) throws DataAccessException;
    void deleteDB() throws DataAccessException;
}
