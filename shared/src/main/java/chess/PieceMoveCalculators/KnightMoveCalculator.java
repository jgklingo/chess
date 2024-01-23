package chess.PieceMoveCalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMoveCalculator extends PieceMoveCalculator {
    public static Collection<ChessMove> moves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<ChessMove>();
        Collection<ChessPosition> possiblePositions = new ArrayList<ChessPosition>();
        possiblePositions.add(myPosition.b().b().l());
        possiblePositions.add(myPosition.l().l().b());
        possiblePositions.add(myPosition.l().l().t());
        possiblePositions.add(myPosition.t().t().l());
        possiblePositions.add(myPosition.t().t().r());
        possiblePositions.add(myPosition.r().r().t());
        possiblePositions.add(myPosition.r().r().b());
        possiblePositions.add(myPosition.b().b().r());

        for (ChessPosition p : possiblePositions) if (canMove(board,p)) {
            moves.add(new ChessMove(myPosition, p, null));
        } else if (inbounds(board, p)) {
            capture(board, p, myPosition, moves);
        }

        return moves;
    }
}