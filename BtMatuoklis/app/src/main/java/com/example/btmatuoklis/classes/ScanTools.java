package com.example.btmatuoklis.classes;

import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;
import java.util.Random;

public class ScanTools{

    public ScanTools(){}

    public void scanLogic(BluetoothDevice device, int rssi, Room room){
        String currentName = device.getName();
        String currentMAC = device.getAddress();
        byte currentRSSI = (byte)rssi;
        int id;
        if (room.getMACList().contains(currentMAC)) {
            id = room.getMACList().indexOf(currentMAC);
            room.getBeacons().get(id).setRSSI(currentRSSI);
        } else {
            room.getBeacons().add(new Beacon(currentName, currentMAC));
            id = room.getMACList().indexOf(currentMAC);
            room.getBeacons().get(id).setRSSI(currentRSSI);
        }
    }

    public void calibrateLogic(BluetoothDevice device, int rssi, Room room){
        ArrayList<String> macs = room.getMACList();
        String currentMAC = device.getAddress();
        byte currentRSSI = (byte)rssi;
        if (macs.contains(currentMAC)){
            int macPosition = macs.indexOf(currentMAC);
            room.getBeacons().get(macPosition).getCalibratedRSSI().add(currentRSSI);
        }
    }

    public String detectRoomLogic(BluetoothDevice device, int rssi, ArrayList<String> beacons, Room scanEnviroment){
        String currentMAC = device.getAddress();
        if (beacons.contains(currentMAC)){
            //---
        }
        return "Not implemented";
    }

    //------------Debug------------

    public void fakeScanLogic(int generatedBeacons, int generatedRSSIMin, int generatedRSSIMax, Room room){
        int beaconNumber = inetegerGenerator(1, generatedBeacons);
        String name = "Beacon" + beaconNumber;
        String mac = "MAC" + beaconNumber;
        byte rssi = (byte)inetegerGenerator(generatedRSSIMin, generatedRSSIMax);
        int id;
        if (room.getMACList().contains(mac)) {
            id = room.getMACList().indexOf(mac);
            room.getBeacons().get(id).setRSSI(rssi);
        } else {
            room.getBeacons().add(new Beacon(name, mac));
            id = room.getMACList().indexOf(mac);
            room.getBeacons().get(id).setRSSI(rssi);
        }
    }

    public void fakeCalibrateLogic(int generatedBeacons, int generatedRSSIMin, int generatedRSSIMax, Room room){
        int beaconNumber = inetegerGenerator(1, generatedBeacons);
        String mac = "MAC" + beaconNumber;
        byte rssi = (byte)inetegerGenerator(generatedRSSIMin, generatedRSSIMax);

        ArrayList<String> macs = room.getMACList();
        if (macs.contains(mac)){
            int macPosition = macs.indexOf(mac);
            room.getBeacons().get(macPosition).getCalibratedRSSI().add(rssi);
        }
    }

    int inetegerGenerator(int min, int max){
        Random random = new Random();
        int value = random.nextInt(max - min + 1) + min;
        return value;
    }
}
