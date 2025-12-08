package websocket.commands;

public class ConnectGameCommand extends UserGameCommand {

    private boolean player;
    public ConnectGameCommand(CommandType commandType, String authToken, Integer gameID, boolean observerStatus) {
        super(commandType, authToken, gameID);
        this.player = observerStatus;
    }

    public boolean observerStatus() {
        return player;
    }

}
