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

import java.util.ArrayList;

//List adapter for assignment of beacons to room
public class AssignAdapter extends BaseExpandableListAdapter {

    private static int checkItem = R.id.text1;
    private static int simpleItem = android.R.id.text1;
    private int groupLayout, simpleItemLayout, checkItemLayout;
    private LayoutInflater inflater;
    private RoomsArray scanArray;
    private ArrayList<Integer> selected;
    private BeaconInfoHelper infohelper;

    public AssignAdapter(Context context, RoomsArray scanArray, ArrayList<Integer> selected){
        this.groupLayout = R.layout.list_group;
        this.simpleItemLayout = android.R.layout.simple_list_item_1;
        this.checkItemLayout = R.layout.list_multiple_choice;
        this.scanArray = scanArray;
        this.selected = selected;
        this.infohelper = new BeaconInfoHelper(context);
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getGroupCount() {
        return this.scanArray.getArray().size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.scanArray.getArray().get(groupPosition).getBeacons().size();
    }

    @Override
    public Object getGroup(int groupPosition) { return this.scanArray.getArray().get(groupPosition); }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.scanArray.getArray().get(groupPosition).getBeacons().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return this.scanArray.getArray().get(groupPosition).getName().hashCode();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return this.scanArray.getArray().get(groupPosition).getBeacons().get(childPosition).getMAC().hashCode();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String title = this.scanArray.getArray().get(groupPosition).getName();
        title += " ("+this.scanArray.getArray().get(groupPosition).getBeacons().size()+")";
        if (convertView == null){ convertView = inflater.inflate(this.groupLayout, null); }
        TextView text = (TextView)convertView.findViewById(R.id.text1);
        text.setText(title);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Beacon beacon = this.scanArray.getArray().get(groupPosition).getBeacons().get(childPosition);
        String info = infohelper.getCurrentInfo(beacon);
        View view = convertView;
        if (groupPosition > 0){
            CheckHolder checkHolder;
            if (convertView == null || !(convertView.getTag() instanceof CheckHolder)){
                view = this.inflater.inflate(this.checkItemLayout, parent, false);
                checkHolder = new CheckHolder(view);
                view.setTag(checkHolder);
            } else { checkHolder = (CheckHolder)view.getTag(); }
            checkHolder.checkedView.setText(info);
            checkHolder.checkedView.setChecked(selected.contains(childPosition));
        }
        else {
            SimpleHolder simpleHolder;
            if (convertView == null || !(convertView.getTag() instanceof SimpleHolder)){
                view = this.inflater.inflate(this.simpleItemLayout, parent, false);
                simpleHolder = new SimpleHolder(view);
                view.setTag(simpleHolder);
            } else { simpleHolder = (SimpleHolder)view.getTag(); }
            simpleHolder.simpleView.setText(info);
            simpleHolder.simpleView.setEnabled(false);
        }
        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    class CheckHolder {
        public CheckedTextView checkedView;
        public CheckHolder(View view){
            checkedView = (CheckedTextView)view.findViewById(AssignAdapter.checkItem);
        }
    }

    class SimpleHolder {
        public TextView simpleView;
        public SimpleHolder(View view){
            simpleView = (TextView)view.findViewById(AssignAdapter.simpleItem);
        }
    }
}