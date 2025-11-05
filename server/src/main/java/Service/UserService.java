package Service;

import dataaccess.*;
import datamodel.LoginResponse;
import datamodel.RegisterResponse;
import datamodel.UserData;

import java.util.Objects;

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

        this.dataAccess.addUser(user);
        return new RegisterResponse(user.username(), "xyz");
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

    public UserService(){
        this.dataAccess = new MemoryDataAccess();
    }
}
