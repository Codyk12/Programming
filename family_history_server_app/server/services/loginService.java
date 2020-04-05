package services;

import java.sql.SQLException;

import dataAccessObject.authTokenDAO;
import dataAccessObject.database;
import dataAccessObject.databaseException;
import dataAccessObject.personDAO;
import dataAccessObject.userDAO;
import model.authToken;
import model.person;
import model.user;
import requests.loginRequest;
import results.loginResult;

/**
 * Created by Admin on 2/13/17.
 * Description: Logs in the user and returns an auth token
 */

public class loginService {

    private database db = null;
    private boolean original;

    public loginService(database db)
    {
        this.db = db;
        original = false;
    }

    public loginService() throws databaseException {
        db = new database();
        db.openConnection();
        original = true;
    }

    /**
     *   if (user DAO loginQuery) to get user object,
         make model authToken,
         call authTokenDAO,
         make loginResult,
     * @param r
     * @return loginRequest
     */
    public loginResult login(loginRequest r) {
        userDAO usrDAO = new userDAO(db);
        authTokenDAO tokenDAO = new authTokenDAO(db);
        authToken token = new authToken();
        String userName = r.getUserName();
        String password = r.getPassword();
        loginResult logResult = new loginResult();

        user usr = null;
        try {

            usr = usrDAO.loginQuery(userName, password);

            if(usr != null)
            {


               token = tokenDAO.add(usr, token);
                if(token != null) {
                    logResult = new loginResult(token.getToken(), usr.getUserName(), usr.getPersonID());
                    return logResult;
                }
                else
                {
                    logResult.setMessage("User Not found in database.");
                    return logResult;
                }
            }
            else
            {
                logResult.setMessage("User Not found in database.");
                return logResult;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            if(original)
                try {
                    db.closeConnection(true);
                } catch (databaseException e) {
                    e.printStackTrace();
                }
        }
        return null;
    }
}
