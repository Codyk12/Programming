package dataAccessObject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import model.authToken;
import model.event;
import model.user;

/**
 * Created by Admin on 2/13/17.
 */

public class authTokenDAO {

    database db;

    public authTokenDAO(database db) {
        this.db = db;
    }

    /**
     * adds authtoken to database
     * @param token
     * @return returned authtoken
     */
    public authToken add(user usr, authToken token) throws SQLException {
        PreparedStatement stmt = null;
        try {
            String sql = "INSERT INTO authToken (user_name, token, person_id, time_created)  \n" +
                    "VALUES (?,?,?,?);";
            stmt = db.getConn().prepareStatement(sql);
            stmt.setString(1, usr.getUserName());
            stmt.setString(2, token.getToken());
            stmt.setString(3, usr.getPersonID());
            stmt.setDouble(4, token.getTime_created());


            // OK
            if (stmt.executeUpdate() == 1)
            {
                return token;
            }

// ERROR
        }
        catch (SQLException e) {
            e.printStackTrace();
// ERROR
        }
        finally {
            if (stmt != null) stmt.close();
        }

        return null;
    }

    /**
     * gets username from authtoken from database
     * @param token
     * @return
     */
    public String queryUserName(String token) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT * FROM authToken WHERE token=?";
            stmt = db.getConn().prepareStatement(sql);
            stmt.setString(1, token);
            rs = stmt.executeQuery();

            while (rs.next()) {
                return rs.getString("user_name");
            }

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
     * gets authtoken from username from database
     * @param userName
     * @return
     */
    public String queryToken(String userName) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        event evnt = null;

        try {
            String sql = "SELECT * FROM authToken WHERE user_name=?";
            stmt = db.getConn().prepareStatement(sql);
            stmt.setString(1, userName);
            rs = stmt.executeQuery();

            while (rs.next()) {
                return rs.getString("token");
            }

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
     * gets authtoken from username from database
     * @param userName
     * @return
     */
    public Double queryTime(String column, String userName) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        event evnt = null;

        try {
            String sql = "SELECT * FROM authToken WHERE "+column+"=?";
            stmt = db.getConn().prepareStatement(sql);
            stmt.setString(1, userName);
            rs = stmt.executeQuery();

            while (rs.next()) {
                return rs.getDouble("time_created");
            }

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
