package com.example.btmatuoklis.classes;

import java.util.ArrayList;

public class RoomsArray {

    private ArrayList<Room> rooms;

    public RoomsArray() { this.rooms = new ArrayList<Room>(); }

    public RoomsArray(ArrayList<Room> rooms) { this.rooms = rooms; }

    public ArrayList<Room> getArray() { return this.rooms; }

    public ArrayList<String> getNameList(){
        ArrayList<String> res = new ArrayList<String>();
        for (int i = 0; i < this.rooms.size(); i++){
            res.add(this.rooms.get(i).getName());
        }
        return res;
    }

    public ArrayList<String> getFullMACList(){
        ArrayList<String> res = new ArrayList<String>();
        for (int i = 0; i < this.rooms.size(); i++){
            res.addAll(this.rooms.get(i).getMACList());
        }
        return res;
    }

    public ArrayList<Beacon> getFullBeaconList(){
        ArrayList<Beacon> res = new ArrayList<Beacon>();
        for (int i = 0; i < this.rooms.size(); i++){
            res.addAll(this.rooms.get(i).getBeacons());
        }
        return res;
    }

    public ArrayList<Integer> getFullRoomIndexes(){
        ArrayList<Integer> res = new ArrayList<Integer>();
        for (int i = 0; i < this.rooms.size(); i++){
            int size = this.rooms.get(i).getBeacons().size();
            for (int j = 0; j < size; j++){
                res.add(i);
            }
        }
        return res;
    }

    public int findRoomIndex(String mac){
        int id = -1;
        ArrayList<String> fullMACs = getFullMACList();
        ArrayList<Integer> fullIDs = getFullRoomIndexes();
        if (!fullMACs.isEmpty() & fullMACs.contains(mac)){
            int macID = fullMACs.indexOf(mac);
            id = fullIDs.get(macID);
        }
        return id;
    }

    public int getRoomIndex(String name){
        int id = -1;
        ArrayList<String> names = new ArrayList<String>();
        for (int i = 0; i < this.rooms.size(); i++){
            names.add(this.rooms.get(i).getName());
        }
        if (!names.isEmpty() & names.contains(name)){
            id = names.indexOf(name);
        }
        return id;
    }

    public Beacon findBeacon(String mac){
        Beacon beacon = null;
        int roomID = this.findRoomIndex(mac);
        if ((roomID > -1) & (roomID < this.rooms.size())){
            Room room = this.rooms.get(roomID);
            ArrayList<String> macs = room.getMACList();
            if (!macs.isEmpty() & macs.contains(mac)){
                int id = macs.indexOf(mac);
                beacon = room.getBeacons().get(id);
            }
        }
        return beacon;
    }
}
