import chess.*;
import datamodel.GameData;
import datamodel.PlayerInfo;
import datamodel.UserData;
import server.ChessServerFacade;
import websocket.NotificationHandler;
import websocket.WebSocketFacade;
import websocket.messages.ServerMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ChessClient implements NotificationHandler {

    private static ChessServerFacade server;
    private static WebSocketFacade webSocketServer;
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
    private ChessBoard gameBoardObject;

    public ChessClient() throws Exception {
        String serverUrl = "http://localhost:8080";
        server = new ChessServerFacade(8080);
        webSocketServer = new WebSocketFacade(serverUrl, this);

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

    public void notify(ServerMessage notification) {
        System.out.println(REDONBLACK + notification.getServerMessage() + RESET);

        switch (notification.getServerMessageType()) {
            case ServerMessage.ServerMessageType.LOAD_GAME:
                System.out.println("test for load game notification");
                System.out.println("load game checkpoint 1");
               drawBoard(notification.getGame().getBoard(), notification.getPOV(), null, null);
               break;

            case ServerMessage.ServerMessageType.ERROR:
                System.out.println("Error: " + notification.getServerMessage());
                break;

            case ServerMessage.ServerMessageType.NOTIFICATION:
                System.out.println(notification.getServerMessage());
                break;
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
                    System.out.println("Your game is created!");
                    var allGamesList = server.listGames(helper.authKey);
                    gameList = allGamesList.games();
                    return true;

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                break;
            case "list":
                try {
                    var allGamesList = server.listGames(helper.authKey);
                    gameList = allGamesList.games();
                    for (int i = 0; i < gameList.size(); i++) {
                        var currGame = gameList.get(i);
                        String whitePlayer = currGame.whiteUsername() != null ? currGame.whiteUsername() : " ";
                        String blackPlayer = currGame.blackUsername() != null ? currGame.blackUsername() : " ";
                        System.out.println((i + 1) + ": " + currGame.gameName() + ", White Player: " +
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
                    int listLen = gameList.size();

                    var joinCheckResponse = joinCheck(arguments, listLen);
                    if (joinCheckResponse != null) {
                        System.out.println(joinCheckResponse);
                        return true;
                    }
                    ind = Integer.parseInt(arguments[1]);
                    PlayerInfo playerInfo = new PlayerInfo(arguments[2], gameList.get(ind - 1).gameID());
                    server.joinPlayer(playerInfo, helper.authKey);

                    //move player to game play UI
                    webSocketServer.connect(helper.authKey, playerInfo.gameID(), false, arguments[2]);
                    gamePlayUI(helper, gameList.get(ind - 1).gameID(), false, arguments[2]);
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
                    //move player to game play UI
                    webSocketServer.connect(helper.authKey, gameList.get(ind - 1).gameID(), true, "WHITE");
                    gamePlayUI(helper, gameList.get(ind - 1).gameID(), true, "WHITE");
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

    public static void gamePlayUI(ArgsHelper helper, int gameID, boolean observerStatus, String pov) {
        System.out.println("Welcome to the game!");
        while (true) {

            boolean activeGame = true;

            System.out.print("[GAME_PLAY] >>> ");
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            var arguments = line.split(" ");

            switch (arguments[0].toLowerCase()) {

                case "help":
                    System.out.println("""
                            List of possible commands:
                            
                            'help' - displays possible commands
                            'redraw' - draws and displays the chess board
                            'make_move <START POSITION> <END POSITION>' - make a move
                            'leave' - leave current game
                            'resign' - resign from current game, other player wins
                            'highlight_legal_moves <CHESS PIECE POSITION>' - see current possible moves to make as displayed on the board
                            """);
                    break;
                case "redraw":
                    //TO DO
                    //validate arguments
//                    drawBoard(gameBoardObject);
                    break;
                case "make_move":
                    if (!activeGame) {
                        System.out.println("The game is over dummy;)");
                        break;
                    }
                    // TO DO:
                    // validate makeMove arguments
                    //if !cool
                    //  system.out.println(error)
                    // else {
                    // }
                    break;
                case "leave":
                    //validate arguments
                    try {
                        webSocketServer.leave(helper.authKey, gameID, observerStatus, pov);
                        return;
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    return;
                case "resign":
                    //TO DO: validate arguments

                    System.out.println("Do you really want to resign?👀 ('y'/'n') >>> ");
                    line = scanner.nextLine();

                    if (line.equals("y")) {
                        try {
                            webSocketServer.resign(helper.authKey, gameID);
                            break;
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                    }

                    break;
                case "highlight_legal_moves":
                    //TO DO:
                    // allows user to input piece for which they want legal moves
                    // get possible moves (pieceMoves in ChessPiece.java, takes a board and a current position)
                    // display possible moves, drawBoard(gameState, pov, validMoves, ChessPosition)
                    // @override ? draw board w/ possible moves highlighted (pass in gameState & possMoves)

                    break;


                default:
                    System.out.println("Not a valid command. Please type 'help' for options");
            }

        }
    }

    private static void drawBoard(ChessBoard board, String pov, ArrayList<ChessMove> moves, ChessPosition currPos) {

        ArrayList<String> rows = new ArrayList<>(
                List.of(" 1 ", " 2 ", " 3 ", " 4 ", " 5 ", " 6 ", " 7 ", " 8 ")
        );

        boolean povBlack = pov.equals("BLACK");
        String columns = "    a  b  c  d  e  f  g  h    ";
        if (povBlack) {
            columns = new StringBuilder(columns).reverse().toString();
            printBoardForBlack(board, columns, rows);
        } else {
            printBoardForWhite(board, columns, rows);
        }
    }

    private static void printBoardForWhite(ChessBoard board, String columns, ArrayList<String> rows) {
        for (int i = 0; i < 10; i++) {
            if (i == 0 || i == 9) {
                System.out.println(BLACKONGRAY + columns + RESET);
                continue;
            }
            String line = "";
            for (int j = 0; j < 10; j++) {

                if (j == 0 || j == 9) {
                    line += BLACKONGRAY + rows.get(rows.size() - i);
                    continue;
                }
                line += colorHelper(new ChessPosition(9 - i, j), board);
            }
            System.out.println(line + RESET);
        }
    }

    private static void printBoardForBlack(ChessBoard board, String columns, ArrayList<String> rows) {
        for (int i = 9; i >= 0; i--) {
            if (i == 0 || i == 9) {
                System.out.println(BLACKONGRAY + columns + RESET);
                continue;
            }
            String line = "";
            for (int j = 9; j >= 0; j--) {

                if (j == 0 || j == 9) {
                    line += BLACKONGRAY + rows.get(rows.size() - i);
                    continue;
                }
                line += colorHelper(new ChessPosition(9 - i, j), board);
            }
            System.out.println(line + RESET);
        }
    }

    private static String colorHelper(ChessPosition position, ChessBoard board) {
        String color = "\u001b[";
        var piece = board.getPiece(position);
        String foregroundColor = "34;";
        String pieceType = " ";
        if (piece != null) {
            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                foregroundColor = "31;";
            }
            pieceType = convertPieceType(piece.getPieceType());
        }
        String backgroundColor = "107;";
        if ((position.getRow() % 2 == 0 && position.getColumn() % 2 == 0) ||
                (position.getRow() % 2 == 1 && position.getColumn() % 2 == 1)) {
            backgroundColor = "40;";
        }
        return color + foregroundColor + backgroundColor + "1m" + " " + pieceType + " ";
    }

    private static String convertPieceType(ChessPiece.PieceType type) {
        switch (type) {
            case ChessPiece.PieceType.ROOK:
                return "R";
            case ChessPiece.PieceType.BISHOP:
                return "B";
            case ChessPiece.PieceType.KNIGHT:
                return "N";
            case ChessPiece.PieceType.KING:
                return "K";
            case ChessPiece.PieceType.QUEEN:
                return "Q";
            case ChessPiece.PieceType.PAWN:
                return "P";
            default:
                return " ";
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
            return "Oh no! Looks like you put in too many" + RESET + "inputs:)" +
                    "\nPlease make sure to format your input like this: " +
                    "\n     'login <USERNAME> <PASSWORD>'";
        }
        return null;
    }

    private static String registerCheck(String[] inputs) {
        if (inputs.length < 4) {
            return """
                    Oh no! Looks like we're missing a username, password, or email!\
                    
                    Please make sure you format your input like this: \
                    
                    'register <USERNAME> <PASSWORD> <EMAIL>'""";
        } else if (inputs.length > 4) {
            return "Oh no! Looks like you put in too many" + RESET + "inputs:)" +
                    "\nPlease make sure to format your input like this: " +
                    "\n      'register <USERNAME> <PASSWORD> <EMAIL>'";
        }
        return null;
    }

    private static String createCheck(String[] inputs) {
        if (inputs.length < 2) {
            return """
                     Oh no! Looks like you're missing the name of the game:)\
                                    \s
                     What would you like to name the game?\
                    \s
                     Please format your input like this: \
                    \s
                         'create <GAME NAME>'""";
        } else if (inputs.length > 2) {
            return "Oh no! Looks like you have too many" + RESET + "inputs:)" +
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
            return "Oh no! Looks like you have too many" + RESET + "inputs:)" +
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
        //check game number is valid
        if (checkGameNumber(inputs, listLen) != null) {
            return checkGameNumber(inputs, listLen);
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

        ind = Integer.parseInt(inputs[1]);
        if (ind > listLen || ind < 1) {
            return """
                    Oh no! Looks like that game doesn't exist:)
                    Please enter a valid game number with your query:
                    """;
        }

        return null;
    }

//    List of possible commands:
//
//            'help' - displays possible commands
//                            'resign' - resign from current game, other player wins
//                            'highlight_legal_moves <CHESS PIECE POSITION>' - see current possible moves to make as displayed on the board

    private static String checkRedraw(String[] inputs) {

        if (inputs.length > 2) {
            return "Oh no! Looks like you have too many" + RESET + "inputs:)" +
                    "\n Please format your input like this: " +
                    "\n--->'redraw'";
        }
        return null;
    }

    private static String checkMakeMove(String[] inputs) {

        if (inputs.length > 3) {
            return "Oh no! Looks like you have too many" + RESET + "inputs:)" +
                    "\n Please format your input like this: " +
                    "\n--->'make_move <START POSITION> <END POSITION>'";
        } else if (inputs.length < 3) {
            return """
                    Oh no! Looks like you're missing a field:)
                    Please format your input like this:
                    --->'make_move <START POSITION> <END POSITION>'
                    """;
        }

        if (!inputs[1].matches("[a-h][1-8]")) {
            return """
                    Wait a second, your first move doesn't look like a valid move🤔
                    Please format your moves as a letter and number, like this: a5, b3, h6, d1, f7
                    """;
        } else if (!inputs[2].matches("[a-h][1-8]")) {
            return """
                    Wait a second, your second move doesn't look like a valid move🤔
                    Please format your moves as a letter and number, like this: a5, b3, h6, d1, f7
                    """;
        }
        return null;
    }

    private static String checkLeave(String[] inputs) {
        if (inputs.length > 2) {
            return """
                    Oh no! Looks like you have too many inputs:)\
                    
                     Please format your input like this: \
                    
                    --->'leave'""";
        }
        return null;
    }

    private static String checkResign(String[] inputs) {
        if (inputs.length > 2) {
            return """
                    Oh no! Looks like you have too many inputs:)\
                    
                     Please format your input like this: \
                    
                    --->'resign'""";
        }
        return null;
    }

}