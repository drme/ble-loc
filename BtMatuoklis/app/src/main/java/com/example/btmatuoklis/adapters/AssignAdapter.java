package com.example.btmatuoklis.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import com.example.btmatuoklis.classes.Room;

import java.util.ArrayList;

//List adapter for assignment of beacons to room
public class AssignAdapter extends BaseExpandableListAdapter {

    private int layout;
    private LayoutInflater inflater;
    private ArrayList<Room> rooms;

    public AssignAdapter(Context context, int resource, ArrayList<Room> rooms){
        this.layout = resource;
        this.rooms = rooms;
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getGroupCount() { return this.rooms.size(); }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.rooms.get(groupPosition).getBeacons().size();
    }

    @Override
    public Object getGroup(int groupPosition) { return this.rooms.get(groupPosition); }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.rooms.get(groupPosition).getBeacons().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) { return groupPosition; }

    @Override
    public long getChildId(int groupPosition, int childPosition) { return childPosition; }

    @Override
    public boolean hasStableIds() { return false; }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        return null;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        return null;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) { return false; }
}
