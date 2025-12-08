package server;

import io.javalin.websocket.*;
import server.websocket.WebSocketHandler;
import websocket.commands.UserGameCommand;
//import websocket.commands.UserGameCommand;ServerMessage;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import service.*;
import com.google.gson.Gson;
import datamodel.*;
import datamodel.Error;
import io.javalin.*;
import io.javalin.http.Context;

public class Server {

    private final Javalin myServer;
    private final UserService userService = new UserService();
    private final static String AUTHTOKEN = "authorization";

    public Server() {

        myServer = Javalin.create(config -> config.staticFiles.add("web"));

        WebSocketHandler wsHandler = new WebSocketHandler(userService);

        myServer.ws("/ws", ws -> {
            ws.onConnect(wsHandler);
//            ws.onMessage(wsHandler);
            ws.onClose(wsHandler);
        });

        //handle endpoints here
        myServer.delete("db", this::clear);
        myServer.post("user", this::register);
        myServer.post("session", this::login);
        myServer.delete("session", this::logout);
        myServer.get("game", this::listGames);
        myServer.post("game", this::createGames);
        myServer.put("game", this::joinGame);
    }


    public void clear(Context ctx) {
        var serializer = new Gson();

        try {
            userService.delete();
        } catch (Exception ex){
            ctx.status(500).result(serializer.toJson(new Error(ex.getMessage())));
        }
    }

//    Register a new user.
    private void register(Context ctx) {

        var serializer = new Gson();
        var user = serializer.fromJson(ctx.body(), UserData.class);
        try {
            var registrationResponse = userService.register(user);
            var response = serializer.toJson(registrationResponse);
            ctx.status(200).result(response);

        } //What is the bad request supposed to be?
        catch (BadRequestException ex) {ctx.status(400).result(serializer.toJson(new Error(ex.getMessage())));}
        catch (InvalidAccountException ex){
            var serial = serializer.toJson(new Error(ex.getMessage()));
            ctx.status(403).result(serializer.toJson(new Error(ex.getMessage())));
        } catch (Exception ex) {
            ctx.status(500).result(serializer.toJson(new Error(ex.getMessage())));
        }
    }

//    Logs in an existing user (returns a new authToken)
    private void login(Context ctx){
        var serializer = new Gson();
        var user = serializer.fromJson(ctx.body(), UserData.class);

        try {
            var loginResponse = userService.login(user);
            var response = serializer.toJson(loginResponse);
            ctx.status(200).result(response);
        } catch (BadRequestException ex) {
            ctx.status(400).result(serializer.toJson(new Error(ex.getMessage())));
        } catch (InvalidAccountException ex) {
            ctx.status(401).result(serializer.toJson(new Error(ex.getMessage())));
        }  catch (Exception ex) {
            ctx.status(500).result(serializer.toJson(new Error(ex.getMessage())));
        }
    }

//    Logs out the user represented by the authToken
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

//    Description	Gives a list of all games
    public void listGames(Context ctx){
        var serializer = new Gson();
        String auth = ctx.header(AUTHTOKEN);

        try {
            var listResponse = userService.listGames(auth);
            var response = serializer.toJson(listResponse);
            ctx.status(200).result(response);
        } catch (InvalidAuthTokenException ex) {
            ctx.status(401).result(serializer.toJson(new Error(ex.getMessage())));
        } catch (Exception ex) {
            ctx.status(500).result(serializer.toJson(new Error(ex.getMessage())));
        }
    }

//    Description	Creates a new game.
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

//Verifies that the specified game exists and adds the caller as the requested color to the game.
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
        myServer.start(desiredPort);
        return myServer.port();
    }

    public void stop() {
        myServer.stop();
    }
}
