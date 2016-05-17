package com.example.btmatuoklis.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;

import com.example.btmatuoklis.R;
import com.example.btmatuoklis.classes.Settings;

public class SettingsActivity extends PreferenceActivity {

    Settings settings;
    SharedPreferences.OnSharedPreferenceChangeListener preferenceListener;
    EditTextPreference editShadow;
    SwitchPreference switchFastSleep, switchNullDevices;
    SwitchPreference switchGenerator;
    EditTextPreference debugBeacons, debugRSSIMin, debugRSSIMax;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setSubtitle(getString(R.string.subtitle_settings));
        addPreferencesFromResource(R.xml.settings_preferences);
        editShadow = (EditTextPreference)findPreference(this.getString(R.string.key_shadow));
        switchFastSleep = (SwitchPreference)findPreference(this.getString(R.string.key_fast_sleep));
        switchNullDevices = (SwitchPreference)findPreference(this.getString(R.string.key_show_null));
        switchGenerator = (SwitchPreference)findPreference(this.getString(R.string.debug_generator));
        debugBeacons = (EditTextPreference)findPreference(this.getString(R.string.debug_beacons));
        debugRSSIMin = (EditTextPreference)findPreference(this.getString(R.string.debug_rssi_min));
        debugRSSIMax = (EditTextPreference)findPreference(this.getString(R.string.debug_rssi_max));

        setDefaultValues();
        createPreferencesListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(preferenceListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(preferenceListener);
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    void createPreferencesListener(){
        preferenceListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                settings.refreshValues();
                setDefaultValues();
            }
        };
    }

    //Nustatomos "default" reiksmes
    //Jeigu programa leidziama ne pirma karta - nustatomos issaugotos reiksmes
    void setDefaultValues(){
        settings = MainActivity.settings;
        editShadow.setSummary(getString(R.string.settingsactivity_subtext_value)+" "+settings.getShadow());

        debugBeacons.setSummary(getString(R.string.settingsactivity_subtext_value)+" "+settings.getDebugBeacons());
        debugRSSIMin.setSummary(getString(R.string.settingsactivity_subtext_value)+" "+settings.getDebugRSSIMin());
        debugRSSIMax.setSummary(getString(R.string.settingsactivity_subtext_value)+" "+settings.getDebugRSSIMax());
        toggleDebugSettings(settings.isGeneratorEnabled());
    }

    void toggleDebugSettings(boolean toggle){
        debugBeacons.setEnabled(toggle);
        debugRSSIMin.setEnabled(toggle);
        debugRSSIMax.setEnabled(toggle);
    }
}