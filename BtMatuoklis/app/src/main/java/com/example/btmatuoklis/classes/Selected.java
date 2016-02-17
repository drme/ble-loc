package com.example.btmatuoklis.classes;

/**
 * Created by sauli_000 on 2016-02-12.
 */
public class Selected {
    private String RoomName;
    private String BeaconName;
    private String BeaconMac;
    private String RSSI;

    public Selected(){}

    public String getRoomName() {
        return RoomName;
    }

    public void setRoomName(String roomName) {
        RoomName = roomName;
    }

    public String getRSSI() {
        return RSSI;
    }

    public void setRSSI(String RSSI) {
        this.RSSI = RSSI;
    }

    public String getBeaconMac() {
        return BeaconMac;
    }

    public void setBeaconMac(String beaconMac) {
        BeaconMac = beaconMac;
    }

    public String getBeaconName() {
        return BeaconName;
    }

    public void setBeaconName(String beaconName) {
        BeaconName = beaconName;
    }

    @Override
    public String toString() {
        return "Selected{" +
                "RoomName='" + RoomName + '\'' +
                ", BeaconName='" + BeaconName + '\'' +
                ", BeaconMac='" + BeaconMac + '\'' +
                ", RSSI='" + RSSI + '\'' +
                '}';
    }

    public Selected(String roomName, String beaconName, String beaconMac, String RSSI) {
        RoomName = roomName;
        BeaconName = beaconName;
        BeaconMac = beaconMac;
        this.RSSI = RSSI;
    }
}


