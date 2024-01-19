package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return diagonals(board, myPosition, board.getPiece(myPosition).getTeamColor());
    }
    private Collection<ChessMove> diagonals(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor color) {
        // likely won't work for capturing, TODO: fix this
        ChessPosition currentPosition = myPosition;
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
    private void capture(ChessBoard board, ChessPosition position, ChessPosition currentPosition, Collection<ChessMove> moves) {
        if (0 < position.getRow() && position.getRow() < 9 && 0 < position.getColumn() && position.getColumn() < 9 &&
                board.getPiece(position).getTeamColor() != this.getTeamColor()) {
//            board.removePiece(position);
            moves.add(new ChessMove(currentPosition, position, null));
        }
    }

    /**
     * Determines whether a position is a valid place to move a piece. It must be within the bounds of the 8x8 board and
     * there must not already be a piece there. Does not take into account capturing.
     * @return boolean
     */
    private Boolean canMove(ChessBoard board, ChessPosition position) {
        return 0 < position.getRow() && position.getRow() < 9 && 0 < position.getColumn() && position.getColumn() < 9
                && board.getPiece(position) == null;
    }
}
