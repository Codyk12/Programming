package dataAccessObject;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Admin on 2/14/17.
 */

public class clearDAO {

    database db;

    public clearDAO(database db) {
        this.db = db;
    }

    /**
     * Clearns all tables in database
     * @return true if success
     */
    public boolean clear() throws SQLException {
        PreparedStatement stmt = null;
        PreparedStatement stmt2 = null;
        PreparedStatement stmt3 = null;
        PreparedStatement stmt4 = null;

        try {
            String sql = "DELETE FROM user;";
            stmt = db.getConn().prepareStatement(sql);
            stmt.executeUpdate();
            if(stmt != null)
                stmt.close();

            sql = "DELETE FROM person;";
            stmt2 = db.getConn().prepareStatement(sql);
            stmt2.executeUpdate();
            if(stmt2 != null)
                stmt2.close();

            sql = "DELETE FROM event;";
            stmt3 = db.getConn().prepareStatement(sql);
            stmt3.executeUpdate();
            if(stmt3 != null)
                stmt3.close();


            sql = "DELETE FROM authToken;";
            stmt4 = db.getConn().prepareStatement(sql);
            stmt4.executeUpdate();
            if(stmt4 != null)
                stmt4.close();

                return true;
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            return false;
        }
        finally {
            if (stmt != null) stmt.close();
            if (stmt2 != null) stmt2.close();
            if (stmt3 != null) stmt3.close();
            if (stmt4 != null) stmt4.close();
        }
    }
}
