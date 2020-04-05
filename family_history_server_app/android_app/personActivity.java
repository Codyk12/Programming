package com.codac.admin.familyhistoryapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.codac.admin.familyhistoryapp.aModel.aModel;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;

import java.util.ArrayList;
import java.util.HashMap;

import model.event;
import model.person;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

public class personActivity extends AppCompatActivity {

    private MenuItem item;
    private ExpandableListView eventExpList;
    private ExpandableListView personExpList;

    private eventsAdapter adapter;
    private ArrayList<event> events;
    private ArrayList<person> family;
    private ArrayList<String> personListHeaders;
    private ArrayList<String> eventListHeaders;
    private HashMap<String, ArrayList<event>> eventChildList;
    private HashMap<String, ArrayList<person>> personChildList;
    private TextView mfirstName;
    private TextView mLastName;
    private TextView mgender;
    private Util util = new Util();
    private Activity thisA = this;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        Toolbar toolbar = (Toolbar) findViewById(R.id.somethingelse);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        eventListHeaders = new ArrayList<>();
        personListHeaders = new ArrayList<>();
        eventChildList = new HashMap<>();
        personChildList = new HashMap<>();
        events = new ArrayList<>();
        eventListHeaders.add("Life Events");
        personListHeaders.add("Family");

        mfirstName = (TextView) findViewById(R.id.first_name_display);
        mLastName = (TextView) findViewById(R.id.last_name_display);
        mgender = (TextView) findViewById(R.id.gender_display);

        Intent intent = getIntent();
        String personID = intent.getStringExtra("personID");

        eventExpList = (ExpandableListView) findViewById(R.id.event_expand_list);
        personExpList = (ExpandableListView) findViewById(R.id.person_expand_list);


        aModel m = aModel.getInstance();
        HashMap<String, person> people;
        people = m.getPeople();
        person prsn = people.get(personID);

        mfirstName.setText(prsn.getFirstName());
        mLastName.setText(prsn.getLastName());
        mgender.setText(prsn.getGender());

        for(event evnt : m.getPersonEvents().get(personID))
        {
            events.add(evnt);
        }

        family = new ArrayList<person>();

        if(people.get(prsn.getFather()) != null && people.get(prsn.getMother()) != null)
        {
            family.add(people.get(people.get(prsn.getFather()).getPersonID()));
            family.add(people.get(people.get(prsn.getMother()).getPersonID()));
        }
        if(people.get(prsn.getSpouse()) != null)
        {
            family.add(people.get(people.get(prsn.getSpouse()).getPersonID()));
        }


        eventChildList.put("Life Events", events);
        personChildList.put("Family", family);


        final eventsAdapter evntAdapter = new eventsAdapter(this, eventListHeaders, eventChildList);
        final peopleAdapter pplAdapter = new peopleAdapter(this, personListHeaders, personChildList, m.getPeople().get(personID));

        personExpList.setAdapter(pplAdapter);
        eventExpList.setAdapter(evntAdapter);

        eventExpList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

                event evnt = (event) evntAdapter.getChild(groupPosition, childPosition);
                String evntS = evnt.toString();
                util.startMapActivity(thisA, evnt);
                //Toast.makeText(getApplicationContext(), "Child " + evntS + " is clicked", Toast.LENGTH_LONG).show();
                return false;
            }
        });

        personExpList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

                person prsn = (person) pplAdapter.getChild(groupPosition, childPosition);
                String prsnS = prsn.toString();
                util.startPersonActivity(thisA, prsn);
                //Toast.makeText(getApplicationContext(), "Child " + prsnS + " is clicked", Toast.LENGTH_LONG).show();
                return false;
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.up_arrow:
                util.startMainActivityToTop(this);
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.person, menu);

        item = (MenuItem) menu.findItem(R.id.up_arrow);
        Drawable upIcon = new IconDrawable(this, Iconify.IconValue.fa_angle_double_up).sizeDp(40);
        item.setIcon(upIcon);

        return true;

    }

}
