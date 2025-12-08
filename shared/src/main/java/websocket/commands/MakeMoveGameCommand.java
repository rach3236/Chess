package websocket.commands;

import chess.ChessMove;

public class MakeMoveGameCommand {

    private ChessMove move;

    private MakeMoveGameCommand(UserGameCommand.CommandType commandType, String authToken, Integer gameID, ChessMove move) {
        this.move = move;
    }

    public ChessMove getMove() {
        return move;
    }

}
