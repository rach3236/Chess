package server;

import Service.*;
import com.google.gson.Gson;
import datamodel.*;
import datamodel.Error;
import io.javalin.*;
import io.javalin.http.Context;


public class Server {

    private final Javalin my_server;
    private final UserService userService = new UserService();
    private final static String AUTHTOKEN = "authorization";


    public Server() {
        my_server = Javalin.create(config -> config.staticFiles.add("web"));

        //handle endpoints here
        my_server.delete("db", this::clear);
        my_server.post("user", this::register);
        my_server.post("session", this::login);
        my_server.delete("session", this::logout);
        my_server.get("game", this::listGames);
        my_server.post("game", this::createGames);
        my_server.put("game", this::joinGame);

    }

//    Description	Clears the database. Removes all users, games, and authTokens.
//    URL path	/db
//    HTTP Method	DELETE
//    Success response	[200] {}
//    Failure response	[500] { "message": "Error: (description of error)" }
    public void clear(Context ctx) {
        var serializer = new Gson();

        try {
            userService.delete();
        } catch (Exception ex){
            ctx.status(500).result(serializer.toJson(new Error(ex.getMessage())));
        }
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
            var serial = serializer.toJson(new Error(ex.getMessage()));
            ctx.status(403).result(serializer.toJson(new Error(ex.getMessage())));
        } catch (Exception ex) {
            ctx.status(500).result(serializer.toJson(new Error(ex.getMessage())));
        }
    }

// NOTE FOR THE TA: what's the bad request supposed to be?

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
        } catch (BadRequestException ex) {

            ctx.status(400).result(serializer.toJson(new Error(ex.getMessage())));
        } catch (BadPasswordException ex) {
            ctx.status(401).result(serializer.toJson(new Error(ex.getMessage())));
        }  catch (Exception ex) {
            ctx.status(500).result(serializer.toJson(new Error(ex.getMessage())));
        }
    }

    //NOTE FOR THE TA: We're just deleting the authtoken, right???
    //Deleting the authdata

//    Description	Logs out the user represented by the authToken.
//    URL path	/session
//    HTTP Method	DELETE
//    Headers	authorization: <authToken>
//    Success response	[200] {}
//    Failure response	[401] { "message": "Error: unauthorized" }
//    Failure response	[500] { "message": "Error: (description of error)" }
    private void logout(Context ctx) {
        var serializer = new Gson();
        String auth = ctx.header(AUTHTOKEN);

        try {
            userService.logout(auth);
            ctx.status(200);
        } catch (InvalidAuthTokenException ex) {
            ctx.status(401).result(serializer.toJson(new Error(ex.getMessage())));
        } catch (Exception ex) {
            ctx.status(500).result(serializer.toJson(new Error(ex.getMessage())));
        }
    }

    // TO DO: Clean up error codes

//    Description	Gives a list of all games.
//    URL path	/game
//    HTTP Method	GET
//    Headers	authorization: <authToken>
//    Success response	[200] { "games": [{"gameID": 1234, "whiteUsername":"", "blackUsername":"", "gameName:""} ]}
//        Failure response	[401] { "message": "Error: unauthorized" }
//        Failure response	[500] { "message": "Error: (description of error)" }
    public void listGames(Context ctx){
        var serializer = new Gson();
        String auth = ctx.header(AUTHTOKEN);

        try {
            var listResponse = userService.listGames(auth);
            var response = serializer.toJson(listResponse);
            ctx.status(200).result(response);
        } catch (InvalidAuthTokenException ex) {
            ctx.status(401).result(serializer.toJson(serializer.toJson(new Error(ex.getMessage()))));
        } catch (Exception ex) {
            ctx.status(500).result(serializer.toJson(serializer.toJson(new Error(ex.getMessage()))));
        }
    }

    // NOTES FOR THE TA: again, what is the bad request supposed to do?
    // it's for when the wrong thing (or null thing) is passed in
//    Description	Creates a new game.
//    URL path	/game
//    HTTP Method	POST
//    Headers	authorization: <authToken>
//    Body	{ "gameName":"" }
//    Success response	[200] { "gameID": 1234 }
//    Failure response	[400] { "message": "Error: bad request" }
//    Failure response	[401] { "message": "Error: unauthorized" }
//    Failure response	[500] { "message": "Error: (description of error)" }
    public void createGames(Context ctx) {
        var serializer = new Gson();
        String auth = ctx.header(AUTHTOKEN);
        var body = ctx.body();
        var game = serializer.fromJson(body, GameData.class);

        try {
            var gameIDResponse = userService.createGame(game.gameName(), auth);
            ctx.status(200).result(serializer.toJson(new GameID(gameIDResponse)));
        } catch (BadRequestException ex) {
            ctx.status(400).result(serializer.toJson(new Error(ex.getMessage())));
        } catch (InvalidAuthTokenException ex) {
            ctx.status(401).result(serializer.toJson(new Error(ex.getMessage())));
        } catch (Exception ex) {
            ctx.status(500).result(serializer.toJson(new Error(ex.getMessage())));
        }
    }

//    Description	Verifies that the specified game exists and adds the caller as the requested color to the game.
//    URL path	/game
//    HTTP Method	PUT
//    Headers	authorization: <authToken>
//    Body	{ "playerColor":"WHITE/BLACK", "gameID": 1234 }
//    Success response	[200] {}
//    Failure response	[400] { "message": "Error: bad request" }
//    Failure response	[401] { "message": "Error: unauthorized" }
//    Failure response	[403] { "message": "Error: already taken" }
//    Failure response	[500] { "message": "Error: (description of error)" }
    public void joinGame(Context ctx) {
        var serializer = new Gson();
        String auth = ctx.header(AUTHTOKEN);
        var playerInfo = serializer.fromJson(ctx.body(), PlayerInfo.class);

        try {
            userService.joinGame(auth, playerInfo);
            ctx.status(200);
        } catch (BadRequestException ex) {
            ctx.status(400).result(serializer.toJson(new Error(ex.getMessage())));
        } catch (InvalidAuthTokenException ex) {
            ctx.status(401).result(serializer.toJson(new Error(ex.getMessage())));
        } catch (InvalidAccountException ex) {
            ctx.status(403).result(serializer.toJson(new Error(ex.getMessage())));
        } catch (Exception ex) {
            ctx.status(500).result(serializer.toJson(new Error(ex.getMessage())));
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
