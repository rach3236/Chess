package dataaccess;

import chess.ChessGame;
import datamodel.GameData;
import datamodel.Games;
import datamodel.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;

import static dataaccess.DatabaseManager.createDatabase;

public class SQLDataAccess implements DataAccess {

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
            addSession(auth, user.username());
        } catch (Exception e) {
            //TO DO
        }
    }

    // TO DO: remove quotes if/when we modify to the results thing in pet shop
    @Override
    public void addSession(String auth, String username) {
        try {
            DatabaseManager.addSession(auth, username);
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

//    boolean verifyUser(String username, String providedClearTextPassword) {
//        // read the previously hashed password from the database
//        var hashedPassword = readHashedPasswordFromDatabase(username);
//
//        return BCrypt.checkpw(providedClearTextPassword, hashedPassword);
//    }

    @Override
    public boolean validUser(String username, String providedPassword){
        try {
            var userInfo = DatabaseManager.getUser(username);
            if (userInfo == null) { return false;}
            return BCrypt.checkpw(providedPassword, userInfo.password());
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
            ChessGame gameObject = new ChessGame();
            return DatabaseManager.addGameData(gameName, gameObject);
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

    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }
}
