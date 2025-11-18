package chess;

import java.util.ArrayList;
import java.util.Collection;

public class Rulebook {

    private ChessPiece myPiece;

    public Rulebook(){

    }
// Checks
    public boolean inBounds(ChessPosition myPosition){
        return (myPosition.getRow() <= 8) && (myPosition.getColumn() <= 8) && (myPosition.getRow() >= 1) && (myPosition.getColumn() >= 1);
    }
    private boolean checkSquare(ChessPosition newPosition, ChessBoard board) {
        return (board.getPiece(newPosition) == null);
    }
    private boolean enemyPiece(ChessPosition newPosition, ChessBoard board) {
        ChessPiece piece = board.getPiece(newPosition);

        return (myPiece.getTeamColor() != piece.getTeamColor());
    }
    private boolean ifKing() {
        return (myPiece.getPieceType() == ChessPiece.PieceType.KING);
    }
    private boolean ifPawn() {
        return (myPiece.getPieceType() == ChessPiece.PieceType.PAWN);
    }
    private boolean firstMoveCheck(ChessPosition move) {
        if (move.getRow() == 2 && myPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {return true;}
        else if (move.getRow() == 7 && myPiece.getTeamColor() == ChessGame.TeamColor.BLACK) {return true;}
        else {return false;}
    }

    private boolean runChecks(ChessPosition newPosition, ChessBoard board) {
        return (inBounds(newPosition)  && (checkSquare(newPosition, board) || enemyPiece(newPosition, board)));
    }

// Movement functions
public void upRight(ChessPosition myPosition, Collection<ChessPosition> possPositions, ChessBoard board) {
        ChessPosition newPosition = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()+1);

        if (!inBounds(newPosition)){
            return;
        } else if (checkSquare(newPosition, board)){
            possPositions.add(newPosition);
            if (ifKing()) {return;}
            upRight(newPosition, possPositions, board);
        } else if (enemyPiece(newPosition, board)) {
            possPositions.add(newPosition);
            return;
        } else if (!enemyPiece(newPosition, board)) {
            return;
        }
    }

    public void upLeft(ChessPosition myPosition, Collection<ChessPosition> possPositions, ChessBoard board) {
        ChessPosition newPosition = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()-1);

        if (!inBounds(newPosition)){
            return;
        } else if (checkSquare(newPosition, board)){
            possPositions.add(newPosition);
            if (ifKing()) {return;}
            upLeft(newPosition, possPositions, board);
        } else if (enemyPiece(newPosition, board)) {
            possPositions.add(newPosition);
            return;
        } else if (!enemyPiece(newPosition, board)) {
            return;
        }
    }

    public void downRight(ChessPosition myPosition, Collection<ChessPosition> possPositions, ChessBoard board) {
        ChessPosition newPosition = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()+1);

        if (!inBounds(newPosition)){
            return;
        } else if (checkSquare(newPosition, board)){
            possPositions.add(newPosition);
            if (ifKing()) {return;}
            downRight(newPosition, possPositions, board);
        } else if (enemyPiece(newPosition, board)) {
            possPositions.add(newPosition);
            return;
        } else if (!enemyPiece(newPosition, board)) {
            return;
        }
    }

    public void downLeft(ChessPosition myPosition, Collection<ChessPosition> possPositions, ChessBoard board) {
        ChessPosition newPosition = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()-1);

        if (!inBounds(newPosition)){
            return;
        } else if (checkSquare(newPosition, board)){
            possPositions.add(newPosition);
            if (ifKing()) {return;}
            downLeft(newPosition, possPositions, board);
        } else if (enemyPiece(newPosition, board)) {
            possPositions.add(newPosition);
            return;
        } else if (!enemyPiece(newPosition, board)) {
            return;
        }
    }

    public void straightUp(ChessPosition myPosition, Collection<ChessPosition> possPositions, ChessBoard board) {
        ChessPosition newPosition = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn());

        if (!inBounds(newPosition)){
            return;
        } else if (checkSquare(newPosition, board)){
            possPositions.add(newPosition);
            if (ifKing()) {return;}
            else if (ifPawn()) {return;}
            straightUp(newPosition, possPositions, board);
        } else if (enemyPiece(newPosition, board)) {
            possPositions.add(newPosition);
            return;
        } else if (!enemyPiece(newPosition, board)) {
            return;
        }
        return;

        }

    public void straightDown(ChessPosition myPosition, Collection<ChessPosition> possPositions, ChessBoard board) {
        ChessPosition newPosition = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn());

        if (!inBounds(newPosition)){
            return;
        } else if (checkSquare(newPosition, board)){
            possPositions.add(newPosition);
            if (ifKing() || ifPawn()) {return;}
            straightDown(newPosition, possPositions, board);
        } else if (enemyPiece(newPosition, board)) {
            possPositions.add(newPosition);
            return;
        } else if (!enemyPiece(newPosition, board)) {
            return;
        }

    }

    public void straightLeft(ChessPosition myPosition, Collection<ChessPosition> possPositions, ChessBoard board) {
        ChessPosition newPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn()-1);

        if (!inBounds(newPosition)){
            return;
        } else if (checkSquare(newPosition, board)){
            possPositions.add(newPosition);
            if (ifKing()) {return;}
            straightLeft(newPosition, possPositions, board);
        } else if (enemyPiece(newPosition, board)) {
            possPositions.add(newPosition);
            return;
        } else if (!enemyPiece(newPosition, board)) {
            return;
        }

    }

    public void straightRight(ChessPosition myPosition, Collection<ChessPosition> possPositions, ChessBoard board) {
        ChessPosition newPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn()+1);

        if (!inBounds(newPosition)){
            return;
        } else if (checkSquare(newPosition, board)){
            possPositions.add(newPosition);
            if (ifKing()) {return;}
            straightRight(newPosition, possPositions, board);
        } else if (enemyPiece(newPosition, board)) {
            possPositions.add(newPosition);
            return;
        } else if (!enemyPiece(newPosition, board)) {
            return;
        }
        return;
    }

    public void getOrthoganalMoves(ChessPosition myPosition, Collection<ChessPosition> possPositions, ChessBoard board) {

        straightUp(myPosition, possPositions, board);
        straightDown(myPosition, possPositions, board);
        straightLeft(myPosition, possPositions, board);
        straightRight(myPosition, possPositions, board);
    }

    public void getDiagonalMoves(ChessPosition myPosition, Collection<ChessPosition> possPositions, ChessBoard board) {

        upRight(myPosition, possPositions, board);
        upLeft(myPosition, possPositions, board);
        downRight(myPosition, possPositions, board);
        downLeft(myPosition, possPositions, board);
    }

    public void knightMoves(ChessPosition myPosition, Collection<ChessPosition> possPositions, ChessBoard board){
        //1
        ChessPosition newPos = new ChessPosition(myPosition.getRow()+2, myPosition.getColumn()+1);
        if (runChecks(newPos, board)) {possPositions.add(newPos);}
        //2
        ChessPosition newPos2 = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()+2);
        if (runChecks(newPos2, board)) {possPositions.add(newPos2);}
        //3
        ChessPosition newPos3 = new ChessPosition(myPosition.getRow()-2, myPosition.getColumn()+1);
        if (runChecks(newPos3, board)) {possPositions.add(newPos3);}
        //4
        ChessPosition newPos4 = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()+2);
        if (runChecks(newPos4, board)) {possPositions.add(newPos4);}
        //5
        ChessPosition newPos5 = new ChessPosition(myPosition.getRow()-2, myPosition.getColumn()-1);
        if (runChecks(newPos5, board)) {possPositions.add(newPos5);}
        //6
        ChessPosition newPos6 = new ChessPosition(myPosition.getRow()+2, myPosition.getColumn()-1);
        if (runChecks(newPos6, board)) {possPositions.add(newPos6);}
        //7
        ChessPosition newPos7 = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()-2);
        if (runChecks(newPos7, board)) {possPositions.add(newPos7);}
        //8
        ChessPosition newPos8 = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()-2);
        if (runChecks(newPos8, board)) {possPositions.add(newPos8);}

        return;
    }

    public Collection<ChessMove> bishopRules(ChessPosition myPosition, ChessBoard board) {
        //make list of possible moves
        //add possible moves to it as you recurse
        Collection<ChessPosition> possPositions = new ArrayList<>();

        myPiece = board.getPiece(myPosition);
        // base condition, if the next square isn't empty/isn't on the board

        //go up and to the right
        getDiagonalMoves(myPosition, possPositions, board);

        Collection<ChessMove> possibleMoves = new ArrayList<>();
        for (ChessPosition pos : possPositions) {
            possibleMoves.add(new ChessMove(myPosition, pos, null));
        }

        return possibleMoves;
    }

// rook
    // straight up and down, left and right
    public Collection<ChessMove> rookRules(ChessPosition myPosition, ChessBoard board) {
        Collection<ChessPosition> possPositions = new ArrayList<>();

        myPiece = board.getPiece(myPosition);

        //go straight up, down, left, right
        getOrthoganalMoves(myPosition, possPositions, board);

        Collection<ChessMove> possibleMoves = new ArrayList<>();
        for (ChessPosition pos : possPositions) {
            possibleMoves.add(new ChessMove(myPosition, pos, null));
        }

        return possibleMoves;
    }


    // queen
    // rook + bishop combined!
    public Collection<ChessMove> queenRules(ChessPosition myPosition, ChessBoard board) {
        return kingOrQueenRules(myPosition, board);
    }

    // king
    // Just like a queen, except no recursion:)
    public Collection<ChessMove> kingRules(ChessPosition myPosition, ChessBoard board) {
        return kingOrQueenRules(myPosition, board);
    }

    private Collection<ChessMove> kingOrQueenRules(ChessPosition myPosition, ChessBoard board) {
        Collection<ChessPosition> possPositions = new ArrayList<>();

        myPiece = board.getPiece(myPosition);

        getDiagonalMoves(myPosition, possPositions, board);

        getOrthoganalMoves(myPosition, possPositions, board);

        Collection<ChessMove> possibleMoves = new ArrayList<>();
        for (ChessPosition pos : possPositions) {
            possibleMoves.add(new ChessMove(myPosition, pos, null));
        }

        return possibleMoves;
    }

    //knight
    public Collection<ChessMove> knightRules(ChessPosition myPosition, ChessBoard board) {
        Collection<ChessPosition> possPositions = new ArrayList<>();

        myPiece = board.getPiece(myPosition);

        knightMoves(myPosition, possPositions, board);

        Collection<ChessMove> possibleMoves = new ArrayList<>();
        for (ChessPosition pos : possPositions) {
            possibleMoves.add(new ChessMove(myPosition, pos, null));
        }

        return possibleMoves;
    }


    //pawn
    public Collection<ChessMove> pawnRules(ChessPosition myPosition, ChessBoard board) {
        Collection<ChessPosition> possPositions = new ArrayList<>();
        Collection<ChessMove> possibleMoves = new ArrayList<>();

        myPiece = board.getPiece(myPosition);


        if (myPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {

            ChessPosition upMove = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn());
            if (inBounds(upMove) && checkSquare(upMove, board)) {possPositions.add(upMove);}

            ChessPosition firstMove = new ChessPosition(myPosition.getRow()+2, myPosition.getColumn());
            if (inBounds(firstMove) && firstMoveCheck(myPosition) && checkSquare(firstMove, board) && checkSquare(upMove, board)) {
                possPositions.add(firstMove);
            }

            ChessPosition enemyPos1 = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()+1);
            if (inBounds(enemyPos1) && !checkSquare(enemyPos1, board)) {
                if (enemyPiece(enemyPos1, board)) {
                    possPositions.add(enemyPos1);
                }
            }
            ChessPosition enemyPos2 = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()-1);
            if (inBounds(enemyPos2)){
                if (!checkSquare(enemyPos2, board)) {
                    if (enemyPiece(enemyPos2, board)) {
                          possPositions.add(enemyPos2);
                    }
                }
            }
        }

        if (myPiece.getTeamColor() == ChessGame.TeamColor.BLACK) {

            ChessPosition downMove = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn());
            if (inBounds(downMove) && checkSquare(downMove, board)) {possPositions.add(downMove);}

            ChessPosition firstMove = new ChessPosition(myPosition.getRow()-2, myPosition.getColumn());
            if (inBounds(firstMove) && firstMoveCheck(myPosition)
                    && checkSquare(firstMove, board) && checkSquare(downMove, board)) {possPositions.add(firstMove);}

            ChessPosition enemyPos1 = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()+1);
            if (inBounds(enemyPos1) && !checkSquare(enemyPos1, board)) {
                if (enemyPiece(enemyPos1, board)) {
                    possPositions.add(enemyPos1);
                }
            }
            ChessPosition enemyPos2 = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()-1);
            if (inBounds(enemyPos2)) {
                if (!checkSquare(enemyPos2, board)){
                    if (enemyPiece(enemyPos2, board)) {
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
