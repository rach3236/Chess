package dataaccess;

import java.sql.*;
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
