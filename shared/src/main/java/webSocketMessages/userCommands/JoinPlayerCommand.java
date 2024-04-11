package webSocketMessages.userCommands;

import chess.ChessGame;
import com.google.gson.Gson;

public class JoinPlayerCommand extends JoinObserverCommand {
    public String teamColor;

    public JoinPlayerCommand(String authToken, Integer gameID, ChessGame.TeamColor teamColor) {
        super(authToken, gameID);
        this.teamColor = new Gson().toJson(teamColor);
        this.commandType = CommandType.JOIN_PLAYER;
    }
    public String TeamColor() {
        return teamColor;
    }
}
