package com.example.btmatuoklis.classes;

import com.example.btmatuoklis.activities.MainActivity;

import java.util.ArrayList;

public class RoomDetector {

    public RoomDetector() {}

    public String getRoomName(ArrayList<Room> rooms, Room enviroment){
        if (!rooms.isEmpty()) {
            int roomID = -1;
            short max = -1;
            for (int i = 0; i < rooms.size(); i++) {
                Room room = rooms.get(i);
                ArrayList<Beacon> beacons = room.getBeacons();
                for (int j = 0; j < beacons.size(); j++) {
                    ArrayList<Beacon> scannedBeacons = enviroment.getBeacons();
                    for (int k = 0; k < scannedBeacons.size(); k++) {
                        if (room.getMACList().contains(scannedBeacons.get(k).getMAC()) && room.isCalibrated()) {
                            short res = compareCalibrationShadow(beacons.get(j).getFullRSSI(), scannedBeacons.get(k).getFullRSSI());
                            if (res >= max){
                                roomID = rooms.indexOf(room);
                                max = res;
                            }
                        }
                    }
                }
            }
            if (max > getAccuracy() && roomID > -1) { return rooms.get(roomID).getName(); }
            return "Neaptikta!";
        }
        return "Nėra sukurtų kambarių!";
    }

    private short compareCalibrationShadow(ArrayList<Byte> calibrations, ArrayList<Byte> rssis){
        long res = 0;
        byte min = getMinRange(calibrations);
        int size = rssis.size();
        for (short i = 0; i < size; i++){
            byte currentRSSI = rssis.get(i);
            if (currentRSSI >= min){ res += 100; }
        }
        return (short)(res/size);
    }

    private byte getMinRange(ArrayList<Byte> calibrations){
        long res = 0;
        int size = calibrations.size();
        for (int i = 0; i < size; i++){
            res += calibrations.get(i);
        }
        return (byte)(res/size);
    }

    private byte getAccuracy(){
        Settings settings = MainActivity.settings;
        return settings.getAccuracy();
    }
}
