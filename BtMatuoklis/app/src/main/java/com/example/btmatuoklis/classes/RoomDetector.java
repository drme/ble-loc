package com.example.btmatuoklis.classes;

import android.content.Context;

import com.example.btmatuoklis.R;

import java.util.ArrayList;
import java.util.Collections;

public class RoomDetector {

    private String locatedIn, notDetected, noRooms;

    public RoomDetector(Context context) {
        this.locatedIn = context.getString(R.string.roomdetector_located)+" ";
        this.notDetected = context.getString(R.string.roomdetector_not_detected);
        this.noRooms = context.getString(R.string.roomdetector_no_rooms);
    }

    public int getDetectedRoomIndex(RoomsArray created, RoomsArray scan){
        int index = -2;
        if (!created.getArray().isEmpty()) {
            index = -1;
            RoomsArray matched = this.matchParametrizedRooms(created, scan);
            if (!matched.getArray().isEmpty()){
                RoomsArray coeffs = this.calcAllRoomCoeffs(matched, scan);
                matched = this.removePartiallyDetectedRooms(coeffs);
                if (!matched.getArray().isEmpty()){
                    index = this.getLowestCoeffRoomIndex(matched, created);
                }
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

    private RoomsArray matchParametrizedRooms(RoomsArray created, RoomsArray scan){
        RoomsArray matched = new RoomsArray();
        ArrayList<String> macs = created.getFullMACList();
        for (int i = 1; i < scan.getArray().size(); i++){
            String mac = scan.getArray().get(i).getBeacons().get(0).getMAC();
            if (macs.contains(mac)){
                int index = created.findRoomIndex(mac);
                Room createdRoom = created.getArray().get(index);
                Room room = new Room(i, createdRoom.getName());
                for (int j = 0; j < createdRoom.getBeacons().size(); j++){
                    Beacon beacon = createdRoom.getBeacons().get(j);
                    if (!beacon.getFullRSSI().isEmpty()){
                        room.getBeacons().add(new Beacon(beacon.getName(), beacon.getMAC(), beacon.getRSSIMin()));
                    }
                }
                ArrayList<String> scanMACs = scan.getArray().get(i).getMACList();
                if (!room.getBeacons().isEmpty() && scanMACs.containsAll(room.getMACList())){
                    matched.getArray().add(room);
                }
            }
        }
        return matched;
    }

    private Room calcRoomCoeffs(Room matched, Room scan){
        Room room = new Room(matched.getName());
        ArrayList<String> macs = scan.getMACList();
        for (int i = 0; i < matched.getBeacons().size(); i++){
            Beacon paramBeacon = matched.getBeacons().get(i);
            int index = macs.indexOf(paramBeacon.getMAC());
            Beacon scanBeacon = scan.getBeacons().get(index);
            byte coeff = this.compareParamShadow(paramBeacon.getFullRSSI().get(0), scanBeacon.getFullRSSI());
            if (coeff < 0){
                Beacon beacon = new Beacon(paramBeacon.getName(), paramBeacon.getMAC(), coeff);
                room.getBeacons().add(beacon);
            }
            else { room.getBeacons().add(null); }
        }
        return room;
    }

    private RoomsArray calcAllRoomCoeffs(RoomsArray matched, RoomsArray scan){
        RoomsArray calculated = new RoomsArray();
        for (int i = 0; i < matched.getArray().size(); i++){
            Room paramRoom = matched.getArray().get(i);
            int index = paramRoom.getID();
            Room scanRoom = scan.getArray().get(index);
            Room room = this.calcRoomCoeffs(paramRoom, scanRoom);
            calculated.getArray().add(room);
        }
        return calculated;
    }

    private RoomsArray removePartiallyDetectedRooms(RoomsArray matched){
        RoomsArray cleaned = new RoomsArray();
        for (int i = 0; i < matched.getArray().size(); i++){
            if (!matched.getArray().get(i).getBeacons().contains(null)){
                cleaned.getArray().add(matched.getArray().get(i));
            }
        }
        return cleaned;
    }

    private int getLowestCoeffRoomIndex(RoomsArray matched, RoomsArray created){
        int index = -1;
        if (!matched.getArray().isEmpty()){
            ArrayList<Byte> coeffs = new ArrayList<Byte>();
            ArrayList<Integer> indexes = new ArrayList<Integer>();
            for (int i = 0; i < matched.getArray().size(); i++){
                ArrayList<Byte> roomCoeffs = matched.getArray().get(i).getBeaconsCurrentRSSIs();
                for (int j = 0; j < roomCoeffs.size(); j++){ indexes.add(i); }
                coeffs.addAll(roomCoeffs);
            }
            if (!coeffs.isEmpty()){
                Byte minCoeff = Collections.min(coeffs);
                int coeffIndex = coeffs.indexOf(minCoeff);
                int cleanedIndex = indexes.get(coeffIndex);
                String cleanedName = matched.getArray().get(cleanedIndex).getName();
                index = created.getRoomIndex(cleanedName);
            }
        }
        return index;
    }

    private byte compareParamShadow(byte minParam, ArrayList<Byte> rssis){
        long res = 0;
        int size = rssis.size();
        for (int i = 0; i < size; i++){
            byte rssi = rssis.get(i);
            if (rssi >= minParam){ res += minParam - rssi; }
        }
        return (byte)(res/size);
    }
}
