package com.example.btmatuoklis.classes;

import android.content.Context;

import com.example.btmatuoklis.R;

import java.util.ArrayList;
import java.util.Collections;

public class RoomDetector {

    private String locatedIn, notDetected, noRooms;

    public RoomDetector(Context context) {
        this.locatedIn = context.getString(R.string.roomdetector_located);
        this.notDetected = context.getString(R.string.roomdetector_not_detected);
        this.noRooms = context.getString(R.string.roomdetector_no_rooms);
    }

    public int getDetectedRoomIndex(RoomsArray created, RoomsArray enviroment){
        int index = -2;
        if (!created.getArray().isEmpty()) {
            index = -1;
            RoomsArray matched = this.matchCalibratedRooms(created, enviroment);
            if (!matched.getArray().isEmpty()){
                RoomsArray matchedCoeff = this.calcAllRoomCoeffs(matched, enviroment);
                RoomsArray cleanMatched = this.removePartiallyDetectedRooms(matchedCoeff);
                index = this.getLowestCoeffRoomIndex(cleanMatched, created);
            }
        }
        return index;
    }

    public RoomsArray matchCalibratedRooms(RoomsArray created, RoomsArray enviroment){
        RoomsArray matched = new RoomsArray();
        ArrayList<String> macs = created.getFullMACList();
        for (int i = 1; i < enviroment.getArray().size(); i++){
            String macCheck = enviroment.getArray().get(i).getBeacons().get(0).getMAC();
            if (macs.contains(macCheck)){
                int index = created.findRoomIndex(macCheck);
                Room room = created.getArray().get(index);
                Room newRoom = new Room(i, room.getName());
                for (int j = 0; j < room.getBeacons().size(); j++){
                    Beacon beacon = room.getBeacons().get(j);
                    if (!beacon.getFullRSSI().isEmpty()){
                        newRoom.getBeacons().add(new Beacon(beacon.getName(), beacon.getMAC(), beacon.getRSSIMin()));
                    }
                }
                ArrayList<String> scanMACs = enviroment.getArray().get(i).getMACList();
                if (!newRoom.getBeacons().isEmpty() && scanMACs.containsAll(newRoom.getMACList())){
                    matched.getArray().add(newRoom);
                }
            }
        }
        return matched;
    }

    private Room calcRoomCoeffs(Room matched, Room enviroment){
        Room room = new Room(matched.getName());
        ArrayList<String> macs = enviroment.getMACList();
        for (int i = 0; i < matched.getBeacons().size(); i++){
            Beacon calibBeacon = matched.getBeacons().get(i);
            int index = macs.indexOf(calibBeacon.getMAC());
            Beacon scanBeacon = enviroment.getBeacons().get(index);
            byte coeff = this.compareCalibrationShadow(calibBeacon.getFullRSSI().get(0), scanBeacon.getFullRSSI());
            if (coeff < 0){
                Beacon beacon = new Beacon(calibBeacon.getName(), calibBeacon.getMAC(), coeff);
                room.getBeacons().add(beacon);
            }
            else {
                room.getBeacons().add(null);
            }
        }
        return room;
    }

    private RoomsArray calcAllRoomCoeffs(RoomsArray matched, RoomsArray enviroment){
        RoomsArray array = new RoomsArray();
        for (int i = 0; i < matched.getArray().size(); i++){
            Room calibRoom = matched.getArray().get(i);
            int index = calibRoom.getID();
            Room scanRoom = enviroment.getArray().get(index);
            Room newRoom = this.calcRoomCoeffs(calibRoom, scanRoom);
            array.getArray().add(newRoom);
        }
        return array;
    }

    private RoomsArray removePartiallyDetectedRooms(RoomsArray matched){
        RoomsArray cleanMatched = new RoomsArray();
        for (int i = 0; i < matched.getArray().size(); i++){
            ArrayList<Beacon> beacons = matched.getArray().get(i).getBeacons();
            if (!beacons.contains(null)){
                cleanMatched.getArray().add(matched.getArray().get(i));
            }
        }
        return cleanMatched;
    }

    private int getLowestCoeffRoomIndex(RoomsArray matched, RoomsArray created){
        int index = -1;
        if (!matched.getArray().isEmpty()){
            ArrayList<Byte> coeffs = new ArrayList<Byte>();
            ArrayList<Integer> indexes = new ArrayList<Integer>();
            for (int i = 0; i < matched.getArray().size(); i++){
                ArrayList<Byte> tempCoeffs = matched.getArray().get(i).getBeaconsCurrentRSSIs();
                for (int j = 0; j < tempCoeffs.size(); j++){ indexes.add(i); }
                coeffs.addAll(tempCoeffs);
            }
            if (!coeffs.isEmpty()){
                Byte minCoeff = Collections.min(coeffs);
                int coeffIndex = coeffs.indexOf(minCoeff);
                int matchedRoomIndex = indexes.get(coeffIndex);
                String name = matched.getArray().get(matchedRoomIndex).getName();
                index = created.getRoomIndex(name);
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

    private byte compareCalibrationShadow(byte minCalib, ArrayList<Byte> rssis){
        long res = 0;
        int size = rssis.size();
        for (int i = 0; i < size; i++){
            byte rssi = rssis.get(i);
            if (rssi >= minCalib){ res += minCalib - rssi; }
        }
        return (byte)(res/size);
    }
}
