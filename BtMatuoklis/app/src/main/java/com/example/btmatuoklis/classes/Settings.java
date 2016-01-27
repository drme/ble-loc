package com.example.btmatuoklis.classes;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Settings {
    public static byte REQUEST_ENABLE_BT = 1;
    private SharedPreferences preferences;
    private SharedPreferences.Editor edit;

    //Maksimalus teorinis BLE aptikimo atstumas metrais
    public static byte maxRange = 100;

    //Kas kiek laiko kartosis scan
    //Matuojant su maziau negu 300ms, po kurio laiko uzstringa
    private short defaultDelay = 1000;
    private short delay = defaultDelay;

    //"Default" BTLE irenginio stiprumas
    private byte txPow = 50;//Reiksme [1-100] intervale

    //Matavimu kiekis, vieno beacon'o vidutinei RSSI reiksmei surasti
    private byte defaultAverage = 1;
    private byte average = defaultAverage;

    public Settings(Context context){
        preferences = context.getSharedPreferences(
                Settings.class.getSimpleName(), Activity.MODE_PRIVATE);
        edit = preferences.edit();
        //Nuskaitomi paskutiniai naudoti nustatymai
        txPow = (byte)preferences.getInt("savedTxPow", txPow);
        delay = (short)preferences.getInt("savedDelay", delay);
        average = (byte)preferences.getInt("savedAverage", average);
    }

    public byte getMaxRange(){ return this.maxRange; }

    public byte getTxPow(){ return this.txPow; }

    public short getDelay(){ return this.delay; }

    public byte getAverage(){ return this.average; }

    public short getDefaultDelay() { return this.defaultDelay; }

    public byte getDefaultAverage() { return this.defaultAverage; }

    public void setMaxRange(byte mr) { this.maxRange = mr; }

    public void setTxPow(byte tx){ this.txPow = tx; }

    public void setDelay(short dl){ this.delay = dl; }

    public void setAverage(byte av){ this.average = av; }

    public void saveDelay(){
        edit.putInt("savedDelay", delay);
        edit.apply();
    }

    public void saveTxPow(){
        edit.putInt("savedTxPow", txPow);
        edit.apply();
    }

    public void saveAverage(){
        edit.putInt("savedAverage", average);
        edit.apply();
    }
}
