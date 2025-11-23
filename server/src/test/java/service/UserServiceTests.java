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
    void set_up() {
        service.delete();
    }

    @Test
    @Order(1)
    @DisplayName("Delete Test")
    public void deleteTest() {

        RegisterResponse user_auth_info = service.register(user1);

        service.createGame("game1", user_auth_info.authToken());

        service.delete();

        assertThrows(InvalidAccountException.class, () -> {
            service.login(user1);
        });

        assertThrows(InvalidAuthTokenException.class, () -> {
            service.logout(user_auth_info.authToken());
        });

        assertThrows(InvalidAuthTokenException.class, () -> {
            service.listGames(user_auth_info.authToken());
        });
    }

    @Test
    @Order(2)
    @DisplayName("Register Fails")
    public void registerFail() {
        UserData bad_user1 = new UserData(null, "pass", "mail");
        UserData bad_user2 = new UserData("smth", null, "mail");
        UserData bad_user3 = new UserData("smth", "pass", null);

        // Assert no username
        assertThrows(BadRequestException.class, () -> {
            service.register(bad_user1);
        }, "~~~Null username worked and it shouldn't have~~~");

        // Assert no password
        assertThrows(BadRequestException.class, () -> {
            service.register(bad_user2);
        }, "~~~Null password worked and it shouldn't have~~~");

        // Assert no email
        assertThrows(BadRequestException.class, () -> {
            service.register(bad_user3);
        }, "~~~Null email worked and it shouldn't have~~~");
    }


    @Test
    @Order(3)
    @DisplayName("Register Success")
    public void register_success() {

        var register_result = service.register(user1);
        var expected_register = new RegisterResponse("username", register_result.authToken());

;        assertEquals(expected_register, register_result, "Register didn't work you suck lol");
    }

    @Test
    @Order(4)
    @DisplayName("Login Fail")
    public void login_fail() {
        //register user beforehand
        service.register(user1);

        UserData bad_user1 = new UserData(null, "pass", null);
        UserData bad_user2 = new UserData("username", null, null);

        //test null username
        assertThrows(BadRequestException.class, () -> {
                    service.login(bad_user1);
                }, "Successful login with a null username (bad)");

        //test null password
        assertThrows(BadRequestException.class, () -> {
            service.login(bad_user2);
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
    public void login_success() {
         service.register(user1);

         var login_result = service.login(user1);
         var expected_login = new LoginResponse(user1.username(), login_result.authToken());

         assertEquals(expected_login, login_result, "Normal Login did not work:(");
    }

    @Test
    @Order(6)
    @DisplayName("Logout Fail!")
    public void logout_fail() {
        var regi_response = service.register(user1);

        assertThrows(InvalidAuthTokenException.class, () -> {
            service.logout("bad auth token hehehe");
        }, "Logged user out when unauthorized");
    }

    @Test
    @DisplayName("Logout Success")
    public void logout_success() {
        var regi_response = service.register(user1);

        assertDoesNotThrow(() -> service.logout(regi_response.authToken()), "Couldn't log out properly");
    }

    @Test
    @DisplayName("Create Game Fail")
    public void create_fail() {
        //set up
        var regi_response = service.register(user1);
        //test null game name
        assertThrows(BadRequestException.class, () -> {
           service.createGame(null, regi_response.authToken());
        });
        //test null auth token
        assertThrows(BadRequestException.class, () -> {
            service.createGame(null, regi_response.authToken());
        });
        //test bad auth token
        assertThrows(InvalidAuthTokenException.class, () -> {
            service.createGame("GAME", "bad auth token :(");
        });
    }

    // TO DO change all variables to camelCase

    @Test
    @DisplayName("Create Game Success")
    public void create_success() {
        //set up
        var regi_response = service.register(user1);
        var create_response = service.createGame("GAME", regi_response.authToken());

        Assertions.assertTrue(create_response > 0,  "Could not successfully create game");
    }

    @Test
    @DisplayName("List Game Fail")
    public void list_fail() {
        // set up
        var regi_response = service.register(user1);

        //check empty list games?
        assertThrows(InvalidAuthTokenException.class, () -> {
            service.listGames("badAuth");
        }, "bad auth token (empty list)");
        //make multiple games
        service.createGame("Game1", regi_response.authToken());
        service.createGame("Game2", regi_response.authToken());
        service.createGame("Game3", regi_response.authToken());

        //assert
        assertThrows(InvalidAuthTokenException.class, () -> {
            service.listGames("bad_auth");
        }, "bad auth token");
    }

    @Test
    @DisplayName("List Game Success")
    public void list_success() {
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
    public void join_fail() {
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
    public void join_success() {
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
