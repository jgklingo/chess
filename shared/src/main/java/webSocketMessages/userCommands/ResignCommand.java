package webSocketMessages.userCommands;

import chess.ChessGame;

public class ResignCommand extends UserGameCommand {
    Integer gameID;
    String game;
    public ResignCommand(String authToken, Integer gameID, String updatedGame) {
        super(authToken);
        commandType = CommandType.RESIGN;
        this.gameID = gameID;
        this.game = updatedGame;
    }
    public Integer gameID() {
        return gameID;
    }
    public String game() {return game;}
}
