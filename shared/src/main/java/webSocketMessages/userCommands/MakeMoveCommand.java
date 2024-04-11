package webSocketMessages.userCommands;

import chess.ChessMove;

public class MakeMoveCommand extends UserGameCommand {
    Integer gameID;
    ChessMove chessMove;
    public MakeMoveCommand(String authToken, Integer gameID, ChessMove move) {
        super(authToken);
        commandType = CommandType.MAKE_MOVE;
        this.gameID = gameID;
        this.chessMove = move;
    }
    public Integer gameID() {
        return gameID;
    }
    public ChessMove chessMove() {
        return chessMove;
    }
}
