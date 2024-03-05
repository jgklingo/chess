package service;

import dataAccess.UserDataAccess;
import model.AuthData;
import model.UserData;

public class RegistrationService {
    private final UserDataAccess dataAccess;

    public RegistrationService(UserDataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }
//    public AuthData register(UserData userData) {
//
//    }
}
