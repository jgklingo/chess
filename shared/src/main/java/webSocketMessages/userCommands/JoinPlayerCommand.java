package webSocketMessages.userCommands;

import chess.ChessGame;
import com.google.gson.Gson;

public class JoinPlayerCommand extends JoinObserverCommand {
    public ChessGame.TeamColor playerColor;

    public JoinPlayerCommand(String authToken, Integer gameID, ChessGame.TeamColor playerColor) {
        super(authToken, gameID);
        this.playerColor = playerColor;
//        if (teamColor == ChessGame.TeamColor.BLACK) {
//            this.teamColor = "black";
//        } else if (teamColor == ChessGame.TeamColor.WHITE) {
//            this.teamColor = "white";
//        }
        this.commandType = CommandType.JOIN_PLAYER;
    }
    public ChessGame.TeamColor PlayerColor() {
        return playerColor;
    }
}
