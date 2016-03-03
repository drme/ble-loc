package com.example.btmatuoklis.classes;

import java.util.ArrayList;
import java.util.Collections;

public class RoomDetector {

    public RoomDetector() {}

    public String getRoomName(ArrayList<Room> rooms, Room enviroment){
        if (!rooms.isEmpty()){
            ArrayList<Integer> roomIDs = new ArrayList<Integer>();
            ArrayList<Float> compareList = new ArrayList<Float>();
            for (int i = 0; i < rooms.size(); i++){
                Room room = rooms.get(i);
                ArrayList<Beacon> beacons = room.getBeacons();
                for (int j = 0; j < beacons.size(); j++){
                    ArrayList<Beacon> scannedBeacons = enviroment.getBeacons();
                    for (int k = 0; k < scannedBeacons.size(); k++){
                        if (room.getMACList().contains(scannedBeacons.get(k).getMAC())){
                            float res = compareCalibrationShadow(beacons.get(j).getCalibratedRSSI(), scannedBeacons.get(k).getFullRSSI());
                            roomIDs.add(rooms.indexOf(room));
                            compareList.add(res);
                        }
                    }
                }
            }
            if (!compareList.isEmpty()){
                float max = Collections.max(compareList);
                int id = compareList.indexOf(max);
                int roomID = roomIDs.get(id);
                return rooms.get(roomID).getName();
            }
            else {
                return "Neaptikta";
            }
        }
        else {
            return "Nėra sukurtų kambarių!";
        }
    }

    private float compareCalibrationShadow(ArrayList<Byte> calibrations, ArrayList<Byte> rssis){
        byte min = Collections.min(calibrations);
        byte max = Collections.max(calibrations);
        int size = rssis.size();
        float res = 0;
        for (int i = 0; i < size; i++){
            byte currentRSSI = rssis.get(i);
            if ((currentRSSI >= min) && (currentRSSI <= max)){
                res = res + 1;
            }
        }
        res = res/size;
        return res;
    }
}
