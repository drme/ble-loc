package com.example.btmatuoklis.classes;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.btmatuoklis.R;

public class Settings {
    private Context context;
    private SharedPreferences preferences;
    public static byte REQUEST_ENABLE_BT = 1;

    private String key_shadow, key_fast_sleep, key_show_null;
    private String debug_generator, debug_beacons, debug_rssi_min, debug_rssi_max;

    //Maksimalus teorinis BTLE aptikimo atstumas metrais
    public static byte maxRange;

    //Kiek RSSI saugoti aktyvaus Scan rezimu
    private byte shadow, defaultShadow;

    //Nustatymas, kuris reguliuoja ar sarasuose bus rodomi BTLE irenginiai, kurie neturi pavadinimu
    private boolean fastSleep, defaultFastSleep;

    //Nustatymas, kuris reguliuoja ar sarasuose bus rodomi BTLE irenginiai, kurie neturi pavadinimu
    private boolean showNull, defaultShowNull;

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
        this.refreshValues();
    }

    private void initKeys(Context context){
        key_shadow = context.getString(R.string.key_shadow);
        key_fast_sleep = context.getString(R.string.key_fast_sleep);
        key_show_null = context.getString(R.string.key_show_null);
        debug_generator = context.getString(R.string.debug_generator);
        debug_beacons = context.getString(R.string.debug_beacons);
        debug_rssi_min = context.getString(R.string.debug_rssi_min);
        debug_rssi_max = context.getString(R.string.debug_rssi_max);
    }

    public void setDefaultValues(){
        defaultShadow = (byte)context.getResources().getInteger(R.integer.default_shadow);
        defaultFastSleep = context.getResources().getBoolean(R.bool.default_fast_sleep);
        defaultShowNull = context.getResources().getBoolean(R.bool.default_show_null);
        defaultGenerator = context.getResources().getBoolean(R.bool.debug_default_generator);
        defaultGeneratedBeacons = (byte)context.getResources().getInteger(R.integer.debug_default_beacons);
        defaultGeneratedRSSIMin = (byte)context.getResources().getInteger(R.integer.debug_default_rssi_min);
        defaultGeneratedRSSIMax = (byte)context.getResources().getInteger(R.integer.debug_default_rssi_max);
        maxRange = (byte)context.getResources().getInteger(R.integer.max_range);
    }

    public void refreshValues(){
        shadow = Byte.parseByte(preferences.getString(key_shadow, Byte.toString(defaultShadow)));
        fastSleep = preferences.getBoolean(key_fast_sleep, defaultFastSleep);
        showNull = preferences.getBoolean(key_show_null, defaultShowNull);
        generator = preferences.getBoolean(debug_generator, defaultGenerator);
        generatedBeacons = Byte.parseByte(preferences.getString(debug_beacons, Byte.toString(defaultGeneratedBeacons)));
        generatedRSSIMin = Byte.parseByte(preferences.getString(debug_rssi_min, Byte.toString(defaultGeneratedRSSIMin)));
        generatedRSSIMax = Byte.parseByte(preferences.getString(debug_rssi_max, Byte.toString(defaultGeneratedRSSIMax)));
    }

    public byte getMaxRange(){ return maxRange; }

    public byte getShadow(){ return this.shadow; }

    public boolean isFastSleep() { return this.fastSleep; }

    public boolean showNullDevices() { return this.showNull; }

    public boolean isGeneratorEnabled() { return this.generator; }

    public byte getDebugBeacons() { return this.generatedBeacons; }

    public byte getDebugRSSIMin() { return this.generatedRSSIMin; }

    public byte getDebugRSSIMax() { return this.generatedRSSIMax; }
}