package com.example.btmatuoklis.classes;

/**
 * Created by sauli_000 on 2016-02-11.
 */
public class Calibration {


    private int id;
    private int roomId;
    private int beaconId;
    private String RSSI;

    public Calibration(){}

    public Calibration(int roomId, int beaconId, String RSSI)
    {
        this.roomId = roomId;
        this.beaconId = beaconId;
        this.RSSI = RSSI;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getBeaconId() {
        return beaconId;
    }

    public void setBeaconId(int beaconId) {
        this.beaconId = beaconId;
    }

    public String getRSSI() {
        return RSSI;
    }

    public void setRSSI(String RSSI) {
        this.RSSI = RSSI;
    }

    public String toString() {
        return "RSSI [roomId=" + id + " roomId=" + roomId +
                ", beaconId=" + beaconId +", RSSI " + RSSI +" \"]";
    }
}
