package com.example.btmatuoklis;

public class DevInfo {
    private String Name;
    private String Mac;
    private byte Rssi;
    private boolean Coord_set = false;
    private float X_val;
    private float Y_val;

    public DevInfo(){}

    public DevInfo(String name, String mac){
        this.Name = name;
        this.Mac = mac;
    }

    public void setRssi(byte rssi) { this.Rssi = rssi; }

    public void setCoords(float x_val, float y_val){
        this.X_val = x_val;
        this.Y_val = y_val;
        this.Coord_set = true;
    }

    public boolean coordIsSet() { return this.Coord_set; }

    public String getName(){ return this.Name; }

    public String getMac(){
        return this.Mac;
    }

    public byte getRssi() { return this.Rssi; }

    public float getX_val() { return this.X_val; }

    public float getY_val() { return this.Y_val; }
}
