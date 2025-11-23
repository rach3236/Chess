package service;


import chess.ChessGame;
import dataaccess.MemoryDataAccess;
import datamodel.*;
import service.UserService;
import org.junit.jupiter.api.*;
import passoff.model.*;
import server.Server;

import java.net.HttpURLConnection;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {

    private static UserService service;
    private static UserData user1;
    private static MemoryDataAccess mda;

    @AfterAll
    static void cleanUp(){

    }

    @BeforeAll
    static void init() {
        service = new UserService();
        user1 = new UserData("username", "pass", "mail@mail.com");
    }

    @BeforeEach
    void setUp() {
        service.delete();
    }

    @Test
    @Order(1)
    @DisplayName("Delete Test")
    public void deleteTest() {

        RegisterResponse userAuthInfo = service.register(user1);

        service.createGame("game1", userAuthInfo.authToken());

        service.delete();

        assertThrows(InvalidAccountException.class, () -> {
            service.login(user1);
        });

        assertThrows(InvalidAuthTokenException.class, () -> {
            service.logout(userAuthInfo.authToken());
        });

        assertThrows(InvalidAuthTokenException.class, () -> {
            service.listGames(userAuthInfo.authToken());
        });
    }

    @Test
    @Order(2)
    @DisplayName("Register Fails")
    public void registerFail() {
        UserData badUser1 = new UserData(null, "pass", "mail");
        UserData badUser2 = new UserData("smth", null, "mail");
        UserData badUser3 = new UserData("smth", "pass", null);

        // Assert no username
        assertThrows(BadRequestException.class, () -> {
            service.register(badUser1);
        }, "~~~Null username worked and it shouldn't have~~~");

        // Assert no password
        assertThrows(BadRequestException.class, () -> {
            service.register(badUser2);
        }, "~~~Null password worked and it shouldn't have~~~");

        // Assert no email
        assertThrows(BadRequestException.class, () -> {
            service.register(badUser3);
        }, "~~~Null email worked and it shouldn't have~~~");
    }


    @Test
    @Order(3)
    @DisplayName("Register Success")
    public void registerSuccess() {

        var registerResult = service.register(user1);
        var expectedRegister = new RegisterResponse("username", registerResult.authToken());

;        assertEquals(expectedRegister, registerResult, "Register didn't work you suck lol");
    }

    @Test
    @Order(4)
    @DisplayName("Login Fail")
    public void loginFail() {
        //register user beforehand
        service.register(user1);

        UserData badUser1 = new UserData(null, "pass", null);
        UserData badUser2 = new UserData("username", null, null);

        //test null username
        assertThrows(BadRequestException.class, () -> {
                    service.login(badUser1);
                }, "Successful login with a null username (bad)");

        //test null password
        assertThrows(BadRequestException.class, () -> {
            service.login(badUser2);
        }, "Successful login with a null password (bad)");

        //test if password doesn't match and still logs in
        assertThrows(InvalidAccountException.class, () -> {
            service.login(new UserData(user1.username(), "bad_password", null));
        }, "Successful login with a bad password (bad)");
    }


    //TO DO maybe delete badPasswordException?

    @Test
    @Order(5)
    @DisplayName("Login Success")
    public void loginSuccess() {
         service.register(user1);

         var loginResult = service.login(user1);
         var expected_login = new LoginResponse(user1.username(), loginResult.authToken());

         assertEquals(expected_login, loginResult, "Normal Login did not work:(");
    }

    @Test
    @Order(6)
    @DisplayName("Logout Fail!")
    public void logoutFail() {
        var regiResponse = service.register(user1);

        assertThrows(InvalidAuthTokenException.class, () -> {
            service.logout("bad auth token hehehe");
        }, "Logged user out when unauthorized");
    }

    @Test
    @DisplayName("Logout Success")
    public void logoutSuccess() {
        var regiResponse = service.register(user1);

        assertDoesNotThrow(() -> service.logout(regiResponse.authToken()), "Couldn't log out properly");
    }

    @Test
    @DisplayName("Create Game Fail")
    public void createFail() {
        //set up
        var regiResponse = service.register(user1);
        //test null game name
        assertThrows(BadRequestException.class, () -> {
           service.createGame(null, regiResponse.authToken());
        });
        //test null auth token
        assertThrows(BadRequestException.class, () -> {
            service.createGame(null, regiResponse.authToken());
        });
        //test bad auth token
        assertThrows(InvalidAuthTokenException.class, () -> {
            service.createGame("GAME", "bad auth token :(");
        });
    }

    // TO DO change all variables to camelCase

    @Test
    @DisplayName("Create Game Success")
    public void createSuccess() {
        //set up
        var regiResponse = service.register(user1);
        var createResponse = service.createGame("GAME", regiResponse.authToken());

        Assertions.assertTrue(createResponse > 0,  "Could not successfully create game");
    }

    @Test
    @DisplayName("List Game Fail")
    public void listFail() {
        // set up
        var regiResponse = service.register(user1);

        //check empty list games?
        assertThrows(InvalidAuthTokenException.class, () -> {
            service.listGames("badAuth");
        }, "bad auth token (empty list)");
        //make multiple games
        service.createGame("Game1", regiResponse.authToken());
        service.createGame("Game2", regiResponse.authToken());
        service.createGame("Game3", regiResponse.authToken());

        //assert
        assertThrows(InvalidAuthTokenException.class, () -> {
            service.listGames("bad_auth");
        }, "bad auth token");
    }

    @Test
    @DisplayName("List Game Success")
    public void listSuccess() {
        var regiResponse = service.register(user1);

        var listGamesResponse = service.listGames(regiResponse.authToken());

        //check empty list games?
        assertNotNull(listGamesResponse, "empty list responded");
        //make multiple games
        service.createGame("Game1", regiResponse.authToken());
        service.createGame("Game2", regiResponse.authToken());
        service.createGame("Game3", regiResponse.authToken());

        //check regular amount of games
        Games listGamesResponse2 = service.listGames(regiResponse.authToken());
        assertEquals(3, listGamesResponse2.size(), "Did not return all the games");
    }

    @Test
    @DisplayName("Join Game Fail")
    public void joinFail() {
        var regiResponse = service.register(user1);
        var gameID = service.createGame("Game1", regiResponse.authToken());

        //check null playercolor
        PlayerInfo badPlayerInfo = new PlayerInfo(null, gameID);
        assertThrows(BadRequestException.class, () -> {
            service.joinGame(regiResponse.authToken(), badPlayerInfo);
        }, "didn't flag null info");
        //check bad game ID
        PlayerInfo badGameIDInfo = new PlayerInfo("WHITE", 0);
        assertThrows(BadRequestException.class, () -> {
            service.joinGame(regiResponse.authToken(), badGameIDInfo);
        }, "added user with bad gameID");
        //check bad player color
        PlayerInfo badColorInfo = new PlayerInfo("bad", gameID);
        assertThrows(BadRequestException.class, () -> {
            service.joinGame(regiResponse.authToken(), badGameIDInfo);
        }, "added player with not valid player color");
        //check bad auth token
        assertThrows(InvalidAuthTokenException.class, () -> {
            service.joinGame("bad auth", new PlayerInfo("WHITE", gameID));
        }, "bad auth token");
        // check if black color taken
        service.joinGame(regiResponse.authToken(), new PlayerInfo("BLACK", gameID));
        assertThrows(InvalidAccountException.class, () -> {
            service.joinGame(regiResponse.authToken(), new PlayerInfo("BLACK", gameID));
        }, "black color taken but still added player");

        // check if white color taken
        service.joinGame(regiResponse.authToken(), new PlayerInfo("WHITE", gameID));
        assertThrows(InvalidAccountException.class, () -> {
            service.joinGame(regiResponse.authToken(), new PlayerInfo("WHITE", gameID));
        }, "white color taken but still added player");
    }

    @Test
    @DisplayName("Join Game Success")
    public void joinSuccess() {
        //set up
        var regiResponse = service.register(user1);
        var gameID = service.createGame("Game1", regiResponse.authToken());

        //assert
        assertDoesNotThrow(() -> service.joinGame(regiResponse.authToken(),
                new PlayerInfo("WHITE", gameID)), "could not properly add black player");
        assertDoesNotThrow(() -> service.joinGame(regiResponse.authToken(),
                new PlayerInfo("BLACK", gameID)), "could not properly add black player");
    }
}
