package com.example.btmatuoklis.classes;

import com.example.btmatuoklis.activities.MainActivity;

import java.util.ArrayList;
import java.util.Collections;

public class RoomDetector {

    public RoomDetector() {}

    public String getRoomName(RoomsArray created, RoomsArray enviroment){
        String locatedIn = "Esate patalpoje: ";
        String notDetected = "Lokacija nenustatyta!";
        String noRooms = "Nėra sukurtų kambarių!";
        if (!created.getArray().isEmpty()) {
            ArrayList<Byte> coeff = new ArrayList<Byte>();
            ArrayList<Integer> indexes = new ArrayList<Integer>();
            ArrayList<Beacon> scannedBeacons = enviroment.getFullBeaconList();
            for (int i = 0; i < scannedBeacons.size(); i++){
                Beacon scannedBeacon = scannedBeacons.get(i);
                Beacon calibratedBeacon = created.findBeacon(scannedBeacon.getMAC());
                if (calibratedBeacon != null){
                    byte res = compareCalibrationShadow(calibratedBeacon.getFullRSSI(), scannedBeacon.getFullRSSI());
                    if (res < 0){ coeff.add(res); indexes.add(i); }
                }
            }
            if (!coeff.isEmpty()){
                Byte minCoeff = Collections.min(coeff);
                int coeffIndex = coeff.indexOf(minCoeff);
                int minIndex = indexes.get(coeffIndex);
                //ateiciai - prideti if'a kuris patikrintu ar beaconas vis dar yra aptinkamas
                //jeigu ne - imti sekancia maziausia koeficiento reiksme
                String minMAC = scannedBeacons.get(minIndex).getMAC();
                int roomIndex = created.findRoomIndex(minMAC);
                return locatedIn+created.getArray().get(roomIndex).getName();
            }
            return notDetected;
        }
        return noRooms;
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

    private byte getAccuracy(){
        Settings settings = MainActivity.settings;
        return settings.getAccuracy();
    }
}
