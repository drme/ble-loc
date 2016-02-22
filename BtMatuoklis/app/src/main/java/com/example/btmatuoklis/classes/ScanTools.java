package com.example.btmatuoklis.classes;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

public class ScanTools{

    public ScanTools(){}

    public void scanLogic(BluetoothDevice device, int rssi, byte txPower, ArrayList<Beacon> beaconsList, ArrayList<String> displayList){
        byte numDev = 0;
        byte listSize = (byte)beaconsList.size();
        byte currentRSSI = (byte)rssi;
        if (listSize == 0) {
            beaconsList.add(new Beacon(device.getName(), device.getAddress()));
            beaconsList.get(0).setRSSI(currentRSSI);
            displayList.add(beaconsList.get(0).getCurrentInfo(txPower));
        } else {
            for (byte i = 0; i < listSize; i++) {
                if (beaconsList.get(i).getMAC().equals(device.getAddress())) {
                    beaconsList.get(i).setRSSI(currentRSSI);
                    displayList.set(i, beaconsList.get(i).getCurrentInfo(txPower));
                } else {
                    numDev++;
                }
            }
            if (numDev > listSize - 1) {
                beaconsList.add(new Beacon(device.getName(), device.getAddress()));
                beaconsList.get(numDev).setRSSI(currentRSSI);
                displayList.add(numDev, beaconsList.get(numDev).getCurrentInfo(txPower));
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

    //------------Debug------------

    public void fakeScanLogic(int generatedBeacons, int generatedRSSIMin, int generatedRSSIMax,
                              byte txPower, ArrayList<Beacon> beaconsList, ArrayList<String> displayList){
        int beaconNumber = inetegerGenerator(1, generatedBeacons);
        String name = "Beacon" + beaconNumber;
        String mac = "MAC" + beaconNumber;
        byte rssi = (byte)inetegerGenerator(generatedRSSIMin, generatedRSSIMax);

        byte numDev = 0;
        byte listSize = (byte)beaconsList.size();
        if (listSize == 0) {
            beaconsList.add(new Beacon(name, mac));
            beaconsList.get(0).setRSSI(rssi);
            displayList.add(beaconsList.get(0).getCurrentInfo(txPower));
            Log.d("Device Added", mac);
        }
        else {
            for (byte i = 0; i < listSize; i++) {
                if (beaconsList.get(i).getMAC().equals(mac)) {
                    beaconsList.get(i).setRSSI(rssi);
                    displayList.set(i, beaconsList.get(i).getCurrentInfo(txPower));
                } else {
                    numDev++;
                }
            }
            if (numDev > listSize - 1) {
                beaconsList.add(new Beacon(name, mac));
                beaconsList.get(numDev).setRSSI(rssi);
                displayList.add(numDev, beaconsList.get(numDev).getCurrentInfo(txPower));
            }
        }
    }

    public void fakeCalibrateLogic(int generatedBeacons, int generatedRSSIMin, int generatedRSSIMax, Room room){
        int beaconNumber = inetegerGenerator(1, generatedBeacons);
        String name = "Beacon" + beaconNumber;
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
