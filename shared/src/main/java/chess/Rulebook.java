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

// Movement functions
public void up_right(ChessPosition myPosition, Collection<ChessPosition> poss_positions, ChessBoard board) {
        ChessPosition new_position = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()+1);

        if (!in_bounds(new_position)){
            return;
        } else if (check_square(new_position, board)){
            poss_positions.add(new_position);
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
    public Collection<ChessMove> king_rules(ChessPosition, myPosition, ChessBoard board) {
        Collection<ChessPosition> poss_positions = new ArrayList<>();


    }


    //knight



    //pawn



}
