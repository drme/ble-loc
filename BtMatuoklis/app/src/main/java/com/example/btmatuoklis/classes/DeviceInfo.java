package com.example.btmatuoklis.classes;

public class DeviceInfo {
    private String deviceName;
    private String deviceMAC;
    private byte[] calibratedRSSI;
    private byte currentRSSI;

    public DeviceInfo(){}

    public DeviceInfo(String name, String mac){
        this.deviceName = name;
        this.deviceMAC = mac;
    }

    public void setRSSI(byte rssi) { this.currentRSSI = rssi; }

    public void setCalibratedRSSI(byte[] rssiArray) {
        this.calibratedRSSI = rssiArray;
    }

    public String getName(){ return this.deviceName; }

    public String getMAC(){ return this.deviceMAC; }

    public byte getRSSI() { return this.currentRSSI; }

    public byte[] getCalibratedRSSI() { return  this.calibratedRSSI; }

    //BT irenginio informacija (List formavimui)
    public String getInfo() {
        String info = "Pavadinimas: " + this.deviceName;
        info += "\nMAC: " + this.deviceMAC;
        return info;
    }

    //BT irenginio papildoma informacija (List formavimui)
    public String getCurrentInfo(byte txPower){
        String info = "Pavadinimas: " + this.deviceName;
        info += "\nMAC: " + this.deviceMAC;
        info += "\nRSSI: " + this.currentRSSI;
        info += " " + RangeCalculator.getRange(txPower, this.currentRSSI);
        return info;
    }
}
