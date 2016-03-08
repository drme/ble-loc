package com.example.btmatuoklis.classes;

public class Calibration {

    private int id;
    private int roomID;
    private int beaconID;
    private String RSSI;

    public Calibration(){}

    public Calibration(int id, int roomID, int beaconID, String RSSI)
    {
        this.id = id;
        this.roomID = roomID;
        this.beaconID = beaconID;
        this.RSSI = RSSI;
    }

    public Calibration(int roomID, int beaconID, String RSSI)
    {
        this.roomID = roomID;
        this.beaconID = beaconID;
        this.RSSI = RSSI;
    }

    public Calibration(int roomID, int beaconID)
    {
        this.roomID = roomID;
        this.beaconID = beaconID;
    }

    public int getID() {
        return this.id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public int getRoomID() {
        return roomID;
    }

    public void setRoomID(int roomId) {
        this.roomID = roomId;
    }

    public int getBeaconID() {
        return this.beaconID;
    }

    public void setBeaconID(int beaconID) {
        this.beaconID = beaconID;
    }

    public String getRSSI() {
        return this.RSSI;
    }

    public void setRSSI(String RSSI) { this.RSSI = RSSI; }
}
