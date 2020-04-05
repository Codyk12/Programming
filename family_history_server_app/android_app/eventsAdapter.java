package com.codac.admin.familyhistoryapp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;

import java.util.ArrayList;
import java.util.HashMap;

import model.event;

/**
 * Created by Admin on 4/8/17.
 */

public class eventsAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<String> headerArray;
    private HashMap<String, ArrayList<event>> childArray;
    private LayoutInflater infalInflater;

    public eventsAdapter(Context context, ArrayList<String> headerArray, HashMap<String, ArrayList<event>> childArray) {
        this.context = context;
        this.headerArray = headerArray;
        this.childArray = childArray;
        infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getGroupCount() {
        return this.headerArray.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.childArray.get(this.headerArray.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.headerArray.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.childArray.get(this.headerArray.get(groupPosition))
                .get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {

            convertView = infalInflater.inflate(R.layout.list_parent,
                    null);
        }

        TextView textViewHeader = (TextView) convertView.findViewById(R.id.list_header);
        textViewHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        event evnt = (event) getChild(groupPosition, childPosition);
        final String childText = evnt.toString();

        if (convertView == null) {

            convertView = infalInflater.inflate(R.layout.list_child, null);
        }

        ImageView icon = (ImageView) convertView.findViewById(R.id.child_icon);
        Drawable img = new IconDrawable(context, Iconify.IconValue.fa_map_marker).sizeDp(40);
        icon.setImageDrawable(img);

        TextView textViewChild = (TextView) convertView
                .findViewById(R.id.textViewChild);

        textViewChild.setText(childText);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
