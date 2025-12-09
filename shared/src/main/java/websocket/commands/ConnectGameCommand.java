package websocket.commands;

public class ConnectGameCommand extends UserGameCommand {

    private boolean player;
    private String pov;
    public ConnectGameCommand(CommandType commandType, String authToken, Integer gameID, boolean observerStatus, String pov) {
        super(commandType, authToken, gameID);
        this.player = observerStatus;
        this.pov = pov;
    }

    public boolean observerStatus() {
        return player;
    }

    public String getPOV() {
        return this.pov;
    }

}
