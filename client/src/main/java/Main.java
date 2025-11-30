import chess.*;
import datamodel.*;
import server.ChessServerFacade;

import java.util.Scanner;


public class Main {
    private static ChessServerFacade server;

    public static void main(String[] args) {

        String serverUrl = "http://localhost:8080";
        server = new ChessServerFacade(serverUrl);

        System.out.println("♕ Welcome to 240 Chess. Type Help to get started. ♕");

        var helper = new ArgsHelper();
        helper.loggedStatus = false;

        while (true) {

            System.out.printf((helper.loggedStatus ? "[LOGGED_IN]" : "[LOGGED_OUT]") + " >>> ");
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            var arguments = line.split(" ");

            if (helper.loggedStatus) {
                // call post login response
                if (!postLoginUI(arguments, helper)) {
                    return;
                }
            } else {
                if (!preLoginUI(arguments, helper)) {
                    return;
                }
            }
        }
    }

    private static boolean preLoginUI(String[] arguments, ArgsHelper helper) {
        UserData newUser;
        switch (arguments[0].toLowerCase()) {
            case "help":
                //TO DO: make display messages pretty! (should be diff colors)
                System.out.println("register <USERNAME> <PASSWORD> <EMAIL> - to create an account");
                System.out.println("login <USERNAME> <PASSWORD> - to play chess");
                System.out.println("quit - playing chess");
                System.out.println("help - with possible commands");
                break;

            case "quit":
                System.out.println("Goodbye!");
                //will only return false when user quits
                return false;

            case "login":
                //TO DO: validate arguments!!!
                newUser = new UserData(arguments[1], arguments[2], null);
                try {
                    var loginResponse = server.login(newUser);
                    helper.authKey = loginResponse.authToken();
                    helper.loggedStatus = true;
                } catch (Exception e) {
                    //TO DO: display user-friendly error message to the user
                    return true;
                }
                break;

            case "register":
                //TO DO: validate arguments!!!
                newUser = new UserData(arguments[1], arguments[2], arguments[3]);
                try {
                    var registerResponse = server.register(newUser);
                    helper.authKey = registerResponse.authToken();
                    helper.loggedStatus = true;
                } catch (Exception e) {
                    //TO DO: display user-friendly error message to the user
                    return true;
                }
                break;

            default:
                System.out.println("Not a valid command. Please type 'help' for possible commands.");
        }
        return true;
    }

    private static boolean postLoginUI(String[] arguments, ArgsHelper helper) {
        switch (arguments[0].toLowerCase()) {
            case "create":
                GameData game = new GameData(0, null, null, arguments[1], null);
                try {
                    GameID gameID = server.createGame(game, helper.authKey);
                    //TO DO: output gameID, or whatever
                    return true;
                } catch (Exception e) {
                    //TO DO: give user-friendly error message to user
                }
                break;
            case "list":
                try {
                    //format games array to be readable
                    var gamesList = server.listGames(helper.authKey);
                    for (GameData gameInfo : gamesList.games()) {
                        // TO DO: display everything but the game object (json string)
                        System.out.println(gameInfo);
                    }
                } catch (Exception e) {
                    //TO DO: give user-friendly error message to user
                }
                break;

            case "join":
                try {
                    PlayerInfo player1 = new PlayerInfo(arguments[2], Integer.parseInt(arguments[1]));
                    server.joinPlayer(player1, helper.authKey);
                    //notification that user joined the game (game ID as "color" player)
                    //TO DO: draw the board
                    //helper function to draw the board, pass in white/black (arguments2) pov
                } catch (Exception e) {
                    //TO DO: give user-friendly error message
                }
                break;
            case "observe":
                //TO DO: connect observer to the game
                //TO DO: draw the board
                //helper function to draw the board, pass in white pov
                break;
            case "logout":
                //log the service api as out
                try {
                    server.logout(helper.authKey);
                    helper.loggedStatus = false;
                } catch (Exception e) {
                    //TO DO: user-friendly error message;
                }
                break;
            case "quit":
                System.out.println("Goodbye!");
                return false;
            case "help":
                //TO DO: make display messages pretty! (should be diff colors)
                System.out.println("create <NAME> - a game");
                System.out.println("list - games");
                System.out.println("join <ID> [WHITE|BLACK] - a game");
                System.out.println("observe <ID> - a game");
                System.out.println("logout - when you are done");
                System.out.println("quit - playing chess");
                System.out.println("help - with possible commands");
                break;
            default:
                System.out.println("Not a valid command. Please type 'help' for possible commands.");
        }
        return true;
    }
}