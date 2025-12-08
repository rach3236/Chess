package websocket.commands;

import chess.ChessMove;

public class MakeMoveGameCommand extends UserGameCommand {

    private ChessMove move;

    public MakeMoveGameCommand(UserGameCommand.CommandType commandType, String authToken, Integer gameID, ChessMove move) {
        super(commandType, authToken, gameID);
        this.move = move;
    }

    public ChessMove getMove() {
        return move;
    }

}
