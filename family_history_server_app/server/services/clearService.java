package services;

import java.sql.SQLException;

import dataAccessObject.authTokenDAO;
import dataAccessObject.clearDAO;
import dataAccessObject.database;
import dataAccessObject.databaseException;
import requests.clearRequest;
import results.clearResult;

/**
 * Created by Admin on 2/13/17.
 * Deletes ALL data from the database, including user accounts, auth tokens, and
 generated person and event data.
 */

public class clearService {

    private database db = null;
    private boolean original;

    public clearService(database db) {
        this.db = db;
        original = false;
    }

    public clearService() throws databaseException {
        db = new database();
        db.openConnection();
        original = true;
    }

    /**
     * Clears all tables in the database,
     * Calls clear DAO,
     * @param r
     * @return
     */
    public clearResult clear(clearRequest r)
    {
        clearDAO clrDAO = new clearDAO(db);
        clearResult clrResult = new clearResult();

        authTokenDAO tokenDAO = new authTokenDAO(db);

        try
        {
            if(original) {
                if(tokenDAO.queryUserName(r.getToken()) != null) {
                    if (!tokenDAO.queryUserName(r.getToken()).equals("")) {
                        if ((tokenDAO.queryTime("user_name", tokenDAO.queryUserName(r.getToken())) - System.currentTimeMillis()) > 3600000) {
                            clrResult.setMessage("Authtoken Invaild.");
                            return clrResult;
                        }
                    } else {
                        clrResult.setMessage("Authtoken doesnt exist");
                        return clrResult;
                    }
                }

            }
            if(clrDAO.clear())
                clrResult.setMessage("Clear Sucessful");
                return clrResult;

        } catch (SQLException e)
        {
            clrResult.setMessage(e.getMessage());
            return clrResult;
        }
        finally {
            try {
                if(original)
                    db.closeConnection(true);
            } catch (databaseException e) {
                e.printStackTrace();
            }
        }
    }
}
