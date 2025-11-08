package dataaccess;

import datamodel.GameData;
import datamodel.UserData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MemoryDataAccess implements DataAccess {
    //username, userdata
    private final HashMap<String, UserData> users = new HashMap<>();
    // auth, authToken
    private final HashMap<String, String> authData = new HashMap<>();
    // game name, game data
    private final HashMap<String, GameData> gameData = new HashMap<>();
    // for game ID
    private int gameID = 0;

    @Override
    public void clear() {
        users.clear();
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
        GameData gameInfo = new GameData(gameID, "", "", gameName);

        gameData.put(gameName, gameInfo);
        return gameInfo;
    }

    // get game info()
    @Override
    public GameData getGameInfo(String gameName) {
        return gameData.get(gameName);
    }

    @Override
    public ArrayList<GameData> getAllGames() {
        ArrayList<GameData> gameList = new ArrayList<GameData>();
        for (Map.Entry<String, GameData> entry : gameData.entrySet()) {
            gameList.add(entry.getValue());
        }
        return gameList;
    }


}
