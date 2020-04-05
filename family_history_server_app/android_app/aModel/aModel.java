package com.codac.admin.familyhistoryapp.aModel;

import android.media.audiofx.BassBoost;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Filter;
import model.person;
import model.event;
import model.user;
import results.personsResult;

import static com.google.android.gms.maps.model.BitmapDescriptorFactory.*;

/**
 * Created by Admin on 4/3/17.
 */

public class aModel {

    //Current User data
    private String usrName;
    private String authToken;
    private person user;

    private String host;
    private String port;

    //Current ancestors in list and map form
    private HashMap<String, person> people;
    private ArrayList<person> peopleList;

    private HashMap<String, person> paternalAncestors;
    private HashMap<String, person> maternalAncestors;
    private HashMap <String, ArrayList<person>> personChildren;

    //People to events
    private HashMap <String, ArrayList<event>> personEvents;

    //Event maps and types
    private HashMap<String, Integer> eventTypes;
    private HashMap <String, event> events;
    private ArrayList<event> eventList;

    //Setting and filters data
    private BassBoost.Settings settings;
    private Filter filter;

    //Data for MAP
    private HashMap<String, Boolean> filterMap;
    private Float[] colors = {HUE_AZURE, HUE_BLUE, HUE_CYAN, HUE_GREEN, HUE_MAGENTA, HUE_ORANGE, HUE_RED, HUE_ROSE, HUE_VIOLET, HUE_YELLOW};
    private String mapType;

    //Filters and map lines
    private HashMap<String, ArrayList<event>> eventFilters;
    private ArrayList<String> filters;
    private Boolean spouseLines;
    private String spouseLineColor;
    private Boolean treeLines;
    private String treeLineColor;
    private Boolean lifeLines;
    private String lifeLineColor;

    private static aModel mInstance = null;

    public static aModel getInstance(){
        if(mInstance == null)
        {
            mInstance = new aModel();
        }
        return mInstance;
    }

    public ArrayList<person> getPeopleList() {
        return peopleList;
    }

    public void setPeopleList(ArrayList<person> peopleList) {
        this.peopleList = peopleList;
    }

    public Boolean getSpouseLines() {
        return spouseLines;
    }

    public Boolean getTreeLines() {
        return treeLines;
    }

    public Boolean getLifeLines() {
        return lifeLines;
    }

    public void setSpouseLines(Boolean spouseLines) {
        this.spouseLines = spouseLines;
    }

    public void setTreeLines(Boolean treeLines) {
        this.treeLines = treeLines;
    }

    public void setLifeLines(Boolean lifeLines) {
        this.lifeLines = lifeLines;
    }

    public String getUsrName() {
        return usrName;
    }

    public void setUsrName(String usrName) {
        this.usrName = usrName;
    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public static void setmInstance(aModel mInstance) {
        aModel.mInstance = mInstance;
    }

    public String getMapType() {
        return mapType;
    }

    public void setMapType(String mapType) {
        this.mapType = mapType;
    }

    public HashMap<String, Boolean> getFilterMap() {
        return filterMap;
    }

    public void setFilterMap(HashMap<String, Boolean> filterMap) {
        this.filterMap = filterMap;
    }

    public ArrayList<String> getFilters() {
        return filters;
    }

    public void setFilters(ArrayList<String> filters) {
        this.filters = filters;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public Float[] getColors() {
        return colors;
    }

    public void setColors(Float[] colors) {
        this.colors = colors;
    }

    public void setEventList(ArrayList<event> eventList) {
        this.eventList = eventList;
    }

    public ArrayList<event> getEventList() {
        return eventList;
    }

    public void setPeople(HashMap<String, person> people) {
        this.people = people;
    }

    public void setEvents(HashMap<String, event> events) {
        this.events = events;
    }

    public void setPersonEvents(HashMap<String, ArrayList<event>> personEvents) {
        this.personEvents = personEvents;
    }

    public void setSettings(BassBoost.Settings settings) {
        this.settings = settings;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public void setEventTypes(HashMap<String, Integer> eventTypes) {
        this.eventTypes = eventTypes;
    }

    public void setUser(person user) {
        this.user = user;
    }

    public void setPaternalAncestors(HashMap<String, person> paternalAncestors) {
        this.paternalAncestors = paternalAncestors;
    }

    public void setMaternalAncestors(HashMap<String, person> maternalAncestors) {
        this.maternalAncestors = maternalAncestors;
    }

    public void setPersonChildren(HashMap<String, ArrayList<person>> personChildren) {
        this.personChildren = personChildren;
    }

    public HashMap<String, person> getPeople() {
        return people;
    }

    public HashMap<String, event> getEvents() {
        return events;
    }

    public HashMap<String, ArrayList<event>> getPersonEvents() {
        return personEvents;
    }

    public BassBoost.Settings getSettings() {
        return settings;
    }

    public Filter getFilter() {
        return filter;
    }

    public HashMap<String, Integer> getEventTypes() {
        return eventTypes;
    }

    public person getUser() {
        return user;
    }

    public HashMap<String, person> getPaternalAncestors() {
        return paternalAncestors;
    }

    public HashMap<String, person> getMaternalAncestors() {
        return maternalAncestors;
    }

    public HashMap<String, ArrayList<person>> getPersonChildren() {
        return personChildren;
    }


    public void setEventFilters(HashMap<String,ArrayList<event>> eventFilters) {
        this.eventFilters = eventFilters;
    }

    public HashMap<String, ArrayList<event>> getEventFilters() {
        return eventFilters;
    }

    public void setTreeLineColor(String treeLineColor) {
        this.treeLineColor = treeLineColor;
    }

    public String getTreeLineColor() {
        return treeLineColor;
    }

    public String getLifeLineColor() {
        return lifeLineColor;
    }

    public String getSpouseLineColor() {
        return spouseLineColor;
    }

    public void setLifeLineColor(String lifeLineColor) {
        this.lifeLineColor = lifeLineColor;
    }

    public void setSpouseLineColor(String spouseLineColor) {
        this.spouseLineColor = spouseLineColor;
    }
}

