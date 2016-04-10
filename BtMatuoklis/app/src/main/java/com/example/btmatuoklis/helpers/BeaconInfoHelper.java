package com.example.btmatuoklis.helpers;

import android.content.Context;

import com.example.btmatuoklis.R;
import com.example.btmatuoklis.classes.Beacon;

public class BeaconInfoHelper {

    String name, mac, rssi, rssi_now, num_calib, range;

    public BeaconInfoHelper(Context context){
        this.name = context.getString(R.string.beacon_name);
        this.mac = context.getString(R.string.beacon_mac);
        this.rssi = context.getString(R.string.beacon_rssi);
        this.rssi_now = context.getString(R.string.beacon_rssi_current);
        this.num_calib = context.getString(R.string.beacon_calibration_number);
        this.range = context.getString(R.string.beacon_range);
    }

    public String getInfo(Beacon beacon){
        String info = this.name+" "+beacon.getName();
        info += "\n"+this.mac+" "+beacon.getMAC();
        return info;
    }

    public String getCurrentInfo(Beacon beacon){
        String info = this.getInfo(beacon);
        info += "\n"+this.rssi+" "+beacon.getPreviousRSSI()+" "+this.rssi_now+" "+beacon.getCurrentRSSI();
        return info;
    }

    public String getCalibrationInfo(Beacon beacon){
        String info = this.getInfo(beacon);
        info += "\n"+this.num_calib+" "+beacon.getFullRSSI().size();
        return info;
    }
}
