package services;

import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.sql.SQLException;
import java.util.UUID;

import dataAccessObject.authTokenDAO;
import dataAccessObject.database;
import dataAccessObject.databaseException;
import dataAccessObject.personDAO;
import dataAccessObject.userDAO;
import model.locations;
import model.menNames;
import model.person;
import model.sirNames;
import model.user;
import model.womanNames;
import requests.fillRequest;
import requests.loginRequest;
import requests.registerRequest;
import results.fillResult;
import results.loginResult;
import results.registerResult;

/**
 * Created by Admin on 2/13/17.
 * Description: Creates a new user account, generates 4 generations of ancestor data for the new
 user, logs the user in, and returns an auth token.
 */

public class registerService {

    private database db = null;

    public registerService(database db) {
        this.db = db;
    }

    public registerService() {
        db = new database();
        try {
            db.openConnection();
        } catch (databaseException e) {
            e.printStackTrace();
        }
    }


    /**
     * create user model object,
      create person model object,
      person DAO ges back personID,
      add person id to user model,
      call DAO user,
      Model person.generateAncestors(4),
      Loop through generations call person DAO add - will also call event DAO to add events,
      Make login Request,
      Call login service,
      MAP login object to register result object,
      return it
     *
     * @param r
     * @return registerResult object
     */
    public registerResult register(registerRequest r) {
        userDAO usrDAO = new userDAO(db);
        personDAO prsnDAO = new personDAO(db);
        authTokenDAO tokenDAO = new authTokenDAO(db);
        Gson gson = new Gson();
        registerResult rgstrResult = new registerResult();
        Reader reader = null;
//        System.out.println(r);


        try {
            user usr = new user(r.getUserName(), r.getPassword(), r.getEmail(), r.getFirstName(), r.getLastName(), r.getGender(), "");

            if (usrDAO.userQuery(usr.getUserName()) != null) {
                rgstrResult.setMessage("Username already in use");
                return rgstrResult;
            }

            person prsn = new person(usr.getFirstName(), usr.getLastName(), usr.getGender(), r.getUserName());
            prsn.setDescendant(r.getUserName());


            reader = new FileReader("json/locations.json");
            locations locData = gson.fromJson(reader, locations.class);
            prsn.setLocals(locData.getData());
            prsn.generateEvents(1993,0, null);

            prsn.setMother(UUID.randomUUID().toString());
            prsn.setFather(UUID.randomUUID().toString());
            prsn = prsnDAO.add(prsn);


            if (prsn != null) {
                usr.setPersonID(prsn.getPersonID());

                usr = usrDAO.add(usr);

                if (usr != null) {

                    db.closeConnection(true);
                    db.openConnection();
                    fillRequest request = new fillRequest(usr.getUserName(), 4);
                    fillService service = new fillService(db);
                    fillResult result = service.fill(request);

                    if(result != null)
                    {
                        db.closeConnection(true);
                        db.openConnection();
                        loginRequest lr = new loginRequest(usr.getUserName(), usr.getPassword());
                        loginService logService = new loginService(db);
                        loginResult logResult = logService.login(lr);

                        rgstrResult = new registerResult(logResult.getAuthToken(), usr.getUserName(), usr.getPersonID());

                        return rgstrResult;
                    }
                }
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        catch(databaseException dbe)
        {
            dbe.printStackTrace();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        finally
        {
            try {
                db.closeConnection(true);
            } catch (databaseException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}

