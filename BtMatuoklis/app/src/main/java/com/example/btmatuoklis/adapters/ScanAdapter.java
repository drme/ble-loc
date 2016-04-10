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
import com.example.btmatuoklis.helpers.BeaconInfoHelper;
import com.example.btmatuoklis.helpers.ChartHelper;
import com.jjoe64.graphview.GraphView;

import java.util.ArrayList;
import java.util.Collections;

//List adapter for scan/room detection mode
public class ScanAdapter extends BaseExpandableListAdapter {

    private static int textItem = R.id.text1;
    private static int chartItem = R.id.chart1;
    private int groupLayout;
    private int itemLayout;
    private LayoutInflater inflater;
    private RoomsArray rooms, enviroment;
    private BeaconInfoHelper infohelper;
    private ChartHelper charthelper;

    public ScanAdapter(Context context, RoomsArray rooms, RoomsArray enviroment) {
        this.groupLayout = R.layout.list_group;
        this.itemLayout = R.layout.list_scan_item;
        this.rooms = rooms;
        this.enviroment = enviroment;
        this.infohelper = new BeaconInfoHelper(context);
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
        String info = this.infohelper.getCurrentInfo(beacon);
        Beacon temp = this.rooms.findBeacon(beacon.getMAC());
        if (temp != null && !temp.getFullRSSI().isEmpty()){ rssiMin = Collections.min(temp.getFullRSSI()); }
        View view = convertView;
        ChartHolder chartholder;
        if (convertView == null){
            view = this.inflater.inflate(this.itemLayout, parent, false);
            chartholder = new ChartHolder(view);
            view.setTag(chartholder);
            charthelper.setScanChart(chartholder.chartView, rssiMin);
        } else { chartholder = (ChartHolder)view.getTag(); }
        chartholder.textView.setText(info);
        charthelper.resetScanChart(chartholder.chartView, rssiMin);
        charthelper.updateScanChart(chartholder.chartView, rssiMin, beacon);
        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    class ChartHolder {
        public TextView textView;
        public GraphView chartView;
        public ChartHolder(View view){
            textView = (TextView)view.findViewById(ScanAdapter.textItem);
            chartView = (GraphView)view.findViewById(ScanAdapter.chartItem);
        }
    }
}
