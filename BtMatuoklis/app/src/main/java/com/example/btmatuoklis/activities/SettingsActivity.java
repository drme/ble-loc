package com.example.btmatuoklis.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.util.Log;

import com.example.btmatuoklis.R;
import com.example.btmatuoklis.classes.Settings;

public class SettingsActivity extends PreferenceActivity {

    Settings settings;
    SharedPreferences.OnSharedPreferenceChangeListener preferenceListener;
    EditTextPreference editFrequency, editShadow;
    SwitchPreference switchNullDevices;
    SwitchPreference switchGenerator;
    EditTextPreference debugBeacons, debugRSSIMin, debugRSSIMax;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setSubtitle(getString(R.string.subtitle_settings));
        addPreferencesFromResource(R.xml.settings_preferences);
        editFrequency = (EditTextPreference)findPreference(this.getString(R.string.key_delay));
        editShadow = (EditTextPreference)findPreference(this.getString(R.string.key_shadow));
        switchNullDevices = (SwitchPreference)findPreference(this.getString(R.string.key_shownull));
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
                Log.d("SettingsActivity", "Preferences changed");
                settings.refreshValues();
                setDefaultValues();
            }
        };
    }

    //Nustatomos "default" reiksmes
    //Jeigu programa leidziama ne pirma karta - nustatomos issaugotos reiksmes
    void setDefaultValues(){
        settings = MainActivity.settings;
        editFrequency.setSummary(getString(R.string.settingsactivity_subtext_value)+" "+settings.getFrequency() + "ms");
        editShadow.setSummary(getString(R.string.settingsactivity_subtext_value)+" "+settings.getShadow());
        editFrequency.setDialogMessage(getString(R.string.settingsactivity_hint_frequency)+" "+settings.getDefaultFrequency());

        debugBeacons.setSummary(getString(R.string.settingsactivity_subtext_value)+" "+settings.getDebugBeacons());
        debugRSSIMin.setSummary(getString(R.string.settingsactivity_subtext_value)+" "+settings.getDebugRSSIMin());
        debugRSSIMax.setSummary(getString(R.string.settingsactivity_subtext_value)+" "+settings.getDebugRSSIMax());
        toogleDebugSettings(settings.isGeneratorEnabled());
    }

    void toogleDebugSettings(boolean toggle){
        debugBeacons.setEnabled(toggle);
        debugRSSIMin.setEnabled(toggle);
        debugRSSIMax.setEnabled(toggle);
    }
}