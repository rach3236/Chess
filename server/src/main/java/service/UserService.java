package service;

import dataaccess.*;
import datamodel.*;

import java.util.UUID;

public class UserService {
    private DataAccess dataAccess;
    private UserData user;
    private int gameID = 0;

    public void delete() {
        dataAccess.delete();
    }

    private boolean authorized(String auth) {
        return (dataAccess.isAuth(auth));
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public RegisterResponse register(UserData user) throws InvalidAccountException, BadRequestException{
        this.user = user;
        if (user.username() == null || user.password() == null || user.email() == null || user.username().isEmpty() ||
                user.password().isEmpty() || user.email().isEmpty()) {
            throw new BadRequestException("Error: Bad request");
        }

        if (this.dataAccess.userExists(user.username())) {
        //return the exception that has the code
            throw new InvalidAccountException("Error: User already exists");
        }

        //generate auth token
        String auth = generateToken();
        this.dataAccess.addUser(user, auth);
        return new RegisterResponse(user.username(), auth);
    }

    public LoginResponse login(UserData user) throws InvalidAccountException, BadRequestException {
        this.user = user;
        if (user.username() == null || user.password() == null) {
            throw new BadRequestException("Error: Bad Request");
        }

        if (!this.dataAccess.validUser(user.username(), user.password())) {
            throw new InvalidAccountException("Error: Unauthorized");
        }
        String auth = generateToken();
        dataAccess.addSession(auth, user.username());
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
        return dataAccess.addGame(gameName);
    }

    public void joinGame(String auth, PlayerInfo playerInfo) throws InvalidAuthTokenException, BadRequestException, InvalidAccountException {
        // bad request
        if (playerInfo.playerColor() == null || playerInfo.playerColor().isEmpty() || playerInfo.gameID() <= 0) {
            throw new BadRequestException("Error: Empty field");
        } else if (!playerInfo.playerColor().equals("WHITE") && !playerInfo.playerColor().equals("BLACK")) {
            throw new BadRequestException("Error: Bad request");
        }
        // authorize token
        if (!authorized(auth)) {
            throw new InvalidAuthTokenException("Error: Unauthorized");
        }
        // if team color already taken
        var gameInfo = dataAccess.getGameInfo(playerInfo.gameID());
        if ((playerInfo.playerColor().equals("WHITE") && gameInfo.whiteUsername() != null
                || (playerInfo.playerColor().equals("BLACK") && gameInfo.blackUsername() != null))) {
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


    public String getUsername(String auth) {
        return dataAccess.getUsername(auth);
    }

    public UserService() {
//        this.dataAccess = new MemoryDataAccess();
        this.dataAccess = new SQLDataAccess();
    }
}
