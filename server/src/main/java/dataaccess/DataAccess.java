package dataaccess;

import datamodel.GameData;
import datamodel.UserData;

import java.util.ArrayList;
import java.util.HashMap;

public interface DataAccess {
    void clear();
    // insert user
    void addUser(UserData user, String auth);
    public UserData getUser(String username);
    public void deleteSessionInfo(String auth);
    public String getAuth(String auth);
    public GameData getGameInfo(String gameName);
    public GameData addGame(String gameName);
    // will not return a hashmap when we implement
    public ArrayList<GameData> getAllGames();

}
