package chess.PieceMoveCalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;

public class KingMoveCalculator extends PieceMoveCalculator {
    public static ArrayList<ChessMove> moves(ChessBoard board, ChessPosition position) {
        ArrayList<ChessMove> moves = new ArrayList<ChessMove>();

        ArrayList<ChessPosition> possiblePositions = new ArrayList<>();
        possiblePositions.add(position.t());
        possiblePositions.add(position.tr());
        possiblePositions.add(position.r());
        possiblePositions.add(position.br());
        possiblePositions.add(position.b());
        possiblePositions.add(position.bl());
        possiblePositions.add(position.l());
        possiblePositions.add(position.tl());

        for (ChessPosition p : possiblePositions) {
            if (canMove(board, p)) {
                moves.add(new ChessMove(position, p, null));
            } else {
                moves.addAll(capture(board, position, p, board.getPiece(position).getTeamColor()));
            }
        }

        return moves;
    }
}
