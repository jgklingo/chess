package chess.PieceMoveCalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;

public class RookMoveCalculator extends PieceMoveCalculator {
    public static Collection<ChessMove> moves(ChessBoard board, ChessPosition myPosition) {
        return verticals(board, myPosition);
    }
}