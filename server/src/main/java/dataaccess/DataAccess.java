package dataaccess;

import datamodel.GameData;
import datamodel.Games;
import datamodel.UserData;

import java.util.ArrayList;
import java.util.HashMap;

public interface DataAccess {
    void clear();
    // insert user
    void addUser(UserData user, String auth);
    public UserData getUser(String username);
    public String getUsername(String auth);
    public void deleteSessionInfo(String auth);
    public String getAuth(String auth);
    public GameData getGameInfo(Integer gameID);
    public GameData addGame(String gameName);
    // will not return a hashmap when we implement
    public Games getAllGames();
    public void updateGameData(int gameID, String whiteUsername, String blackUsername, String gameName);

}
