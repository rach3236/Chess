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


//    public void delete();
//    // insert user
//    void addUser(UserData user, String auth);
//    void addSession(String auth, String username);
//    public boolean userExists(String username);
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

//        assert
    }

}
