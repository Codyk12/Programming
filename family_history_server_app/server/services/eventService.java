package services;

import java.sql.SQLException;
import java.util.ArrayList;

import dataAccessObject.authTokenDAO;
import dataAccessObject.database;
import dataAccessObject.databaseException;
import dataAccessObject.eventDAO;
import dataAccessObject.userDAO;
import model.event;
import requests.eventRequest;
import results.eventResult;

/**
 * Created by Admin on 2/14/17.
 * Description: Returns the single Event object with the specified ID.
 * Description: Returns ALL events for ALL family members of the current user. The current
 user is determined from the provided auth token.
 */

public class eventService {

    private database db = null;
    private boolean original = true;

    public eventService(database db) {
        this.db = db;
        original = false;
    }

    public eventService() throws databaseException {
        db = new database();
        db.openConnection();
    }

    /* if r.eventID is not NULL,
    //      query event DAO (r.eventID),
    //      create eventResult,
    //  else,
    //      query authtoken DAO to get username,
    //      query Person dao to get all ancestors of person with username,
    //      query events for all ancestors,
    // create eventResult,
    */
    public eventResult getEvents(eventRequest r) {
        eventDAO evntDAO = new eventDAO(db);
        authTokenDAO tokenDAO = new authTokenDAO(db);
        userDAO usrDAO = new userDAO(db);
        String token = r.getAuthToken();
        String usrName;
        ArrayList<event> events = new ArrayList<event>();
        eventResult evntResult = new eventResult();



        try {

            if(original) {
                if(tokenDAO.queryTime("token", r.getAuthToken()) != null)
                {
                    if ((System.currentTimeMillis() - tokenDAO.queryTime("token", r.getAuthToken()) > 3600000)) {
                        evntResult.setMessage("Authtoken Invaild.");
                        return evntResult;
                    }
                }
            }
            if (r.getEventID().equals("")) {
                usrName = tokenDAO.queryUserName(token);
                events = evntDAO.queryAll(usrName);
                evntResult.setEvents(events);
                return evntResult;
            } else {
                if(tokenDAO.queryUserName(r.getAuthToken()) != null)
                {
                    String userName = tokenDAO.queryUserName(r.getAuthToken());
                    event evnt = evntDAO.query(r.getEventID());
                    if(evnt != null)
                    {
                        if(!evnt.getdescendant().equals(userName))
                        {
                            evntResult.setMessage("Authtoken Invaild for requesting information.");
                            return evntResult;
                        }
                        events.add(evnt);
                        evntResult.setEvents(events);
                        return evntResult;
                    }
                }

            }
        } catch (SQLException e) {
            evntResult.setMessage(e.getMessage());
            return evntResult;
        } finally {
            if (original) {
                try {
                    db.closeConnection(true);
                } catch (databaseException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
