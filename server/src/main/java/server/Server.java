package server;

import io.javalin.*;

public class Server {

    private final Javalin my_server;

    public Server() {
        my_server = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.

        //since the db doesn't exist yet this will help the tests
        my_server.delete("db", ctx -> ctx.result("{}"));

        my_server.post("user", ctx -> ctx.result("{ \"username\":\"\", \"authToken\":\"\"}"));

    }

    public int run(int desiredPort) {
        my_server.start(desiredPort);
        return my_server.port();
    }

    public void stop() {
        my_server.stop();
    }
}
