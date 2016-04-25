package com.example.btmatuoklis.classes;

import java.util.ArrayList;

public class ScanTools{

    Room sample;

    public ScanTools(){ this.sample = new Room("Sample"); }

    public void prepareSample(){ this.sample.getBeacons().clear(); }

    public void assignSample(String name, String mac, byte rssi){
        ArrayList<String> macs = this.sample.getMACList();
        if (macs.contains(mac)){ this.sample.findBeacon(mac).getFullRSSI().add(rssi); }
        else { this.sample.getBeacons().add(new Beacon(name, mac, rssi)); }
    }

    public void assignAppend(ArrayList<String> macs, RoomsArray enviroment){
        ArrayList<Beacon> beacons = this.sample.getBeacons();
        for (int i = 0; i < beacons.size(); i++){
            Beacon beacon = beacons.get(i);
            this.assign(beacon.getName(), beacon.getMAC(), beacon.getRSSIAverage(), macs, enviroment);
        }
        this.prepareSample();
    }

    public void assign(String name, String mac, byte rssi, ArrayList<String> macs, RoomsArray enviroment){
        int beaconIndex, roomIndex;
        if (macs.contains(mac)){ roomIndex = 0; }
        else { roomIndex = 1; }
        if (enviroment.getArray().get(roomIndex).getMACList().contains(mac)){
            beaconIndex = enviroment.getArray().get(roomIndex).getMACList().indexOf(mac);
            enviroment.getArray().get(roomIndex).getBeacons().get(beaconIndex).setRSSI(rssi);
        }
        else { enviroment.getArray().get(roomIndex).getBeacons().add(new Beacon(name, mac, rssi)); }
    }

    public void scanSample(String name, String mac, byte rssi){
        ArrayList<String> macs = this.sample.getMACList();
        if (macs.contains(mac)){ this.sample.findBeacon(mac).getFullRSSI().add(rssi); }
        else { this.sample.getBeacons().add(new Beacon(name, mac, rssi)); }
    }

    public void scanAppend(RoomsArray rooms, RoomsArray enviroment){
        ArrayList<Beacon> beacons = this.sample.getBeacons();
        for (int i = 0; i < beacons.size(); i++){
            Beacon beacon = beacons.get(i);
            this.scan(beacon.getName(), beacon.getMAC(), beacon.getRSSIAverage(), rooms, enviroment);
        }
        this.prepareSample();
    }

    public void scan(String name, String mac, byte rssi, RoomsArray rooms, RoomsArray enviroment){
        int roomIndex = rooms.findRoomIndex(mac);
        if (roomIndex > -1){
            String roomName = rooms.getArray().get(roomIndex).getName();
            int environmentIndex = enviroment.getRoomIndex(roomName);
            if (environmentIndex > -1){
                int beaconIndex = enviroment.getArray().get(environmentIndex).findBeaconIndex(mac);
                if (beaconIndex > -1){ enviroment.getArray().get(environmentIndex).getBeacons().get(beaconIndex).setRSSI(rssi); }
                else { enviroment.getArray().get(environmentIndex).getBeacons().add(new Beacon(environmentIndex, name, mac, rssi)); }
            } else {
                enviroment.getArray().add(new Room(roomName));
                int newIndex = enviroment.getArray().size() - 1;
                enviroment.getArray().get(newIndex).getBeacons().add(new Beacon(name, mac, rssi));
            }
        } else {
            int beaconIndex = enviroment.getArray().get(0).findBeaconIndex(mac);
            if (beaconIndex > -1){ enviroment.getArray().get(0).getBeacons().get(beaconIndex).setRSSI(rssi); }
            else { enviroment.getArray().get(0).getBeacons().add(new Beacon(beaconIndex, name, mac, rssi)); }
        }
    }

    public void calibratePrepare(Room room){
        this.prepareSample();
        ArrayList<Beacon> temp = room.getBeacons();
        ArrayList<Beacon> beacons = this.sample.getBeacons();
        for (int i = 0; i < temp.size(); i++){
            beacons.add(new Beacon(temp.get(i).getName(), temp.get(i).getMAC()));
        }
    }

    public void calibrateSample(String mac, byte rssi, ArrayList<String> macs){
        if (macs.contains(mac)){ this.sample.findBeacon(mac).getFullRSSI().add(rssi); }
    }

    public void calibrateAppend(Room room){
        ArrayList<Beacon> beacons = this.sample.getBeacons();
        for (int i = 0; i < beacons.size(); i++){
            Beacon beacon = beacons.get(i);
            if (!beacon.getFullRSSI().isEmpty()){
                this.calibrate(beacon.getMAC(), beacon.getRSSIAverage(), room);
            }
        }
        for (int i = 0; i < beacons.size(); i++){ beacons.get(i).getFullRSSI().clear(); }
    }

    public void calibrate(String mac, byte rssi, Room room){
        room.findBeacon(mac).getFullRSSI().add(rssi);
    }
}
