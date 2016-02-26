package com.example.btmatuoklis.classes;

import java.util.ArrayList;

public class Beacon {
    private String Name;
    private String mac;
    private ArrayList<Byte> calibratedRSSI;
    private ArrayList<Byte> rssi;
    private int id;

    public Beacon(){
        this.calibratedRSSI = new ArrayList<Byte>();
        this.rssi = new ArrayList<Byte>();
    }

    public Beacon(String name, String mac){
        this.Name = name;
        this.mac = mac;
        this.calibratedRSSI = new ArrayList<Byte>();
        this.rssi = new ArrayList<Byte>();
    }

    public Beacon(String name, String mac, ArrayList<Byte> calibratedRSSI){
        this.Name = name;
        this.mac = mac;
        this.calibratedRSSI = calibratedRSSI;
        this.rssi = new ArrayList<Byte>();
    }

    public Beacon(int id, String name, String mac, ArrayList<Byte> calibratedRSSI){
        this.id = id;
        this.Name = name;
        this.mac = mac;
        this.calibratedRSSI = calibratedRSSI;
        this.rssi = new ArrayList<Byte>();
    }

    public void setRSSI(byte rssi) {
        if (this.rssi.size() == 5){
            this.rssi.remove(0);
            this.rssi.add(rssi);
        }
        else {
            this.rssi.add(rssi);
        }
    }

    public String getName(){ return this.Name; }

    public String getMAC(){ return this.mac; }

    public ArrayList<Byte> getCalibratedRSSI() { return this.calibratedRSSI; }

    public Byte getCurrentRSSI(){
        return this.rssi.get(rssi.size()-1);
    }

    public ArrayList<Byte> getPreviousRSSI(){
        ArrayList<Byte> previousRSSI = new ArrayList<Byte>();
        previousRSSI.addAll(this.rssi);
        previousRSSI.remove(this.rssi.size()-1);
        return previousRSSI;
    }

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
        info += "\nRSSI: " + getPreviousRSSI() + " Current: " + getCurrentRSSI();
        info += "\n" + RangeCalculator.getRange(txPower, this.getCurrentRSSI());
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
