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

    public void addDevice(DeviceInfo di){
        this.devices.add(di);
    }

    public void addDeviceAtIndex(int in, DeviceInfo di){
        this.devices.add(in, di);
    }

    public void removeDeviceAtIndex(int in){
        this.devices.remove(in);
    }

    public void removeAllDevices(){
        this.devices.clear();
    }

    public String getName(){
        return this.name;
    }

    public int getSize(){
        return this.devices.size();
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
