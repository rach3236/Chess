package Service;

import dataaccess.*;
import datamodel.*;

import java.util.ArrayList;
import java.util.UUID;

public class UserService {
    private DataAccess dataAccess;
    private UserData user;
    private int gameID = 0;

    public boolean authorized(String auth) {
        return (dataAccess.getAuth(auth) != null);
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public RegisterResponse register(UserData user) throws Exception{
        this.user = user;
        var existingUser = this.dataAccess.getUser(user.username());
        if (existingUser != null) {
            //return the exception that has the code
            throw new InvalidAccountException("Error: User already exists");
        }

        //generate auth token
        String auth = generateToken();
        this.dataAccess.addUser(user, auth);
        return new RegisterResponse(user.username(), auth);
    }

    public LoginResponse login(UserData user) throws InvalidAccountException, BadPasswordException, BadRequestException {
        this.user = user;
        var existingUser = this.dataAccess.getUser(user.username());
        if (user.username() == null || user.password() == null || existingUser == null) {
            throw new BadRequestException("Error: Bad Request");
        }
        if (!existingUser.password().equals(user.password())) {
            throw new BadPasswordException("Error: Unauthorized");
        }
        String auth = generateToken();
        return new LoginResponse(user.username(), auth);
    }

    public void logout(String auth) throws InvalidAuthTokenException {
        if (!authorized(auth)) {
            throw new InvalidAuthTokenException("Error: unauthorized");
        }
        dataAccess.deleteSessionInfo(auth);
    }

    public Games listGames(String auth) throws InvalidAuthTokenException {
//         validate authToken
        if (!authorized(auth)) {
            throw new InvalidAuthTokenException("Error: unauthorized");
        }
        return dataAccess.getAllGames();
    }

    public int createGame(String gameName, String auth) throws InvalidAuthTokenException, BadRequestException {
        //bad request check
        if ((gameName == null) || auth == null) {
            throw new BadRequestException("Error: field empty");
        }
        //verify authtoken
        if (!authorized(auth)) {
            throw new InvalidAuthTokenException("Error: unauthorized");
        }
        GameData gameInfo = dataAccess.addGame(gameName);
        return gameInfo.gameID();
    }

    public void joinGame(String auth, PlayerInfo playerInfo) throws InvalidAuthTokenException, BadRequestException, InvalidAccountException {
        // bad request
        if (playerInfo.playerColor() == null || playerInfo.gameID() <= 0) {
            throw new BadRequestException("Error: Empty field");
        } else if (!playerInfo.playerColor().equals("WHITE") && !playerInfo.playerColor().equals("BLACK")) {
            throw new InvalidAccountException("Error: already taken");
        }
        // authorize token
        if (!authorized(auth)) {
            throw new InvalidAuthTokenException("Error: Unauthorized");
        }
        // if team color already taken
        var gameInfo = dataAccess.getGameInfo(playerInfo.gameID());
        if ((playerInfo.playerColor().equals("WHITE") && !gameInfo.whiteUsername().isBlank()) || (playerInfo.playerColor().equals("BLACK") && !gameInfo.blackUsername().isBlank())) {
            throw new InvalidAccountException("Error: Already taken");
        }

        //get username
        var playerUsername = dataAccess.getUsername(auth);
        String whiteUsername = null;
        String blackUsername = null;

        //match tried color and update username
        if (playerInfo.playerColor().equals("WHITE")) {
            whiteUsername = playerUsername;
            blackUsername = gameInfo.blackUsername();
        }
        if (playerInfo.playerColor().equals("BLACK")) {
            blackUsername = playerUsername;
            whiteUsername = gameInfo.whiteUsername();
        }
        String gameName = gameInfo.gameName();

        //update gameData w/ new playerInfo
        dataAccess.updateGameData(playerInfo.gameID(), whiteUsername, blackUsername, gameName);

    }

    public UserService(){
        this.dataAccess = new MemoryDataAccess();
    }
}
