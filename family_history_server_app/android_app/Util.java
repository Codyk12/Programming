package com.codac.admin.familyhistoryapp;

import android.content.Context;
import android.content.Intent;
import com.codac.admin.familyhistoryapp.aModel.aModel;
import java.util.ArrayList;
import java.util.HashMap;
import model.event;
import model.person;
import results.eventResult;
import results.loginResult;
import results.personsResult;
import results.registerResult;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

/**
 * Created by Admin on 4/11/17.
 * Populates Model and provides activity switching functions
 */

public class Util {

    private aModel m = aModel.getInstance();
    private HashMap<String, person> persons;
    private HashMap<String, person> fathersSide = new HashMap<>();
    private HashMap<String, person> mothersSide = new HashMap<>();

    public Util() {
    }


    //Populates the data for the model class
    public void setModelData(String host, String port, personsResult prsnResult, personsResult prsnsResult, eventResult evntResult, loginResult logResult, registerResult regResult)
    {
        aModel m = aModel.getInstance();

        HashMap<String, event> events = new HashMap<String, event>();
        HashMap<String, Integer> eventType = new HashMap<String, Integer>();
        ArrayList<event> eventsList = new ArrayList<event>();
        persons = new HashMap<>();
        HashMap<String, ArrayList<person>> personChildren = new HashMap<String, ArrayList<person>>();
        HashMap <String, ArrayList<event>> personEvents = new HashMap<String, ArrayList<event>>();
        ArrayList<String> filters = new ArrayList<>();
        HashMap<String, ArrayList<event>> eventFilters = new HashMap<>();
        ArrayList<event> maleEvents = new ArrayList<>();
        ArrayList<event> femaleEvents = new ArrayList<>();
        ArrayList<event> fatherEvents = new ArrayList<>();
        ArrayList<event> motherEvents = new ArrayList<>();
        HashMap<String, Boolean> filterMap = new HashMap<>();

        //Initialize port and host numbers
        m.setHost(host);
        m.setPort(port);

        //initialize certain always true variables
        m.setMapType("Normal");
        m.setLifeLineColor("Red");
        m.setSpouseLineColor("Green");
        m.setTreeLineColor("Blue");
        m.setSpouseLines(false);
        m.setTreeLines(false);
        m.setLifeLines(false);


        m.setPeopleList(prsnResult.getPersons());

        person usr = prsnResult.getPersons().get(0);
        m.setUser(usr);

        for(person prsn : prsnsResult.getPersons())
        {
            persons.put(prsn.getPersonID(), prsn);
        }

        if(logResult != null) {
            m.setAuthToken(logResult.getAuthToken());
            m.setUsrName(logResult.getUserName());
        }
        else if(regResult != null) {
            m.setAuthToken(regResult.getAuthToken());
            m.setUsrName(regResult.getUserName());
        }

        int eventTypeNumber = 0;
        for(event evnt : evntResult.getEvents())
        {
            if(!eventType.containsKey(evnt.getEventType())) {
                eventType.put(evnt.getEventType(), eventTypeNumber);
                eventTypeNumber++;
            }

            events.put(evnt.getEventID(), evnt);
            eventsList.add(evnt);

            //Split events by type for mapping purposes
            ArrayList<event> eFilters = eventFilters.get(evnt.getEventType());
            if(eFilters == null) {
                eFilters = new ArrayList<event>();
                eFilters.add(evnt);
                personEvents.put(evnt.getEventType(), eFilters);
            } else {
                // add if item is not already in list
                if(!eFilters.contains(evnt))
                    eFilters.add(evnt);
            }
            // Puts types into list
            if(!filters.contains(evnt.getEventType())) {
                filters.add(evnt.getEventType());
            }

            if(!filterMap.containsKey(evnt.getEventType()))
            {
                filterMap.put(evnt.getEventType(), true);
            }

            //Forms Arrays of events based on gender
            if(persons.get(evnt.getPersonID()).getGender().equals("male"))
                maleEvents.add(evnt);
            else
                femaleEvents.add(evnt);

            ArrayList<event> eventList = personEvents.get(evnt.getPersonID());

            // if list does not exist create it
            if(eventList == null) {
                eventList = new ArrayList<event>();
                eventList.add(evnt);
                personEvents.put(evnt.getPersonID(), eventList);
            } else {
                // add if item is not already in list
                if(!eventList.contains(evnt)) eventList.add(evnt);
            }
        }

        eventFilters.put("Father's Side", fatherEvents);
        eventFilters.put("Mother's Side", motherEvents);
        eventFilters.put("Male Events", maleEvents);
        eventFilters.put("Female Events", femaleEvents);

        filters.add("Father's Side");
        filters.add("Mother's Side");
        filters.add("Male Events");
        filters.add("Female Events");

        filterMap.put("Father's Side", true);
        filterMap.put("Mother's Side", true);
        filterMap.put("Male Events", true);
        filterMap.put("Female Events", true);

        calculateFathersSide(usr.getPersonID());
        calculateMothersSide(usr.getPersonID());

        m.setMaternalAncestors(mothersSide);
        m.setPaternalAncestors(fathersSide);

        m.setFilterMap(filterMap);
        m.setFilters(filters);
        m.setEventFilters(eventFilters);
        m.setEvents(events);
        m.setEventList(eventsList);
        m.setEventTypes(eventType);

        m.setPersonEvents(personEvents);
        m.setPersonChildren(personChildren);
        m.setPeople(persons);
    }

    public void calculateFathersSide(String fatherID)
    {
        fathersSide.put(fatherID, persons.get(fatherID));
        calculateFathersSideHelper(persons.get(fatherID).getFather());
        calculateFathersSideHelper(persons.get(fatherID).getMother());
    }

    public void calculateFathersSideHelper(String parentID)
    {
        if(persons.containsKey(parentID)) {
            fathersSide.put(parentID, persons.get(parentID));
            calculateFathersSideHelper(persons.get(parentID).getFather());
            calculateFathersSideHelper(persons.get(parentID).getMother());
        }
    }

    public void calculateMothersSide(String motherID) {
        mothersSide.put(motherID, persons.get(motherID));
        calculateMothersSideHelper(persons.get(motherID).getFather());
        calculateMothersSideHelper(persons.get(motherID).getMother());
    }

    public void calculateMothersSideHelper(String personID)
    {
        if(persons.containsKey(personID)) {
            mothersSide.put(personID, persons.get(personID));
            calculateMothersSideHelper(persons.get(personID).getFather());
            calculateMothersSideHelper(persons.get(personID).getMother());
        }
    }

    public void startMainActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    public void startPersonActivity(Context context, person prsn) {
        Intent intent = new Intent(context, personActivity.class);
        intent.putExtra("personID",  prsn.getPersonID());
        context.startActivity(intent);
    }

    public void startMapActivity(Context context, event evnt) {
        Intent intent = new Intent(context, mapActivity.class);
        intent.putExtra("eventID", evnt.getEventID());
        context.startActivity(intent);
    }

    public void startSettingsActivity(Context context) {
        Intent intent = new Intent(context, settingsActivity.class);
        context.startActivity(intent);
    }

    public void startFilterActivity(Context context) {
        Intent intent = new Intent(context, filterActivity.class);
        context.startActivity(intent);
    }

    public void startSearchActivity(Context context) {
        Intent intent = new Intent(context, searchActivity.class);
        context.startActivity(intent);
    }

    public void startMainActivityToTop(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }
}