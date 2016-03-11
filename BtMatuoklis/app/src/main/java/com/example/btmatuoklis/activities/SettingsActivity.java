package com.example.btmatuoklis.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;

import com.example.btmatuoklis.R;
import com.example.btmatuoklis.classes.SeekBarPreference;
import com.example.btmatuoklis.classes.Settings;

public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    Settings settings;
    EditTextPreference editFrequency, editTimeout, editShadow, editAverage;
    SeekBarPreference sliderAccuracy, sliderTXPower;
    SwitchPreference switchGenerator;
    EditTextPreference debugBeacons, debugRSSIMin, debugRSSIMax;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setSubtitle(getString(R.string.subtitle_settings));
        addPreferencesFromResource(R.xml.settings_preferences);
        editFrequency = (EditTextPreference)findPreference("savedDelay");
        editTimeout = (EditTextPreference)findPreference("savedTimeout");
        editShadow = (EditTextPreference)findPreference("savedShadow");
        sliderAccuracy = (SeekBarPreference)findPreference("savedAccuracy");
        sliderTXPower = (SeekBarPreference)findPreference("savedTXPower");
        editAverage = (EditTextPreference)findPreference("savedAverage");
        switchGenerator = (SwitchPreference)findPreference("debugGenerator");
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
        editFrequency.setSummary(getString(R.string.settingsactivity_subtext_value)+" "+settings.getFrequency() + "ms");
        editTimeout.setSummary(getString(R.string.settingsactivity_subtext_value)+" "+settings.getTimeout());
        editShadow.setSummary(getString(R.string.settingsactivity_subtext_value)+" "+settings.getShadow());
        sliderAccuracy.setSummary(getString(R.string.settingsactivity_subtext_value)+" "+settings.getAccuracy()+"%");
        sliderTXPower.setSummary(getString(R.string.settingsactivity_subtext_value)+" "+settings.getTXPower());
        editAverage.setSummary(getString(R.string.settingsactivity_subtext_value)+" "+settings.getAverage());
        editFrequency.setDialogMessage(getString(R.string.settingsactivity_hint_frequency)+" "+settings.getDefaultFrequency());

        debugBeacons.setSummary(getString(R.string.settingsactivity_subtext_value)+" "+settings.getDebugBeacons());
        debugRSSIMin.setSummary(getString(R.string.settingsactivity_subtext_value)+" "+settings.getDebugRSSIMin());
        debugRSSIMax.setSummary(getString(R.string.settingsactivity_subtext_value)+" "+settings.getDebugRSSIMax());
        toogleDebugSettings(settings.isGeneratorEnabled());

        editTimeout.setEnabled(false);
    }

    void toogleDebugSettings(boolean toggle){
        debugBeacons.setEnabled(toggle);
        debugRSSIMin.setEnabled(toggle);
        debugRSSIMax.setEnabled(toggle);
    }
}