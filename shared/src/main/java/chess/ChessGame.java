package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private TeamColor team;
    private ChessBoard newBoard;

    public ChessGame() {
        this.board = new ChessBoard();
        this.board.resetBoard();
        this.team = TeamColor.WHITE;
    }

    public ChessBoard copyBoard() {
        ChessBoard newBoard = new ChessBoard();
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition pos = new ChessPosition(i, j);
                if (board.getPiece(pos) != null)
                {newBoard.addPiece(pos, board.getPiece(pos));}
            }
        }
        return newBoard;
    }

    //helper function to find the king
    public boolean checkHelper(TeamColor teamColor, ChessBoard currBoard) {
        ChessPosition kingPos = null;

        boolean breakout = false;

        for (int i=1; i < 9; i++) {
            for (int j=1; j < 9; j++){
                ChessPiece piece = currBoard.getPiece(new ChessPosition(i, j));
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
                    kingPos = new ChessPosition(i, j);
                    breakout = true;
                    break;
                }
            }
            if (breakout) {break;}
        }

        for (int i=1; i < 9; i++){
            for (int j=1; j < 9; j++) {
                if (checkForPiece(teamColor, currBoard, i, j, kingPos)) {
                    return true;
                }

            }
        }
        return false;
    }

    private static boolean checkForPiece(TeamColor teamColor, ChessBoard currBoard, int i, int j, ChessPosition kingPos) {
        if(currBoard.getPiece(new ChessPosition(i, j)) == null) {
            return false;
        }
        ChessPiece piece = currBoard.getPiece(new ChessPosition(i, j));
        if (piece.getTeamColor() == teamColor) {
            return false;
        }
        var temp = ChessPiece.pieceMoves(currBoard, new ChessPosition(i, j));
        for (ChessMove move : temp) {
            if (kingPos != null && move.getEndPosition().getRow() == kingPos.getRow()
                    && move.getEndPosition().getColumn() == kingPos.getColumn()) {
                return true;
            }
        }
        return false;
    }


    /**
     * @return Which team's turn it is
     *
     */
    public TeamColor getTeamTurn() {
        return team;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.team = team;
    }
    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        // make a new board
        //see if the king moves into check
        // remove those positions from valid moves
        Collection<ChessMove> validMoves = ChessPiece.pieceMoves(board, startPosition);

        ChessPiece piece = board.getPiece(startPosition);

        Collection<ChessMove> forSureValid = new ArrayList<ChessMove>();
        for (ChessMove move : validMoves) {
            newBoard = copyBoard();

            newBoard.addPiece(move.getEndPosition(), board.getPiece(move.getStartPosition()));
            newBoard.removePiece(move.getStartPosition());

            if (!checkHelper(piece.getTeamColor(), newBoard)) {
                forSureValid.add(move);
            }
        }
        return forSureValid;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (piece == null) {
            throw new InvalidMoveException();
        } else if (piece.getTeamColor() != getTeamTurn()) {
            throw new InvalidMoveException();
        } else if (validMoves(move.getStartPosition()).contains(move)) {
            if (move.getPromotionPiece() != null) {
                ChessPiece promoPiece = new ChessPiece(piece.getTeamColor(), ChessPiece.PieceType.PAWN);
                if (move.getPromotionPiece() == ChessPiece.PieceType.QUEEN) {
                    piece = new ChessPiece((piece.getTeamColor()), ChessPiece.PieceType.QUEEN);
                } else if (move.getPromotionPiece() == ChessPiece.PieceType.BISHOP) {
                    piece = new ChessPiece((piece.getTeamColor()), ChessPiece.PieceType.BISHOP);
                } else if (move.getPromotionPiece() == ChessPiece.PieceType.ROOK) {
                    piece = new ChessPiece((piece.getTeamColor()), ChessPiece.PieceType.ROOK);
                } else if (move.getPromotionPiece() == ChessPiece.PieceType.KNIGHT) {
                    piece = new ChessPiece((piece.getTeamColor()), ChessPiece.PieceType.KNIGHT);
                }
            }
            board.addPiece(move.getEndPosition(), piece);
            board.removePiece(move.getStartPosition());

            setTeamTurn(getTeamTurn() == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE);
        } else {
            throw new InvalidMoveException();
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPos = null;

        return checkHelper(teamColor, board);
    }
    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        ChessPosition kingPos = null;

        for (int i=1; i < 9; i++) {
            for (int j=1; j < 9; j++){
                ChessPiece piece = board.getPiece(new ChessPosition(i, j));
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
                    kingPos = new ChessPosition(i, j);
                }
            }
        }

        Collection<ChessMove> kingPosMoves = validMoves(kingPos);
        Collection<ChessMove> enemyCheckMoves = new ArrayList<ChessMove>();

        for (int i=1; i < 9; i++){
            for (int j=1; j < 9; j++) {
                ChessPiece piece = board.getPiece(new ChessPosition(i, j));
                if (piece != null && piece.getTeamColor() != teamColor) {
                    var temp = validMoves(new ChessPosition(i, j));
                    for (ChessMove move : temp) {
                        enemyCheckMoves.add(move);
                    }
                }
            }
        }

        for (ChessMove kingMove : kingPosMoves){
            if (!enemyCheckMoves.contains(kingMove) && isInCheck(teamColor)) {
                return false;
            }
        }

        //run through, find pieces
        for (int i=1; i < 9; i++){
            for (int j=1; j < 9; j++) {
                if (checkforCheck(teamColor, i, j)) {
                    return false;
                }
            }
        }
        return isInCheck(teamColor);
    }

    private boolean checkforCheck(TeamColor teamColor, int i, int j) {
        ChessPiece piece = board.getPiece(new ChessPosition(i, j));
        if (piece != null && piece.getTeamColor() == teamColor) {
            //find valid moves
            var myTeamMoves = validMoves(new ChessPosition(i, j));
            for (ChessMove move : myTeamMoves) {
                //copy the board
                newBoard = copyBoard();

                //make move
                newBoard.addPiece(move.getEndPosition(), piece);
                newBoard.removePiece(move.getStartPosition());

                //check in check
                if (!checkHelper(teamColor, newBoard)){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        // for every character piece on the board, any valid moves, return false;
        // if validMoves.size() != 0

        boolean inCheck = isInCheck(teamColor);
        for (int i=1; i < 9; i++) {
            for (int j=1; j < 9; j++) {
                if (checkForStalemate(teamColor, i, j, inCheck)) {
                    return false;
                }
            }
        }
        return (!isInCheckmate(teamColor));
    }

    private boolean checkForStalemate(TeamColor teamColor, int i, int j, boolean inCheck) {
        ChessPiece piece = board.getPiece(new ChessPosition(i, j));
        if (piece != null && piece.getTeamColor() == teamColor) {
            var valids = validMoves(new ChessPosition(i, j));
            if (valids.size() > 0 && !inCheck) {
                return true;
            }
        }
        return false;
    }

    //TO DO
    public boolean checkSubmittedMoves(){
        return false;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ChessGame chessGame)) {
            return false;
        }
        return Objects.equals(board, chessGame.board) && team == chessGame.team && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, team, newBoard);
    }
}









