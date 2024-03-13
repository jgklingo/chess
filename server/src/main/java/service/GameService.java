package service;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;

public class GameService {
    private final DataAccess dataAccess;

    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public ArrayList<GameData> listGames(String username) throws DataAccessException {
        return dataAccess.getGames();
    }

    public GameData createGame(String name) throws DataAccessException {
        return dataAccess.newGame(name);
    }

    public void joinGame(AuthData authData, String playerColor, Integer gameID) throws DataAccessException {
        dataAccess.addPlayer(authData.username(), playerColor, gameID);
    }
}
