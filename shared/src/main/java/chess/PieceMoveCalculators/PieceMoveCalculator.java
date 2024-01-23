package chess.PieceMoveCalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class PieceMoveCalculator {
    public static Collection<ChessMove> diagonals(ChessBoard board, ChessPosition myPosition) {
        ChessPosition currentPosition;
        Collection<ChessMove> moves = new ArrayList<ChessMove>();

        currentPosition = myPosition.tr();
        while (canMove(board, currentPosition)) {
            moves.add(new ChessMove(myPosition, currentPosition, null));
            currentPosition = currentPosition.tr();
        }
        capture(board, currentPosition, myPosition, moves);

        currentPosition = myPosition.tl();
        while (canMove(board, currentPosition)) {
            moves.add(new ChessMove(myPosition, currentPosition, null));
            currentPosition = currentPosition.tl();
        }
        capture(board, currentPosition, myPosition, moves);

        currentPosition = myPosition.br();
        while (canMove(board, currentPosition)) {
            moves.add(new ChessMove(myPosition, currentPosition, null));
            currentPosition = currentPosition.br();
        }
        capture(board, currentPosition, myPosition, moves);

        currentPosition = myPosition.bl();
        while (canMove(board, currentPosition)) {
            moves.add(new ChessMove(myPosition, currentPosition, null));
            currentPosition = currentPosition.bl();
        }
        capture(board, currentPosition, myPosition, moves);

        return moves;
    }
    public static Collection<ChessMove> verticals(ChessBoard board, ChessPosition myPosition) {
        ChessPosition currentPosition;
        Collection<ChessMove> moves = new ArrayList<ChessMove>();

        currentPosition = myPosition.t();
        while (canMove(board, currentPosition)) {
            moves.add(new ChessMove(myPosition, currentPosition, null));
            currentPosition = currentPosition.t();
        }
        capture(board, currentPosition, myPosition, moves);

        currentPosition = myPosition.b();
        while (canMove(board, currentPosition)) {
            moves.add(new ChessMove(myPosition, currentPosition, null));
            currentPosition = currentPosition.b();
        }
        capture(board, currentPosition, myPosition, moves);

        currentPosition = myPosition.r();
        while (canMove(board, currentPosition)) {
            moves.add(new ChessMove(myPosition, currentPosition, null));
            currentPosition = currentPosition.r();
        }
        capture(board, currentPosition, myPosition, moves);

        currentPosition = myPosition.l();
        while (canMove(board, currentPosition)) {
            moves.add(new ChessMove(myPosition, currentPosition, null));
            currentPosition = currentPosition.l();
        }
        capture(board, currentPosition, myPosition, moves);

        return moves;
    }
    private static void capture(ChessBoard board, ChessPosition position, ChessPosition currentPosition, Collection<ChessMove> moves) {
        if (0 < position.getRow() && position.getRow() < 9 && 0 < position.getColumn() && position.getColumn() < 9 &&
                board.getPiece(position).getTeamColor() != board.getPiece(currentPosition).getTeamColor()) {
            moves.add(new ChessMove(currentPosition, position, null));
        }
    }
    /**
     * Determines whether a position is a valid place to move a piece. It must be within the bounds of the 8x8 board and
     * there must not already be a piece there. Does not take into account capturing.
     * @return boolean
     */
    private static Boolean canMove(ChessBoard board, ChessPosition position) {
        return 0 < position.getRow() && position.getRow() < 9 && 0 < position.getColumn() && position.getColumn() < 9
                && board.getPiece(position) == null;
    }
}
