package services;

import java.sql.SQLException;

import dataAccessObject.clearDAO;
import dataAccessObject.database;
import dataAccessObject.databaseException;
import dataAccessObject.eventDAO;
import dataAccessObject.personDAO;
import dataAccessObject.userDAO;
import model.event;
import model.person;
import model.user;
import requests.loadRequest;
import results.clearResult;
import results.loadResult;

/**
 * Created by Admin on 2/14/17.
 * Description: Clears all data from the database (just like the /clear API), and then loads the
 posted user, person, and event data into the database.
 */

public class loadService {

    private database db = null;
    private boolean original = true;

    public loadService(database db) {
        this.db = db;
        original = false;
    }

    public loadService() throws databaseException {
        db = new database();
        db.openConnection();
        original = true;
    }


    /**
     *
     *  for each element of each array,
            create object,
            call Correct DAO,
        create loadResult object,
     * @param r
     * @return
     */
    public loadResult load(loadRequest r) {
        userDAO usrDAO = new userDAO(db);
        personDAO prsnDAO = new personDAO(db);
        eventDAO evntDAO = new eventDAO(db);
        clearDAO clrDAO = new clearDAO(db);
        clearResult clrResult = new clearResult();
        loadResult loadReslt = new loadResult(r.getUsers().length,r.getPersons().length,r.getEvents().length);

        try {
            if(clrDAO.clear()) {

                for (user usr : r.getUsers()) {
                    usrDAO.add(usr);
                }
                for (person prsn : r.getPersons()) {
                    prsnDAO.add(prsn);
                }
                for (event evnt : r.getEvents()) {
                    evntDAO.add(evnt);
                }

                return loadReslt;
            }
        }
        catch(SQLException e)
        {
            loadReslt.setMessage(e.getMessage());
            return loadReslt;
        } finally {
                try {
                    db.closeConnection(true);
                } catch (databaseException e) {
                    e.printStackTrace();
                }
        }
        return null;
    }
}


