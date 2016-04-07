package com.example.btmatuoklis.classes;

import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;
import java.util.Random;

public class ScanTools{

    public ScanTools(){}

    public void assignLogic(BluetoothDevice device, int rssi, ArrayList<String> macs, RoomsArray enviroment){
        String currentName = device.getName();
        String currentMAC = device.getAddress();
        byte currentRSSI = (byte)rssi;
        int index;
        if (macs.contains(currentMAC)){
            if (enviroment.getArray().get(0).getMACList().contains(currentMAC)){
                index = enviroment.getArray().get(0).getMACList().indexOf(currentMAC);
                enviroment.getArray().get(0).getBeacons().get(index).setRSSI(currentRSSI);
            }
            else {
                enviroment.getArray().get(0).getBeacons().add(new Beacon(currentName, currentMAC, currentRSSI));
            }
        }
        else {
            if (enviroment.getArray().get(1).getMACList().contains(currentMAC)){
                index = enviroment.getArray().get(1).getMACList().indexOf(currentMAC);
                enviroment.getArray().get(1).getBeacons().get(index).setRSSI(currentRSSI);
            }
            else {
                enviroment.getArray().get(1).getBeacons().add(new Beacon(currentName, currentMAC, currentRSSI));
            }
        }
    }

    public void scanLogic(BluetoothDevice device, int rssi, RoomsArray roomsArray, RoomsArray enviroment){
        String currentName = device.getName();
        String currentMAC = device.getAddress();
        byte currentRSSI = (byte)rssi;
        int roomIndex = roomsArray.findRoomIndex(currentMAC);
        if (roomIndex > -1){
            String roomName = roomsArray.getArray().get(roomIndex).getName();
            int environmentIndex = enviroment.getRoomIndex(roomName);
            if (environmentIndex > -1){
                int beaconIndex = enviroment.getArray().get(environmentIndex).findBeaconIndex(currentMAC);
                if (beaconIndex > -1){ enviroment.getArray().get(environmentIndex).getBeacons().get(beaconIndex).setRSSI(currentRSSI); }
                else { enviroment.getArray().get(environmentIndex).getBeacons().add(new Beacon(environmentIndex, currentName, currentMAC, currentRSSI)); }
            } else {
                enviroment.getArray().add(new Room(roomName));
                int newIndex = enviroment.getArray().size() - 1;
                enviroment.getArray().get(newIndex).getBeacons().add(new Beacon(currentName, currentMAC, currentRSSI));
            }
        } else {
            int beaconIndex = enviroment.getArray().get(0).findBeaconIndex(currentMAC);
            if (beaconIndex > -1){ enviroment.getArray().get(0).getBeacons().get(beaconIndex).setRSSI(currentRSSI); }
            else { enviroment.getArray().get(0).getBeacons().add(new Beacon(beaconIndex, currentName, currentMAC, currentRSSI)); }
        }
    }

    public void calibrateLogic(BluetoothDevice device, int rssi, Room room){
        ArrayList<String> macs = room.getMACList();
        String currentMAC = device.getAddress();
        byte currentRSSI = (byte)rssi;
        if (macs.contains(currentMAC)){
            int index = macs.indexOf(currentMAC);
            room.getBeacons().get(index).getFullRSSI().add(currentRSSI);
        }
    }

    //------------Debug------------

    public void fakeAssignLogic(int generatedBeacons, int generatedRSSIMin, int generatedRSSIMax, ArrayList<String> macs, RoomsArray enviroment){
        int beaconNumber = inetegerGenerator(1, generatedBeacons);
        String currentName = "Beacon" + beaconNumber;
        String currentMAC = "MAC" + beaconNumber;
        byte currentRSSI = (byte)inetegerGenerator(generatedRSSIMin, generatedRSSIMax);
        int index;
        if (macs.contains(currentMAC)){
            if (enviroment.getArray().get(0).getMACList().contains(currentMAC)){
                index = enviroment.getArray().get(0).getMACList().indexOf(currentMAC);
                enviroment.getArray().get(0).getBeacons().get(index).setRSSI(currentRSSI);
            }
            else {
                enviroment.getArray().get(0).getBeacons().add(new Beacon(currentName, currentMAC, currentRSSI));
            }
        }
        else {
            if (enviroment.getArray().get(1).getMACList().contains(currentMAC)){
                index = enviroment.getArray().get(1).getMACList().indexOf(currentMAC);
                enviroment.getArray().get(1).getBeacons().get(index).setRSSI(currentRSSI);
            }
            else {
                enviroment.getArray().get(1).getBeacons().add(new Beacon(currentName, currentMAC, currentRSSI));
            }
        }
    }

    public void fakeScanLogic(int generatedBeacons, int generatedRSSIMin, int generatedRSSIMax, RoomsArray roomsArray, RoomsArray enviroment){
        int beaconNumber = inetegerGenerator(1, generatedBeacons);
        String currentName = "Beacon" + beaconNumber;
        String currentMAC = "MAC" + beaconNumber;
        byte currentRSSI = (byte)inetegerGenerator(generatedRSSIMin, generatedRSSIMax);
        int roomIndex = roomsArray.findRoomIndex(currentMAC);
        if (roomIndex > -1){
            String roomName = roomsArray.getArray().get(roomIndex).getName();
            int environmentIndex = enviroment.getRoomIndex(roomName);
            if (environmentIndex > -1){
                int beaconIndex = enviroment.getArray().get(environmentIndex).findBeaconIndex(currentMAC);
                if (beaconIndex > -1){ enviroment.getArray().get(environmentIndex).getBeacons().get(beaconIndex).setRSSI(currentRSSI); }
                else { enviroment.getArray().get(environmentIndex).getBeacons().add(new Beacon(environmentIndex, currentName, currentMAC, currentRSSI)); }
            } else {
                enviroment.getArray().add(new Room(roomName));
                int newIndex = enviroment.getArray().size() - 1;
                enviroment.getArray().get(newIndex).getBeacons().add(new Beacon(currentName, currentMAC, currentRSSI));
            }
        } else {
            int beaconIndex = enviroment.getArray().get(0).findBeaconIndex(currentMAC);
            if (beaconIndex > -1){ enviroment.getArray().get(0).getBeacons().get(beaconIndex).setRSSI(currentRSSI); }
            else { enviroment.getArray().get(0).getBeacons().add(new Beacon(beaconIndex, currentName, currentMAC, currentRSSI)); }
        }
    }

    public void fakeCalibrateLogic(int generatedBeacons, int generatedRSSIMin, int generatedRSSIMax, Room room){
        int beaconNumber = inetegerGenerator(1, generatedBeacons);
        String currentMAC = "MAC" + beaconNumber;
        byte currentRSSI = (byte)inetegerGenerator(generatedRSSIMin, generatedRSSIMax);
        ArrayList<String> macs = room.getMACList();
        if (macs.contains(currentMAC)){
            int index = macs.indexOf(currentMAC);
            room.getBeacons().get(index).getFullRSSI().add(currentRSSI);
        }
    }

    int inetegerGenerator(int min, int max){
        Random random = new Random();
        int value = random.nextInt(max - min + 1) + min;
        return value;
    }
}
