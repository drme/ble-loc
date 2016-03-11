package com.example.btmatuoklis.classes;

import com.example.btmatuoklis.activities.MainActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class Beacon {
    private String name;
    private String mac;
    private ArrayList<Byte> rssi;
    private int id;

    private RangeCalculator calculator = new RangeCalculator();

    public Beacon(){
        this.rssi = new ArrayList<Byte>();
    }

    public Beacon(String name, String mac){
        this.name = name;
        this.mac = mac;
        this.rssi = new ArrayList<Byte>();
    }

    public Beacon(String name, String mac, ArrayList<Byte> rssi){
        this.name = name;
        this.mac = mac;
        this.rssi = rssi;
    }

    public Beacon(int id, String name, String mac, ArrayList<Byte> rssi){
        this.id = id;
        this.name = name;
        this.mac = mac;
        this.rssi = rssi;
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

    public byte getCurrentRSSI(){ return this.rssi.get(rssi.size()-1); }

    public ArrayList<Byte> getPreviousRSSI(){
        ArrayList<Byte> previousRSSI = new ArrayList<Byte>();
        previousRSSI.addAll(this.rssi);
        previousRSSI.remove(this.rssi.size()-1);
        return previousRSSI;
    }

    public ArrayList<Byte> getFullRSSI(){ return this.rssi; }

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
                info += "\nKalibracijos RSSI reikšmių: " + this.rssi.size();
                break;
            default:
                break;
        }
        return info;
    }

    public byte getRSSIMin(){ return Collections.min(this.rssi); }

    public byte getRSSIMax(){
        return Collections.max(this.rssi);
    }

    public byte getRSSIAverage(){
        long sum = 0;
        int size = this.rssi.size();
        if(!this.rssi.isEmpty()){
            for (int i = 0; i < size; i++){
                sum += this.rssi.get(i);
            }
            return (byte)(sum/size);
        }
        return (byte)sum;
    }

    public ArrayList<Byte> getUniqueRSSIs(){
        HashSet<Byte> unique = new HashSet<Byte>();
        ArrayList<Byte> res = new ArrayList<Byte>();
        unique.addAll(this.rssi);
        res.addAll(unique);
        return res;
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }
}
