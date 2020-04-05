package dataAccessObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Admin on 2/16/17.
 *
 *
 */

public class database {

    private Connection conn;
    private boolean isConnected;

   static {
        try {
            final String driver = "org.sqlite.JDBC";
            Class.forName(driver);
        }
        catch(ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return isConnected;
    }

    public Connection getConn() {
        return conn;
    }

    /**
     * Opens connection to database
     * @throws databaseException
     */
    public void openConnection()  throws databaseException
    {
        try {

            final String CONNECTION_URL = "jdbc:sqlite:database.sqlite";

            // Open a database connection
            conn = DriverManager.getConnection(CONNECTION_URL);

            // Start a transaction
            conn.setAutoCommit(false);
            isConnected = true;
        }
        catch (SQLException e) {
            throw new databaseException("openConnection failed", e);
        }
    }

    /**
     * closes database commits or rollsback
     * @param commit - tells us to roolback database or commit changes
     * @throws databaseException
     */
    public void closeConnection(boolean commit)  throws databaseException
    {
        try {
            if (commit) {
                conn.commit();
            }
            else {
                conn.rollback();
            }

            conn.close();
            conn = null;
        }
        catch (SQLException e) {
            throw new databaseException("closeConnection failed", e);
        }
    }

    /**
     * creates tables in the database
     * @throws databaseException
     */
    public void createTables() throws databaseException {
        try {
            Statement stmt = null;
            try {
                stmt = conn.createStatement();
                stmt.executeUpdate(
                        "create table IF NOT EXISTS user\n" +
                                "(\n" +
                                "id integer primary key autoincrement,\n" +
                                "username varchar(255) not null,\n" +
                                "password varchar(255) not null,\n" +
                                "email varchar(255) not null,\n" +
                                "first_name varchar(255) not null,\n" +
                                "last_name varchar(255) not null,\n" +
                                "gender varchar(255) not null,\n" +
                                "person_id varchar(255) not null,\n" +
                                "history_info varchar(255) \n" +
                                ");");
                stmt.executeUpdate(
                        "create table IF NOT EXISTS person\n" +
                                "(\n" +
                                "id varchar(255) primary key,\n" +
                                "descendant varchar(255) not null,\n" +
                                "first_name varchar(255) not null,\n" +
                                "last_name varchar(255) not null,\n" +
                                "gender varchar(255) not null,\n" +
                                "father varchar(255),\n" +
                                "mother varchar(255),\n" +
                                "spouse varchar(255)\n" +
                                ");");

                stmt.executeUpdate(
                        "create table IF NOT EXISTS event\n" +
                                "(\n" +
                                "id varchar(255) primary key not null,\n" +
                                "personID varchar(255) not null,\n" +
                                "descendant varchar(255) not null,\n" +
                                "latitude REAL not null,\n" +
                                "longitude REAL not null,\n" +
                                "country varchar(255) not null,\n" +
                                "city varchar(255) not null,\n" +
                                "type varchar(255)  not null,\n" +
                                "year int  not null\n" +
                                ");");

                stmt.executeUpdate(
                        "create table IF NOT EXISTS authToken\n" +
                                "(\n" +
                                "user_name text not null,\n" +
                                "token varchar(255) not null,\n" +
                                "person_id varchar(255) not null,\n" +
                                "time_created REAL not null\n" +
                                ");");
            }
            finally {
                if (stmt != null) {
                    stmt.close();
                    stmt = null;
                }
            }
        }
        catch (SQLException e) {
            throw new databaseException("createTables failed", e);
        }
    }
}


