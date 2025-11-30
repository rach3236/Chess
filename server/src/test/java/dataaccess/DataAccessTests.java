package dataaccess;

import chess.ChessGame;
import dataaccess.MemoryDataAccess;
import datamodel.*;
import service.InvalidAccountException;
import service.InvalidAuthTokenException;
import service.UserService;
import org.junit.jupiter.api.*;
import passoff.model.*;
import server.Server;

import java.net.HttpURLConnection;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class DataAccessTests {

    private static DataAccess da;
    private static UserService service;
    private static UserData user1;

    @AfterAll
    static void cleanUp(){
    }

    @BeforeAll
    static void init() {
        da = new SQLDataAccess();
        service = new UserService();
        user1 = new UserData("username", "pass", "mail@mail.com");
    }

    @BeforeEach
    void setUp() {
        da.delete();
    }

    @Test
    @Order(1)
    @DisplayName("Delete Test")
    public void deleteTest() {
        RegisterResponse authInfo = service.register(user1);
        service.createGame("game1", authInfo.authToken());

        da.delete();

        //test by calling login
        assertThrows(InvalidAccountException.class, () -> {
            service.login(user1);
        });

        assertThrows(InvalidAuthTokenException.class, () -> {
            service.logout(authInfo.authToken());
        });

        assertThrows(InvalidAuthTokenException.class, () -> {
            service.listGames(authInfo.authToken());
        });

    }


//    public boolean validUser(String username, String password);
//    public String getUsername(String auth);
//    public void deleteSessionInfo(String auth);
//    public boolean isAuth(String auth);
//    public GameData getGameInfo(Integer gameID);
//    public int addGame(String gameName);
//    // will not return a hashmap when we implement
//    public Games getAllGames();
//    public void updateGameData(int gameID, String whiteUsername, String blackUsername, String gameName);

    @Test
    @DisplayName("Add User Fail")
    public void addUserFail() {

        //test smth
        var auth = service.register(user1);
        //test bad jauth
        assertThrows(Exception.class, () -> {
           da.addUser(user1, null);
        });
    }

    @Test
    @DisplayName("Add User Success")
    public void addUserSuccess() {
        var auth = UserService.generateToken();
        da.addUser(user1, auth);

        var userResult = DatabaseManager.userExists(user1.username());

        assertTrue(userResult, "Didn't add user successfully");
    }

    @Test
    @DisplayName("Add Session Fail")
    public void addSessionFail() {

        //test bad information
        assertThrows(InternalServerException.class, () -> {
           da.addSession(null, user1.username());
        });

        assertThrows(InternalServerException.class, () -> {
           da.addSession("auth", null);
        });
    }

    @Test
    @DisplayName("Add Session Success")
    public void addSessionSuccess() {
        da.addUser(user1, "auth");
        var sessionResult = da.getUsername("auth");
        assertNotNull(sessionResult, "Did not successfully add session info");
    }

    @Test
    @DisplayName ("User Exists Fails")
    public void userExistFail() {
        da.delete();
        //test no user
        assertFalse(da.userExists(user1.username()), "Returned true when there's no users");
        //test that it can return false
        assertFalse(da.userExists("fake"), "Method said user existed where there was none");
    }

    @Test
    @DisplayName("User Exists Works")
    public void userExistSuccess() {
        //test that it works when user exists
        service.register(user1);
        assertTrue(da.userExists(user1.username()), "Did not return true when user exists");
    }

    @Test
    @DisplayName("Valid User Fails")
    public void validUserFail() {
        service.register(user1);
        //test bad password match
        da.validUser(user1.username(), "bad password");
        //test bad username
        assertFalse(da.validUser("bad username", "pass"), "Validated user with bad username and password");
    }

    @Test
    @DisplayName("Valid User Success")
    public void validUserSuccess() {
        service.register(user1);
        assertTrue(da.validUser(user1.username(), user1.password()), "Did not validate user successfully");
    }

    @Test
    @DisplayName("Get Username Fails")
    public void getUsernameFails() {
        //test when there's no data stored
        var realAuth = UserService.generateToken();
        assertNull(da.getUsername(realAuth), "Returned a username when there was none stored");
        //set up
        var auth = service.register(user1);
        var getUsernameResult = da.getUsername("bad auth");
        //test bad auth
        assertNull(getUsernameResult, "Returned a username with bad auth");
    }

    @Test
    @DisplayName("Get Username Success")
    public void getUsernameSuccess() {
        //set up
        var auth = service.register(user1);
        var getUsernameResult = da.getUsername(auth.authToken());
        //test it returned the right username
        assertEquals(user1.username(), getUsernameResult, "Did not get the right username");
    }

    @Test
    @DisplayName("Delete Session Fails")
    public void deleteSessionFail() {
        //test delete w/ bad auth
        var auth = service.register(user1);
        da.deleteSessionInfo(auth.authToken() + "bad salt");
        assertNotNull(da.getUsername(auth.authToken()), "Deleted with bad auth Token");

        //test delete w/ account that doesn't exist
        var fakeAuth = UserService.generateToken();
        da.deleteSessionInfo(fakeAuth);
        assertNotNull(da.getUsername(auth.authToken()), "Deleted wrong session info");
    }

    @Test
    @DisplayName("Delete Session Success")
    public void deleteSessionSuccess() {
        var auth = service.register(user1);
        da.deleteSessionInfo(auth.authToken());

        assertNull(da.getUsername(auth.authToken()), "Returned something when it was supposed to be deleted");
    }

    @Test
    @DisplayName("Is Auth Fails")
    public void isAuthFail() {
        //test when there's no session
        assertFalse(da.isAuth(UserService.generateToken()), "Authorized when Auth Data is empty");
        //test with bad authtoken
        service.register(user1);
        assertFalse(da.isAuth("bad"), "Authorized with a bad auth");
    }

    @Test
    @DisplayName("Is Auth Success")
    public void isAuthSuccess() {
        var auth = service.register(user1);
        assertTrue(da.isAuth(auth.authToken()), "Didn't authorize when it should have");
    }

    @Test
    @DisplayName("Add Game Fail")
    public void addGameFail() {
        assertThrows(Exception.class, () -> {
            da.addGame(null);
        }, "Added game with bad info");
    }

    @Test
    @DisplayName("Add Game Success")
    public void addGameSuccess() {
        var gameId = da.addGame("Normal Game");
        assertTrue(gameId >0, "Did not properly add game");
    }

    @Test
    @DisplayName("Get Game Info Fail")
    public void getGameFail() {
        assertNull(da.getGameInfo(12), "Returned game info that didn't exist");

        da.addGame("game name");
        assertNull(da.getGameInfo(-1), "Returned game info with bad game ID");
    }

    @Test
    @DisplayName("Get Game Info Success")
    public void getGameSuccess() {
        var gameid = da.addGame("game1");
        var gameInfoResponse = da.getGameInfo(gameid);
        assertNotNull(gameInfoResponse, "Did not successfully get game info");
    }

    @Test
    @DisplayName("Update Game Fail")
    public void updateGameFail() {
        da.updateGameData(3, null, null, "fake game");
        assertNull(da.getGameInfo(3), "Returned information when there was none");

        var gameID = da.addGame("New Game");
        GameData newGame = new GameData(gameID, null, null,
                "New Game", new ChessGame());

        //test updating with a bad ID
        da.updateGameData(44567, null, null, "New Game");
        var result = da.getGameInfo(gameID);
        assertEquals(newGame.gameName(), result.gameName(), "Changed username with bad game ID");
    }

    @Test
    @DisplayName("Update Game Success")
    public void updateGameSuccess() {

        var auth = service.register(user1);

        var user2 = new UserData("User 2", "pass 2", "email@whatever.com");
        service.register(user2);

        var gameID = da.addGame("New Game");
        da.updateGameData(gameID, null, null, "New Game");

        //test white username changing
        da.updateGameData(gameID, user1.username(), null, "New Game");
        var result = da.getGameInfo(gameID);
        assertEquals(user1.username(), result.whiteUsername(), "Did not update white username correctly");

        //test black username change
        da.updateGameData(gameID, user1.username(), user2.username(), "New Game");
        var result2 = da.getGameInfo(gameID);
        assertEquals(user2.username(), result2.blackUsername(), "Did not update black username correctly");
    }
}
