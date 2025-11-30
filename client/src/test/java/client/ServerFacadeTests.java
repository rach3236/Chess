package client;

import org.junit.jupiter.api.*;
import server.ChessServerFacade;
import server.Server;


public class ServerFacadeTests {

    private static Server server;
    static ChessServerFacade serverFacade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
//        TO DO: serverFacade = ChessServerFacade(port);
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
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

}
