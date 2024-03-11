package dataAccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import javax.xml.crypto.Data;
import java.util.ArrayList;

public interface DataAccess {
    UserData createUser(UserData userData) throws DataAccessException;
    AuthData createAuth(AuthData authData) throws DataAccessException;
    boolean checkUser(UserData userData) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
    AuthData checkAuth(String authToken) throws DataAccessException;
    ArrayList<GameData> getGames(String username) throws DataAccessException;
    void deleteDB() throws DataAccessException;
}
