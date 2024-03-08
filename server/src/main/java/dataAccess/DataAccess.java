package dataAccess;

import model.AuthData;
import model.UserData;

public interface DataAccess {
    UserData createUser(UserData userData) throws DataAccessException;
    AuthData createAuth(AuthData authData) throws DataAccessException;
    void deleteDB() throws DataAccessException;
}
