package dataaccess;

import com.google.gson.Gson;
import datamodel.GameData;
import datamodel.UserData;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
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

    //TO DO: maybe change password database type

    private static final String[] createUserData = {
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

    private static final String[] createGameData = {
            """
            CREATE TABLE IF NOT EXISTS  GameData (
              `gameID` int NOT NULL AUTO_INCREMENT,
              `whiteUserDataID` INT NOT NULL,
              `blackUserDataID` INT NOT NULL,
              `gameName` varchar(256) NOT NULL,
              PRIMARY KEY (`gameID`),
              INDEX(gameName)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
            """
    };

    private static final String[] createAuthData = {
            """
            CREATE TABLE IF NOT EXISTS  AuthData (
              `authDataID` int NOT NULL AUTO_INCREMENT,
              `authToken` varchar(256) NOT NULL,
              `userDataID` INT NOT NULL,
              PRIMARY KEY (`authDataID`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
            """
    };

    // TO DO: add table creation scripts
    static public void createDatabase() throws DataAccessException, SQLException {
        var statement = "CREATE DATABASE IF NOT EXISTS " + databaseName;
        try (var conn = DriverManager.getConnection(connectionUrl, dbUsername, dbPassword);
             var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.executeUpdate();

            var preparedStatement1 = conn.prepareStatement("USE " + databaseName + ";");
                preparedStatement1.executeUpdate();

                //TO DO: clean up errors, convert database to call sql commands

            for (String tableScript : createUserData) {
                try {
                    var preparedStatement2 = conn.prepareStatement(tableScript);
                    preparedStatement2.executeUpdate();
                } catch (SQLException ex) {
                    throw new DataAccessException("failed to create user data table", ex);
                }
            }

            for (String tableScript : createGameData) {
                try {
                    var preparedStatement2 = conn.prepareStatement(tableScript);
                    preparedStatement2.executeUpdate();
                } catch (SQLException ex) {
                    throw new DataAccessException("failed to create game data table", ex);
                }
            }

            for (String tableScript : createAuthData) {
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


    public static void ExecuteSQLCommand(String command) {

        try (var conn = DriverManager.getConnection(connectionUrl, dbUsername, dbPassword);
             var preparedStatement = conn.prepareStatement(command)) {
            preparedStatement.executeUpdate();
        } catch (Exception ex) {

        }
    }

    public static int getUserID(String command) {

        try (var conn = DriverManager.getConnection(connectionUrl, dbUsername, dbPassword);
             var preparedStatement = conn.prepareStatement(command)) {
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("userDataID");
                }
            }
        } catch (Exception ex) {
        }
        return -1;
    }


    public static UserData getUserInfo(String username) throws Exception {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM UserData WHERE username=?;";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new Exception("wrong");
//            throw new ResponseException(ResponseException.Code.ServerError, String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    public static String getUsernameFromAuth(String auth) throws Exception {
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
            throw new Exception("wrong");
//            throw new ResponseException(ResponseException.Code.ServerError, String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    public static void deleteAuthData(String auth) {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "DELETE FROM AuthData WHERE authToken=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, auth);
            }
        } catch (Exception e) {
            //TO DO
        }
    }

    public static boolean isAuthorized(String auth) throws Exception {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM UserData WHERE authToken=?;";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, auth);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            throw new Exception("wrong"); // TO DO
//            throw new ResponseException(ResponseException.Code.ServerError, String.format("Unable to read data: %s", e.getMessage()));
        }
        return false;
    }

    public static int addGameData(String name) throws Exception {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO GameData (gameName) OUTPUT INSERTED.gameID VALUES (?)";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, name);
                ResultSet rs = ps.executeQuery();
                return rs.getInt("gameID");
            } catch (Exception e) {

            }
        } catch (Exception e) {
            throw new Exception("wrong");
//            throw new ResponseException(ResponseException.Code.ServerError, String.format("Unable to read data: %s", e.getMessage()));
        }
        return -1;
    }

    public static GameData getGameInfo(int id) throws Exception {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT " +
                    "gameData.gameID, " +
                    "gameData.gameName, " +
                    "whiteUser.Username AS whiteUsername, " +
                    "blackUser.Username AS blackUsername, " +
                    "FROM " +
                    "    GameData " +
                    "JOIN " +
                    "    UserData AS whiteUser ON GameData.whiteUserDataID = whiteUser.UserDataID " +
                    "JOIN " +
                    "    UserData AS blackUser ON GameData.blackUserDataID = blackUser.UserDataID " +
                    "WHERE gameID=?;";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new Exception("wrong");
//            throw new ResponseException(ResponseException.Code.ServerError, String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    public static GameData[] getAllGameInfo() throws Exception {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT " +
                    "gameData.gameID, " +
                    "gameData.gameName, " +
                    "whiteUser.Username AS whiteUsername, " +
                    "blackUser.Username AS blackUsername, " +
                    "FROM " +
                    "    GameData " +
                    "JOIN " +
                    "    UserData AS whiteUser ON GameData.whiteUserDataID = whiteUser.UserDataID " +
                    "JOIN " +
                    "    UserData AS blackUser ON GameData.blackUserDataID = blackUser.UserDataID;";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    ArrayList<GameData> gameData = new ArrayList<GameData>();
                    while (rs.next()) {
                        gameData.add(readGame(rs));
                    }
                    //TO DO?
                    return (GameData[]) gameData.toArray();
                } catch (Exception e) {
                    //TO DO
                }
            }
        } catch (Exception e) {
            throw new Exception("wrong");
//            throw new ResponseException(ResponseException.Code.ServerError, String.format("Unable to read data: %s", e.getMessage()));
        }
        return new GameData[0];
    }


    private static UserData readUser(ResultSet rs) throws SQLException {
        var username = rs.getString("username");
        var password = rs.getString("password");
        var email = rs.getString("email");
        return new UserData(username, password, email);
    }

    private static GameData readGame(ResultSet rs) throws SQLException {
        var gameID = rs.getInt("gameID");
        var whiteUsername = rs.getString("whiteUsername");
        var blackUsername = rs.getString("blackUsername");
        var gameName = rs.getString("gameName");
        return new GameData(gameID, whiteUsername, blackUsername, gameName);
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
