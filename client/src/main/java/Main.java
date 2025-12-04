import chess.*;
import datamodel.*;
import server.ChessServerFacade;

import java.util.ArrayList;
import java.util.Scanner;


public class Main {
    private static ChessServerFacade server;
    private final static String WHITE = "WHITE";
    private final static String BLACK = "BLACK";
    //color combos
    public static final String RESET = "\u001b[0m";
    public static final String BLACKONGRAY = "\u001b[30;47;1m";
    public static final String REDONWHITE = "\u001b[35;107;1m";
    public static final String REDONBLACK = "\u001b[35;40;1m";
    public static final String BLUEONWHITE = "\u001b[34;107;1m";
    public static final String BLUEONBLACK = "\u001b[34;40;1m";
    //highlighting square color combo
    //(phase 6)
    private static ArrayList<GameData> gameList;

    public static void main(String[] args) {

//        String serverUrl = "http://localhost:8080";
        server = new ChessServerFacade(8080);

        System.out.println("♕ Welcome to 240 Chess. Type Help to get started. ♕");

        var helper = new ArgsHelper();
        //SHOULD BE FALSE, for testing purposes only
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
                System.out.println("\u001b[31;mregister <USERNAME> <PASSWORD> <EMAIL> - to create an account");
                System.out.println("login <USERNAME> <PASSWORD> - to play chess");
                System.out.println("quit - playing chess");
                System.out.println("help - with possible commands");
                break;

            case "quit":
                System.out.println("Goodbye!");
                //will only return false when user quits
                return false;

            case "login":
                var checkResult = loginCheck(arguments);
                if (checkResult != null) {
                    System.out.println(checkResult);
                    return true;
                }
                newUser = new UserData(arguments[1], arguments[2], null);
                try {
                    var loginResponse = server.login(newUser);
                    helper.authKey = loginResponse.authToken();
                    helper.loggedStatus = true;
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    return true;
                }
                break;

            case "register":
                var regCheckResult = registerCheck(arguments);
                if (regCheckResult != null) {
                    System.out.println(regCheckResult);
                    return true;
                }
                newUser = new UserData(arguments[1], arguments[2], arguments[3]);
                try {
                    var registerResponse = server.register(newUser);
                    helper.authKey = registerResponse.authToken();
                    helper.loggedStatus = true;
                } catch (Exception e) {
                    System.out.println(e.getMessage());
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
                var createCheckResponse = createCheck(arguments);
                if (createCheckResponse != null) {
                    System.out.println(createCheckResponse);
                    break;
                }
                GameData game = new GameData(0, null, null, arguments[1], null);
                try {
                    server.createGame(game, helper.authKey);
                    System.out.println("Your game is created! Please call 'list' to see the full list of games.");
                    return true;
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                break;
            case "list":
                try {
                    var allGamesList = server.listGames(helper.authKey);
                    gameList = allGamesList.games();
                    for (int i = 0; i<gameList.size(); i++) {
                        var currGame = gameList.get(i);
                        String whitePlayer = currGame.whiteUsername()!=null ? currGame.whiteUsername() : " ";
                        String blackPlayer = currGame.blackUsername()!=null ? currGame.blackUsername() : " ";
                        System.out.println((i+1) +": " + currGame.gameName() + ", White Player: " +
                                whitePlayer + ", Black Player: " + blackPlayer);
                    }
                    System.out.println("List length: " + String.valueOf(gameList.size()));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                break;
            case "join":
                try {
                    int ind = 0;
                    var allGamesList = server.listGames(helper.authKey);
                    gameList = allGamesList.games();
                    int listLen = gameList.size();

                    var joinCheckResponse = joinCheck(arguments, listLen);
                    if (joinCheckResponse != null) {
                        System.out.println(joinCheckResponse);
                    }
                    ind = Integer.parseInt(arguments[1]);
                    PlayerInfo player1 = new PlayerInfo(arguments[2], gameList.get(ind-1).gameID());
                    server.joinPlayer(player1, helper.authKey);

                    drawBoard(arguments[2]);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                break;
            case "observe":
                try {
                    var allGamesList = server.listGames(helper.authKey);
                    gameList = allGamesList.games();
                    int listLen = gameList.size();
                    var observeCheckResponse = observeCheck(arguments, listLen);
                    if (observeCheckResponse != null) {
                        System.out.println(observeCheckResponse);
                        return true;
                    }
                    int ind = Integer.parseInt(arguments[1]);
                    int gameID = gameList.get(ind - 1).gameID();
                    drawBoard(BLACK);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    return true;
                }
                break;
            case "logout":
                try {
                    server.logout(helper.authKey);
                    helper.loggedStatus = false;
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    return true;
                }
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

    private static void drawBoard(String colorPOV) {
        //default board
        if (colorPOV.equals(WHITE)) {
            System.out.println(BLACKONGRAY + "    a  b  c  d  e  f  g  h    " + RESET);
            System.out.println(BLACKONGRAY + " 8 " + BLUEONWHITE + " R " + BLUEONBLACK +
                    " N " + BLUEONWHITE + " B " + BLUEONBLACK + " Q " + BLUEONWHITE +
                    " K " + BLUEONBLACK + " B " + BLUEONWHITE + " N " + BLUEONBLACK +
                    " R " + BLACKONGRAY + " 8 " + RESET);
            System.out.println(BLACKONGRAY + " 7 " + BLUEONBLACK + " P " + BLUEONWHITE +
                    " P " + BLUEONBLACK + " P " + BLUEONWHITE + " P " + BLUEONBLACK +
                    " P " + BLUEONWHITE + " P " + BLUEONBLACK + " P " + BLUEONWHITE +
                    " P " + BLACKONGRAY + " 7 " + RESET);
            System.out.println(BLACKONGRAY + " 6 " + BLUEONWHITE + "   " + BLUEONBLACK +
                    "   " + BLUEONWHITE + "   " + BLUEONBLACK + "   " + BLUEONWHITE +
                    "   " + BLUEONBLACK + "   " + BLUEONWHITE + "   " + BLUEONBLACK +
                    "   " + BLACKONGRAY + " 6 " + RESET);
            System.out.println(BLACKONGRAY + " 5 " + BLUEONBLACK + "   " + BLUEONWHITE +
                    "   " + BLUEONBLACK + "   " + BLUEONWHITE + "   " + BLUEONBLACK +
                    "   " + BLUEONWHITE + "   " + BLUEONBLACK + "   " + BLUEONWHITE +
                    "   " + BLACKONGRAY + " 5 " + RESET);
            System.out.println(BLACKONGRAY + " 4 " + BLUEONWHITE + "   " + BLUEONBLACK +
                    "   " + BLUEONWHITE + "   " + BLUEONBLACK + "   " + BLUEONWHITE +
                    "   " + BLUEONBLACK + "   " + BLUEONWHITE + "   " + BLUEONBLACK +
                    "   " + BLACKONGRAY + " 4 " + RESET);
            System.out.println(BLACKONGRAY + " 3 " + BLUEONBLACK + "   " + BLUEONWHITE +
                    "   " + BLUEONBLACK + "   " + BLUEONWHITE + "   " + BLUEONBLACK +
                    "   " + BLUEONWHITE + "   " + BLUEONBLACK + "   " + BLUEONWHITE +
                    "   " + BLACKONGRAY + " 3 " + RESET);
            System.out.println(BLACKONGRAY + " 2 " + REDONWHITE + " P " + REDONBLACK +
                    " P " + REDONWHITE + " P " + REDONBLACK + " P " + REDONWHITE +
                    " P " + REDONBLACK + " P " + REDONWHITE + " P " + REDONBLACK +
                    " P " + BLACKONGRAY + " 2 " + RESET);
            System.out.println(BLACKONGRAY + " 1 " + REDONBLACK + " R " + REDONWHITE +
                    " N " + REDONBLACK + " B " + REDONWHITE + " Q " + REDONBLACK +
                    " K " + REDONWHITE + " B " + REDONBLACK + " N " + REDONWHITE +
                    " R " + BLACKONGRAY + " 1 " + RESET);
            System.out.println(BLACKONGRAY + "    a  b  c  d  e  f  g  h    " + RESET);
        } else if (colorPOV.equals(BLACK)) {
            System.out.println(BLACKONGRAY + "    h  g  f  e  d  c  b  a    " + RESET);
            System.out.println(BLACKONGRAY + " 1 " + REDONWHITE + " R " + REDONBLACK +
                    " N " + REDONWHITE + " B " + REDONBLACK + " K " + REDONWHITE +
                    " Q " + REDONBLACK + " B " + REDONWHITE + " N " + REDONBLACK +
                    " R " + BLACKONGRAY + " 1 " + RESET);
            System.out.println(BLACKONGRAY + " 2 " + REDONBLACK + " P " + REDONWHITE +
                    " P " + REDONBLACK + " P " + REDONWHITE + " P " + REDONBLACK +
                    " P " + REDONWHITE + " P " + REDONBLACK + " P " + REDONWHITE +
                    " P " + BLACKONGRAY + " 2 " + RESET);
            System.out.println(BLACKONGRAY + " 3 " + BLUEONWHITE + "   " + BLUEONBLACK +
                    "   " + BLUEONWHITE + "   " + BLUEONBLACK + "   " + BLUEONWHITE +
                    "   " + BLUEONBLACK + "   " + BLUEONWHITE + "   " + BLUEONBLACK +
                    "   " + BLACKONGRAY + " 3 " + RESET);
            System.out.println(BLACKONGRAY + " 4 " + BLUEONBLACK + "   " + BLUEONWHITE +
                    "   " + BLUEONBLACK + "   " + BLUEONWHITE + "   " + BLUEONBLACK +
                    "   " + BLUEONWHITE + "   " + BLUEONBLACK + "   " + BLUEONWHITE +
                    "   " + BLACKONGRAY + " 4 " + RESET);
            System.out.println(BLACKONGRAY + " 5 " + BLUEONWHITE + "   " + BLUEONBLACK +
                    "   " + BLUEONWHITE + "   " + BLUEONBLACK + "   " + BLUEONWHITE +
                    "   " + BLUEONBLACK + "   " + BLUEONWHITE + "   " + BLUEONBLACK +
                    "   " + BLACKONGRAY + " 5 " + RESET);
            System.out.println(BLACKONGRAY + " 6 " + BLUEONBLACK + "   " + BLUEONWHITE +
                    "   " + BLUEONBLACK + "   " + BLUEONWHITE + "   " + BLUEONBLACK +
                    "   " + BLUEONWHITE + "   " + BLUEONBLACK + "   " + BLUEONWHITE +
                    "   " + BLACKONGRAY + " 6 " + RESET);
            System.out.println(BLACKONGRAY + " 7 " + BLUEONWHITE + " P " + BLUEONBLACK +
                    " P " + BLUEONWHITE + " P " + BLUEONBLACK + " P " + BLUEONWHITE +
                    " P " + BLUEONBLACK + " P " + BLUEONWHITE + " P " + BLUEONBLACK +
                    " P " + BLACKONGRAY + " 7 " + RESET);
            System.out.println(BLACKONGRAY + " 8 " + BLUEONBLACK + " R " + BLUEONWHITE +
                    " N " + BLUEONBLACK + " B " + BLUEONWHITE + " K " + BLUEONBLACK +
                    " Q " + BLUEONWHITE + " B " + BLUEONBLACK + " N " + BLUEONWHITE +
                    " R " + BLACKONGRAY + " 8 " + RESET);
            System.out.println(BLACKONGRAY + "    h  g  f  e  d  c  b  a    " + RESET);
        }
    }

    private static String loginCheck(String[] inputs) {
//        System.out.println("login <USERNAME> <PASSWORD> - to play chess");
        if (inputs.length < 3) {
            return """
                    Oh no! Looks like we don't have enough the username AND password🤭
                    Please make sure to format your input like this: 
                    --->'login <USERNAME> <PASSWORD>'
                    """;

        } else if (inputs.length > 3) {
            return "Oh no! Looks like you put in \\u001B[3m too many" + RESET + "inputs:)" +
                    "\nPlease make sure to format your input like this: " +
                    "\n     'login <USERNAME> <PASSWORD>'";
        }
        return null;
    }

    private static String registerCheck(String[] inputs) {
        if (inputs.length < 4) {
            return "Oh no! Looks like we're missing a username, password, or email!" +
                    "\nPlease make sure you format your input like this: " +
                    "\n'register <USERNAME> <PASSWORD> <EMAIL>'";
        } else if (inputs.length > 4) {
            return "Oh no! Looks like you put in \\u001B[3m too many" + RESET + "inputs:)" +
                    "\nPlease make sure to format your input like this: " +
                    "\n      'register <USERNAME> <PASSWORD> <EMAIL>'";
        }
        return null;
    }

    private static String createCheck(String[] inputs) {
        if (inputs.length < 2) {
            return "Oh no! Looks like you're missing the name of the game:)" +
                    "\nWhat would you like to name the game?" +
                    "\nPlease format your input like this: " +
                    "\n    'create <GAME NAME>'";
        } else if (inputs.length > 2) {
            return "Oh no! Looks like you have \\u001B[3m too many" + RESET + "inputs:)" +
                    "\n Please format your input like this: " +
                    "\n    'create <GAME NAME>'";
        }
        return null;
    }

    private static String joinCheck(String[] inputs, int listLen) {
//        System.out.println("join <ID> [WHITE|BLACK] - a game");

        //check arg lengths
        if (inputs.length < 3) {
            return """
                    Oh no! Looks like you're missing something:)
                    Please format your input like this:
                        'join <ID> [WHITE|BLACK]'
                    """;
        } else if (inputs.length > 3) {
            return "Oh no! Looks like you have \\u001B[3m too many" + RESET + "inputs:)" +
                    "\n Please format your input like this: " +
                    "\n    'join <ID> [WHITE|BLACK]'";
        }
        //check order of args
        if (inputs[1].equals("WHITE") || inputs[1].equals("BLACK")) {
            return """
                    Oh no! Looks like your arguments got mixed up.
                    Please format your input like this:
                        'join <ID> [WHITE|BLACK]'
                    """;
        } else if (!inputs[2].equals("WHITE") && !inputs[2].equals("BLACK")) {
            return """
                    Oh no! Looks like your arguments got mixed up.
                    Please format your input like this:
                        'join <ID> [WHITE|BLACK]'
                    """;
        }
        //check game number is valid
        if (checkGameNumber(inputs, listLen) != null) {
            return checkGameNumber(inputs, listLen);
        }

        return null;
    }

    private static String observeCheck(String[] inputs, int listLen) {

        if (inputs.length < 2) {
            return """
                    Oh no! Looks like you're missing the game number:)
                    Please format your input like this:
                    --->'observe <GAME NUMBER>'
                    """;
        } else if (inputs.length > 2) {
            return """
                    Oh no! Looks like you have too many inputs:)
                    Please format your input like this:
                    --->'observe <GAME NUMBER>'
                    """;
        }

        return null;
    }

    private static String checkGameNumber(String[] inputs, int listLen) {
        int ind = 0;
        try {
            ind = Integer.parseInt(inputs[1]);
        } catch (NumberFormatException e) {
            System.out.println("Game Number was not a valid integer. Please pass in a valid integer.");
            System.out.println("""
                    Please format your input like this:
                    --->'join <ID> [WHITE|BLACK]'
                    """);
        }

        if (ind > listLen || ind < 1) {
            return """
                    Oh no! Looks like that game doesn't exist:)
                    Please enter a valid game number with your query:
                    """;
        }

        return null;
    }


}





