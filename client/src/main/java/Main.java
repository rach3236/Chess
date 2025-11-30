import chess.*;

import java.util.Locale;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
//        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
//        System.out.println("♕ 240 Chess Client: " + piece);
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
        switch (arguments[0].toLowerCase()) {
            case "help":
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
                // call user service login!
                // set log status to equal userService success
                helper.loggedStatus = true;
                break;
            case "register":
                // call user service register w/ arguments 1, 2, and 3
                helper.loggedStatus = true;

                break;

            default:
                System.out.println("Not a valid command. Please type 'help' for possible commands.");
        }
        return true;
    }

    private static boolean postLoginUI(String[] arguments, ArgsHelper helper) {
        switch (arguments[0].toLowerCase()) {
            case "create":
                break;
            case "list":
                break;
            case "join":
                break;
            case "observe":
                break;
            case "logout":
                //log the service api as out
                helper.loggedStatus = false;
                break;
            case "quit":
                System.out.println("Goodbye!");
                return false;
            case "help":
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