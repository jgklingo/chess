package chess.PieceMoveCalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

public class KingMoveCalculator extends PieceMoveCalculator {
    public static Collection<ChessMove> moves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<ChessMove>();
        Collection<ChessPosition> possiblePositions = new ArrayList<ChessPosition>();
        possiblePositions.add(myPosition.b());
        possiblePositions.add(myPosition.bl());
        possiblePositions.add(myPosition.l());
        possiblePositions.add(myPosition.tl());
        possiblePositions.add(myPosition.t());
        possiblePositions.add(myPosition.tr());
        possiblePositions.add(myPosition.r());
        possiblePositions.add(myPosition.br());

        for (ChessPosition p : possiblePositions) if (canMove(board,p)) {
            moves.add(new ChessMove(myPosition, p, null));
        } else if (inbounds(board, p)) {
            capture(board, p, myPosition, moves);
        }

        return moves;
    }
}