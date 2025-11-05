package Service;

import dataaccess.*;
import datamodel.LoginResponse;
import datamodel.RegisterResponse;
import datamodel.UserData;

public class UserService {
    private DataAccess dataAccess;
    private UserData user;

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
        if (dataAccess.getSessionInfo(auth) == null) {
            throw new InvalidAuthTokenException("Error: unauthorized");
        }
        dataAccess.deleteSessionInfo(auth);
    }


    public UserService(){
        this.dataAccess = new MemoryDataAccess();
    }
}
