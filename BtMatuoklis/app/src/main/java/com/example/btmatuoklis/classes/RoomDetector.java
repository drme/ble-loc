package com.example.btmatuoklis.classes;

import java.util.ArrayList;
import java.util.Collections;

public class RoomDetector {

    public RoomDetector() {}

    public String getRoomName(ArrayList<Room> rooms, Room enviroment){
        if (!rooms.isEmpty()){
            ArrayList<String> roomIDs = new ArrayList<String>();
            ArrayList<Byte> rssiMin = new ArrayList<Byte>();
            ArrayList<Byte> rssiMax = new ArrayList<Byte>();
            for (int i = 0; i < rooms.size(); i++){
                Room currentRoom = rooms.get(i);
                for (int j = 0; j < currentRoom.getBeacons().size(); j++){
                    ArrayList<Byte> currentRSSI = currentRoom.getBeacons().get(j).getCalibratedRSSI();
                    rssiMin.add(Collections.min(currentRSSI));
                    rssiMax.add(Collections.max(currentRSSI));
                }
            }
            if (rssiMin.isEmpty() | rssiMax.isEmpty()){
                return "Nėra kambarių kalibracijų";
            }
            else {

            }
        }
        return "Kambarys neaptiktas";
    }
}
