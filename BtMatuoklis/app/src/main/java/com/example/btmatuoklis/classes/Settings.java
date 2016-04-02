package com.example.btmatuoklis.classes;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.btmatuoklis.R;

public class Settings implements SharedPreferences.OnSharedPreferenceChangeListener {
    private Context context;
    private SharedPreferences preferences;
    public static byte REQUEST_ENABLE_BT = 1;

    //Maksimalus teorinis BTLE aptikimo atstumas metrais
    public static byte maxRange;

    //Kas kiek laiko kartosis scan
    //Matuojant su maziau negu 300ms, po kurio laiko uzstringa
    private short frequency, defaultFrequency;

    //Kokiu intervalu kartosis aptiktu ienginiu salinimas
    //Salinimo daznis: timeout * delay = x ms
    private byte timeout, defaultTimeout;

    //Kiek RSSI saugoti aktyvaus Scan rezimu
    private byte shadow, defaultShadow;

    //Kiek RSSI turi pakliuti i kalibravimo intervala
    private byte accuracy, defaultAccuracy;

    //"Default" BTLE irenginio stiprumas, reiksme [1-100] intervale
    private byte txPower, defaultTXPower;

    //Matavimu kiekis, vieno beacon'o vidutinei RSSI reiksmei surasti
    private byte average, defaultAverage;

    //Debug - netikru irenginiu generatoriaus reiksmes
    private boolean generator, defaultGenerator;
    private byte generatedBeacons, defaultGeneratedBeacons;
    private byte generatedRSSIMin, defaultGeneratedRSSIMin;
    private byte generatedRSSIMax, defaultGeneratedRSSIMax;

    public Settings(Context context){
        this.context = context;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.preferences.registerOnSharedPreferenceChangeListener(this);
        setDefaultValues();
        refreshValues();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        refreshValues();
    }

    public void setDefaultValues(){
        defaultFrequency = (short)context.getResources().getInteger(R.integer.default_frequency);
        defaultTimeout = (byte)context.getResources().getInteger(R.integer.default_timeout);
        defaultShadow = (byte)context.getResources().getInteger(R.integer.default_shadow);
        defaultAccuracy = (byte)context.getResources().getInteger(R.integer.default_accuracy);
        defaultTXPower = (byte)context.getResources().getInteger(R.integer.default_txpower);
        defaultAverage = (byte)context.getResources().getInteger(R.integer.default_average);
        defaultGenerator = context.getResources().getBoolean(R.bool.debug_default_generator);
        defaultGeneratedBeacons = (byte)context.getResources().getInteger(R.integer.debug_default_beacons);
        defaultGeneratedRSSIMin = (byte)context.getResources().getInteger(R.integer.debug_default_rssi_min);
        defaultGeneratedRSSIMax = (byte)context.getResources().getInteger(R.integer.debug_default_rssi_max);
        maxRange = (byte)context.getResources().getInteger(R.integer.default_max_range);
    }

    public void refreshValues(){
        frequency = Short.parseShort(preferences.getString("savedDelay", Short.toString(defaultFrequency)));
        timeout = Byte.parseByte(preferences.getString("savedTimeout", Byte.toString(defaultTimeout)));
        shadow = Byte.parseByte(preferences.getString("savedShadow", Byte.toString(defaultShadow)));
        accuracy = (byte)preferences.getInt("savedAccuracy", defaultAccuracy);
        txPower = (byte)preferences.getInt("savedTXPower", defaultTXPower);
        average = Byte.parseByte(preferences.getString("savedAverage", Byte.toString(defaultAverage)));
        generator = preferences.getBoolean("debugGenerator", defaultGenerator);
        generatedBeacons = Byte.parseByte(preferences.getString("debugBeacons", Byte.toString(defaultGeneratedBeacons)));
        generatedRSSIMin = Byte.parseByte(preferences.getString("debugRSSIMin", Byte.toString(defaultGeneratedRSSIMin)));
        generatedRSSIMax = Byte.parseByte(preferences.getString("debugRSSIMax", Byte.toString(defaultGeneratedRSSIMax)));
    }

    public byte getMaxRange(){ return this.maxRange; }

    public short getFrequency(){ return this.frequency; }

    public byte getTimeout(){ return this.timeout; }

    public byte getShadow(){ return this.shadow; }

    public byte getAccuracy(){ return this.accuracy; }

    public byte getTXPower(){ return this.txPower; }

    public byte getAverage(){ return this.average; }

    public short getDefaultFrequency() { return this.defaultFrequency; }

    public byte getDefaultAverage() { return this.defaultAverage; }

    public boolean isGeneratorEnabled() { return this.generator; }

    public byte getDebugBeacons() { return this.generatedBeacons; }

    public byte getDebugRSSIMin() { return this.generatedRSSIMin; }

    public byte getDebugRSSIMax() { return this.generatedRSSIMax; }
}