package com.example.btmatuoklis.activities;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.example.btmatuoklis.R;
import com.example.btmatuoklis.classes.Settings;

public class SettingsActivity extends PreferenceActivity {

    Settings settings;
    SeekBar sliderTXPower;
    TextView displayTXPower, hintFrequency, hintGenerator;
    EditTextPreference editFrequency, editAverage;
    Switch switchGenerator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_settings);
        getActionBar().setSubtitle(getString(R.string.subtitle_settings));
        addPreferencesFromResource(R.xml.settings_preferences);
        //sliderTXPower = (SeekBar)findViewById(R.id.seekbarSettings_TxPower);
        //displayTXPower = (TextView)findViewById(R.id.textSettings_ActiveTxPower);
        editFrequency = (EditTextPreference)findPreference("savedDelay");
        //hintFrequency = (TextView)findViewById(R.id.textSettings_FrequencyHint);
        editAverage = (EditTextPreference)findPreference("savedAverage");
        //switchGenerator = (Switch)findViewById(R.id.switchSettings_FakeValues);
        //hintGenerator = (TextView)findViewById(R.id.textSettings_FakeValuesHint);

        setDefaultValues();
        //setSliderListener();
        //setGeneratorSwitchListener();
    }

    @Override
    public void onBackPressed() {
        //this.finish();
        this.recreate();
    }

    /*public void onMsButtonClick(View view){
        if (editFrequency.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.toast_warning_empty_entry), Toast.LENGTH_SHORT).show();
        } else {
            short ivest = Short.parseShort(editFrequency.getText().toString());
            if (ivest < 250 || ivest > 5000 || editFrequency.getText() == null) {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.toast_warning_wrong_range), Toast.LENGTH_SHORT).show();
            } else {
                settings.setDelay(ivest);
                //pakeista reiksme is kart issaugoma ateiciai
                settings.saveDelay();
                //patvirtinus ivesti, paslepiama klaviatura
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                editFrequency.clearFocus();
                editAverage.clearFocus();
                Toast.makeText(getApplicationContext(),
                        getString(R.string.toast_info_saved), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onAverageButtonClick(View view){
        if (editAverage.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.toast_warning_empty_entry), Toast.LENGTH_SHORT).show();
        } else {
            byte ivest = Byte.parseByte(editAverage.getText().toString());
            if (ivest < 1 || ivest > 10 || editAverage.getText() == null) {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.toast_warning_wrong_range), Toast.LENGTH_SHORT).show();
            } else {
                settings.setAverage(ivest);
                //pakeista reiksme is kart issaugoma ateiciai
                settings.saveAverage();
                //patvirtinus ivesti, paslepiama klaviatura
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                editAverage.clearFocus();
                editFrequency.clearFocus();
                Toast.makeText(getApplicationContext(),
                        getString(R.string.toast_info_saved), Toast.LENGTH_SHORT).show();
            }
        }
    }

    void setSliderListener(){
        sliderTXPower.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                settings.setTXPower((byte) progress);
                displayTXPower.setText(Byte.toString(settings.getTXPower()));
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                //pakeista reiksme is kart issaugoma ateiciai
                settings.saveTXPower();
            }
        });
    }

    void setGeneratorSwitchListener(){
        switchGenerator.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.setGenerator(isChecked);
                settings.saveGenerator();
            }
        });
    }*/

    //Nustatomos "default" reiksmes
    //Jeigu programa leidziama ne pirma karta - nustatomos issaugotos reiksmes
    void setDefaultValues(){
        settings = MainActivity.settings;
        editFrequency.setSummary("Dabartinis dažnis: " + Integer.toString(settings.getDelay()));
        editAverage.setSummary("Dabartinis kiekis: " + Integer.toString(settings.getAverage()));
        /*sliderTXPower.setProgress(settings.getTXPower());
        displayTXPower.setText(Byte.toString(settings.getTXPower()));
        editFrequency.setText(Integer.toString(settings.getDelay()));
        hintFrequency.setText(getString(R.string.settingsactivity_hint_frequency) + Short.toString(settings.getDefaultDelay()));
        editAverage.setText(Byte.toString(settings.getAverage()));
        switchGenerator.setChecked(settings.getGenerator());
        hintGenerator.setText(getString(R.string.settingsactivity_hint_generator));*/
    }
}
