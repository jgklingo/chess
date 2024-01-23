package chess.PieceMoveCalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMoveCalculator extends PieceMoveCalculator {
    public static Collection<ChessMove> moves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<ChessMove>();
        switch (board.getPiece(myPosition).getTeamColor()) {
            case WHITE -> {
                if (canMove(board, myPosition.t())) {
                    moves.add(new ChessMove(myPosition, myPosition.t(), null));
                }
                if (myPosition.getRow() == 2 && canMove(board, myPosition.t()) && canMove(board, myPosition.t().t())) {
                    moves.add(new ChessMove(myPosition, myPosition.t().t(), null));
                }
                capture(board, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1), myPosition, moves);
                capture(board, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1), myPosition, moves);
            }
            case BLACK -> {
                if (canMove(board, myPosition.b())) {
                    moves.add(new ChessMove(myPosition, myPosition.b(), null));
                }
                if (myPosition.getRow() == 7 && canMove(board, myPosition.b()) && canMove(board, myPosition.b().b())) {
                    moves.add(new ChessMove(myPosition, myPosition.b().b(), null));
                }
                capture(board, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1), myPosition, moves);
                capture(board, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1), myPosition, moves);
            }
        }

        return moves;
    }
}