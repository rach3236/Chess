package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

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
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        Collection<ChessMove> possibleMoves = new ArrayList<>();

        Rulebook rules = new Rulebook();

        if (piece.getPieceType() == PieceType.BISHOP){
            possibleMoves = rules.bishopRules(myPosition, board);
        } else if (piece.getPieceType() == PieceType.ROOK) {
            possibleMoves = rules.rookRules(myPosition, board);
        } else if (piece.getPieceType() == PieceType.QUEEN) {
            possibleMoves = rules.queenRules(myPosition, board);
        } else if (piece.getPieceType() == PieceType.KING) {
            possibleMoves = rules.kingRules(myPosition, board);
        } else if (piece.getPieceType() == PieceType.KNIGHT) {
            possibleMoves = rules.knightRules(myPosition, board);
        } else if (piece.getPieceType() == PieceType.PAWN) {
            possibleMoves = rules.pawnRules(myPosition, board);
        }
        return possibleMoves;
    }


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ChessPiece that)) {
            return false;
        }
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
