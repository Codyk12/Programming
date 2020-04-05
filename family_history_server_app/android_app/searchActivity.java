package com.codac.admin.familyhistoryapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.codac.admin.familyhistoryapp.aModel.aModel;

import java.util.ArrayList;

import model.event;
import model.person;

public class searchActivity extends AppCompatActivity {

    MenuItem item;
    SearchView searchView;
    RecyclerView peopleList;
    RecyclerView eventsList;
    ArrayList<person> filteredPeople;
    ArrayList<event> filteredEvents;
    aModel m = aModel.getInstance();
    Activity thisA = this;
    personSearchAdapter pAdapter;
    eventSearchAdapter eAdapter;
    Util util = new Util();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        searchView = (SearchView) findViewById(R.id.search);
        peopleList = (RecyclerView) findViewById(R.id.people_list);
        eventsList = (RecyclerView) findViewById(R.id.events_list);

        RecyclerView.LayoutManager pLayoutManager = new LinearLayoutManager(this);
        peopleList.setLayoutManager(pLayoutManager);
        RecyclerView.LayoutManager eLayoutManager = new LinearLayoutManager(this);
        eventsList.setLayoutManager(eLayoutManager);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filteredPeople = filterPeople(newText.toLowerCase());
                filteredEvents = filterEvents(newText.toLowerCase());

                pAdapter = new personSearchAdapter(filteredPeople, thisA);
                peopleList.setAdapter(pAdapter);

                eAdapter = new eventSearchAdapter(filteredEvents, thisA);
                eventsList.setAdapter(eAdapter);

                return false;
            }
        });
    }

    private ArrayList<event> filterEvents(String newText) {
        ArrayList<event> filteredEvents = new ArrayList<>();

        for(event evnt : m.getEventList())
        {
            if(evnt.toString().toLowerCase().contains(newText))
                if(m.getFilterMap().get(evnt.getEventType()))
                    filteredEvents.add(evnt);
        }

        return filteredEvents;
    }

    private ArrayList<person> filterPeople(String newText) {
        ArrayList<person> filteredPeople = new ArrayList<>();

        for(person prsn : m.getPeopleList())
        {
            if(prsn.toString().toLowerCase().contains(newText))
                filteredPeople.add(prsn);
        }

        return filteredPeople;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                util.startMainActivityToTop(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.person, menu);
        return true;
    }

}
