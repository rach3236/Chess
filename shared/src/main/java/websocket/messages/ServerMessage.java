package websocket.messages;

import chess.ChessGame;
import com.google.gson.Gson;

import java.util.Objects;

/**
 * Represents a Message the server can send through a WebSocket
 * <p>
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class ServerMessage {
    ServerMessageType serverMessageType;
    String message;
    String errorMessage;
    ChessGame game;
    String pov;


    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }

    public ServerMessage(ServerMessageType type, String message, String errorMessage, ChessGame game, String pov) {
        this.serverMessageType = type;
        this.message = message;
        this.errorMessage = errorMessage;
        this.game = game;
        this.pov = pov;

    }

    public ServerMessageType getServerMessageType() {
        return this.serverMessageType;
    }

    public String getServerMessage() {
        return message;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getPOV() {
        return this.pov;
    }

    public ChessGame getGame() {
        return this.game;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServerMessage that)) {
            return false;
        }
        return getServerMessageType() == that.getServerMessageType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType());
    }
}
