package Service;

import dataaccess.*;
import datamodel.GameData;
import datamodel.LoginResponse;
import datamodel.RegisterResponse;
import datamodel.UserData;

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

    public ArrayList<GameData> listGames(String auth) throws InvalidAuthTokenException {
//         validate authToken
        if (!authorized(auth)) {
            throw new InvalidAuthTokenException("Error: unauthorized");
        }
        var games = dataAccess.getAllGames();
        return games;
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


    public UserService(){
        this.dataAccess = new MemoryDataAccess();
    }
}
