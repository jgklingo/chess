package service;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import model.GameData;

import java.util.HashMap;

public class GameService {
    private final DataAccess dataAccess;

    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public HashMap<Integer, GameData> listGames() throws DataAccessException {
        return dataAccess.getGames();
    }

    public GameData createGame(String name) throws DataAccessException {
        return dataAccess.newGame(name);
    }

    public void joinGame(String username, String playerColor, Integer gameID) throws DataAccessException {
        dataAccess.addPlayer(username, playerColor, gameID);
    }
}
