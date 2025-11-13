package chess;

import java.util.ArrayList;
import java.util.Collection;

public class Rulebook {

    private ChessPiece myPiece;

    public Rulebook(){

    }
// Checks
    public boolean in_bounds(ChessPosition myPosition){
        return (myPosition.getRow() <= 8) && (myPosition.getColumn() <= 8) && (myPosition.getRow() >= 1) && (myPosition.getColumn() >= 1);
    }
    private boolean check_square(ChessPosition newPosition, ChessBoard board) {
        return (board.getPiece(newPosition) == null);
    }
    private boolean enemy_piece(ChessPosition newPosition, ChessBoard board) {
        ChessPiece piece = board.getPiece(newPosition);

        return (myPiece.getTeamColor() != piece.getTeamColor());
    }
    private boolean team_piece(ChessPosition newPosition, ChessBoard board){
        ChessPiece piece = board.getPiece(newPosition);
        return (myPiece.getTeamColor() == piece.getTeamColor());
    }
    private boolean if_king() {
        return (myPiece.getPieceType() == ChessPiece.PieceType.KING);
    }
    private boolean if_pawn() {
        return (myPiece.getPieceType() == ChessPiece.PieceType.PAWN);
    }
    private boolean firstMove_check(ChessPosition move) {
        if (move.getRow() == 2 && myPiece.getTeamColor() == ChessGame.TeamColor.WHITE) return true;
        else if (move.getRow() == 7 && myPiece.getTeamColor() == ChessGame.TeamColor.BLACK) return true;
        else return false;
    }

    private boolean run_checks(ChessPosition newPosition, ChessBoard board) {
        return (in_bounds(newPosition)  && (check_square(newPosition, board) || enemy_piece(newPosition, board)));
    }

// Movement functions
public void up_right(ChessPosition myPosition, Collection<ChessPosition> possPositions, ChessBoard board) {
        ChessPosition newPosition = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()+1);

        if (!in_bounds(newPosition)){
            return;
        } else if (check_square(newPosition, board)){
            possPositions.add(newPosition);
            if (if_king()) {return;}
            up_right(newPosition, possPositions, board);
        } else if (enemy_piece(newPosition, board)) {
            possPositions.add(newPosition);
            return;
        } else if (!enemy_piece(newPosition, board)) {
            return;
        }
    }

    public void up_left(ChessPosition myPosition, Collection<ChessPosition> possPositions, ChessBoard board) {
        ChessPosition newPosition = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()-1);

        if (!in_bounds(newPosition)){
            return;
        } else if (check_square(newPosition, board)){
            possPositions.add(newPosition);
            if (if_king()) {return;}
            up_left(newPosition, possPositions, board);
        } else if (enemy_piece(newPosition, board)) {
            possPositions.add(newPosition);
            return;
        } else if (!enemy_piece(newPosition, board)) {
            return;
        }
    }

    public void down_right(ChessPosition myPosition, Collection<ChessPosition> possPositions, ChessBoard board) {
        ChessPosition newPosition = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()+1);

        if (!in_bounds(newPosition)){
            return;
        } else if (check_square(newPosition, board)){
            possPositions.add(newPosition);
            if (if_king()) {return;}
            down_right(newPosition, possPositions, board);
        } else if (enemy_piece(newPosition, board)) {
            possPositions.add(newPosition);
            return;
        } else if (!enemy_piece(newPosition, board)) {
            return;
        }
    }

    public void down_left(ChessPosition myPosition, Collection<ChessPosition> possPositions, ChessBoard board) {
        ChessPosition newPosition = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()-1);

        if (!in_bounds(newPosition)){
            return;
        } else if (check_square(newPosition, board)){
            possPositions.add(newPosition);
            if (if_king()) {return;}
            down_left(newPosition, possPositions, board);
        } else if (enemy_piece(newPosition, board)) {
            possPositions.add(newPosition);
            return;
        } else if (!enemy_piece(newPosition, board)) {
            return;
        }
    }

    public void straight_up(ChessPosition myPosition, Collection<ChessPosition> possPositions, ChessBoard board) {
        ChessPosition newPosition = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn());

        if (!in_bounds(newPosition)){
            return;
        } else if (check_square(newPosition, board)){
            possPositions.add(newPosition);
            if (if_king()) {return;}
            else if (if_pawn()) {return;}
            straight_up(newPosition, possPositions, board);
        } else if (enemy_piece(newPosition, board)) {
            possPositions.add(newPosition);
            return;
        } else if (!enemy_piece(newPosition, board)) {
            return;
        }
        return;

        }

    public void straight_down(ChessPosition myPosition, Collection<ChessPosition> possPositions, ChessBoard board) {
        ChessPosition newPosition = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn());

        if (!in_bounds(newPosition)){
            return;
        } else if (check_square(newPosition, board)){
            possPositions.add(newPosition);
            if (if_king() || if_pawn()) {return;}
            straight_down(newPosition, possPositions, board);
        } else if (enemy_piece(newPosition, board)) {
            possPositions.add(newPosition);
            return;
        } else if (!enemy_piece(newPosition, board)) {
            return;
        }

    }

    public void straight_left(ChessPosition myPosition, Collection<ChessPosition> possPositions, ChessBoard board) {
        ChessPosition newPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn()-1);

        if (!in_bounds(newPosition)){
            return;
        } else if (check_square(newPosition, board)){
            possPositions.add(newPosition);
            if (if_king()) {return;}
            straight_left(newPosition, possPositions, board);
        } else if (enemy_piece(newPosition, board)) {
            possPositions.add(newPosition);
            return;
        } else if (!enemy_piece(newPosition, board)) {
            return;
        }

    }

    public void straight_right(ChessPosition myPosition, Collection<ChessPosition> possPositions, ChessBoard board) {
        ChessPosition newPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn()+1);

        if (!in_bounds(newPosition)){
            return;
        } else if (check_square(newPosition, board)){
            possPositions.add(newPosition);
            if (if_king()) {return;}
            straight_right(newPosition, possPositions, board);
        } else if (enemy_piece(newPosition, board)) {
            possPositions.add(newPosition);
            return;
        } else if (!enemy_piece(newPosition, board)) {
            return;
        }
        return;
    }

    public void knight_moves(ChessPosition myPosition, Collection<ChessPosition> possPositions, ChessBoard board){
        //1
        ChessPosition newPos = new ChessPosition(myPosition.getRow()+2, myPosition.getColumn()+1);
        if (run_checks(newPos, board)) {possPositions.add(newPos);}
        //2
        ChessPosition newPos2 = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()+2);
        if (run_checks(newPos2, board)) {possPositions.add(newPos2);}
        //3
        ChessPosition newPos3 = new ChessPosition(myPosition.getRow()-2, myPosition.getColumn()+1);
        if (run_checks(newPos3, board)) {possPositions.add(newPos3);}
        //4
        ChessPosition newPos4 = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()+2);
        if (run_checks(newPos4, board)) {possPositions.add(newPos4);}
        //5
        ChessPosition newPos5 = new ChessPosition(myPosition.getRow()-2, myPosition.getColumn()-1);
        if (run_checks(newPos5, board)) {possPositions.add(newPos5);}
        //6
        ChessPosition newPos6 = new ChessPosition(myPosition.getRow()+2, myPosition.getColumn()-1);
        if (run_checks(newPos6, board)) {possPositions.add(newPos6);}
        //7
        ChessPosition newPos7 = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()-2);
        if (run_checks(newPos7, board)) {possPositions.add(newPos7);}
        //8
        ChessPosition newPos8 = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()-2);
        if (run_checks(newPos8, board)) {possPositions.add(newPos8);}

        return;
    }

    public Collection<ChessMove> bishop_rules(ChessPosition myPosition, ChessBoard board) {
        //make list of possible moves
        //add possible moves to it as you recurse
        Collection<ChessPosition> possPositions = new ArrayList<>();

        myPiece = board.getPiece(myPosition);
        // base condition, if the next square isn't empty/isn't on the board

        //go up and to the right
        up_right(myPosition, possPositions, board);
        up_left(myPosition, possPositions, board);
        down_right(myPosition, possPositions, board);
        down_left(myPosition, possPositions, board);

        Collection<ChessMove> possibleMoves = new ArrayList<>();
        for (ChessPosition pos : possPositions) {
            possibleMoves.add(new ChessMove(myPosition, pos, null));
        }

        return possibleMoves;
    }

// rook
    // straight up and down, left and right
    public Collection<ChessMove> rook_rules(ChessPosition myPosition, ChessBoard board) {
        Collection<ChessPosition> possPositions = new ArrayList<>();

        myPiece = board.getPiece(myPosition);

        //go straight up, down, left, right
        straight_up(myPosition, possPositions, board);
        straight_down(myPosition, possPositions, board);
        straight_left(myPosition, possPositions, board);
        straight_right(myPosition, possPositions, board);

        Collection<ChessMove> possibleMoves = new ArrayList<>();
        for (ChessPosition pos : possPositions) {
            possibleMoves.add(new ChessMove(myPosition, pos, null));
        }

        return possibleMoves;
    }


    // queen
    // rook + bishop combined!
    public Collection<ChessMove> queen_rules(ChessPosition myPosition, ChessBoard board) {
        Collection<ChessPosition> possPositions = new ArrayList<>();

        myPiece = board.getPiece(myPosition);

        up_right(myPosition, possPositions, board);
        up_left(myPosition, possPositions, board);
        down_right(myPosition, possPositions, board);
        down_left(myPosition, possPositions, board);

        straight_up(myPosition, possPositions, board);
        straight_down(myPosition, possPositions, board);
        straight_left(myPosition, possPositions, board);
        straight_right(myPosition, possPositions, board);

        Collection<ChessMove> possibleMoves = new ArrayList<>();
        for (ChessPosition pos : possPositions) {
            possibleMoves.add(new ChessMove(myPosition, pos, null));
        }

        return possibleMoves;
    }


    // king
    // Just like a queen, except no recursion:)
    public Collection<ChessMove> king_rules(ChessPosition myPosition, ChessBoard board) {
        Collection<ChessPosition> possPositions = new ArrayList<>();

        myPiece = board.getPiece(myPosition);

        up_right(myPosition, possPositions, board);
        up_left(myPosition, possPositions, board);
        down_right(myPosition, possPositions, board);
        down_left(myPosition, possPositions, board);

        straight_up(myPosition, possPositions, board);
        straight_down(myPosition, possPositions, board);
        straight_left(myPosition, possPositions, board);
        straight_right(myPosition, possPositions, board);


        Collection<ChessMove> possibleMoves = new ArrayList<>();
        for (ChessPosition pos : possPositions) {
            possibleMoves.add(new ChessMove(myPosition, pos, null));
        }

        return possibleMoves;
    }


    //knight
    public Collection<ChessMove> knight_rules(ChessPosition myPosition, ChessBoard board) {
        Collection<ChessPosition> possPositions = new ArrayList<>();

        myPiece = board.getPiece(myPosition);

        knight_moves(myPosition, possPositions, board);


        Collection<ChessMove> possibleMoves = new ArrayList<>();
        for (ChessPosition pos : possPositions) {
            possibleMoves.add(new ChessMove(myPosition, pos, null));
        }

        return possibleMoves;
    }


    //pawn
    public Collection<ChessMove> pawn_rules(ChessPosition myPosition, ChessBoard board) {
        Collection<ChessPosition> possPositions = new ArrayList<>();
        Collection<ChessMove> possibleMoves = new ArrayList<>();

        myPiece = board.getPiece(myPosition);


        if (myPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {

            ChessPosition upMove = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn());
            if (in_bounds(upMove) && check_square(upMove, board)) {possPositions.add(upMove);}

            ChessPosition firstMove = new ChessPosition(myPosition.getRow()+2, myPosition.getColumn());
            if (in_bounds(firstMove) && firstMove_check(myPosition) && check_square(firstMove, board) && check_square(upMove, board)) {
                possPositions.add(firstMove);
            }

            ChessPosition enemyPos1 = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()+1);
            if (in_bounds(enemyPos1) && !check_square(enemyPos1, board)) {
                if (enemy_piece(enemyPos1, board)) {
                    possPositions.add(enemyPos1);
                }
            }
            ChessPosition enemyPos2 = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()-1);
            if (in_bounds(enemyPos2)){
                if (!check_square(enemyPos2, board)) {

                    if (enemy_piece(enemyPos2, board)) {
                          possPositions.add(enemyPos2);
                    }
                }
            }




        }


        if (myPiece.getTeamColor() == ChessGame.TeamColor.BLACK) {

            ChessPosition downMove = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn());
            if (in_bounds(downMove) && check_square(downMove, board)) {possPositions.add(downMove);}

            ChessPosition firstMove = new ChessPosition(myPosition.getRow()-2, myPosition.getColumn());
            if (in_bounds(firstMove) && firstMove_check(myPosition) && check_square(firstMove, board) && check_square(downMove, board)) {possPositions.add(firstMove);}

            ChessPosition enemyPos1 = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()+1);
            if (in_bounds(enemyPos1) && !check_square(enemyPos1, board)) {
                if (enemy_piece(enemyPos1, board)) {
                    possPositions.add(enemyPos1);
                }
            }
            ChessPosition enemyPos2 = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()-1);
            if (in_bounds(enemyPos2)) {
                if (!check_square(enemyPos2, board)){
                    if (enemy_piece(enemyPos2, board)) {
                        possPositions.add(enemyPos2);
                    }
                }


            }

        }


        for (ChessPosition pos : possPositions) {
            if (pos.getRow() == 8 && myPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                possibleMoves.add(new ChessMove(myPosition, pos, ChessPiece.PieceType.BISHOP));
                possibleMoves.add(new ChessMove(myPosition, pos, ChessPiece.PieceType.ROOK));
                possibleMoves.add(new ChessMove(myPosition, pos, ChessPiece.PieceType.QUEEN));
                possibleMoves.add(new ChessMove(myPosition, pos, ChessPiece.PieceType.KNIGHT));
            } else if (pos.getRow() == 1 && myPiece.getTeamColor() == ChessGame.TeamColor.BLACK) {
                possibleMoves.add(new ChessMove(myPosition, pos, ChessPiece.PieceType.BISHOP));
                possibleMoves.add(new ChessMove(myPosition, pos, ChessPiece.PieceType.ROOK));
                possibleMoves.add(new ChessMove(myPosition, pos, ChessPiece.PieceType.QUEEN));
                possibleMoves.add(new ChessMove(myPosition, pos, ChessPiece.PieceType.KNIGHT));
            } else {
                possibleMoves.add(new ChessMove(myPosition, pos, null));
            }
        }

        return possibleMoves;
    }



}
