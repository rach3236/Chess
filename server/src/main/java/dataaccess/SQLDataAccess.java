package dataaccess;

import datamodel.GameData;
import datamodel.Games;
import datamodel.UserData;
import dataaccess.DatabaseManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static dataaccess.DatabaseManager.createDatabase;

public class SQLDataAccess implements DataAccess {
//    //username, userdata
//    private final HashMap<String, UserData> users = new HashMap<>();
//    // auth, username
//    private final HashMap<String, String> authData = new HashMap<>();
//    // game ID, game data
//    private final HashMap<Integer, GameData> gameData = new HashMap<>();
//    // for game ID
//    private int gameID = 0;

    //constructor
    //TO DO: remove throw exception!!! (Handle the exception)
    //TO DO: remove
    public SQLDataAccess() {
        try {
            createDatabase();
        } catch (Exception ex) {

        }
    }


    @Override
    public void delete() {
        String command = "DELETE * FROM AuthData;";
        DatabaseManager.ExecuteSQLCommand(command);
        command = "DELETE * FROM GameData;";
        DatabaseManager.ExecuteSQLCommand(command);
        command = "DELETE * FROM UserData;";
        DatabaseManager.ExecuteSQLCommand(command);
    }

    @Override
    public void addUser(UserData user, String auth) {
        String addCommand = "INSERT INTO UserData (username, password, email) VALUES (" + user.username() + ", " + user.password() + ", " + user.email() + ");";
        DatabaseManager.ExecuteSQLCommand(addCommand);
        addSession(auth, user.username());
    }

    // TO DO: remove quotes if/when we modify to the results thing in petshop
    @Override
    public void addSession(String auth, String username) {
        String getusernameID = "SELECT userDataID FROM UserData WHERE username=\"" + username + "\";";
        int userID123 = DatabaseManager.getUserID(getusernameID);
        if (userID123 >= 0) {
            String addSessionCommand = "INSERT INTO AuthData (authToken, userDataID) VALUES (\"" + auth + "\", " + userID123 + ");";
            DatabaseManager.ExecuteSQLCommand(addSessionCommand);
        }
    }

    @Override
    public UserData getUser(String username){
        try {
            UserData userInfo = DatabaseManager.getUserInfo(username);
            return userInfo;
        } catch (Exception e) {
        }
//        return users.get(username);
        return null;
    }

    @Override
    public String getUsername(String auth) {
//        return authData.get(auth);
        return "";
    }

    @Override
    public void deleteSessionInfo(String auth) {
//        authData.remove(auth);
    }

    @Override
    public String getAuth(String auth) {
//        return authData.get(auth);
        return "";
    }

    // add game data(create game)
    @Override
    public GameData addGame(String gameName){
//        gameID += 1;
//        GameData gameInfo = new GameData(gameID, null, null, gameName);
//
//        gameData.put(gameID, gameInfo);
//        return gameInfo;
        return null;
    }

    // get game info()
    @Override
    public GameData getGameInfo(Integer gameID) {
//        return gameData.get(gameID);
        return null;
    }


    @Override
    public Games getAllGames() {
//        Games gameList = new Games(new ArrayList<>());
//        for (Map.Entry<Integer, GameData> entry : gameData.entrySet()) {
//            gameList.games().add(entry.getValue());
//        }
//        return gameList;
        return null;
    }

    @Override
    public void updateGameData(int gameID, String whiteUsername, String blackUsername, String gameName){
//        GameData game1 = new GameData(gameID, whiteUsername, blackUsername, gameName);
//        gameData.put(gameID, game1);
    }
}
