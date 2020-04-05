package dataAccessObject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import model.user;

/**
 * Created by Admin on 2/13/17.
 * Creates a new user account, generates 4 generations of ancestor data for the new user, logs the user in,
 * and returns an auth token.
 */

public class userDAO {

    database db;

    public userDAO(database db) {
        this.db = db;
    }

    /**
     * Adds the user object to the database
     *
     * @param usr
     * @return added user object or null if failure
     */
    public user add(user usr) throws SQLException {
        //Insert Statments
        System.out.println(usr.getUserName());
        PreparedStatement stmt = null;
        try {
            String sql = "INSERT INTO user (username, password, email, first_name, last_name, gender, person_id, history_info)  \n" +
                    "VALUES (?,?,?,?,?,?,?,?);";
            stmt = db.getConn().prepareStatement(sql);
            stmt.setString(1, usr.getUserName());
            stmt.setString(2, usr.getPassword());
            stmt.setString(3, usr.getEmail());
            stmt.setString(4, usr.getFirstName());
            stmt.setString(5, usr.getLastName());
            stmt.setString(6, usr.getGender());
            stmt.setString(7, usr.getPersonID());
            stmt.setString(8, usr.getHistoryInfo());


            // OK
            if (stmt.executeUpdate() == 1)
            {
                return usr;
            }

// ERROR
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
// ERROR
        }
        finally {
            if (stmt != null) stmt.close();
        }

        return null;
    }

    /**
     * querys the database for the username and password combination
     *
     * @param username
     * @param password
     * @return found user object or null
     */
    public user loginQuery(String username, String password) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        user usr = null;

        try {
            String sql = "SELECT * FROM user WHERE username = ? AND password = ?";
            stmt = db.getConn().prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);

            rs = stmt.executeQuery();

            while (rs.next()) {
                usr = new user(rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("gender"),
                        rs.getString("history_info"));
                usr.setPersonID(rs.getString("person_id"));
            }

            return usr;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            if (stmt != null) stmt.close();
            if (rs != null) rs.close();
        }
        return null;
    }

    /**
     * querys the database for the username and password combination
     *
     * @param username
     * @return found user object or null
     */
    public user userQuery(String username) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        user usr = null;

        try {
            String sql = "SELECT * FROM user WHERE username=?";
            stmt = db.getConn().prepareStatement(sql);
            stmt.setString(1, username);
            rs = stmt.executeQuery();

            while (rs.next()) {
                usr = new user(rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("gender"),
                        rs.getString("history_info"));
                usr.setPersonID(rs.getString("person_id"));
            }

            return usr;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            if (stmt != null) stmt.close();
            if (rs != null) rs.close();
        }

        return null;
    }
}

