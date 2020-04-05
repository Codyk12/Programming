package dataAccessObject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import model.event;
import model.person;
import model.user;

/**
 * Created by Admin on 2/13/17.
 */

public class personDAO {
    database db;

    public personDAO(database db) {
        this.db = db;
    }

    /**
     * Adds the person object information to the database
     * @param prsn
     * @return added person object or null if failure
     */
    public person add(person prsn) throws SQLException
    {
        eventDAO evntDAO = new eventDAO(db);
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = "INSERT INTO person (id, descendant, first_name, last_name, gender, father, mother, spouse)  \n" +
                    "VALUES (?,?,?,?,?,?,?,?);";
            stmt = db.getConn().prepareStatement(sql);
            stmt.setString(1, prsn.getPersonID());
            stmt.setString(2, prsn.getDescendant());
            stmt.setString(3, prsn.getFirstName());
            stmt.setString(4, prsn.getLastName());
            stmt.setString(5, prsn.getGender());
            stmt.setString(6, prsn.getFather());
            stmt.setString(7, prsn.getMother());
            stmt.setString(8, prsn.getSpouse());

            // OK
            if (stmt.executeUpdate() == 1)
            {
                ArrayList<event> evnts = prsn.getEvents();
                if(evnts != null) {
                    for (event evnt : prsn.getEvents()) {
                        evntDAO.add(evnt);
                    }
                }
                return prsn;
            }

// ERROR
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
// ERROR
        }
        finally {
            if (stmt != null) stmt.close();
            if (rs != null) rs.close();
        }

        return null;
    }

    /**
     * Finds person in the database
     * @param ID/ user or person depending on what to query
     * @return person object if found, null if not
     */
    public person query(String ID) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        person prsn = null;

        try {
            String sql = "SELECT * FROM person WHERE id=?";
            stmt = db.getConn().prepareStatement(sql);
            stmt.setString(1, ID);

            rs = stmt.executeQuery();

            while (rs.next()) {
                prsn = new person(rs.getString("id"),
                        rs.getString("descendant"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("gender"),
                        rs.getString("father"),
                        rs.getString("mother"),
                        rs.getString("spouse"));
            }

            return prsn;

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
     * gets all persons tired to specific user
     *  calls query all events in events DAO to fill person
     * @param userName
     * @return arraylist of all persons or null if no persons
     */
    public ArrayList<person> queryAll(String userName) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        person prsn = null;
        ArrayList<person> persons = new ArrayList<person>();

        try {
            String sql = "SELECT * FROM person WHERE descendant=?";
            stmt = db.getConn().prepareStatement(sql);
            stmt.setString(1, userName);
            rs = stmt.executeQuery();

            while (rs.next()) {
                prsn = new person(rs.getString("id"),
                        rs.getString("descendant"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("gender"),
                        rs.getString("father"),
                        rs.getString("mother"),
                        rs.getString("spouse"));
                persons.add(prsn);
            }

            return persons;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            if (stmt != null) stmt.close();
            if (rs != null) rs.close();
        }

        return null;
    }

    public boolean clearUserAncestors(String userName) throws SQLException {
        PreparedStatement stmt = null;
        try {
            String sql = "DELETE FROM person WHERE descendant=?";
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
