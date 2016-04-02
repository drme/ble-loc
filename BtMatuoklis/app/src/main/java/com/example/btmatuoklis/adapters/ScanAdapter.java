package com.example.btmatuoklis.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.btmatuoklis.R;
import com.example.btmatuoklis.classes.Beacon;
import com.example.btmatuoklis.classes.GlobalClass;
import com.example.btmatuoklis.classes.Room;
import com.example.btmatuoklis.classes.RoomsArray;
import com.example.btmatuoklis.helpers.ChartHelper;
import com.jjoe64.graphview.GraphView;

import java.util.ArrayList;
import java.util.Collections;

public class ScanAdapter extends BaseExpandableListAdapter {

    private int layout;
    private LayoutInflater inflater;
    private ArrayList<Room> enviroment;
    private ChartHelper charthelper;
    private RoomsArray rooms;

    public ScanAdapter(Context context, int resource, ArrayList<Room> enviroment) {
        this.layout = resource;
        this.enviroment = enviroment;
        this.charthelper = new ChartHelper();
        this.rooms = ((GlobalClass)context.getApplicationContext()).getRoomsArray();
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getGroupCount() {
        return this.enviroment.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.enviroment.get(groupPosition).getBeacons().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.enviroment.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.enviroment.get(groupPosition).getBeacons().get(childPosition);
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
        String title = this.enviroment.get(groupPosition).getName();
        title += " ("+this.enviroment.get(groupPosition).getBeacons().size()+")";
        if (convertView == null){ convertView = inflater.inflate(R.layout.list_scan_category, null); }
        TextView text = (TextView)convertView.findViewById(R.id.text1);
        text.setText(title);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Byte rssiMin = 0;
        Beacon beacon = this.enviroment.get(groupPosition).getBeacons().get(childPosition);
        Beacon temp = rooms.findBeacon(beacon.getMAC());
        if (temp != null && !temp.getFullRSSI().isEmpty()){ rssiMin = Collections.min(temp.getFullRSSI()); }
        convertView = this.inflater.inflate(this.layout, parent, false);
        TextView text = (TextView)convertView.findViewById(R.id.text1);
        GraphView chart = (GraphView)convertView.findViewById(R.id.chart1);
        text.setText(beacon.getInfo("current"));
        charthelper.setScanChart(chart, rssiMin);
        charthelper.updateScanChart(chart, rssiMin, beacon);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
