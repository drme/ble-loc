package com.example.btmatuoklis;

public class DevInfo {
    public String Name;
    public String Mac;
    public Integer Rssi;

    public DevInfo(){}

    public DevInfo(String name, String mac, Integer rssi){
        this.Name = name;
        this.Mac = mac;
        this.Rssi = rssi;
    }

    public void updateRssi(Integer rssi){
        this.Rssi = rssi;
    }

    public String getName(){
        return this.Name;
    }

    public String getMac(){
        return this.Mac;
    }

    public Integer getRssi(){
        return this.Rssi;
    }

    public String getInfo() {
        return "Pavadinimas: "+Name+"\n"+"MAC: "+Mac+"\n"+"RSSI: "+Rssi;
    }
}
