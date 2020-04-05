package com.codac.admin.familyhistoryapp;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.codac.admin.familyhistoryapp.aModel.aModel;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;

import java.util.ArrayList;

import model.person;

/**
 * Created by Admin on 4/12/17.
 */

public class personSearchAdapter extends RecyclerView.Adapter<personSearchAdapter.RecyclerViewHolder> {

    ArrayList<person> people = new ArrayList<>();
    aModel m = aModel.getInstance();
    Util util = new Util();
    Activity thisA;

    public personSearchAdapter(ArrayList<person> people, Activity thisA) {
        this.people = people;
        this.thisA = thisA;
    }

    @Override
    public personSearchAdapter.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.person_search_row,parent,false);
        personSearchAdapter.RecyclerViewHolder recyclerViewHolder = new personSearchAdapter.RecyclerViewHolder(v);
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(personSearchAdapter.RecyclerViewHolder holder, final int position) {
        holder.personName.setText(m.getPeople().get(people.get(position).getPersonID()).getFirstName() + " " +
                m.getPeople().get(people.get(position).getPersonID()).getLastName());
        holder.personName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                util.startPersonActivity(thisA, people.get(position));
            }
        });

        Drawable genderIcon;
        if(people.get(position).getGender().equals("male"))
        {
            genderIcon = new IconDrawable(thisA, Iconify.IconValue.fa_male).
                    colorRes(R.color.colorPrimary).sizeDp(40);
        }
        else {
            genderIcon = new IconDrawable(thisA, Iconify.IconValue.fa_female).
                    colorRes(R.color.colorAccent).sizeDp(40);
        }
        holder.personIcon.setImageDrawable(genderIcon);
    }

    @Override
    public int getItemCount() {
        return people.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder
    {
        private TextView personName;
        private ImageView personIcon;
        public RecyclerViewHolder(View itemView) {
            super(itemView);
            personIcon = (ImageView) itemView.findViewById(R.id.person_image);
            personName = (TextView) itemView.findViewById(R.id.person_name);
        }

    }
}
