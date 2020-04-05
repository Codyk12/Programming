package dataAccessObject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import model.event;

/**
 * Created by Admin on 2/13/17.
 */

public class eventDAO {

    database db;

    public eventDAO(database db) {
        this.db = db;
    }

    /**
     * adds event object to the database
     * @param evnt
     * @return the inserted event object or null if failure
     */
    public event add(event evnt) throws SQLException {
        PreparedStatement stmt = null;
        try {
            String sql = "INSERT INTO event (id, personID, descendant, latitude, longitude, country, city, type, year)  \n" +
                    "VALUES (?,?,?,?,?,?,?,?,?);";
            stmt = db.getConn().prepareStatement(sql);
            stmt.setString(1, evnt.getEventID());
            stmt.setString(2, evnt.getPersonID());
            stmt.setString(3, evnt.getdescendant());
            stmt.setDouble(4, evnt.getLatitude());
            stmt.setDouble(5, evnt.getLongitude());
            stmt.setString(6, evnt.getCountry());
            stmt.setString(7, evnt.getCity());
            stmt.setString(8, evnt.getEventType());
            stmt.setString(9, evnt.getYear());


            // OK
            if (stmt.executeUpdate() == 1)
            {
                return evnt;
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
     * finds a specific event based on eventID
     * @param eventID
     * @return return event object or null if not found
     */
    public event query(String eventID) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        event evnt = null;

        try {
            String sql = "SELECT * FROM event WHERE id=?";
            stmt = db.getConn().prepareStatement(sql);
            stmt.setString(1, eventID);
            rs = stmt.executeQuery();

            while (rs.next()) {
                evnt= new event(rs.getString("id"),
                        rs.getString("personID"),
                        rs.getString("descendant"),
                        rs.getFloat("latitude"),
                        rs.getFloat("longitude"),
                        rs.getString("country"),
                        rs.getString("city"),
                        rs.getString("type"),
                        rs.getString("year"));
            }

            return evnt;

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
     *  gets all events for a person
     * @param personID
     * @return event arraylist if successful or null if failure
     */
    public ArrayList<event> queryAll(String personID) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        event evnt = null;
        ArrayList<event> events = new ArrayList<event>();

        try {
            String sql = "SELECT * FROM event WHERE descendant=?";
            stmt = db.getConn().prepareStatement(sql);
            stmt.setString(1, personID);
            rs = stmt.executeQuery();

            while (rs.next()) {
                evnt= new event(rs.getString("id"),
                        rs.getString("personID"),
                        rs.getString("descendant"),
                        rs.getFloat("latitude"),
                        rs.getFloat("longitude"),
                        rs.getString("country"),
                        rs.getString("city"),
                        rs.getString("type"),
                        rs.getString("year"));
                events.add(evnt);
            }

            return events;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            if (stmt != null) stmt.close();
            if (rs != null) rs.close();
        }
        return null;
    }

    public boolean clearUserEvents(String userName) throws SQLException {
        PreparedStatement stmt = null;
        try {
            String sql = "DELETE FROM event WHERE descendant = ?";
            stmt = db.getConn().prepareStatement(sql);
            stmt.setString(1, userName);

            if(stmt.executeUpdate() == 0)
            {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            if (stmt != null) stmt.close();
        }
        return false;
    }
}
