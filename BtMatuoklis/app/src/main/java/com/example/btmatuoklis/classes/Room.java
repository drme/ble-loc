package com.example.btmatuoklis.classes;

import java.util.ArrayList;

public class Room {
    private String name;
    private ArrayList<DeviceInfo> devices;

    public Room(){
        this.devices = new ArrayList<DeviceInfo>();
    }

    public Room(String nm){
        this.name = nm;
        this.devices = new ArrayList<DeviceInfo>();
    }

    public void setName(String nm){
        this.name = nm;
    }

    public String getName(){
        return this.name;
    }

    public ArrayList<String> getMACList(){
        ArrayList<String> res = new ArrayList<String>();
        for (int i = 0; i < this.devices.size(); i++){
            res.add(this.devices.get(i).getMAC());
        }
        return res;
    }

    public ArrayList<String> getDevicesCalibrationCount(){
        ArrayList<String> res = new ArrayList<String>();
        for (int i = 0; i < this.devices.size(); i++){
            res.add(this.devices.get(i).getCalibrationCount());
        }
        return res;
    }

    public ArrayList<Boolean> getCalibratedDevices(){
        ArrayList<Boolean> res = new ArrayList<Boolean>();
        for (int i = 0; i < this.devices.size(); i++){
            if (this.devices.get(i).getCalibratedRSSI().size() == 0){
                res.add(i, false);
            }
            else {
                res.add(i, true);
            }
        }
        return res;
    }

    public ArrayList<DeviceInfo> getDevices(){
        return this.devices;
    }

    //Grazina false, jeigu bent vienas irenginys neturi
    //nei vienos RSSI reiksmes kalibravimo masyve
    public boolean isCalibrated(){
        return !getCalibratedDevices().contains(false);
    }
}