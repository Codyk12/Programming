package com.codac.admin.familyhistoryapp;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.codac.admin.familyhistoryapp.aModel.aModel;

import java.net.URL;
import java.util.ArrayList;

import requests.eventRequest;
import requests.personRequest;
import results.eventResult;
import results.loginResult;
import results.personsResult;
import results.registerResult;
import serverProxy.proxy;

public class settingsActivity extends AppCompatActivity {
    private Spinner mapTypes;
    private aModel m = aModel.getInstance();
    private Button mSync;
    private Button mLogout;
    private personsResult prsnResult;
    private personsResult prsnsResult;
    private eventResult evntResult;
    private loginResult logResult;
    private registerResult regResult = null;
    private Activity thisA = this;
    private Util util = new Util();
    private Switch mLifeStoryLines;
    private Switch mFamilyTreeLines;
    private Switch mSpouseLines;
    private Spinner mSpouseLineColor;
    private Spinner mTreeLineColor;
    private Spinner mLifeLineColor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mapTypes = (Spinner) findViewById(R.id.spinner);
        mSpouseLineColor = (Spinner) findViewById(R.id.spouse_lines_color);
        mTreeLineColor = (Spinner) findViewById(R.id.tree_lines_color);
        mLifeLineColor = (Spinner) findViewById(R.id.life_lines_color);

        mSync = (Button) findViewById(R.id.sync_data);
        mLogout = (Button) findViewById(R.id.logout);

        mSpouseLines = (Switch) findViewById(R.id.spouse_lines);
        mFamilyTreeLines = (Switch) findViewById(R.id.tree_lines);
        mLifeStoryLines = (Switch) findViewById(R.id.life_lines);

        ArrayList<String> colors = new ArrayList<String>();
            colors.add("Blue");
            colors.add("Red");
            colors.add("Green");
            colors.add("Yellow");
            colors.add("Black");
        ArrayList<String> types = new ArrayList<String>();
            types.add("Normal");
            types.add("Hybrid");
            types.add("Satellite");
            types.add("Terrain");


        mSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                syncTask sncTask = new syncTask();
                sncTask.execute();
            }
        });

        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m.setmInstance(null);
                util.startMainActivityToTop(thisA);
            }
        });

    //Sets Life Story Listeners for color and swtich
        mLifeStoryLines.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                m.setLifeLines(isChecked);
            }
        });
        mLifeStoryLines.setChecked(m.getLifeLines());
        ArrayAdapter<String> lifeColorAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, colors);
        lifeColorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mLifeLineColor.setAdapter(lifeColorAdapter);
        mLifeLineColor.setOnItemSelectedListener(new lifeLineColorOnItemSelectedListener());
        mLifeLineColor.setSelection(colors.indexOf(m.getLifeLineColor()));



    //Sets Family Tree Event Listeners for color and swtich
        mFamilyTreeLines.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                m.setTreeLines(isChecked);
            }
        });
        mFamilyTreeLines.setChecked(m.getTreeLines());
        ArrayAdapter<String> treeColorAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, colors);
        treeColorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTreeLineColor.setAdapter(treeColorAdapter);
        mTreeLineColor.setOnItemSelectedListener(new treeLineColorOnItemSelectedListener());
        mTreeLineColor.setSelection(colors.indexOf(m.getTreeLineColor()));



    //Sets Spouse Events Listeners for color and swtich
        mSpouseLines.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                m.setSpouseLines(isChecked);
            }
        });
        mSpouseLines.setChecked(m.getSpouseLines());

        ArrayAdapter<String> spouseColorAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, colors);
        spouseColorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpouseLineColor.setAdapter(spouseColorAdapter);
        mSpouseLineColor.setOnItemSelectedListener(new spouseLineColorOnItemSelectedListener());
        mSpouseLineColor.setSelection(colors.indexOf(m.getSpouseLineColor()));


    //Sets adapter and listener for map type drop down
        ArrayAdapter<String> mapAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, types);
        mapAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mapTypes.setAdapter(mapAdapter);
        mapTypes.setOnItemSelectedListener(new mapTypeOnItemSelectedListener());
        mapTypes.setSelection(types.indexOf(m.getMapType()));

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

    public class mapTypeOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            m.setMapType(parent.getItemAtPosition(pos).toString());
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }

    }

    public class treeLineColorOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            m.setTreeLineColor(parent.getItemAtPosition(pos).toString());
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }

    }

    public class spouseLineColorOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            m.setSpouseLineColor(parent.getItemAtPosition(pos).toString());
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }

    }

    public class lifeLineColorOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            m.setLifeLineColor(parent.getItemAtPosition(pos).toString());
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }

    }

    public class syncTask extends AsyncTask<URL, Integer, Boolean> {

        protected Boolean doInBackground(URL... urls) {

            proxy prxy = new proxy(m.getHost(), m.getPort());

            personRequest pRequest = new personRequest(m.getUser().getPersonID(), m.getAuthToken());
            eventRequest eRequest = new eventRequest("", m.getAuthToken());
            personRequest psRequest = new personRequest("", m.getAuthToken());

            prsnResult = prxy.person(pRequest);
            prsnsResult = prxy.person(psRequest);
            evntResult = prxy.event(eRequest);

            logResult = new loginResult(m.getAuthToken(),m.getUsrName(),m.getUser().getPersonID());

            if(prsnResult != null) {
                return true;
            }
            else
                return false;
        }

        protected void onPostExecute(Boolean success) {
            if(success) {
                util.setModelData(m.getHost(), m.getPort(), prsnResult, prsnsResult, evntResult, logResult, regResult);
                util.startMainActivity(thisA);
            }
            else
                Toast.makeText(thisA, "Error", Toast.LENGTH_LONG).show();

        }
    }


}


