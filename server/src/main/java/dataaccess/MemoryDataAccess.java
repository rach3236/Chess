package dataaccess;

import datamodel.GameData;
import datamodel.Games;
import datamodel.UserData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MemoryDataAccess implements DataAccess {
    //username, userdata
    private final HashMap<String, UserData> users = new HashMap<>();
    // auth, username
    private final HashMap<String, String> authData = new HashMap<>();
    // game ID, game data
    private final HashMap<Integer, GameData> gameData = new HashMap<>();
    // for game ID
    private int gameID = 0;

    @Override
    public void delete() {
        users.clear();
        authData.clear();
        gameData.clear();
    }

    @Override
    public void addUser(UserData user, String auth) {
        users.put(user.username(), user);
        authData.put(auth, user.username());
    }

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }

    @Override
    public String getUsername(String auth) {
        return authData.get(auth);
    }

    @Override
    public void deleteSessionInfo(String auth) {
        authData.remove(auth);
    }

    @Override
    public String getAuth(String auth) {
        return authData.get(auth);
    }

    // add game data(create game)
    @Override
    public GameData addGame(String gameName){
        gameID += 1;
        GameData gameInfo = new GameData(gameID, null, null, gameName);

        gameData.put(gameID, gameInfo);
        return gameInfo;
    }

    // get game info()
    @Override
    public GameData getGameInfo(Integer gameID) {
        return gameData.get(gameID);
    }

    @Override
    public Games getAllGames() {
        Games gameList = new Games(new ArrayList<>());
        for (Map.Entry<Integer, GameData> entry : gameData.entrySet()) {
            gameList.games().add(entry.getValue());
        }
        return gameList;
    }

    @Override
    public void updateGameData(int gameID, String whiteUsername, String blackUsername, String gameName){
        GameData game1 = new GameData(gameID, whiteUsername, blackUsername, gameName);
        gameData.put(gameID, game1);
    }
}
