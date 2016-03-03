package com.example.btmatuoklis.classes;

import com.example.btmatuoklis.activities.MainActivity;

import java.util.ArrayList;

public class Beacon {
    private String name;
    private String mac;
    private ArrayList<Byte> calibratedRSSI;
    private ArrayList<Byte> rssi;
    private int id;

    private RangeCalculator calculator = new RangeCalculator();

    public Beacon(){
        this.calibratedRSSI = new ArrayList<Byte>();
        this.rssi = new ArrayList<Byte>();
    }

    public Beacon(String name, String mac){
        this.name = name;
        this.mac = mac;
        this.calibratedRSSI = new ArrayList<Byte>();
        this.rssi = new ArrayList<Byte>();
    }

    public Beacon(String name, String mac, ArrayList<Byte> calibratedRSSI){
        this.name = name;
        this.mac = mac;
        this.calibratedRSSI = calibratedRSSI;
        this.rssi = new ArrayList<Byte>();
    }

    public Beacon(int id, String name, String mac, ArrayList<Byte> calibratedRSSI){
        this.id = id;
        this.name = name;
        this.mac = mac;
        this.calibratedRSSI = calibratedRSSI;
        this.rssi = new ArrayList<Byte>();
    }

    private byte getShadow(){
        Settings settings = MainActivity.settings;
        return settings.getShadow();
    }

    private byte getTXPower(){
        Settings settings = MainActivity.settings;
        return settings.getTXPower();
    }

    public void setRSSI(byte rssi) {
        if (this.rssi.size() == getShadow()+1){
            this.rssi.remove(0);
            this.rssi.add(rssi);
        }
        else {
            this.rssi.add(rssi);
        }
    }

    public String getName(){ return this.name; }

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

    public ArrayList<Byte> getFullRSSI(){
        return this.rssi;
    }

    //BT irenginio informacija (List formavimui)
    public String getInfo(String choice) {
        String info = "Pavadinimas: " + this.name;
        info += "\nMAC: " + this.mac;
        switch (choice) {
            case "current":
                info += "\nRSSI: " + getPreviousRSSI() + " Last: " + getCurrentRSSI();
                info += "\n" + calculator.getRange(getTXPower(), getCurrentRSSI());
                break;
            case "calibration":
                info += "\nKalibracijos RSSI reikšmių: " + this.calibratedRSSI.size();
                break;
            default:
                break;
        }
        return info;
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }
}
