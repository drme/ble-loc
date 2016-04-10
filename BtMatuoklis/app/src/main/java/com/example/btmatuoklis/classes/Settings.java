package com.example.btmatuoklis.classes;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.btmatuoklis.R;

public class Settings implements SharedPreferences.OnSharedPreferenceChangeListener {
    private Context context;
    private SharedPreferences preferences;
    public static byte REQUEST_ENABLE_BT = 1;

    private String key_delay, key_timeout, key_shadow, key_accuracy, key_txpower, key_shownull, key_average;
    private String debug_generator, debug_beacons, debug_rssi_min, debug_rssi_max;

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

    //BTLE irenginio stiprumas, reiksme [1-100] intervale
    private byte txPower, defaultTXPower;

    //Nustatymas, kuris reguliuoja ar sarasuose bus rodomi BTLE irenginiai, kurie neturi pavadinimu
    private boolean showNull, defaultShowNull;

    //Matavimu kiekis, vieno beacon'o vidutinei RSSI reiksmei surasti
    private byte average, defaultAverage;

    //Debug - netikru irenginiu generatoriaus reiksmes
    private boolean generator, defaultGenerator;
    private byte generatedBeacons, defaultGeneratedBeacons;
    private byte generatedRSSIMin, defaultGeneratedRSSIMin;
    private byte generatedRSSIMax, defaultGeneratedRSSIMax;

    public Settings(Context context){
        this.context = context;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(this.context);
        this.initKeys(this.context);
        this.setDefaultValues();
        this.preferences.registerOnSharedPreferenceChangeListener(this);
        this.refreshValues();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        this.refreshValues();
    }

    private void initKeys(Context context){
        key_delay = context.getString(R.string.key_delay);
        key_timeout = context.getString(R.string.key_timeout);
        key_shadow = context.getString(R.string.key_shadow);
        key_accuracy = context.getString(R.string.key_accuracy);
        key_txpower = context.getString(R.string.key_txpower);
        key_shownull = context.getString(R.string.key_shownull);
        key_average = context.getString(R.string.key_average);
        debug_generator = context.getString(R.string.debug_generator);
        debug_beacons = context.getString(R.string.debug_beacons);
        debug_rssi_min = context.getString(R.string.debug_rssi_min);
        debug_rssi_max = context.getString(R.string.debug_rssi_max);
    }

    public void setDefaultValues(){
        defaultFrequency = (short)context.getResources().getInteger(R.integer.default_frequency);
        defaultTimeout = (byte)context.getResources().getInteger(R.integer.default_timeout);
        defaultShadow = (byte)context.getResources().getInteger(R.integer.default_shadow);
        defaultAccuracy = (byte)context.getResources().getInteger(R.integer.default_accuracy);
        defaultTXPower = (byte)context.getResources().getInteger(R.integer.default_txpower);
        defaultShowNull = context.getResources().getBoolean(R.bool.default_show_null);
        defaultAverage = (byte)context.getResources().getInteger(R.integer.default_average);
        defaultGenerator = context.getResources().getBoolean(R.bool.debug_default_generator);
        defaultGeneratedBeacons = (byte)context.getResources().getInteger(R.integer.debug_default_beacons);
        defaultGeneratedRSSIMin = (byte)context.getResources().getInteger(R.integer.debug_default_rssi_min);
        defaultGeneratedRSSIMax = (byte)context.getResources().getInteger(R.integer.debug_default_rssi_max);
        maxRange = (byte)context.getResources().getInteger(R.integer.default_max_range);
    }

    public void refreshValues(){
        frequency = Short.parseShort(preferences.getString(key_delay, Short.toString(defaultFrequency)));
        timeout = Byte.parseByte(preferences.getString(key_timeout, Byte.toString(defaultTimeout)));
        shadow = Byte.parseByte(preferences.getString(key_shadow, Byte.toString(defaultShadow)));
        accuracy = (byte)preferences.getInt(key_accuracy, defaultAccuracy);
        txPower = (byte)preferences.getInt(key_txpower, defaultTXPower);
        showNull = preferences.getBoolean(key_shownull, defaultShowNull);
        average = Byte.parseByte(preferences.getString(key_average, Byte.toString(defaultAverage)));
        generator = preferences.getBoolean(debug_generator, defaultGenerator);
        generatedBeacons = Byte.parseByte(preferences.getString(debug_beacons, Byte.toString(defaultGeneratedBeacons)));
        generatedRSSIMin = Byte.parseByte(preferences.getString(debug_rssi_min, Byte.toString(defaultGeneratedRSSIMin)));
        generatedRSSIMax = Byte.parseByte(preferences.getString(debug_rssi_max, Byte.toString(defaultGeneratedRSSIMax)));
    }

    public byte getMaxRange(){ return maxRange; }

    public short getFrequency(){ return this.frequency; }

    public byte getTimeout(){ return this.timeout; }

    public byte getShadow(){ return this.shadow; }

    public byte getAccuracy(){ return this.accuracy; }

    public byte getTXPower(){ return this.txPower; }

    public boolean showNullDevices() { return this.showNull; }

    public byte getAverage(){ return this.average; }

    public short getDefaultFrequency() { return this.defaultFrequency; }

    public byte getDefaultAverage() { return this.defaultAverage; }

    public boolean isGeneratorEnabled() { return this.generator; }

    public byte getDebugBeacons() { return this.generatedBeacons; }

    public byte getDebugRSSIMin() { return this.generatedRSSIMin; }

    public byte getDebugRSSIMax() { return this.generatedRSSIMax; }
}