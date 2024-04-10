package webSocketMessages.userCommands;

import chess.ChessGame;

public class JoinPlayerCommand extends JoinObserverCommand {
    public ChessGame.TeamColor teamColor;

    public JoinPlayerCommand(String authToken, Integer gameID, ChessGame.TeamColor teamColor) {
        super(authToken, gameID);
        this.teamColor = teamColor;
        this.commandType = CommandType.JOIN_PLAYER;
    }
    public ChessGame.TeamColor TeamColor() {
        return teamColor;
    }
}
