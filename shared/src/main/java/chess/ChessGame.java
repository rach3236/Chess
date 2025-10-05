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
    private ChessBoard new_board;

    public ChessGame() {
        this.board = new ChessBoard();
        this.board.resetBoard();
        this.team = TeamColor.WHITE;
    }

    public ChessBoard Copy_Board() {
        ChessBoard new_board = new ChessBoard();
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition pos = new ChessPosition(i, j);
                if (board.getPiece(pos) != null)
                {new_board.addPiece(pos, board.getPiece(pos));}
            }
        }
        return new_board;
    }

    //helper function to find the king
    public boolean Check_Helper(TeamColor teamColor, ChessBoard curr_board) {
        ChessPosition king_pos = null;

        boolean breakout = false;

        for (int i=1; i < 9; i++) {
            for (int j=1; j < 9; j++){
                ChessPiece piece = curr_board.getPiece(new ChessPosition(i, j));
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
                    king_pos = new ChessPosition(i, j);
                    breakout = true;
                    break;
                }
            }
            if (breakout) {break;}
        }

        for (int i=1; i < 9; i++){
            for (int j=1; j < 9; j++) {
                if(curr_board.getPiece(new ChessPosition(i,j)) != null) {
                    ChessPiece piece = curr_board.getPiece(new ChessPosition(i, j));
                    if (piece.getTeamColor() != teamColor) {
                        var temp = ChessPiece.pieceMoves(curr_board, new ChessPosition(i,j));
                        for (ChessMove move : temp) {
                            if (king_pos != null && move.getEndPosition().getRow() == king_pos.getRow() && move.getEndPosition().getColumn() == king_pos.getColumn()) {
                                return true;
                            }
                        }
                    }
                }

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
        Collection<ChessMove> valid_moves = ChessPiece.pieceMoves(board, startPosition);

        ChessPiece piece = board.getPiece(startPosition);

        Collection<ChessMove> for_sure_valid = new ArrayList<ChessMove>();
        for (ChessMove move : valid_moves) {
            new_board = Copy_Board();

            new_board.addPiece(move.getEndPosition(), board.getPiece(move.getStartPosition()));
            new_board.removePiece(move.getStartPosition());

            if (!Check_Helper(piece.getTeamColor(), new_board)) {
                for_sure_valid.add(move);
            }
        }
        return for_sure_valid;
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
                ChessPiece promo_piece = new ChessPiece(piece.getTeamColor(), ChessPiece.PieceType.PAWN);
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
        ChessPosition king_pos = null;

        return Check_Helper(teamColor, board);
    }
    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        ChessPosition king_pos = null;

        for (int i=1; i < 9; i++) {
            for (int j=1; j < 9; j++){
                ChessPiece piece = board.getPiece(new ChessPosition(i, j));
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
                    king_pos = new ChessPosition(i, j);
                }
            }
        }

        Collection<ChessMove> king_poss_moves = validMoves(king_pos);
        Collection<ChessMove> enemy_check_moves = new ArrayList<ChessMove>();

        for (int i=1; i < 9; i++){
            for (int j=1; j < 9; j++) {
                ChessPiece piece = board.getPiece(new ChessPosition(i, j));
                if (piece != null && piece.getTeamColor() != teamColor) {
                    var temp = validMoves(new ChessPosition(i, j));
                    for (ChessMove move : temp) {
                        enemy_check_moves.add(move);
                    }
                }
            }
        }

        for (ChessMove king_move : king_poss_moves){
            if (!enemy_check_moves.contains(king_move) && isInCheck(teamColor)) {
                return false;
            }
        }

        //run through, find pieces
        for (int i=1; i < 9; i++){
            for (int j=1; j < 9; j++) {
                ChessPiece piece = board.getPiece(new ChessPosition(i, j));
                if (piece != null && piece.getTeamColor() == teamColor) {
                    //find valid moves
                    var my_team_moves = validMoves(new ChessPosition(i, j));
                    for (ChessMove move : my_team_moves) {
                        //copy the board
                        new_board = Copy_Board();

                        //make move
                        new_board.addPiece(move.getEndPosition(), piece);
                        new_board.removePiece(move.getStartPosition());

                        //check in check
                        if (!Check_Helper(teamColor, new_board)){
                            return false;
                        }
                    }
                }
            }
        }
        return isInCheck(teamColor);
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
                ChessPiece piece = board.getPiece(new ChessPosition(i, j));
                if (piece != null && piece.getTeamColor() == teamColor) {
                    var valids = validMoves(new ChessPosition(i,j));
                    if (valids.size() > 0 && !inCheck) {
                        return false;
                    }
                }
            }
        }
        return (!isInCheckmate(teamColor));
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
        return Objects.hash(board, team, new_board);
    }
}




