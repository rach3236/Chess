package server;

import com.google.gson.Gson;
import io.javalin.*;
import io.javalin.http.Context;

import java.util.Map;

public class Server {

    private final Javalin my_server;

    public Server() {
        my_server = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.

        //since the db doesn't exist yet this will help the tests
        my_server.delete("db", ctx -> ctx.result("{}"));
        my_server.post("user", ctx -> register(ctx));


    }

    private void register(Context ctx) {

        var serializer = new Gson();
        var request = serializer.fromJson(ctx.body(), Map.class);
        // call service to register
        var response = Map.of("username", request.get("username"), "authToken", "xyz");
        ctx.result(serializer.toJson(response));
    }

    public int run(int desiredPort) {
        my_server.start(desiredPort);
        return my_server.port();
    }

    public void stop() {
        my_server.stop();
    }
}
