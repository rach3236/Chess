package dataaccess;

import datamodel.GameData;
import datamodel.Games;
import datamodel.UserData;
import dataaccess.DatabaseManager;
import org.mindrot.jbcrypt.BCrypt;

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


    String generateHashPassword(String clearTextPassword) {
        return BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());
    }

    @Override
    public void delete() {
        DatabaseManager.delete();
    }

    @Override
    public void addUser(UserData user, String auth) {
        try {
            DatabaseManager.addUser(user.username(), generateHashPassword(user.password()), user.email());
        } catch (Exception e) {
            //TO DO
        }
        addSession(auth, user.username());
    }

    // TO DO: remove quotes if/when we modify to the results thing in petshop
    @Override
    public void addSession(String auth, String username) {
        try {
            DatabaseManager.addSession(auth);
        } catch (Exception e) {
            // TO DO
        }
    }

    @Override
    public boolean userExists(String username) {
        try {
            return DatabaseManager.userExists(username);
        } catch (Exception e) {
            //TO DO
        }
        return false;
    }

    @Override
    public boolean validUser(String username, String password){
        try {
            return DatabaseManager.isValidUser(username, generateHashPassword(password));
        } catch (Exception e) {
            //TO DO
        }
        return false;
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
