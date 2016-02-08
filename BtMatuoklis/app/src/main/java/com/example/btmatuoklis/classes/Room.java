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
            res.add(devices.get(i).getMAC());
        }
        return res;
    }

    public ArrayList<DeviceInfo> getDevices(){
        return devices;
    }
}
