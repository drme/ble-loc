package com.example.btmatuoklis.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import com.example.btmatuoklis.R;
import com.example.btmatuoklis.classes.RoomsArray;

//List adapter for assignment of beacons to room
public class AssignAdapter extends BaseExpandableListAdapter {

    private int groupLayout;
    private int itemLayout;
    private LayoutInflater inflater;
    private RoomsArray enviroment;

    public AssignAdapter(Context context, RoomsArray enviroment){
        this.groupLayout = R.layout.list_group;
        this.itemLayout = R.layout.list_multiple_choice;
        this.enviroment = enviroment;
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getGroupCount() {
        return this.enviroment.getArray().size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.enviroment.getArray().get(groupPosition).getBeacons().size();
    }

    @Override
    public Object getGroup(int groupPosition) { return this.enviroment.getArray().get(groupPosition); }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.enviroment.getArray().get(groupPosition).getBeacons().get(childPosition);
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
        return null;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        return null;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
