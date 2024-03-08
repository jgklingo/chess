package service;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import model.AuthData;
import model.UserData;

public class RegistrationService {
    private final DataAccess dataAccess;

    public RegistrationService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }
    public UserData register(UserData userData) throws DataAccessException {
        return dataAccess.createUser(userData);
    }
}
