package client;

import datamodel.*;
import org.junit.jupiter.api.*;
import server.ChessServerFacade;
import server.Server;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ChessServerFacade serverFacade;
    private static UserData user1;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ChessServerFacade(port);
        user1 = new UserData("player1", "password", "p1@email.com");
    }

    @BeforeEach
    public void setUp() {
        try {
            serverFacade.clear();
        }
        catch (Exception e) {
            //TO DO
        }
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    @DisplayName("Register Fail")
    void registerFail() throws Exception {
        serverFacade.register(user1);

        assertThrows(Exception.class, () -> {
           serverFacade.login(new UserData("bad user", "bad pass", "bad email"));
        });
    }


    @Test
    @DisplayName("Register Success")
    void registerSuccess() throws Exception {

        var authData = serverFacade.register(user1);
        assertTrue(authData.authToken().length() > 10);

        serverFacade.logout(authData.authToken());
        assertNotNull(serverFacade.login(user1), "Error logging in, maybe didn't register right");
    }

    @Test
    @DisplayName("Login Fail")
    void loginFail() throws Exception {
        //test no user fail
        assertThrows(Exception.class, () -> serverFacade.login(new UserData("I don't exist", "no password", null)));

        var reg = serverFacade.register(user1);
        serverFacade.logout(reg.authToken());
        //test bad password fail
        assertThrows(Exception.class, () -> serverFacade.login(new UserData(user1.username(), "bad", null)));
        assertThrows(Exception.class, () -> serverFacade.login(new UserData("bad", user1.password(), null)));
    }

    @Test
    @DisplayName("Login Success")
    void loginSuccess() throws Exception {

        var authData = serverFacade.register(user1);

        serverFacade.logout(authData.authToken());
        assertNotNull(serverFacade.login(user1), "Error logging in");
    }

    @Test
    @DisplayName("Logout Fail")
    void logoutFail() throws Exception {
        //test not logged in log out
        assertThrows(Exception.class, () -> serverFacade.logout("bad auth"));

        var reg = serverFacade.register(user1);
        assertThrows(Exception.class, () -> serverFacade.logout("bad auth"));
    }

    @Test
    @DisplayName("Logout Success!")
    void logoutSuccess() throws Exception {
        var reg = serverFacade.register(user1);
        serverFacade.logout(reg.authToken());

        assertNotNull(serverFacade.login(user1), "Bad logout");
    }

    @Test
    @DisplayName("List Fail")
    void listFail() throws Exception {
        var reg = serverFacade.register(user1);
        GameData game1 = new GameData(0, null, null, "Game1", null);
        GameData game2 = new GameData(0, null, null, "Game2", null);

        serverFacade.createGame(game1, reg.authToken());
        serverFacade.createGame(game2, reg.authToken());

        assertThrows(Exception.class, () -> {
           serverFacade.listGames("bad auth");
        });
    }

    @Test
    @DisplayName("List Success!")
    void listSuccess() throws Exception {

        var reg = serverFacade.register(user1);
        GameData game1 = new GameData(0, null, null, "Game1", null);
        GameData game2 = new GameData(0, null, null, "Game2", null);

        serverFacade.createGame(game1, reg.authToken());
        serverFacade.createGame(game2, reg.authToken());

        assertEquals(game1.gameName(), serverFacade.listGames(reg.authToken()).games().get(0).gameName());
        assertEquals(game2.gameName(), serverFacade.listGames(reg.authToken()).games().get(1).gameName());
    }

    @Test
    @DisplayName("Create Fail")
    void createFail() throws Exception {
        var reg = serverFacade.register(user1);

        assertThrows(Exception.class, () -> serverFacade.createGame(
                new GameData(0, null, null, null, null), reg.authToken()));

        assertThrows(Exception.class, () -> serverFacade.createGame(
                new GameData(0, null, null, null, null), "bad auth"));
    }

    @Test
    @DisplayName("Create Success!")
    void createSuccess() throws Exception {
        var reg = serverFacade.register(user1);
        var result = serverFacade.createGame(
                new GameData(0, null, null, "Name!", null), reg.authToken());

        assertTrue(result.gameID() > 0, "Did not return a valid gameID");

    }

    @Test
    @DisplayName("Join Fail")
    void joinFail() throws Exception {
        var reg = serverFacade.register(user1);
        PlayerInfo badPlayer = new PlayerInfo("WHITE", 1);
        assertThrows(Exception.class, () -> serverFacade.joinPlayer(badPlayer, reg.authToken()), "Added user to game that didn't exist");

        GameData game1 = new GameData(0, null, null, "Game1", null);
        GameData game2 = new GameData(0, null, null, "Game2", null);

        var game1ID = serverFacade.createGame(game1, reg.authToken());
        var game2ID = serverFacade.createGame(game2, reg.authToken());

        PlayerInfo player1 = new PlayerInfo("WHITE", game1ID.gameID());
        PlayerInfo player2 = new PlayerInfo("BLACK", game1ID.gameID());
        PlayerInfo badPlayer2 = new PlayerInfo("BLACK", -1);
        PlayerInfo badPlayer3 = new PlayerInfo("BLACK", game1ID.gameID());

        //test bad auth token first
        assertThrows(Exception.class, () -> serverFacade.joinPlayer(player1, "bad auth"));
        //test bad game id
        assertThrows(Exception.class, () -> serverFacade.joinPlayer(badPlayer2, reg.authToken()));
        //test trying to join w/ a taken color
        serverFacade.joinPlayer(player2, reg.authToken());
        assertThrows(Exception.class, () -> serverFacade.joinPlayer(badPlayer3, reg.authToken()));
    }

    @Test
    @DisplayName("Join Success!")
    void joinSuccess() throws Exception {
        var reg = serverFacade.register(user1);
        GameData game1 = new GameData(0, null, null, "Game1", null);
        GameData game2 = new GameData(0, null, null, "Game2", null);

        var game1ID = serverFacade.createGame(game1, reg.authToken());
        serverFacade.createGame(game2, reg.authToken());

        PlayerInfo player1 = new PlayerInfo("WHITE", game1ID.gameID());
        PlayerInfo player2 = new PlayerInfo("BLACK", game1ID.gameID());


        PlayerInfo badPlayer1 = new PlayerInfo("WHITE", game1ID.gameID());
        PlayerInfo badPlayer2 = new PlayerInfo("BLACK", game1ID.gameID());

        serverFacade.joinPlayer(player1, reg.authToken());
        assertThrows(Exception.class, () ->serverFacade.joinPlayer(badPlayer1, reg.authToken()),
                "Didn't successfully block another player from joining as a white player");
        serverFacade.joinPlayer(player2, reg.authToken());
        assertThrows(Exception.class, () ->serverFacade.joinPlayer(badPlayer2, reg.authToken()),
                "Didn't successfully block another player from joining as a black player");
    }
}
