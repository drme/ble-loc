package com.example.btmatuoklis.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;

import com.example.btmatuoklis.R;
import com.example.btmatuoklis.classes.SeekBarPreference;
import com.example.btmatuoklis.classes.Settings;

public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    Settings settings;
    EditTextPreference editFrequency, editAverage;
    SeekBarPreference sliderTXPower;
    EditTextPreference debugBeacons, debugRSSIMin, debugRSSIMax;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setSubtitle(getString(R.string.subtitle_settings));
        addPreferencesFromResource(R.xml.settings_preferences);
        editFrequency = (EditTextPreference)findPreference("savedDelay");
        editAverage = (EditTextPreference)findPreference("savedAverage");
        sliderTXPower = (SeekBarPreference)findPreference("savedTXPower");
        debugBeacons = (EditTextPreference)findPreference("debugBeacons");
        debugRSSIMin = (EditTextPreference)findPreference("debugRSSIMin");
        debugRSSIMax = (EditTextPreference)findPreference("debugRSSIMax");

        setDefaultValues();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        setDefaultValues();
    }

    //Nustatomos "default" reiksmes
    //Jeigu programa leidziama ne pirma karta - nustatomos issaugotos reiksmes
    void setDefaultValues(){
        settings = MainActivity.settings;
        sliderTXPower.setSummary(getString(R.string.settingsactivity_subtext_value) + settings.getTXPower());
        editFrequency.setSummary(getString(R.string.settingsactivity_subtext_value) + settings.getDelay());
        editFrequency.setDialogMessage(getString(R.string.settingsactivity_hint_frequency) + settings.getDefaultDelay());
        editAverage.setSummary(getString(R.string.settingsactivity_subtext_value) + settings.getAverage());
        debugBeacons.setSummary(getString(R.string.settingsactivity_subtext_value) + settings.getDebugBeacons());
        debugRSSIMin.setSummary(getString(R.string.settingsactivity_subtext_value) + settings.getDebugRSSIMin());
        debugRSSIMax.setSummary(getString(R.string.settingsactivity_subtext_value) + settings.getDebugRSSIMax());
    }
}