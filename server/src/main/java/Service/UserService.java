package Service;

import dataaccess.*;
import datamodel.GameData;
import datamodel.LoginResponse;
import datamodel.RegisterResponse;
import datamodel.UserData;

import java.util.ArrayList;

public class UserService {
    private DataAccess dataAccess;
    private UserData user;
    private int gameID = 0;

    public boolean authorized(String auth) {
        return (dataAccess.getSessionInfo(auth) != null);
    }

    public RegisterResponse register(UserData user) throws Exception{
        this.user = user;
        var existingUser = this.dataAccess.getUser(user.username());
        if (existingUser != null) {
            //return the exception that has the code
            throw new InvalidAccountException("User already exists");
        }

        //generate auth token
        String auth = user.username();
        this.dataAccess.addUser(user, auth);
        return new RegisterResponse(auth, user.username());
    }

    public LoginResponse login(UserData user) throws Exception {
        this.user = user;
        var existingUser = this.dataAccess.getUser(user.username());
        if (existingUser == null) {
            throw new InvalidAccountException("Error: Bad Request");
        }
        if (!existingUser.password().equals(user.password())) {
            throw new BadPasswordException("Error: Unauthorized");
        }
        return new LoginResponse(user.username(), "xyz");
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

    public int createGame(String gameName, String auth) {
        //verify authtoken
        if (!authorized(auth)) {
            throw new InvalidAuthTokenException("Error: unauthorized");
        }
        int gameID = dataAccess.addGame(gameName);
        return gameID;
    }


    public UserService(){
        this.dataAccess = new MemoryDataAccess();
    }
}
