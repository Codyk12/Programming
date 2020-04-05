package com.codac.admin.familyhistoryapp;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.codac.admin.familyhistoryapp.aModel.aModel;

import java.util.ArrayList;

import model.event;
import model.person;

/**
 * Created by Admin on 4/12/17.
 */

public class eventSearchAdapter extends RecyclerView.Adapter<eventSearchAdapter.RecyclerViewHolder> {

    ArrayList<event> events = new ArrayList<>();
    aModel m = aModel.getInstance();
    Util util = new Util();
    Activity thisA;

    public eventSearchAdapter(ArrayList<event> events, Activity thisA) {
        this.events = events;
        this.thisA = thisA;
    }

    @Override
    public eventSearchAdapter.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_search_row,parent,false);
        eventSearchAdapter.RecyclerViewHolder recyclerViewHolder = new eventSearchAdapter.RecyclerViewHolder(v);
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(eventSearchAdapter.RecyclerViewHolder holder, final int position) {
        holder.event.setText(events.get(position).toString());
        holder.event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                util.startMapActivity(thisA, events.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder
    {
        private TextView event;
        public RecyclerViewHolder(View itemView) {
            super(itemView);
            event = (TextView) itemView.findViewById(R.id.event_info_row);
        }

    }
}
