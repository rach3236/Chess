package dataaccess;

import datamodel.GameData;
import datamodel.Games;
import datamodel.UserData;

import java.util.ArrayList;
import java.util.HashMap;

public interface DataAccess {


    public void delete();
    // insert user
    void addUser(UserData user, String auth);
    void addSession(String auth, String username);
    public boolean userExists(String username);
    public boolean validUser(String username, String password);
    public String getUsername(String auth);
    public void deleteSessionInfo(String auth);
    public boolean isAuth(String auth);
    public GameData getGameInfo(Integer gameID);
    public int addGame(String gameName);
    // will not return a hashmap when we implement
    public Games getAllGames();
    public void updateGameData(int gameID, String whiteUsername, String blackUsername, String gameName);

}
