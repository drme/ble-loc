package com.example.btmatuoklis.classes;

import android.content.Context;

import com.example.btmatuoklis.R;

import java.util.Random;

public class _DebugBeaconGenerator {

    private String defaultName, defaultMAC;
    private String name, mac;
    private byte rssi;

    public _DebugBeaconGenerator(Context context){
        defaultName = context.getString(R.string.gen_beacon_name);
        defaultMAC = context.getString(R.string.gen_beacon_mac);
    }

    public void generate(int generatedBeacons, int generatedRSSIMin, int generatedRSSIMax){
        int number = this.numGen(1, generatedBeacons);
        this.name = this.defaultName+number;
        this.mac = this.macAppend(this.defaultMAC, number);
        this.rssi = (byte)this.numGen(generatedRSSIMin, generatedRSSIMax);
    }

    public String getName(){ return this.name; }

    public String getMAC(){ return this.mac; }

    public byte getRSSI(){ return this.rssi; }

    private String macAppend(String mac, int number){
        String string = mac;
        if (number > 9){ string += number; }
        else {
            string += 0;
            string += number;
        }
        return string;
    }

    public int numGen(int min, int max){
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }
}
