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

    //TO DO: deal with exceptions
    public SQLDataAccess() {
        try {
            createDatabase();
        } catch (Exception ex) {
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
        DatabaseManager.addUser(user.username(), generateHashPassword(user.password()), user.email());
        addSession(auth, user.username());
    }

    @Override
    public void addSession(String auth, String username) {
        DatabaseManager.addSession(auth, username);
    }

    @Override
    public boolean userExists(String username) {
        return DatabaseManager.userExists(username);
    }

    @Override
    public boolean validUser(String username, String providedPassword) {
        var userInfo = DatabaseManager.getUser(username);
        if (userInfo == null) {
            return false;
        }
        return BCrypt.checkpw(providedPassword, userInfo.password());
    }

    @Override
    public String getUsername(String auth) {
        return DatabaseManager.getUsernameFromAuth(auth);
    }

    @Override
    public void deleteSessionInfo(String auth) {
        DatabaseManager.deleteAuthData(auth);
    }

    @Override
    public boolean isAuth(String auth) {
        return DatabaseManager.isAuthorized(auth);
    }

    // add game data(create game)
    @Override
    public int addGame(String gameName) {
        ChessGame gameObject = new ChessGame();
        return DatabaseManager.addGameData(gameName, gameObject);
    }

    // get game info()
    @Override
    public GameData getGameInfo(Integer gameID) {
        return DatabaseManager.getGameInfo(gameID);
    }


    @Override
    public Games getAllGames() {
        Games gameList = new Games(new ArrayList<>());
        GameData[] allGameData = DatabaseManager.getAllGameInfo();
        for (GameData entry : allGameData) {
            gameList.games().add(entry);
        }
        return gameList;
    }

    @Override
    public void updateGameData(int gameID, String whiteUsername, String blackUsername, String gameName) {
        DatabaseManager.updateGameData(gameID, whiteUsername, blackUsername, gameName);
    }

    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }
}
