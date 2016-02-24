package com.example.btmatuoklis.classes;

import java.util.ArrayList;

public class Beacon {
    private String Name;
    private String mac;
    private ArrayList<Byte> calibratedRSSI;
    private byte currentRSSI;
    private byte previousRSSI;
    private int id;

    public Beacon(){
        this.calibratedRSSI = new ArrayList<Byte>();
    }

    public Beacon(String name, String mac){
        this.Name = name;
        this.mac = mac;
        this.calibratedRSSI = new ArrayList<Byte>();
    }

    public Beacon(String name, String mac, ArrayList<Byte> calibratedRSSI){
        this.Name = name;
        this.mac = mac;
        this.calibratedRSSI = calibratedRSSI;
    }

    public void setRSSI(byte rssi) {
        this.previousRSSI = this.currentRSSI;
        this.currentRSSI = rssi;
    }

    public String getName(){ return this.Name; }

    public String getMAC(){ return this.mac; }

    public byte getCurrentRSSI() { return this.currentRSSI; }

    public byte getPreviousRSSI() { return this.previousRSSI; }

    public ArrayList<Byte> getCalibratedRSSI() { return this.calibratedRSSI; }

    //BT irenginio informacija (List formavimui)
    public String getInfo() {
        String info = "Pavadinimas: " + this.Name;
        info += "\nMAC: " + this.mac;
        return info;
    }

    //BT irenginio papildoma informacija (List formavimui)
    public String getCurrentInfo(byte txPower){
        String info = "Pavadinimas: " + this.Name;
        info += "\nMAC: " + this.mac;
        info += "\nPrevious RSSI: " + this.previousRSSI;
        info += " Current RSSI: " + this.currentRSSI;
        info += "\n" + RangeCalculator.getRange(txPower, this.currentRSSI);
        return info;
    }

    public String getCalibrationCount(){
        String info = "Pavadinimas: " + this.Name;
        info += "\nMAC: " + this.mac;
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
        return "Beacon [id=" + id + ", name=" + Name + ", mac=" + mac + "]";
    }
}
