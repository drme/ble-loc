package com.example.btmatuoklis.classes;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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
    private byte txPower = 50;//Reiksme [1-100] intervale

    //Matavimu kiekis, vieno beacon'o vidutinei RSSI reiksmei surasti
    private byte defaultAverage = 1;
    private byte average = defaultAverage;

    //Debug - netikru irenginiu generatoriaus ijungimo reiksme
    private boolean generator = false;

    public Settings(Context context){
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        edit = preferences.edit();
        //Nuskaitomi paskutiniai naudoti nustatymai
        txPower = (byte)preferences.getInt("savedTXPower", txPower);
        delay = Short.parseShort(preferences.getString("savedDelay", Short.toString(delay)));
        average = Byte.parseByte(preferences.getString("savedAverage", Short.toString(average)));
        generator = preferences.getBoolean("savedGenerator", generator);
    }

    public byte getMaxRange(){ return this.maxRange; }

    public byte getTXPower(){ return this.txPower; }

    public short getDelay(){
        delay = Short.parseShort(preferences.getString("savedDelay", Short.toString(delay)));
        return this.delay;
    }

    public byte getAverage(){ return this.average; }

    public short getDefaultDelay() { return this.defaultDelay; }

    public byte getDefaultAverage() { return this.defaultAverage; }

    public boolean getGenerator() { return this.generator; }

    public void setMaxRange(byte mr) { this.maxRange = mr; }

    public void setTXPower(byte tx){ this.txPower = tx; }

    public void setDelay(short dl){ this.delay = dl; }

    public void setAverage(byte av){ this.average = av; }

    public void setGenerator(boolean vg) { this.generator = vg; }

    public void saveDelay(){
        edit.putInt("savedDelay", delay);
        edit.apply();
    }

    public void saveTXPower(){
        edit.putInt("savedTXPower", txPower);
        edit.apply();
    }

    public void saveAverage(){
            edit.putInt("savedAverage", average);
            edit.apply();
    }

    public void saveGenerator(){
        edit.putBoolean("savedGenerator", generator);
        edit.apply();
    }
}
