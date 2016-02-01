package com.example.btmatuoklis.classes;

import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;

public class ScanTools{

    public ScanTools(){}

    public void scanLogic(BluetoothDevice device, int rssi, byte txPow, ArrayList<DeviceInfo> devicesList, ArrayList<String> displayList){
        byte numDev = 0;
        byte listSize = (byte)devicesList.size();
        byte currentRssi = (byte)rssi;
        if (listSize == 0) {
            devicesList.add(new DeviceInfo(device.getName(), device.getAddress()));
            devicesList.get(0).setRSSI(currentRssi);
            displayList.add(devicesList.get(0).getCurrentInfo(txPow));
        } else {
            for (byte i = 0; i < listSize; i++) {
                if (devicesList.get(i).getMAC().equals(device.getAddress())) {
                    devicesList.get(i).setRSSI(currentRssi);
                    displayList.set(i, devicesList.get(i).getCurrentInfo(txPow));
                } else {
                    numDev++;
                }
            }
            if (numDev > listSize - 1) {
                devicesList.add(new DeviceInfo(device.getName(), device.getAddress()));
                devicesList.get(numDev).setRSSI(currentRssi);
                displayList.add(numDev, devicesList.get(numDev).getCurrentInfo(txPow));
            }
        }
    }

}
