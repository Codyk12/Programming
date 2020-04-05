package services;

import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.sql.SQLException;

import dataAccessObject.clearDAO;
import dataAccessObject.database;
import dataAccessObject.databaseException;
import dataAccessObject.eventDAO;
import dataAccessObject.personDAO;
import dataAccessObject.userDAO;
import model.locations;
import model.menNames;
import model.person;
import model.sirNames;
import model.user;
import model.womanNames;
import requests.fillRequest;
import results.fillResult;

/**
 * Created by Admin on 2/13/17.
 * Populates the server's database with generated data for the specified user name.
 The required "username" parameter must be a user already registered with the server. If there is
 any data in the database already associated with the given user name, it is deleted. The
 optional “generations” parameter lets the caller specify the number of generations of ancestors
 to be generated, and must be a non-negative integer (the default is 4, which results in 31 new
 persons each with associated events).
 */

public class fillService {

    private database db = null;
    private boolean original = true;

    public fillService(database db) {
        this.db = db;
        original = false;
    }

    public fillService() throws databaseException {
        db = new database();
        db.openConnection();
    }

    /**
     *   query username in DAO,
         make person Model object from user returned DAO,
         person.generateAncestors(r.generationCount),
         for each person in person.ancestors,
         call person DAO add,
         make fill result,
     * @param r
     * @return fill result object
     */
    public fillResult fill(fillRequest r){
        userDAO usrDAO = new userDAO(db);
        personDAO prsnDAO = new personDAO(db);
        eventDAO evntDAO = new eventDAO(db);
        Gson gson = new Gson();
        Reader reader = null;
        fillResult result = new fillResult();

        try {

            user usr = usrDAO.userQuery(r.getUserName());
            if (usr != null) {
                person prsn = prsnDAO.query(usr.getPersonID());

                prsnDAO.clearUserAncestors(r.getUserName());
                evntDAO.clearUserEvents(r.getUserName());


                reader = new FileReader("json/locations.json");
                locations locData = gson.fromJson(reader, locations.class);

                reader = new FileReader("json/fnames.json");
                womanNames wNames = gson.fromJson(reader, womanNames.class);

                reader = new FileReader("json/mnames.json");
                menNames mNames = gson.fromJson(reader, menNames.class);

                reader = new FileReader("json/snames.json");
                sirNames sNames = gson.fromJson(reader, sirNames.class);

                prsn.setmNames(mNames.getData());
                prsn.setwNames(wNames.getData());
                prsn.setsNames(sNames.getData());
                prsn.setLocals(locData.getData());

                prsn.generateEvents(1993, 0, null);

                prsn.generateAncestors(r.getGenerationCount());

                prsn = prsnDAO.add(prsn);

                for (person ancestor : prsn.getAncestors()) {
                    if(ancestor.getPersonID().equals(prsn.getFather()))
                        System.out.print("FATHER!");
                    if(ancestor.getPersonID().equals(prsn.getMother()))
                        System.out.print("MOTHER!");
                    prsnDAO.add(ancestor);
                }
                result = new fillResult(prsn.getAncestors().size(), prsn.getEventTotals());

                return result;
            }
            else
            {
                result.setMessage("User not in database");
                return null;
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }  catch (FileNotFoundException e) {
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
        return result;
    }
}
