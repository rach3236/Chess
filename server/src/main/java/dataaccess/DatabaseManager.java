package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import datamodel.GameData;
import datamodel.UserData;

import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

public class DatabaseManager {
    private static String databaseName;
    private static String dbUsername;
    private static String dbPassword;
    private static String connectionUrl;

    /*
     * Load the database information for the db.properties file.
     */
    static {
        loadPropertiesFromResources();
    }

    /**
     * Creates the database if it does not already exist.
     */

    private static final String[] CREATE_USER_DATA = {
     """
     CREATE TABLE IF NOT EXISTS  UserData (
     `userDataID` int NOT NULL AUTO_INCREMENT,
     `username` varchar(256) NOT NULL,
     `password` varchar(256) NOT NULL,
     `email` varchar(256) NOT NULL,
     PRIMARY KEY (`userDataID`),
     INDEX(username)
     ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
     """
     };

    private static final String[] CREATE_GAME_DATA = {
            """
            CREATE TABLE IF NOT EXISTS  GameData (
              `gameID` int NOT NULL AUTO_INCREMENT,
              `whiteUserDataID` INT NULL,
              `blackUserDataID` INT NULL,
              `gameName` varchar(256) NOT NULL,
              `json` TEXT DEFAULT NULL,
              PRIMARY KEY (`gameID`),
              FOREIGN KEY (whiteUserDataID)
              REFERENCES UserData(userDataID),
              FOREIGN KEY (blackUserDataID)
              REFERENCES UserData(userDataID),
              INDEX(gameName)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
            """
    };

    private static final String[] CREATE_AUTH_DATA = {
            """
            CREATE TABLE IF NOT EXISTS  AuthData (
              `authDataID` int NOT NULL AUTO_INCREMENT,
              `authToken` varchar(256) NOT NULL,
              `userDataID` INT NOT NULL,
              PRIMARY KEY (`authDataID`),
              FOREIGN KEY (userDataID)
              REFERENCES UserData(userDataID)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
            """
    };

    static public void createDatabase() throws DataAccessException {
        var statement = "CREATE DATABASE IF NOT EXISTS " + databaseName;
        try (var conn = DriverManager.getConnection(connectionUrl, dbUsername, dbPassword);
             var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.executeUpdate();

            var preparedStatement1 = conn.prepareStatement("USE " + databaseName + ";");
                preparedStatement1.executeUpdate();

            for (String tableScript : CREATE_USER_DATA) {
                try {
                    var preparedStatement2 = conn.prepareStatement(tableScript);
                    preparedStatement2.executeUpdate();
                } catch (SQLException ex) {
                    throw new DataAccessException("failed to create user data table", ex);
                }
            }

            for (String tableScript : CREATE_GAME_DATA) {
                try {
                    var preparedStatement2 = conn.prepareStatement(tableScript);
                    preparedStatement2.executeUpdate();
                } catch (SQLException ex) {
                    throw new DataAccessException("failed to create game data table", ex);
                }
            }

            for (String tableScript : CREATE_AUTH_DATA) {
                try {
                    var preparedStatement2 = conn.prepareStatement(tableScript);
                    preparedStatement2.executeUpdate();
                } catch (SQLException ex) {
                    throw new DataAccessException("failed to create authorization data table", ex);
                }
            }

        } catch (SQLException ex) {
            throw new DataAccessException("failed to create database", ex);
        }
    }

    public static void delete() {
        try (Connection conn = DatabaseManager.getConnection()) {
            String command2 = "DELETE FROM AuthData";
            try (PreparedStatement ps = conn.prepareStatement(command2)) {
                ps.executeUpdate();
            } catch (Exception e) {
                throw new InternalServerException("Error while deleting auth data", e);
            }
            String command3 = "DELETE FROM GameData";
            try (PreparedStatement ps = conn.prepareStatement(command3)) {
                ps.executeUpdate();
            } catch (Exception e) {
                throw new InternalServerException("Error while deleting game data", e);
            }
            String command1 = "DELETE FROM UserData;";
            try (PreparedStatement ps = conn.prepareStatement(command1)) {
                ps.executeUpdate();
            } catch (Exception e) {
                throw new InternalServerException("Error while deleting user data", e);
            }
        } catch (Exception ex) {
            throw new InternalServerException("Error while deleting data", ex);
        }
    }

    public static void addUser(String username, String password, String email) {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO UserData (username, password, email) VALUES (?, ?, ?);";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                ps.setString(2, password);
                ps.setString(3, email);
                ps.executeUpdate();
            } catch (Exception e) {
                throw new InternalServerException("Error while adding user data", e);
            }
        } catch (Exception e) {
            throw new InternalServerException("Error getting a database connection", e);
        }
    }

    public static void addSession(String auth, String username) {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO AuthData (authToken, userDataID) VALUES " +
                    "(?, (SELECT userDataID FROM UserData WHERE username=?))";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, auth);
                ps.setString(2, username);
                ps.executeUpdate();
            } catch (Exception e) {
                throw new InternalServerException("Error while adding game data", e);
            }
        } catch (Exception e) {
            throw new InternalServerException("Error getting database connection", e);
        }
    }

    public static boolean userExists(String username){
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM UserData WHERE username=?;";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return true;
                    }
                } catch (Exception e) {
                    throw new InternalServerException("Error with inputted username", e);
                }
            }
        } catch (Exception e) {
            throw new InternalServerException("Error with the database connection", e);
        }
        return false;
    }

    public static UserData getUser(String username) {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM UserData WHERE username=?;";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    }
                } catch (Exception e) {
                    throw new InternalServerException("Error finding user data", e);
                }
            }
        } catch (Exception e) {
            throw new InternalServerException("Error with database connection", e);
        }
        return null;
    }

    public static String getUsernameFromAuth(String auth) {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username FROM UserData WHERE userDataID=(SELECT userDataID FROM AuthData where authToken=?);";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, auth);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("username");
                    }
                }
            }
        } catch (Exception e) {
            throw new InternalServerException("Error with database connection", e);
        }
        return null;
    }

    public static void deleteAuthData(String auth) {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "DELETE FROM AuthData WHERE authToken=?;";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, auth);
                ps.executeUpdate();
            } catch (Exception e) {
                throw new InternalServerException("Error deleting auth data", e);
            }
        } catch (Exception e) {
            throw new InternalServerException("Error with database connection", e);
        }
    }

    public static boolean isAuthorized(String auth) {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM AuthData WHERE authToken=?;";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, auth);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            throw new InternalServerException("Error with connection", e);
        }
        return false;
    }

    public static int addGameData(String name, ChessGame gameObject) {
        try (Connection conn = DatabaseManager.getConnection()) {
            var serializer = new Gson();
            var statement = "INSERT INTO GameData (gameName, whiteUserDataID, blackUserDataID, json) VALUES (?, null, null, ?);";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, name);
                ps.setString(2, serializer.toJson(gameObject));
                ps.executeUpdate();
            } catch (Exception e) {
                throw new InternalServerException("Error creating the game", e);
            }
            var statement2 = "SELECT LAST_INSERT_ID() AS gameID;";
            try (PreparedStatement ps = conn.prepareStatement(statement2)) {
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    return rs.getInt("gameID");
                }
            } catch (Exception e) {
                throw new InternalServerException("Error getting game ID", e);
            }
        } catch (Exception e) {
            throw new InternalServerException("Error with database connection", e);
        }
        return -1;
    }

    public static GameData getGameInfo(int id) {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT " +
                    "GameData.gameID, " +
                    "GameData.gameName, " +
                    "(SELECT Username FROM UserData WHERE userDataID=GameData.whiteUserDataID) AS whiteUsername, " +
                    "(SELECT Username FROM UserData WHERE userDataID=GameData.blackUserDataID) AS blackUsername, " +
                    "GameData.json " +
                    "FROM GameData " +
                    "WHERE gameID=?;";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                } catch (Exception e) {
                    throw new InternalServerException("Error reading game data", e);
                }
            }
        } catch (Exception e) {
            throw new InternalServerException("Error with database connection", e);
        }
        return null;
    }

    public static GameData[] getAllGameInfo() {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT " +
                    "GameData.gameID, " +
                    "GameData.gameName, " +
                    "(SELECT Username FROM UserData WHERE userDataID=GameData.whiteUserDataID) AS whiteUsername, " +
                    "(SELECT Username FROM UserData WHERE userDataID=GameData.blackUserDataID) AS blackUsername, " +
                    "GameData.json " +
                    "FROM GameData;";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    ArrayList<GameData> GameData = new ArrayList<GameData>();
                    while (rs.next()) {
                        GameData.add(readGame(rs));
                    }
                    return GameData.toArray(new GameData[0]);
                } catch (Exception e) {
                    throw new InternalServerException("Error listing games", e);
                }
            }
        } catch (Exception e) {
            throw new InternalServerException("Error with database connection", e);
        }
    }

    private static String addUserCheck(String username) {
        return username != null? "(SELECT userDataID FROM UserData WHERE Username='" + username + "')" : "Null";
    }

    public static void updateGameData(int gameID, String whiteUsername, String blackUsername, String gameName) {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "UPDATE GameData " +
                    "SET whiteUserDataID = " + addUserCheck(whiteUsername)  + " , " +
                    " blackUserDataID = " + addUserCheck(blackUsername) + " , " +
                    "gameName=? " +
                    "WHERE gameID=?;";

            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, gameName);
                ps.setInt(2, gameID);
                ps.executeUpdate();
            } catch (Exception e) {
                throw new InternalServerException("Error updating game data to join", e);
            }
        } catch (Exception e) {
            throw new InternalServerException("Error with database connection", e);
        }
    }

// READ DATA
    private static GameData readGame(ResultSet rs) throws SQLException {
        var serializer = new Gson();

        var gameID = rs.getInt("gameID");
        var whiteUsername = rs.getString("whiteUsername");
        var blackUsername = rs.getString("blackUsername");
        var gameName = rs.getString("gameName");
        var gameObject = serializer.fromJson(rs.getString("json"), ChessGame.class);
           return new GameData(gameID, whiteUsername, blackUsername, gameName, gameObject);
    }

    private static UserData readUser(ResultSet rs) throws SQLException {
        var username = rs.getString("username");
        var password = rs.getString("password");
        var email = rs.getString("email");
        return new UserData(username, password, email);
    }

    /**
     * Create a connection to the database and sets the catalog based upon the
     * properties specified in db.properties. Connections to the database should
     * be short-lived, and you must close the connection when you are done with it.
     * The easiest way to do that is with a try-with-resource block.
     * <br/>
     * <code>
     * try (var conn = DatabaseManager.getConnection()) {
     * // execute SQL statements.
     * }
     * </code>
     */
    static Connection getConnection() throws DataAccessException {
        try {
            //do not wrap the following line with a try-with-resources
            var conn = DriverManager.getConnection(connectionUrl, dbUsername, dbPassword);
            conn.setCatalog(databaseName);
            return conn;
        } catch (SQLException ex) {
            throw new DataAccessException("failed to get connection", ex);
        }
    }

    private static void loadPropertiesFromResources() {
        try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
            if (propStream == null) {
                throw new Exception("Unable to load db.properties");
            }
            Properties props = new Properties();
            props.load(propStream);
            loadProperties(props);
        } catch (Exception ex) {
            throw new RuntimeException("unable to process db.properties", ex);
        }
    }

    private static void loadProperties(Properties props) {
        databaseName = props.getProperty("db.name");
        dbUsername = props.getProperty("db.user");
        dbPassword = props.getProperty("db.password");

        var host = props.getProperty("db.host");
        var port = Integer.parseInt(props.getProperty("db.port"));
        connectionUrl = String.format("jdbc:mysql://%s:%d", host, port);
    }
}
