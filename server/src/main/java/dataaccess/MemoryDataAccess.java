package dataaccess;

import chess.ChessGame;
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
        addSession(auth, user.username());
    }

    @Override
    public void addSession(String auth, String username) {
        authData.put(auth, username);
    }

    @Override
    public boolean userExists(String username) {
        return users.containsKey(username);
    }

    // TO DO!!!
    @Override
    public boolean validUser(String username, String password) {
        if (users.containsKey(username)) {
            return users.get(username).password().equals(password);
        }
        return false;
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
    public boolean isAuth(String auth) {
        return (authData.get(auth) != null);
    }

    // add game data(create game)
    @Override
    public int addGame(String gameName){
        gameID += 1;
        ChessGame gameObject = new ChessGame();
        GameData gameInfo = new GameData(gameID, null, null, gameName, gameObject);

        gameData.put(gameID, gameInfo);
        return gameInfo.gameID();
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
        GameData game1 = new GameData(gameID, whiteUsername, blackUsername, gameName, gameData.get(gameID).gameObject());
        gameData.put(gameID, game1);
    }
}
