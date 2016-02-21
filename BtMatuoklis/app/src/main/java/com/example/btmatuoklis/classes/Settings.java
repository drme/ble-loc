package com.example.btmatuoklis.classes;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Settings implements SharedPreferences.OnSharedPreferenceChangeListener {
    public static byte REQUEST_ENABLE_BT = 1;
    private SharedPreferences preferences;

    //Maksimalus teorinis BLE aptikimo atstumas metrais
    public static byte maxRange = 100;

    //Kas kiek laiko kartosis scan
    //Matuojant su maziau negu 300ms, po kurio laiko uzstringa
    private short defaultDelay = 1000;
    private short delay;

    //"Default" BTLE irenginio stiprumas
    private byte txPower = 50;//Reiksme [1-100] intervale

    //Matavimu kiekis, vieno beacon'o vidutinei RSSI reiksmei surasti
    private byte defaultAverage = 1;
    private byte average;

    //Debug - netikru irenginiu generatoriaus ijungimo reiksme
    private boolean generator = false;

    public Settings(Context context){
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.registerOnSharedPreferenceChangeListener(this);
        refreshValues();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        refreshValues();
    }

    public void refreshValues(){
        //Nuskaitomi paskutiniai naudoti nustatymai
        txPower = (byte)preferences.getInt("savedTXPower", txPower);
        delay = Short.parseShort(preferences.getString("savedDelay", Short.toString(defaultDelay)));
        average = Byte.parseByte(preferences.getString("savedAverage", Short.toString(defaultAverage)));
        generator = preferences.getBoolean("savedGenerator", generator);
    }

    public byte getMaxRange(){ return this.maxRange; }

    public byte getTXPower(){ return this.txPower; }

    public short getDelay(){ return this.delay; }

    public byte getAverage(){ return this.average; }

    public short getDefaultDelay() { return this.defaultDelay; }

    public byte getDefaultAverage() { return this.defaultAverage; }

    public boolean getGenerator() { return this.generator; }
}
