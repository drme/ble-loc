package com.example.btmatuoklis.classes;

import java.util.ArrayList;

public class DeviceInfo {
    private String deviceName;
    private String deviceMAC;
    //private byte[] calibratedRSSI;
    private ArrayList<Byte> calibratedRSSI;
    private byte currentRSSI;
    private byte previousRSSI;

    public DeviceInfo(){}

    public DeviceInfo(String name, String mac){
        this.deviceName = name;
        this.deviceMAC = mac;
    }

    public void setRSSI(byte rssi) {
        this.previousRSSI = this.currentRSSI;
        this.currentRSSI = rssi;
    }

    public String getName(){ return this.deviceName; }

    public String getMAC(){ return this.deviceMAC; }

    public byte getCurrentRSSI() { return this.currentRSSI; }

    public byte getPreviousRSSI() { return this.previousRSSI; }

    //public byte[] getCalibratedRSSI() { return this.calibratedRSSI; }

    public ArrayList<Byte> getCalibratedRSSI() { return this.calibratedRSSI; }

    //BT irenginio informacija (List formavimui)
    public String getInfo() {
        String info = "Pavadinimas: " + this.deviceName;
        info += "\nMAC: " + this.deviceMAC;
        return info;
    }

    //BT irenginio papildoma informacija (List formavimui)
    public String getCurrentInfo(byte txPower){
        String info = "Pavadinimas: " + this.deviceName;
        info += "\nMAC: " + this.deviceMAC;
        info += "\nPrevious RSSI: " + this.previousRSSI;
        info += " Current RSSI: " + this.currentRSSI;
        info += "\n" + RangeCalculator.getRange(txPower, this.currentRSSI);
        return info;
    }
}
