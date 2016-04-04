package com.example.btmatuoklis.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.btmatuoklis.R;
import com.example.btmatuoklis.classes.Beacon;
import com.example.btmatuoklis.classes.RoomsArray;
import com.example.btmatuoklis.helpers.ChartHelper;
import com.jjoe64.graphview.GraphView;

import java.util.Collections;

//List adapter for scan/room detection mode
public class ScanAdapter extends BaseExpandableListAdapter {

    private int groupLayout;
    private int itemLayout;
    private LayoutInflater inflater;
    private RoomsArray rooms, enviroment;
    private ChartHelper charthelper;

    public ScanAdapter(Context context, RoomsArray rooms, RoomsArray enviroment) {
        this.groupLayout = R.layout.list_group;
        this.itemLayout = R.layout.list_scan_item;
        this.rooms = rooms;
        this.enviroment = enviroment;
        this.charthelper = new ChartHelper();
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
        Byte rssiMin = 0;
        Beacon beacon = this.enviroment.getArray().get(groupPosition).getBeacons().get(childPosition);
        Beacon temp = rooms.findBeacon(beacon.getMAC());
        if (temp != null && !temp.getFullRSSI().isEmpty()){ rssiMin = Collections.min(temp.getFullRSSI()); }
        convertView = this.inflater.inflate(this.itemLayout, parent, false);
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
