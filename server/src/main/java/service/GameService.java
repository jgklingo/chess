package service;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import model.GameData;

import java.util.ArrayList;

public class GameService {
    private final DataAccess dataAccess;

    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public ArrayList<GameData> listGames(String username) throws DataAccessException {
        return dataAccess.getGames(username);
    }
}
