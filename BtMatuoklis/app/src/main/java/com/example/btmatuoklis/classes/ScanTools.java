package com.example.btmatuoklis.classes;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import java.util.ArrayList;

public class ScanTools{

    public ScanTools(){}

    public void scanLogic(BluetoothDevice device, int rssi, byte txPow, ArrayList<DeviceInfo> devicesList, ArrayList<String> displayList){
        byte numDev = 0;
        byte listSize = (byte)devicesList.size();
        byte currentRSSI = (byte)rssi;
        if (listSize == 0) {
            devicesList.add(new DeviceInfo(device.getName(), device.getAddress()));
            devicesList.get(0).setRSSI(currentRSSI);
            displayList.add(devicesList.get(0).getCurrentInfo(txPow));
        } else {
            for (byte i = 0; i < listSize; i++) {
                if (devicesList.get(i).getMAC().equals(device.getAddress())) {
                    devicesList.get(i).setRSSI(currentRSSI);
                    displayList.set(i, devicesList.get(i).getCurrentInfo(txPow));
                } else {
                    numDev++;
                }
            }
            if (numDev > listSize - 1) {
                devicesList.add(new DeviceInfo(device.getName(), device.getAddress()));
                devicesList.get(numDev).setRSSI(currentRSSI);
                displayList.add(numDev, devicesList.get(numDev).getCurrentInfo(txPow));
            }
        }
    }

    public boolean calibrateLogic(BluetoothDevice device, int rssi, Room room){
        ArrayList<String> macs = room.getMACList();
        String currentMAC = device.getAddress();
        byte currentRSSI = (byte)rssi;
        if (macs.contains(currentMAC)){
            int macPosition = macs.indexOf(currentMAC);
            //--------
            //cia gaunamas NullPointerException
            room.getDevices().get(macPosition).getCalibratedRSSI().add(currentRSSI);
            //--------
        }
        //--------
        //cia taip pat NullPointerException
        return room.isCalibrated();
        //--------
    }

}
