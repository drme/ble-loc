package com.example.btmatuoklis.classes;

import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;
import java.util.Random;

public class ScanTools{

    public ScanTools(){}

    private void assignLogic(String name, String mac, byte rssi, ArrayList<String> macs, RoomsArray enviroment){
        int index;
        if (macs.contains(mac)){
            if (enviroment.getArray().get(0).getMACList().contains(mac)){
                index = enviroment.getArray().get(0).getMACList().indexOf(mac);
                enviroment.getArray().get(0).getBeacons().get(index).setRSSI(rssi);
            }
            else {
                enviroment.getArray().get(0).getBeacons().add(new Beacon(name, mac, rssi));
            }
        }
        else {
            if (enviroment.getArray().get(1).getMACList().contains(mac)){
                index = enviroment.getArray().get(1).getMACList().indexOf(mac);
                enviroment.getArray().get(1).getBeacons().get(index).setRSSI(rssi);
            }
            else {
                enviroment.getArray().get(1).getBeacons().add(new Beacon(name, mac, rssi));
            }
        }
    }

    private void scanLogic(String name, String mac, byte rssi, RoomsArray rooms, RoomsArray enviroment){
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

    private void calibrateLogic(String mac, byte rssi, Room room){
        ArrayList<String> macs = room.getMACList();
        if (macs.contains(mac)){
            int index = macs.indexOf(mac);
            room.getBeacons().get(index).getFullRSSI().add(rssi);
        }
    }

    public void assign(BluetoothDevice device, int rssi, ArrayList<String> macs, RoomsArray enviroment){
        String currentName = device.getName();
        String currentMAC = device.getAddress();
        byte currentRSSI = (byte)rssi;
        this.assignLogic(currentName, currentMAC, currentRSSI, macs, enviroment);
    }

    public void scan(BluetoothDevice device, int rssi, RoomsArray roomsArray, RoomsArray enviroment){
        String currentName = device.getName();
        String currentMAC = device.getAddress();
        byte currentRSSI = (byte)rssi;
        this.scanLogic(currentName, currentMAC, currentRSSI, roomsArray, enviroment);
    }

    public void calibrate(BluetoothDevice device, int rssi, Room room){
        String currentMAC = device.getAddress();
        byte currentRSSI = (byte)rssi;
        this.calibrateLogic(currentMAC, currentRSSI, room);
    }

    //------------Debug------------

    public void fakeAssign(int generatedBeacons, int generatedRSSIMin, int generatedRSSIMax, ArrayList<String> macs, RoomsArray enviroment){
        int beaconNumber = inetegerGenerator(1, generatedBeacons);
        String currentName = "Beacon" + beaconNumber;
        String currentMAC = "MAC" + beaconNumber;
        byte currentRSSI = (byte)inetegerGenerator(generatedRSSIMin, generatedRSSIMax);
        this.assignLogic(currentName, currentMAC, currentRSSI, macs, enviroment);
    }

    public void fakeScan(int generatedBeacons, int generatedRSSIMin, int generatedRSSIMax, RoomsArray roomsArray, RoomsArray enviroment){
        int beaconNumber = inetegerGenerator(1, generatedBeacons);
        String currentName = "Beacon" + beaconNumber;
        String currentMAC = "MAC" + beaconNumber;
        byte currentRSSI = (byte)inetegerGenerator(generatedRSSIMin, generatedRSSIMax);
        this.scanLogic(currentName, currentMAC, currentRSSI, roomsArray, enviroment);
    }

    public void fakeCalibrate(int generatedBeacons, int generatedRSSIMin, int generatedRSSIMax, Room room){
        int beaconNumber = inetegerGenerator(1, generatedBeacons);
        String currentMAC = "MAC" + beaconNumber;
        byte currentRSSI = (byte)inetegerGenerator(generatedRSSIMin, generatedRSSIMax);
        this.calibrateLogic(currentMAC, currentRSSI, room);
    }

    int inetegerGenerator(int min, int max){
        Random random = new Random();
        int value = random.nextInt(max - min + 1) + min;
        return value;
    }
}
