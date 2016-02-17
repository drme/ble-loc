package com.example.btmatuoklis.classes;

import java.util.ArrayList;

public class Beacon {
    private String beaconName;
    private String beaconMAC;
    private ArrayList<Byte> calibratedRSSI;
    private byte currentRSSI;
    private byte previousRSSI;
    private int id;

    public Beacon(){
        this.calibratedRSSI = new ArrayList<Byte>();
    }

    public Beacon(String name, String mac){
        this.beaconName = name;
        this.beaconMAC = mac;
        this.calibratedRSSI = new ArrayList<Byte>();
    }

    public void setRSSI(byte rssi) {
        this.previousRSSI = this.currentRSSI;
        this.currentRSSI = rssi;
    }

    public String getName(){ return this.beaconName; }

    public String getMAC(){ return this.beaconMAC; }

    public byte getCurrentRSSI() { return this.currentRSSI; }

    public byte getPreviousRSSI() { return this.previousRSSI; }

    public ArrayList<Byte> getCalibratedRSSI() { return this.calibratedRSSI; }

    //BT irenginio informacija (List formavimui)
    public String getInfo() {
        String info = "Pavadinimas: " + this.beaconName;
        info += "\nMAC: " + this.beaconMAC;
        return info;
    }

    //BT irenginio papildoma informacija (List formavimui)
    public String getCurrentInfo(byte txPower){
        String info = "Pavadinimas: " + this.beaconName;
        info += "\nMAC: " + this.beaconMAC;
        info += "\nPrevious RSSI: " + this.previousRSSI;
        info += " Current RSSI: " + this.currentRSSI;
        info += "\n" + RangeCalculator.getRange(txPower, this.currentRSSI);
        return info;
    }

    public String getCalibrationCount(){
        String info = "Pavadinimas: " + this.beaconName;
        info += "\nMAC: " + this.beaconMAC;
        info += "\nKalibracijos RSSI reikšmių: " + this.calibratedRSSI.size();
        return info;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Beacon [id=" + id + ", name=" + beaconName + ", mac=" + beaconMAC + "]";
    }
}
