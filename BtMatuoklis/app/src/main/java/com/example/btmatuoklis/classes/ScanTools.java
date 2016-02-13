package com.example.btmatuoklis.classes;

import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;

public class ScanTools{

    public ScanTools(){}

    public void scanLogic(BluetoothDevice device, int rssi, byte txPow, ArrayList<Beacon> beaconsList, ArrayList<String> displayList){
        byte numDev = 0;
        byte listSize = (byte)beaconsList.size();
        byte currentRSSI = (byte)rssi;
        if (listSize == 0) {
            beaconsList.add(new Beacon(device.getName(), device.getAddress()));
            beaconsList.get(0).setRSSI(currentRSSI);
            displayList.add(beaconsList.get(0).getCurrentInfo(txPow));
        } else {
            for (byte i = 0; i < listSize; i++) {
                if (beaconsList.get(i).getMAC().equals(device.getAddress())) {
                    beaconsList.get(i).setRSSI(currentRSSI);
                    displayList.set(i, beaconsList.get(i).getCurrentInfo(txPow));
                } else {
                    numDev++;
                }
            }
            if (numDev > listSize - 1) {
                beaconsList.add(new Beacon(device.getName(), device.getAddress()));
                beaconsList.get(numDev).setRSSI(currentRSSI);
                displayList.add(numDev, beaconsList.get(numDev).getCurrentInfo(txPow));
            }
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

}
