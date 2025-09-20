package chess;

import java.util.ArrayList;
import java.util.Collection;

public class Rulebook {

    private ChessPiece myPiece;

    public Rulebook(){

    }
// Checks
    public boolean in_bounds(ChessPosition myPosition){
        return (myPosition.getRow() <= 8) && (myPosition.getColumn() <= 8) && (myPosition.getRow() > 0) && (myPosition.getColumn() > 0);
    }
    private boolean check_square(ChessPosition newPosition, ChessBoard board) {
        return ((in_bounds(newPosition)) && (board.getPiece(newPosition) == null));
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
    private boolean first_move_white(ChessPosition myPosition){
        return (myPosition.getRow() == 2);
    }
//    private boolean first_move_black(ChessPosition myPosition) {
//        return (myPosition.getRow());
//    }
    private boolean run_checks(ChessPosition newPosition, ChessBoard board) {
        return (in_bounds(newPosition)  && (check_square(newPosition, board) || enemy_piece(newPosition, board)));
    }

// Movement functions
public void up_right(ChessPosition myPosition, Collection<ChessPosition> poss_positions, ChessBoard board) {
        ChessPosition new_position = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()+1);

        if (!in_bounds(new_position)){
            return;
        } else if (check_square(new_position, board)){
            poss_positions.add(new_position);
            if (if_king()) {return;}
            up_right(new_position, poss_positions, board);
        } else if (enemy_piece(new_position, board)) {
            poss_positions.add(new_position);
            return;
        } else if (!enemy_piece(new_position, board)) {
            return;
        }
    }

    public void up_left(ChessPosition myPosition, Collection<ChessPosition> poss_positions, ChessBoard board) {
        ChessPosition new_position = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()-1);

        if (!in_bounds(new_position)){
            return;
        } else if (check_square(new_position, board)){
            poss_positions.add(new_position);
            if (if_king()) {return;}
            up_left(new_position, poss_positions, board);
        } else if (enemy_piece(new_position, board)) {
            poss_positions.add(new_position);
            return;
        } else if (!enemy_piece(new_position, board)) {
            return;
        }
    }

    public void down_right(ChessPosition myPosition, Collection<ChessPosition> poss_positions, ChessBoard board) {
        ChessPosition new_position = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()+1);

        if (!in_bounds(new_position)){
            return;
        } else if (check_square(new_position, board)){
            poss_positions.add(new_position);
            if (if_king()) {return;}
            down_right(new_position, poss_positions, board);
        } else if (enemy_piece(new_position, board)) {
            poss_positions.add(new_position);
            return;
        } else if (!enemy_piece(new_position, board)) {
            return;
        }
    }

    public void down_left(ChessPosition myPosition, Collection<ChessPosition> poss_positions, ChessBoard board) {
        ChessPosition new_position = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()-1);

        if (!in_bounds(new_position)){
            return;
        } else if (check_square(new_position, board)){
            poss_positions.add(new_position);
            if (if_king()) {return;}
            down_left(new_position, poss_positions, board);
        } else if (enemy_piece(new_position, board)) {
            poss_positions.add(new_position);
            return;
        } else if (!enemy_piece(new_position, board)) {
            return;
        }
    }

    public void straight_up(ChessPosition myPosition, Collection<ChessPosition> poss_positions, ChessBoard board) {
        ChessPosition new_position = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn());

        if (!in_bounds(new_position)){
            return;
        } else if (check_square(new_position, board)){
            poss_positions.add(new_position);
            if (if_king() || if_pawn()) {return;}
            straight_up(new_position, poss_positions, board);
        } else if (enemy_piece(new_position, board)) {
            poss_positions.add(new_position);
            return;
        } else if (!enemy_piece(new_position, board)) {
            return;
        }

        }

    public void straight_down(ChessPosition myPosition, Collection<ChessPosition> poss_positions, ChessBoard board) {
        ChessPosition new_position = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn());

        if (!in_bounds(new_position)){
            return;
        } else if (check_square(new_position, board)){
            poss_positions.add(new_position);
            if (if_king() || if_pawn()) {return;}
            straight_down(new_position, poss_positions, board);
        } else if (enemy_piece(new_position, board)) {
            poss_positions.add(new_position);
            return;
        } else if (!enemy_piece(new_position, board)) {
            return;
        }

    }

    public void straight_left(ChessPosition myPosition, Collection<ChessPosition> poss_positions, ChessBoard board) {
        ChessPosition new_position = new ChessPosition(myPosition.getRow(), myPosition.getColumn()-1);

        if (!in_bounds(new_position)){
            return;
        } else if (check_square(new_position, board)){
            poss_positions.add(new_position);
            if (if_king()) {return;}
            straight_left(new_position, poss_positions, board);
        } else if (enemy_piece(new_position, board)) {
            poss_positions.add(new_position);
            return;
        } else if (!enemy_piece(new_position, board)) {
            return;
        }

    }

    public void straight_right(ChessPosition myPosition, Collection<ChessPosition> poss_positions, ChessBoard board) {
        ChessPosition new_position = new ChessPosition(myPosition.getRow(), myPosition.getColumn()+1);

        if (!in_bounds(new_position)){
            return;
        } else if (check_square(new_position, board)){
            poss_positions.add(new_position);
            if (if_king()) {return;}
            straight_right(new_position, poss_positions, board);
        } else if (enemy_piece(new_position, board)) {
            poss_positions.add(new_position);
            return;
        } else if (!enemy_piece(new_position, board)) {
            return;
        }
        return;
    }

    public void knight_moves(ChessPosition myPosition, Collection<ChessPosition> poss_positions, ChessBoard board){
        //1
        ChessPosition new_pos = new ChessPosition(myPosition.getRow()+2, myPosition.getColumn()+1);
        if (run_checks(new_pos, board)) {poss_positions.add(new_pos);}
        //2
        ChessPosition new_pos2 = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()+2);
        if (run_checks(new_pos2, board)) {poss_positions.add(new_pos2);}
        //3
        ChessPosition new_pos3 = new ChessPosition(myPosition.getRow()-2, myPosition.getColumn()+1);
        if (run_checks(new_pos3, board)) {poss_positions.add(new_pos3);}
        //4
        ChessPosition new_pos4 = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()+2);
        if (run_checks(new_pos4, board)) {poss_positions.add(new_pos4);}
        //5
        ChessPosition new_pos5 = new ChessPosition(myPosition.getRow()-2, myPosition.getColumn()-1);
        if (run_checks(new_pos5, board)) {poss_positions.add(new_pos5);}
        //6
        ChessPosition new_pos6 = new ChessPosition(myPosition.getRow()+2, myPosition.getColumn()-1);
        if (run_checks(new_pos6, board)) {poss_positions.add(new_pos6);}
        //7
        ChessPosition new_pos7 = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()-2);
        if (run_checks(new_pos7, board)) {poss_positions.add(new_pos7);}
        //8
        ChessPosition new_pos8 = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()-2);
        if (run_checks(new_pos8, board)) {poss_positions.add(new_pos8);}

        return;
    }

    public Collection<ChessMove> bishop_rules(ChessPosition myPosition, ChessBoard board) {
        //make list of possible moves
        //add possible moves to it as you recurse
        Collection<ChessPosition> poss_positions = new ArrayList<>();

        myPiece = board.getPiece(myPosition);
        // base condition, if the next square isn't empty/isn't on the board

        //go up and to the right
        up_right(myPosition, poss_positions, board);
        up_left(myPosition, poss_positions, board);
        down_right(myPosition, poss_positions, board);
        down_left(myPosition, poss_positions, board);

        Collection<ChessMove> possible_moves = new ArrayList<>();
        for (ChessPosition pos : poss_positions) {
            possible_moves.add(new ChessMove(myPosition, pos, null));
        }

        return possible_moves;
    }

// rook
    // straight up and down, left and right
    public Collection<ChessMove> rook_rules(ChessPosition myPosition, ChessBoard board) {
        Collection<ChessPosition> poss_positions = new ArrayList<>();

        myPiece = board.getPiece(myPosition);

        //go straight up, down, left, right
        straight_up(myPosition, poss_positions, board);
        straight_down(myPosition, poss_positions, board);
        straight_left(myPosition, poss_positions, board);
        straight_right(myPosition, poss_positions, board);

        Collection<ChessMove> possible_moves = new ArrayList<>();
        for (ChessPosition pos : poss_positions) {
            possible_moves.add(new ChessMove(myPosition, pos, null));
        }

        return possible_moves;
    }


    // queen
    // rook + bishop combined!
    public Collection<ChessMove> queen_rules(ChessPosition myPosition, ChessBoard board) {
        Collection<ChessPosition> poss_positions = new ArrayList<>();

        myPiece = board.getPiece(myPosition);

        up_right(myPosition, poss_positions, board);
        up_left(myPosition, poss_positions, board);
        down_right(myPosition, poss_positions, board);
        down_left(myPosition, poss_positions, board);

        straight_up(myPosition, poss_positions, board);
        straight_down(myPosition, poss_positions, board);
        straight_left(myPosition, poss_positions, board);
        straight_right(myPosition, poss_positions, board);

        Collection<ChessMove> possible_moves = new ArrayList<>();
        for (ChessPosition pos : poss_positions) {
            possible_moves.add(new ChessMove(myPosition, pos, null));
        }

        return possible_moves;
    }


    // king
    // Just like a queen, except no recursion:)
    public Collection<ChessMove> king_rules(ChessPosition myPosition, ChessBoard board) {
        Collection<ChessPosition> poss_positions = new ArrayList<>();

        myPiece = board.getPiece(myPosition);

        up_right(myPosition, poss_positions, board);
        up_left(myPosition, poss_positions, board);
        down_right(myPosition, poss_positions, board);
        down_left(myPosition, poss_positions, board);

        straight_up(myPosition, poss_positions, board);
        straight_down(myPosition, poss_positions, board);
        straight_left(myPosition, poss_positions, board);
        straight_right(myPosition, poss_positions, board);


        Collection<ChessMove> possible_moves = new ArrayList<>();
        for (ChessPosition pos : poss_positions) {
            possible_moves.add(new ChessMove(myPosition, pos, null));
        }

        return possible_moves;
    }


    //knight
    public Collection<ChessMove> knight_rules(ChessPosition myPosition, ChessBoard board) {
        Collection<ChessPosition> poss_positions = new ArrayList<>();

        myPiece = board.getPiece(myPosition);

        knight_moves(myPosition, poss_positions, board);


        Collection<ChessMove> possible_moves = new ArrayList<>();
        for (ChessPosition pos : poss_positions) {
            possible_moves.add(new ChessMove(myPosition, pos, null));
        }

        return possible_moves;
    }


    //pawn
    public Collection<ChessMove> pawn_rules(ChessPosition myPosition, ChessBoard board) {
        Collection<ChessPosition> poss_positions = new ArrayList<>();
        Collection<ChessMove> possible_moves = new ArrayList<>();

        myPiece = board.getPiece(myPosition);

        //for white pawns going up
        if (myPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            if (first_move_white(myPosition)) {
                ChessPosition first_new_position = new ChessPosition(myPosition.getRow()+2, myPosition.getColumn());
                if (run_checks(first_new_position, board)) poss_positions.add(first_new_position);
                straight_up(myPosition, poss_positions, board);
            } else if (myPosition.getRow() == 7) {
                ChessPosition new_position = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn());
                if (run_checks(new_position, board)) poss_positions.add(new_position);
            }
//            } else if (myPosition.getRow() == 8) {
//                straight_down(myPosition, poss_positions, board);
//                straight_up(myPosition, poss_positions, board);
//                straight_left(myPosition, poss_positions, board);
//                straight_right(myPosition, poss_positions, board);
//                up_right(myPosition, poss_positions, board);
//                up_left(myPosition, poss_positions, board);
//                down_right(myPosition, poss_positions, board);
//                down_left(myPosition, poss_positions, board);
//                knight_moves(myPosition, poss_positions, board);


            ChessPosition poss_w_enemy_piece_positions1 = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()+1);
            ChessPosition poss_w_enemy_piece_positions2 = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()-1);
//
//            if (enemy_piece(poss_w_enemy_piece_positions1, board)) {
//                poss_positions.add(poss_w_enemy_piece_positions1);
//            }
//            if (enemy_piece(poss_w_enemy_piece_positions2, board)) {
//                poss_positions.add(poss_w_enemy_piece_positions2);
//            }

        }

//        if (myPiece.getTeamColor() == ChessGame.TeamColor.BLACK) {
//            if (first_move_black(myPosition)) {
//                poss_positions.add(new ChessPosition(myPosition.getRow()-2, myPosition.getColumn()));
//                straight_down(myPosition, poss_positions, board);
//            } else if (myPosition.getRow() == 2) {
//                poss_positions.add(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn()));
//            }
////            else if (myPosition.getRow() == 8) {
////                straight_down(myPosition, poss_positions, board);
////                straight_up(myPosition, poss_positions, board);
////                straight_left(myPosition, poss_positions, board);
////                straight_right(myPosition, poss_positions, board);
////                up_right(myPosition, poss_positions, board);
////                up_left(myPosition, poss_positions, board);
////                down_right(myPosition, poss_positions, board);
////                down_left(myPosition, poss_positions, board);
////                knight_moves(myPosition, poss_positions, board);
//
//
//
//            ChessPosition poss_b_enemy_piece_positions1 = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()+1);
//            ChessPosition poss_b_enemy_piece_positions2 = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()-1);
//
//            if (enemy_piece(poss_b_enemy_piece_positions1, board)) {
//                poss_positions.add(poss_b_enemy_piece_positions1);
//            }
//            if (enemy_piece(poss_b_enemy_piece_positions2, board)) {
//                poss_positions.add(poss_b_enemy_piece_positions2);
//            }
//        }


        // you only have to add to the list of moves, (add move that gets you to the top, plus bishop, rook, knight, and queen rules)

        for (ChessPosition pos : poss_positions) {
            possible_moves.add(new ChessMove(myPosition, pos, null));
        }

        return possible_moves;
    }



}
