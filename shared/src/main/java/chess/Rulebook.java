package chess;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

public class Rulebook {
    private Object newPosition;

    public Rulebook(){

    }

    public boolean in_bounds(ChessPosition myPosition){
        return (myPosition.getRow() < 8) && (myPosition.getColumn() < 8) && (myPosition.getRow() > -1) && (myPosition.getColumn() > -1);
    }
    private boolean check_square(ChessPosition newPosition, ChessBoard board) {
        return ((board.getPiece(newPosition) == null) && (in_bounds(newPosition)));
    }



    // bishop
    // diagonal to the top right, top left, bottom right, bottom left
    public List<ChessPosition> bishop_rules(ChessPosition myPosition, ChessBoard board, Collection poss_positions) {
        //fix this later haha
        //add possible moves to it as you recurse

        // base condition, if the next square isn't empty/isn't on the board
//        if (ChessPosition == null |  {}

        //go up and to the right

            return poss_positions;
        }


    }

    public void up_right(ChessPosition myPosition, List poss_positions, ChessBoard board) {
        ChessPosition new_move = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()+1);

        if (check_square(new_move, board)){
            poss_positions.add(new_move);
        }
        return;
    }




// rook
    // straight up and down, left and right


    // queen
    // rook + bishop combined!


    // king
    // Just like a queen, except no recursion:)


    //rook


    //pawn



}
