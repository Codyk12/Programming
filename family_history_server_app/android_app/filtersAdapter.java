package com.codac.admin.familyhistoryapp;

import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.codac.admin.familyhistoryapp.aModel.aModel;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Admin on 4/10/17.
 */

public class filtersAdapter extends RecyclerView.Adapter<filtersAdapter.RecyclerViewHolder> {

    ArrayList<String> filters;
    aModel m = aModel.getInstance();


    public filtersAdapter(ArrayList<String> filters) {
        this.filters = filters;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.filter,parent,false);
        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(v);
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        HashMap<String, Boolean> filterMap = m.getFilterMap();
        holder.filterSwitch.setChecked(filterMap.get(filters.get(position)));
        holder.filterSwitch.setText(filters.get(position));
    }


    @Override
    public int getItemCount() {
        return filters.size();
    }


    public static class RecyclerViewHolder extends RecyclerView.ViewHolder
    {
        private Switch filterSwitch;
        public RecyclerViewHolder(View itemView) {
            super(itemView);
            filterSwitch = (Switch) itemView.findViewById(R.id.filter);
            filterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    aModel.getInstance().getFilterMap().put(filterSwitch.getText().toString(), isChecked);
                }
            });
        }

    }

}


