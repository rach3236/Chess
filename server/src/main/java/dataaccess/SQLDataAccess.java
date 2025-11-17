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
    private int gameID = 0;

    //constructor
    //TO DO: remove throw exception!!! (Handle the exception)
    //TO DO: remove
    public SQLDataAccess() {
        try {
            createDatabase();
        } catch (Exception ex) {
            //TO DO
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
            //TO DO
        }
        return null;
    }

    @Override
    public String getUsername(String auth) {
        try {
            return DatabaseManager.getUsernameFromAuth(auth);
        } catch (Exception e) {

        }
        return null;
    }

    @Override
    public void deleteSessionInfo(String auth) {
        try {
            DatabaseManager.deleteAuthData(auth);
        } catch (Exception ex) {
            //TO DO
        }
    }

    @Override
    public boolean isAuth(String auth) {
        try {
            return DatabaseManager.isAuthorized(auth);
        } catch (Exception e_) {
            //TO DO
        }
//        return authData.get(auth);
        return false;
    }

    // add game data(create game)
    @Override
    public int addGame(String gameName){
        try {
            return DatabaseManager.addGameData(gameName);
        } catch (Exception e) {
            //TO DO
        }
        return -1;
    }

    // get game info()
    @Override
    public GameData getGameInfo(Integer gameID) {
        try {
            return DatabaseManager.getGameInfo(gameID);
        } catch (Exception e) {
            //TO DO
        }
        return null;
    }


    @Override
    public Games getAllGames() {
        try {
            Games gameList = new Games(new ArrayList<>());
            GameData[] allGameData = DatabaseManager.getAllGameInfo();
            for (GameData entry : allGameData) {
                gameList.games().add(entry);
            }
            return gameList;
        } catch (Exception e) {
            //TO DO
        }
        return null;
    }

    @Override
    public void updateGameData(int gameID, String whiteUsername, String blackUsername, String gameName){
        try {
            DatabaseManager.updateGameData(gameID, whiteUsername, blackUsername, gameName);
        } catch (Exception e) {
            //TO DO
        }
    }
}
