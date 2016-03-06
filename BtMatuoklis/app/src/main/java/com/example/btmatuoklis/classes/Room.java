package com.example.btmatuoklis.classes;

import java.util.ArrayList;

public class Room {
    private String name;
    private ArrayList<Beacon> beacons;
    private int id;

    public Room(){
        this.beacons = new ArrayList<Beacon>();
    }

    public Room(String nm){
        this.name = nm;
        this.beacons = new ArrayList<Beacon>();
    }

    public Room(int id, String nm){
        this.id = id;
        this.name = nm;
        this.beacons = new ArrayList<Beacon>();
    }

    public void setName(String nm){
        this.name = nm;
    }

    public String getName(){
        return this.name;
    }

    public ArrayList<String> getMACList(){
        ArrayList<String> res = new ArrayList<String>();
        for (int i = 0; i < this.beacons.size(); i++){
            res.add(this.beacons.get(i).getMAC());
        }
        return res;
    }

    public ArrayList<Byte> getBeaconsCurrentRSSIs(){
        ArrayList<Byte> res = new ArrayList<Byte>();
        for (int i = 0; i < this.beacons.size(); i++){
            res.add(this.beacons.get(i).getCurrentRSSI());
        }
        return res;
    }

    public ArrayList<Integer> getBeaconsIDs(){
        ArrayList<Integer> res = new ArrayList<Integer>();
        for (int i = 0; i < this.beacons.size(); i++){
            res.add(this.beacons.get(i).getID());
        }
        return res;
    }

    public ArrayList<String> getBeaconsCalibrationCount(){
        ArrayList<String> res = new ArrayList<String>();
        for (int i = 0; i < this.beacons.size(); i++){
            res.add(this.beacons.get(i).getInfo("calibration"));
        }
        return res;
    }

    public ArrayList<Boolean> getCalibratedBeacons(){
        ArrayList<Boolean> res = new ArrayList<Boolean>();
        for (int i = 0; i < this.beacons.size(); i++){
            if (this.beacons.get(i).getFullRSSI().size() == 0){
                res.add(i, false);
            }
            else {
                res.add(i, true);
            }
        }
        return res;
    }

    public ArrayList<String> getCurrentInfoList(){
        ArrayList<String> res = new ArrayList<String>();
        for (int i = 0; i < this.beacons.size(); i++){
            res.add(this.beacons.get(i).getInfo("current"));
        }
        return res;
    }

    public ArrayList<Beacon> getBeacons(){
        return this.beacons;
    }

    //Grazina false, jeigu bent vienas irenginys neturi
    //nei vienos RSSI reiksmes kalibravimo masyve
    public boolean isCalibrated(){
        return !getCalibratedBeacons().contains(false);
    }

    //Grazina true, jeigu bent vienas irenginys turi
    //nors viena RSSI reiksme kalibravimo masyve
    public boolean isCalibrationStarted(){ return getCalibratedBeacons().contains(true); }

    public int getID() {
        return id;
    }

    public void setID(int id) { this.id = id; }
}
