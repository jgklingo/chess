package chess.PieceMoveCalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;

public class QueenMoveCalculator extends PieceMoveCalculator {
    public static Collection<ChessMove> moves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> movesCollection = verticals(board, myPosition);
        movesCollection.addAll(diagonals(board, myPosition));
        return movesCollection;
    }
}