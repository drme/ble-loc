package com.example.btmatuoklis.classes;

import android.app.Application;

public class GlobalClass extends Application{

    private boolean scanning = false;
    private RoomsArray roomsArray = new RoomsArray();

    public void setScanning(boolean scanning) { this.scanning = scanning; }

    public boolean isScanning() { return this.scanning; }

    public RoomsArray getRoomsArray(){ return this.roomsArray; }
}
