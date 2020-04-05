package com.codac.admin.familyhistoryapp;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.codac.admin.familyhistoryapp.aModel.aModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import model.event;
import model.person;
import model.user;
import results.eventResult;
import results.loginResult;
import results.personsResult;
import results.registerResult;

public class MainActivity extends AppCompatActivity {

    //private
    private FragmentManager fm;
    private aModel m = aModel.getInstance();
    private String evntID = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fm = this.getSupportFragmentManager();

        if(m.getAuthToken() != null) {
            m = aModel.getInstance();
            evntID = m.getPersonEvents().get(m.getUser().getPersonID()).get(0).getEventID();
            mapFragSwtich();
        }
        else
            loginFragSwtich();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(m.getAuthToken() != null) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.toolbar, menu);
        }
        return true;
    }

    public void loginFragSwtich(){
        loginFragment loginFrag = new loginFragment();
        fm.beginTransaction()
                .replace(R.id.container, loginFrag)
                .commit();
    }

    public void mapFragSwtich()
    {
        Bundle bundle = new Bundle();
        bundle.putString("eventID", evntID);
        mapFragment mapFrag = new mapFragment();
        mapFrag.setArguments(bundle);

        fm.beginTransaction()
                .replace(R.id.container, mapFrag)
                .commit();
    }
}
