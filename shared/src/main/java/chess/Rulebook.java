package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Rulebook {

    private ChessPiece myPiece;

    public Rulebook(){

    }
// Checks
    public boolean in_bounds(ChessPosition myPosition){
        return (myPosition.getRow() <= 8) && (myPosition.getColumn() <= 8) && (myPosition.getRow() >= 1) && (myPosition.getColumn() >= 1);
    }
    private boolean check_square(ChessPosition newPosition, ChessBoard board) {
        return ((in_bounds(newPosition)) && (board.getPiece(newPosition) == null));
    }
    private boolean enemy_piece(ChessPosition newPosition, ChessBoard board) {
        ChessPiece piece = board.getPiece(newPosition);

        return (myPiece.getTeamColor() != piece.getTeamColor());
    }
    private boolean if_king() {
        return (myPiece.getPieceType() == ChessPiece.PieceType.KING);
    }
    private boolean if_pawn() {
        return (myPiece.getPieceType() == ChessPiece.PieceType.PAWN);
    }
//    private boolean first_move(){
//        return
//    }
    private boolean run_checks(ChessPosition newPosition, ChessBoard board) {
        return (in_bounds(newPosition) && check_square(newPosition, board));
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
        return;
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
        return;
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
        return;
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
        return;
    }

    public void straight_up(ChessPosition myPosition, Collection<ChessPosition> poss_positions, ChessBoard board) {
        ChessPosition new_position = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn());

        if (!in_bounds(new_position)){
            return;
        } else if (check_square(new_position, board)){
            poss_positions.add(new_position);
            if (if_king()) {return;}
            else if (if_pawn()) {return;}
            straight_up(new_position, poss_positions, board);
        } else if (enemy_piece(new_position, board)) {
            poss_positions.add(new_position);
            return;
        } else if (!enemy_piece(new_position, board)) {
            return;
        }
        return;

        }

    public void straight_down(ChessPosition myPosition, Collection<ChessPosition> poss_positions, ChessBoard board) {
        ChessPosition new_position = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn());

        if (!in_bounds(new_position)){
            return;
        } else if (check_square(new_position, board)){
            poss_positions.add(new_position);
            if (if_king()) {return;}
            straight_down(new_position, poss_positions, board);
        } else if (enemy_piece(new_position, board)) {
            poss_positions.add(new_position);
            return;
        } else if (!enemy_piece(new_position, board)) {
            return;
        }
        return;

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
        return;

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
        // base condition, if the next square isn't empty/isn't on the board

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
        if (run_checks(new_pos, board)) {poss_positions.add(new_pos7);}
        //8
        ChessPosition new_pos8 = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()-2);
        if (run_checks(new_pos8, board)) {poss_positions.add(new_pos8);}

        Collection<ChessMove> possible_moves = new ArrayList<>();
        for (ChessPosition pos : poss_positions) {
            possible_moves.add(new ChessMove(myPosition, pos, null));
        }

        return possible_moves;
    }


    //pawn
    public Collection<ChessMove> pawn_rules(ChessPosition myPosition, ChessBoard board) {
        Collection<ChessPosition> poss_positions = new ArrayList<>();

        ChessPiece myPiece = board.getPiece(myPosition);

        //HELP

//        straight_up(myPosition, poss_positions, board);
//        if (first_move) {
//            poss_positions.add(new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()));
//        }


        //if first time
        // poss moves are row+2, col, row+1, col,
            // and if there's an enemy piece row+1, col+1, or row-1, col-1, add to poss_positions



        //if end of board (.getRow() == 8) promotion = TRUE


        Collection<ChessMove> possible_moves = new ArrayList<>();
        for (ChessPosition pos : poss_positions) {
            possible_moves.add(new ChessMove(myPosition, pos, null));
        }

        return possible_moves;
    }



}
