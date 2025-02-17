package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard implements Cloneable {

    private ChessPiece[][] squares = new ChessPiece[8][8];
    private ChessMove previousMove;

    public ChessBoard() {
        previousMove = null;
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow()-1][position.getColumn()-1];
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "squares=" + Arrays.toString(squares) +
                '}';
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        addPiece(new ChessPosition(1, 1), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(1, 2), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(1, 3), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(1, 4), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(1, 5), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(1, 6), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(1, 7), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(1, 8), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        for (int i = 1; i <= 8; i++) {
            addPiece(new ChessPosition(2, i), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        }
        addPiece(new ChessPosition(8, 1), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(8, 2), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8, 3), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8, 4), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(8, 5), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(8, 6), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8, 7), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8, 8), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        for (int i = 1; i <= 8; i++) {
            addPiece(new ChessPosition(7, i), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        }
    }

    public void makeMove(ChessMove move) {
        if (move.getPromotionPiece() != null) {
            makePromotionMove(move);
            return;
        }
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        ChessPiece myPiece = getPiece(startPosition);
        if (myPiece != null) {
            if (myPiece.getPieceType() == ChessPiece.PieceType.KING && (Math.abs(startPosition.getColumn() - endPosition.getColumn()) == 2)) {
                /* Castle */
                ChessMove rookMove = getCastleMove(startPosition, endPosition);
                makeMove(rookMove);
            }
            if (myPiece.getPieceType() == ChessPiece.PieceType.PAWN ) {
                if (Math.abs(startPosition.getColumn() - endPosition.getColumn()) == 1) {
                    if (Math.abs(startPosition.getRow() - endPosition.getRow()) == 1) {
                        /* En Passant */
                        ChessMove enPassantMove = getEnPassantMove(startPosition, endPosition);
                        makeMove(enPassantMove);
                    }
                }
            }
        }
        addPiece(endPosition, myPiece);
        if (myPiece != null) {
            myPiece.setHasMoved(true);
        }
        addPiece(startPosition, null);
        previousMove = move;
    }

    private static ChessMove getCastleMove(ChessPosition startPosition, ChessPosition endPosition) {
        int rookRow = startPosition.getRow();
        int rookDirection = (startPosition.getColumn() < endPosition.getColumn()) ? -1 : 1;
        int rookStartCol = rookDirection == -1 ? 8 : 1;
        int rookEndCol = endPosition.getColumn() + rookDirection;
        ChessPosition rookStartPosition = new ChessPosition(rookRow, rookStartCol);
        ChessPosition rookEndPosition = new ChessPosition(rookRow, rookEndCol);
        return new ChessMove(rookStartPosition, rookEndPosition, null);
    }

    private static ChessMove getEnPassantMove(ChessPosition startPosition, ChessPosition endPosition) {
        int direction = (endPosition.getRow() > startPosition.getRow()) ? -1 : 1;
        ChessPosition enemyPosition = new ChessPosition(endPosition.getRow() + direction, endPosition.getColumn());
        return new ChessMove(endPosition, enemyPosition, null);
    }

    public ChessMove getPreviousMove() {
        return previousMove;
    }

    private void makePromotionMove(ChessMove move) {
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        ChessPiece originalPiece = getPiece(startPosition);
        ChessPiece promotionPiece = new ChessPiece(originalPiece.getTeamColor(), move.getPromotionPiece());
        addPiece(endPosition, promotionPiece);
        promotionPiece.setHasMoved(true);
        addPiece(startPosition, null);
    }

    @Override
    public ChessBoard clone() {
        try {
            ChessBoard clone = (ChessBoard) super.clone();
            ChessPiece[][] oldBoard = clone.squares;
            clone.squares = new ChessPiece[8][8];
            for (int i = 0; i < squares.length; i++) {
                for (int j = 0; j < squares[i].length; j++) {
                    if (oldBoard[i][j] != null) {
                        clone.squares[i][j] = oldBoard[i][j].clone();
                    }
                }
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
