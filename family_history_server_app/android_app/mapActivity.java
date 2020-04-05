package com.codac.admin.familyhistoryapp;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

public class mapActivity extends AppCompatActivity {

    private FragmentManager fm;
    String evntID;
    MenuItem item;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fm = this.getSupportFragmentManager();


        Intent intent = getIntent();
        evntID = intent.getStringExtra("eventID");

        Bundle bundle = new Bundle();
        bundle.putString("eventID", evntID);

        mapFragment mapFrag = new mapFragment();

        mapFrag.setArguments(bundle);

        fm.beginTransaction()
                .replace(R.id.container, mapFrag)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.up_arrow:
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
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
        inflater.inflate(R.menu.map, menu);

        item = (MenuItem) menu.findItem(R.id.up_arrow);
        Drawable upIcon = new IconDrawable(this, Iconify.IconValue.fa_angle_double_up).sizeDp(40);
        item.setIcon(upIcon);

        return true;

    }
}

