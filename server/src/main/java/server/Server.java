package server;

import Service.*;
import com.google.gson.Gson;
import datamodel.LoginResponse;
import datamodel.UserData;
import io.javalin.*;
import io.javalin.http.Context;
import passoff.model.TestResult;

import java.util.Map;

public class Server {

    private final Javalin my_server;
    private final UserService userService = new UserService();

    public Server() {
        my_server = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.

        //since the db doesn't exist yet this will help the tests
        my_server.delete("db", ctx -> ctx.result("{}"));
        my_server.post("user", ctx -> register(ctx));
        my_server.post("session", ctx -> login(ctx));


    }

//    Description	Register a new user.
//    URL path	/user
//    HTTP Method	POST
//    Body	{ "username":"", "password":"", "email":"" }
//    Success response	[200] { "username":"", "authToken":"" }
//    Failure response	[400] { "message": "Error: bad request" }
//    Failure response	[403] { "message": "Error: already taken" }
//    Failure response	[500] { "message": "Error: (description of error)" }
    private void register(Context ctx) {

        var serializer = new Gson();
        var user = serializer.fromJson(ctx.body(), UserData.class);
        try {
            var registrationResponse = userService.register(user);
            var response = serializer.toJson(registrationResponse);
            ctx.status(200).result(response);

        } //What is the bad request supposed to be? catch (BadRequestException ex) {ctx.status(400).result(ex.getMessage());}
        catch (InvalidAccountException ex){
            ctx.status(403).result(ex.getMessage());
        } catch (Exception ex) {
            ctx.status(500).result(ex.getMessage());
        }
    }


//    Description	Logs in an existing user (returns a new authToken).
//    URL path	/session
//    HTTP Method	POST
//    Body	{ "username":"", "password":"" }
//    Success response	[200] { "username":"", "authToken":"" }
//    Failure response	[400] { "message": "Error: bad request" }
//    Failure response	[401] { "message": "Error: unauthorized" }
//    Failure response	[500] { "message": "Error: (description of error)" }
    private void login(Context ctx){
        var serializer = new Gson();
        var user = serializer.fromJson(ctx.body(), UserData.class);

        try {
            var loginResponse = userService.login(user);
            var response = serializer.toJson(loginResponse);
            ctx.status(200).result(response);
        } catch (InvalidAccountException ex) {
            ctx.status(400).result(serializer.toJson(ex.getMessage()));
        } catch (BadPasswordException ex) {
            ctx.status(401).result(serializer.toJson(ex.getMessage()));
        }  catch (Exception ex) {
            ctx.status(500).result(serializer.toJson(ex.getMessage()));
        }

    }


    public int run(int desiredPort) {
        my_server.start(desiredPort);
        return my_server.port();
    }

    public void stop() {
        my_server.stop();
    }
}
