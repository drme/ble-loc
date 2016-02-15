package com.example.btmatuoklis.classes;

import android.app.Application;

import java.util.ArrayList;

public class GlobalClass extends Application{
    private boolean scanning = false;
    private ArrayList<String> roomsList = new ArrayList<String>();
    private ArrayList<Room> roomsArray= new ArrayList<Room>();

    public void setScanning(boolean scn) { this.scanning = scn; }

    public boolean isScanning() { return this.scanning; }

    public ArrayList<String> getRoomsList(){
        return this.roomsList;
    }

    public ArrayList<Room> getRoomsArray(){
        return this.roomsArray;
    }
}
