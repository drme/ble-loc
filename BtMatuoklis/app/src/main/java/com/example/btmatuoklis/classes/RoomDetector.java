package com.example.btmatuoklis.classes;

import android.content.Context;

import com.example.btmatuoklis.R;
import com.example.btmatuoklis.activities.MainActivity;

import java.util.ArrayList;
import java.util.Collections;

public class RoomDetector {

    private String locatedIn, notDetected, noRooms;
    private ArrayList<Byte> coeff;
    private ArrayList<Integer> indexes;
    private ArrayList<Beacon> scannedBeacons;
    private Beacon scannedBeacon, calibratedBeacon;
    private Settings settings;

    public RoomDetector(Context context) {
        settings = MainActivity.settings;
        this.locatedIn = context.getString(R.string.roomdetector_located);
        this.notDetected = context.getString(R.string.roomdetector_not_detected);
        this.noRooms = context.getString(R.string.roomdetector_no_rooms);
    }

    public int getDetectedRoomIndex(RoomsArray created, RoomsArray enviroment){
        int index = -2;
        if (!created.getArray().isEmpty()) {
            index = -1;
            coeff = new ArrayList<Byte>();
            indexes = new ArrayList<Integer>();
            scannedBeacons = enviroment.getFullBeaconList();
            for (int i = 0; i < scannedBeacons.size(); i++){
                scannedBeacon = scannedBeacons.get(i);
                calibratedBeacon = created.findBeacon(scannedBeacon.getMAC());
                if (calibratedBeacon != null && !calibratedBeacon.getFullRSSI().isEmpty()){
                    byte res = compareCalibrationShadow(calibratedBeacon.getFullRSSI(), scannedBeacon.getFullRSSI());
                    if (res < 0){ coeff.add(res); indexes.add(i); }
                }
            }
            if (!coeff.isEmpty() && !indexes.isEmpty()){
                Byte minCoeff = Collections.min(coeff);
                int coeffIndex = coeff.indexOf(minCoeff);
                int minIndex = indexes.get(coeffIndex);
                //ateiciai - prideti if'a kuris patikrintu ar beaconas vis dar yra aptinkamas
                //jeigu ne - imti sekancia maziausia koeficiento reiksme
                String minMAC = scannedBeacons.get(minIndex).getMAC();
                index = created.findRoomIndex(minMAC);
            }
        }
        return index;
    }

    public String getDetectedRoomName(RoomsArray created, int index){
        switch (index){
            case -2: return noRooms;
            case -1: return notDetected;
            default: return locatedIn+created.getArray().get(index).getName();
        }
    }

    private byte compareCalibrationShadow(ArrayList<Byte> calibrations, ArrayList<Byte> rssis){
        long res = 0;
        byte min = Collections.min(calibrations);
        int size = rssis.size();
        for (int i = 0; i < size; i++){
            byte rssi = rssis.get(i);
            if (rssi >= min){ res += min - rssi; }
        }
        return (byte)(res/size);
    }
}
