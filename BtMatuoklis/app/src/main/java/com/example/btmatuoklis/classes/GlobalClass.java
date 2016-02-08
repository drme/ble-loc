package com.example.btmatuoklis.classes;

import android.app.Application;

import java.util.ArrayList;

public class GlobalClass extends Application{
    private ArrayList<String> roomsList = new ArrayList<String>();;
    private ArrayList<Room> roomsArray= new ArrayList<Room>();

    public ArrayList<String> getRoomsList(){
        return this.roomsList;
    }

    public ArrayList<Room> getRoomsArray(){
        return this.roomsArray;
    }
}
