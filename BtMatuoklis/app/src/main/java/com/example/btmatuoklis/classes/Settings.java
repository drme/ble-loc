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

    //Kokiu intervalu kartosis aptiktu ienginiu salinimas
    //Salinimo daznis: timeout * delay = x ms
    private byte defaultTimeout = 10;
    private byte timeout;

    //Kiek RSSI saugoti aktyvaus Scan rezimu
    private byte defaultShadow = 1;
    private byte shadow;

    //"Default" BTLE irenginio stiprumas
    private byte txPower = 50;//Reiksme [1-100] intervale

    //Matavimu kiekis, vieno beacon'o vidutinei RSSI reiksmei surasti
    private byte defaultAverage = 1;
    private byte average;

    //Debug - netikru irenginiu generatoriaus reiksmes
    private boolean generator;
    private byte generatedBeacons = 2;
    private byte generatedRSSIMin = -90;
    private byte generatedRSSIMax = 4;

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
        delay = Short.parseShort(preferences.getString("savedDelay", Short.toString(defaultDelay)));
        timeout = Byte.parseByte(preferences.getString("savedTimeout", Byte.toString(defaultTimeout)));
        shadow = Byte.parseByte(preferences.getString("savedShadow", Byte.toString(defaultShadow)));
        txPower = (byte)preferences.getInt("savedTXPower", txPower);
        average = Byte.parseByte(preferences.getString("savedAverage", Byte.toString(defaultAverage)));
        generator = preferences.getBoolean("debugGenerator", false);
        generatedBeacons = Byte.parseByte(preferences.getString("debugBeacons", Byte.toString(generatedBeacons)));
        generatedRSSIMin = Byte.parseByte(preferences.getString("debugRSSIMin", Byte.toString(generatedRSSIMin)));
        generatedRSSIMax = Byte.parseByte(preferences.getString("debugRSSIMax", Byte.toString(generatedRSSIMax)));
    }

    public byte getMaxRange(){ return this.maxRange; }

    public short getDelay(){ return this.delay; }

    public byte getTimeout(){ return this.timeout; }

    public byte getShadow(){ return this.shadow; }

    public byte getTXPower(){ return this.txPower; }

    public byte getAverage(){ return this.average; }

    public short getDefaultDelay() { return this.defaultDelay; }

    public byte getDefaultAverage() { return this.defaultAverage; }

    public boolean isGeneratorEnabled() { return this.generator; }

    public byte getDebugBeacons() { return this.generatedBeacons; }

    public byte getDebugRSSIMin() { return this.generatedRSSIMin; }

    public byte getDebugRSSIMax() { return this.generatedRSSIMax; }
}
