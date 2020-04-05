package com.codac.admin.familyhistoryapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codac.admin.familyhistoryapp.aModel.aModel;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;

import java.util.ArrayList;
import java.util.HashMap;

import model.event;
import model.person;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 *
 * to handle interaction events.
 *
 * create an instance of this fragment.
 */
public class mapFragment extends Fragment implements GoogleMap.OnMarkerClickListener, OnMapReadyCallback {
    // TODO: Rename parameter arguments, choose names that match
    private GoogleMap mMap;
    private TextView name;
    private TextView eventText;
    private ImageView icon;
    private LinearLayout eventInfo;
    private String eventID;
    private person prsn;
    private Util util = new Util();
    private Activity thisA;
    private ArrayList<Polyline> lines = new ArrayList<>();
    private HashMap<String, Integer> colors = new HashMap<>();
    private aModel m = aModel.getInstance();
    private HashMap<String, person> people = m.getPeople();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        name = (TextView) v.findViewById(R.id.person_name);
        eventText = (TextView) v.findViewById(R.id.event_text);
        icon = (ImageView) v.findViewById(R.id.gender_icon);
        eventInfo = (LinearLayout) v.findViewById(R.id.event_info);

        if(getArguments() != null)
            eventID = getArguments().getString("eventID");

        super.onCreate(savedInstanceState);

        thisA = getActivity();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        return v;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                return true;
            case R.id.settings:
                util.startSettingsActivity(thisA);
                return true;
            case R.id.filter:
                util.startFilterActivity(thisA);
                return true;
            case R.id.search:
                util.startSearchActivity(thisA);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        if(m.getMapType().equals("Normal"))
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        else if(m.getMapType().equals("Hybrid"))
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        else if(m.getMapType().equals("Satellite"))
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        else if(m.getMapType().equals("Terrain"))
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        else
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        for(event evnt : m.getEventList())
        {
            if(m.getFilterMap().get(evnt.getEventType())) {

                if(m.getPaternalAncestors().containsKey(evnt.getPersonID()) && !m.getFilterMap().get("Father's Side"))
                    continue;
                if(m.getMaternalAncestors().containsKey(evnt.getPersonID()) && !m.getFilterMap().get("Mother's Side"))
                    continue;
                if(m.getPeople().get(evnt.getPersonID()).getGender().equals("male") && !m.getFilterMap().get("Male Events"))
                    continue;
                if(m.getPeople().get(evnt.getPersonID()).getGender().equals("female") && !m.getFilterMap().get("Female Events"))
                    continue;

                    LatLng postn = new LatLng(evnt.getLatitude(), evnt.getLongitude());
                    int typeIndex = m.getEventTypes().get(evnt.getEventType());
                    Float color = m.getColors()[typeIndex];
                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(postn)
                            .title(evnt.getEventType())
                            .icon(BitmapDescriptorFactory.defaultMarker(color)));
                    marker.setTag(evnt);
                if(!eventID.equals(""))
                    if(evnt.getEventID().equals(eventID)) {
                        marker.showInfoWindow();
                        onMarkerClick(marker);
                    }
                }

            }
        if(!eventID.equals(""))
        {
            LatLng postn = new LatLng(m.getEvents().get(eventID).getLatitude(), m.getEvents().get(eventID).getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(postn);
            mMap.animateCamera(cameraUpdate);
        }

        mMap.setOnMarkerClickListener(this);
    }

    /** Called when the user clicks a marker. */
    @Override
    public boolean onMarkerClick(final Marker marker) {

        event evnt = (event) marker.getTag();
        prsn = m.getPeople().get(evnt.getPersonID());

        drawLines(mMap, marker);

        Drawable genderIcon;

        if(prsn.getGender().equals("male"))
        {
            genderIcon = new IconDrawable(getActivity(), Iconify.IconValue.fa_male).
                    colorRes(R.color.colorPrimary).sizeDp(40);
        }
        else {
            genderIcon = new IconDrawable(getActivity(), Iconify.IconValue.fa_female).
                    colorRes(R.color.colorAccent).sizeDp(40);
        }
        icon.setImageDrawable(genderIcon);

        name.setText(prsn.toString());
        eventText.setText(evnt.toString());

        eventInfo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
              util.startPersonActivity(thisA, prsn);
            }
        });

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    public void clearLines()
    {
        if(lines.size() > 0) {
            for (Polyline line : lines) {
                line.remove();
            }
        }
    }

    public void drawLines(GoogleMap mMap, Marker marker)
    {
        clearLines();

        event evnt = (event) marker.getTag();
        colors.put("Red", Color.RED);
        colors.put("Blue", Color.BLUE);
        colors.put("Green", Color.GREEN);
        colors.put("Yellow", Color.YELLOW);
        colors.put("Black", Color.BLACK);

        prsn = people.get(evnt.getPersonID());

        //Check filter for life lines for map
        if(m.getLifeLines())
        {
            ArrayList<event> events = m.getPersonEvents().get(prsn.getPersonID());

            for(int i = 0; i < events.size()-1; i++) {
                LatLng curPosition = new LatLng(events.get(i).getLatitude(), events.get(i).getLongitude());
                LatLng nextPosition = new LatLng(events.get(i+1).getLatitude(), events.get(i+1).getLongitude());
                Polyline line = mMap.addPolyline(new PolylineOptions()
                        .add(curPosition, nextPosition)
                        .width(5)
                        .color(colors.get(m.getLifeLineColor())));
                lines.add(line);
            }
        }

        //Check filter for tree lines for map
        if(m.getTreeLines())
        {
            treeLinesRecusion(evnt.getPersonID(), evnt, 10);
        }

        //check spouse lines toggle to draw lines on map
        if(m.getSpouseLines())
        {
            if(people.containsKey(prsn.getSpouse())) {
                ArrayList<event> events = m.getPersonEvents().get(prsn.getSpouse());
                if(events.size() > 0) {
                    event pEvnt = findEarliestEvent(events);
                    if(pEvnt != null) {
                        LatLng spousePosition = new LatLng(pEvnt.getLatitude(), pEvnt.getLongitude());
                        LatLng curPosition = new LatLng(evnt.getLatitude(), evnt.getLongitude());
                        Polyline line = mMap.addPolyline(new PolylineOptions()
                                .add(curPosition, spousePosition)
                                .width(5)
                                .color(colors.get(m.getSpouseLineColor())));
                        lines.add(line);
                    }
                }
            }
        }

    }

    //helper method to draw family lines on map
    public void treeLinesRecusion(String personID, event evnt, int width)
    {
        if(people.containsKey(people.get(personID).getFather())) {

            event earliestEvent = findEarliestEvent(m.getPersonEvents().get(people.get(personID).getFather()));
            LatLng curPosition = new LatLng(evnt.getLatitude(), evnt.getLongitude());
            LatLng nextPosition = new LatLng(earliestEvent.getLatitude(), earliestEvent.getLongitude());

            Polyline line = mMap.addPolyline(new PolylineOptions()
                    .add(curPosition, nextPosition)
                    .width(width)
                    .color(colors.get(m.getTreeLineColor())));
            lines.add(line);

            treeLinesRecusion(people.get(personID).getFather(), earliestEvent, width-2);
        }

        if(people.containsKey(people.get(personID).getMother())) {

            event earliestEvent = findEarliestEvent(m.getPersonEvents().get(people.get(personID).getMother()));
            LatLng curPosition = new LatLng(evnt.getLatitude(), evnt.getLongitude());
            LatLng nextPosition = new LatLng(earliestEvent.getLatitude(), earliestEvent.getLongitude());

            Polyline line = mMap.addPolyline(new PolylineOptions()
                    .add(curPosition, nextPosition)
                    .width(width)
                    .color(colors.get(m.getTreeLineColor())));
            lines.add(line);

            treeLinesRecusion(people.get(personID).getMother(), earliestEvent, width-2);
        }
    }

    //Helper method to get earliest year for events for a specific person or array of events
    public event findEarliestEvent(ArrayList<event> events)
    {
        event earliestEvent = null;

        if(events.size() > 0){
            for(event evnt : events)
            {
                if(evnt.getEventType().equals("birth"))
                    return evnt;
            }

            int earliestYear = 9999;
            for(event evnt : events)
            {
                if(Integer.parseInt(evnt.getYear()) < earliestYear) {
                    earliestEvent = evnt;
                    earliestYear = Integer.parseInt(evnt.getYear());
                }
            }
        }
        return earliestEvent;
    }
}

