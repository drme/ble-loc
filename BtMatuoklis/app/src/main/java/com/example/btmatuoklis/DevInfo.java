package com.example.btmatuoklis;

public class DevInfo {
    public String Name;
    public String Mac;
    public short Rssi;

    public DevInfo(){}

    public DevInfo(String name, String mac, short rssi){
        this.Name = name;
        this.Mac = mac;
        this.Rssi = rssi;
    }

    public void updateRssi(short rssi){
        this.Rssi = rssi;
    }

    public String getName(){
        return this.Name;
    }

    public String getMac(){
        return this.Mac;
    }

    public short getRssi() { return this.Rssi; }
}
