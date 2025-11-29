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

}
