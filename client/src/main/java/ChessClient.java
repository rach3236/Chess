import chess.*;
import datamodel.GameData;
import datamodel.PlayerInfo;
import datamodel.UserData;
import server.ChessServerFacade;
import websocket.NotificationHandler;
import websocket.WebSocketFacade;
import websocket.messages.ServerMessage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

public class ChessClient implements NotificationHandler {

    private static ChessServerFacade server;
    private static WebSocketFacade webSocketServer;
    private static ValidationChecks validation;
    private final static String WHITE = "WHITE";
    private final static String BLACK = "BLACK";
    //color combos
    public static final String RESET = "\u001b[0m";
    public static final String BLACKONGRAY = "\u001b[30;47;1m";
    public static final String REDONBLACK = "\u001b[35;40;1m";
    //highlighting square color combo
    //(phase 6)
    private static ArrayList<GameData> gameList;
    private static ChessGame gameBoardGame;
    private static ChessBoard gameBoardObject;
    private static String clientPOV;

    public ChessClient() throws Exception {
        String serverUrl = "http://localhost:8080";
        server = new ChessServerFacade(8080);
        webSocketServer = new WebSocketFacade(serverUrl, this);

        System.out.println("♕ Welcome to 240 Chess. Type Help to get started. ♕");

        var helper = new ArgsHelper();
        helper.loggedStatus = false;
        gameBoardGame = new ChessGame();
        gameBoardObject = new ChessGame().getBoard();
        clientPOV = "WHITE";

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
        System.out.println();
        switch (notification.getServerMessageType()) {
            case ServerMessage.ServerMessageType.LOAD_GAME:
               drawBoard(notification.getGame().getBoard(), clientPOV, new ArrayList<>(), new ChessPosition(0,0));
               gameBoardObject = notification.getGame().getBoard();
               gameBoardGame = notification.getGame();
                System.out.print("[GAME_PLAY] >>> ");
               break;

            case ServerMessage.ServerMessageType.ERROR:
                System.out.println(REDONBLACK + notification.getErrorMessage() + RESET);
                System.out.print("[GAME_PLAY] >>> ");
                break;

            case ServerMessage.ServerMessageType.NOTIFICATION:
                System.out.println(REDONBLACK + notification.getServerMessage() + RESET);
                System.out.print("[GAME_PLAY] >>> ");
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
                var checkResult = validation.loginCheck(arguments);
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
                var regCheckResult = validation.registerCheck(arguments);
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
                var createCheckResponse = validation.createCheck(arguments);
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
                    System.out.println("List length: " + gameList.size());
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                break;
            case "join":
                try {
                    int ind = 0;
                    int listLen = gameList.size();
                    var joinCheckResponse = validation.joinCheck(arguments, listLen);
                    if (joinCheckResponse != null) {
                        System.out.println(joinCheckResponse);
                        return true;
                    }
                    ind = Integer.parseInt(arguments[1]);
                    PlayerInfo playerInfo = new PlayerInfo(arguments[2], gameList.get(ind - 1).gameID());
                    server.joinPlayer(playerInfo, helper.authKey);

                    webSocketServer.connect(helper.authKey, playerInfo.gameID(), false, arguments[2]);
                    clientPOV = arguments[2];
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
                    var observeCheckResponse = validation.observeCheck(arguments, listLen);
                    if (observeCheckResponse != null) {
                        System.out.println(observeCheckResponse);
                        return true;
                    }
                    int ind = Integer.parseInt(arguments[1]);
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
                            'highlight <CHESS PIECE POSITION>' - see current possible moves to make as displayed on the board
                            """);
                    break;
                case "redraw":
                    var checkRedrawArgs = validation.checkRedraw(arguments);
                    if (checkRedrawArgs != null) {System.out.println(checkRedrawArgs); break;}
                       drawBoard(gameBoardObject, pov, new ArrayList<>(), new ChessPosition(0, 0));
                    break;
                case "make_move":
                    if (!activeGame) {
                        System.out.println("The game is over dummy;)");
                        break;
                    }

                    var checkMoveArgs = validation.checkMakeMove(arguments);
                    if (checkMoveArgs != null) {System.out.println(checkMoveArgs); break;}

                    var startPos = new ChessPosition(Integer.parseInt(arguments[1].substring(1,2)), columnTranslator(arguments[1].substring(0,1)));
                    var endPos = new ChessPosition(Integer.parseInt(arguments[2].substring(1,2)), columnTranslator(arguments[2].substring(0,1)));

                    var move = new ChessMove(startPos, endPos, null);
                    if (checkPromotion(move, pov)) {
                        System.out.println("Looks like you're about to promote! Which piece do you want to promote to?" +
                                "\n'queen' | 'bishop' | 'rook' | 'knight'");
                        Scanner scanner2 = new Scanner(System.in);
                        String line2 = scanner.nextLine();
                        var promoArguments = line.split(" ");

                        if (promoArguments[0].equalsIgnoreCase("queen") || promoArguments[0].equalsIgnoreCase("bishop")
                                || promoArguments[0].equalsIgnoreCase("rook") || promoArguments[0].equalsIgnoreCase("knight")) {
                            System.out.println("Not one of the options. Invalid move. You suck.");
                            break;
                        }
                    }

                    try {
                        webSocketServer.makeMove(helper.authKey, gameID, false, pov, move);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case "leave":
                    var checkLeaveArgs = validation.checkLeave(arguments);
                    if (checkLeaveArgs != null) {System.out.println(checkLeaveArgs); break;}

                    try {
                        webSocketServer.leave(helper.authKey, gameID, observerStatus, pov);
                        clientPOV = "WHITE";
                        return;
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    return;
                case "resign":
                    var checkResignArgs = validation.checkResign(arguments);
                    if (checkResignArgs != null) {System.out.println(checkResignArgs); break;}

                    System.out.println("Do you really want to resign?👀 ('y'/'n') >>> ");
                    line = scanner.nextLine();

                    if (line.equals("y")) {
                        try {
                            webSocketServer.resign(helper.authKey, gameID, observerStatus, pov);
                            break;
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }

                    }

                    break;
                case "highlight":
                    var checkHighlightResponse = validation.checkHighlight(arguments);
                    if (checkHighlightResponse != null) {System.out.println(checkHighlightResponse); break;}

                    ChessPosition highlightPos = new ChessPosition(Integer.parseInt(arguments[1].substring(1,2)),
                            columnTranslator(arguments[1].substring(0,1)));

                    if (gameBoardGame.getBoard().getPiece(highlightPos) == null) {
                        System.out.println("There's no piece there silly!");
                        break;
                    }

                    var movesList = gameBoardGame.validMoves(highlightPos);

                    drawBoard(gameBoardObject, pov, movesList, highlightPos);
                    break;
                default:
                    System.out.println("Not a valid command. Please type 'help' for options");
                    return;
            }
        }
    }

    public static boolean checkPromotion(ChessMove move, String pov) {
        var startPos = move.getStartPosition();
        var endPos = move.getEndPosition();

        if (pov.equals("WHITE") && gameBoardObject.getPiece(startPos).getPieceType() == ChessPiece.PieceType.PAWN && endPos.getRow() == 8) {
            return true;
        } else if (pov.equals("BLACK") && gameBoardObject.getPiece(startPos).getPieceType() == ChessPiece.PieceType.PAWN && endPos.getRow() == 1) {
            return true;
        }
        return false;
    }

    private static int columnTranslator(String letter) {
        switch (letter) {
            case "a":
                return 1;
            case "b":
                return 2;
            case "c":
                return 3;
            case "d":
                return 4;
            case "e":
                return 5;
            case "f":
                return 6;
            case "g":
                return 7;
            case "h":
                return 8;
            default:
                return -1;
        }
    }

    private static void drawBoard(ChessBoard board, String pov, Collection<ChessMove> moves, ChessPosition currPos) {

        ArrayList<String> rows = new ArrayList<>(
                List.of(" 1 ", " 2 ", " 3 ", " 4 ", " 5 ", " 6 ", " 7 ", " 8 ")
        );

        boolean povBlack = pov.equals("BLACK");
        String columns = "    a  b  c  d  e  f  g  h    ";
        if (povBlack) {
            columns = new StringBuilder(columns).reverse().toString();
            printBoardForBlack(board, columns, rows, moves, currPos);
        } else {
            printBoardForWhite(board, columns, rows, moves, currPos);
        }
    }

    private static void printBoardForWhite(ChessBoard board, String columns, ArrayList<String> rows,
                                           Collection<ChessMove> moves, ChessPosition currPos) {
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
                line += colorHelper(new ChessPosition(9 - i, j), board, moves, currPos);
            }
            System.out.println(line + RESET);
        }
    }

    private static void printBoardForBlack(ChessBoard board, String columns, ArrayList<String> rows,
                                           Collection<ChessMove> moves, ChessPosition currPos) {
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
                line += colorHelper(new ChessPosition(9 - i, j), board, moves, currPos);
            }
            System.out.println(line + RESET);
        }
    }

    private static String colorHelper(ChessPosition position, ChessBoard board, Collection<ChessMove> moves, ChessPosition startPos) {

        String color = "\u001b[";
        var piece = board.getPiece(position);
        String foregroundColor = "34;";
        String pieceType = " ";
        if (piece != null) {
            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                foregroundColor = "31;";
            }
            if (position.equals(startPos) || moveContains(moves, position)) {
                foregroundColor = "30;";
            }
            pieceType = convertPieceType(piece.getPieceType());
        }

        String backgroundColor = "107;";
        if (moveContains(moves, position)) {
            backgroundColor = "102;";
        }

        if ((position.getRow() % 2 == 0 && position.getColumn() % 2 == 0) ||
                (position.getRow() % 2 == 1 && position.getColumn() % 2 == 1)) {

            backgroundColor = "40;";

            if (moveContains(moves, position)) {
                backgroundColor = "42;";
            }
        }
        if (position.equals(startPos)) {
            backgroundColor = "103;";
        }
        return color + foregroundColor + backgroundColor + "1m" + " " + pieceType + " ";
    }

    private static boolean moveContains(Collection<ChessMove> moves, ChessPosition chessPos) {
        for (var move : moves) {
            if (move.getEndPosition().equals(chessPos)) {
                return true;
            }
        }
        return false;
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
}