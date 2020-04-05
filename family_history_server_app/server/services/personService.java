package services;

import java.sql.SQLException;
import java.util.ArrayList;

import dataAccessObject.authTokenDAO;
import dataAccessObject.database;
import dataAccessObject.databaseException;
import dataAccessObject.personDAO;
import dataAccessObject.userDAO;
import model.person;
import requests.personRequest;
import results.personsResult;

/**
 * Created by Admin on 3/8/17.
 */

public class personService {

    private database db = null;
    private boolean original;

    public personService(database db) {
        this.db = db;
        original = false;
    }

    public personService() throws databaseException {
        db = new database();
        db.openConnection();
        original = true;

    }


    public personsResult getPersons(personRequest request) {
        personDAO prsnDAO = new personDAO(db);
        authTokenDAO tokenDAO = new authTokenDAO(db);
        userDAO usrDAO = new userDAO(db);

        String auth = request.getAuthToken();
        String personID = request.getPersonID();
        personsResult prsnsResult = new personsResult();
        ArrayList<person> persons = new ArrayList<person>();

        try {

            String userName = tokenDAO.queryUserName(request.getAuthToken());
            if(original) {
                if(tokenDAO.queryTime("user_name", userName) != null)
                {
                    if ((tokenDAO.queryTime("user_name", userName) - System.currentTimeMillis()) > 3600000
                            || (tokenDAO.queryToken(userName).equals(""))) {
                        prsnsResult.setMessage("Authtoken Invaild.");
                        return prsnsResult;
                    }
                }
            }


                if (personID.equals("")) {
                    userName = tokenDAO.queryUserName(auth);
                    persons = prsnDAO.queryAll(userName);
                    prsnsResult.setPersons(persons);
                    return prsnsResult;
                } else {
                    if(tokenDAO.queryUserName(request.getAuthToken()) != null) {
                        userName = tokenDAO.queryUserName(request.getAuthToken());
                        person prsn = prsnDAO.query(request.getPersonID());

                        if(prsn != null)
                        {
                            if(!prsn.getDescendant().equals(userName))
                            {
                                prsnsResult.setMessage("Authtoken Invaild.");
                                return prsnsResult;
                            }
                            prsn = prsnDAO.query(personID);
                            persons.add(prsn);
                            prsnsResult.setPersons(persons);
                            return prsnsResult;
                        }

                    }
                }
        }
        catch(SQLException e)
            {
                prsnsResult.setMessage(e.getMessage());
            }
        finally
            {
                try {
                    if (original)
                        db.closeConnection(true);
                } catch (databaseException e) {
                    e.printStackTrace();
                }
            }
        return null;
    }
}
