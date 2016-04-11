package com.example.btmatuoklis.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.example.btmatuoklis.R;
import com.example.btmatuoklis.classes.Beacon;
import com.example.btmatuoklis.classes.RoomsArray;
import com.example.btmatuoklis.helpers.BeaconInfoHelper;

//List adapter for display of beacons which are linked(bound) to to room
public class LinkAdapter extends BaseExpandableListAdapter {

    private static int checkItem = R.id.text1;
    private int groupLayout, itemLayout;
    private LayoutInflater inflater;
    private RoomsArray enviroment;
    private BeaconInfoHelper infohelper;

    public LinkAdapter(Context context, RoomsArray enviroment){
        this.groupLayout = R.layout.list_group;
        this.itemLayout = R.layout.list_checked;
        this.enviroment = enviroment;
        this.infohelper = new BeaconInfoHelper(context);
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
        String title = this.enviroment.getArray().get(groupPosition).getName();
        title += " ("+this.enviroment.getArray().get(groupPosition).getBeacons().size()+")";
        if (convertView == null){ convertView = inflater.inflate(this.groupLayout, null); }
        TextView text = (TextView)convertView.findViewById(R.id.text1);
        text.setText(title);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Beacon beacon = this.enviroment.getArray().get(groupPosition).getBeacons().get(childPosition);
        String info = this.infohelper.getCalibrationInfo(beacon);
        View view = convertView;
        CheckHolder checkHolder;
        if (convertView == null){
            view = this.inflater.inflate(this.itemLayout, parent, false);
            checkHolder = new CheckHolder(view);
            view.setTag(checkHolder);
        } else { checkHolder = (CheckHolder)view.getTag(); }
        checkHolder.checkedView.setText(info);
        checkHolder.checkedView.setChecked(beacon.getFullRSSI().size() > 0);
        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    class CheckHolder {
        public CheckedTextView checkedView;
        public CheckHolder(View view){
            checkedView = (CheckedTextView)view.findViewById(LinkAdapter.checkItem);
        }
    }
}