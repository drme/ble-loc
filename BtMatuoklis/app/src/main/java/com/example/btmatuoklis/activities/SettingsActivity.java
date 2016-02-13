package com.example.btmatuoklis.activities;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.btmatuoklis.R;
import com.example.btmatuoklis.classes.Settings;

public class SettingsActivity extends AppCompatActivity {

    Settings settings;
    TextView txVal, hintFrequency, hintGenerator;
    EditText msVal, averageVal;
    Button setMs, setAverage;
    SeekBar txSlider;
    Switch valuesGenerator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setSubtitle(getText(R.string.subtitle_settings));
        settings = MainActivity.settings;

        txVal = (TextView)findViewById(R.id.textSettings_ActiveTxPower);
        hintFrequency = (TextView)findViewById(R.id.textSettings_FrequencyHint);
        msVal = (EditText)findViewById(R.id.editSettings_Frequency);
        averageVal = (EditText)findViewById(R.id.editSettings_Average);
        setMs = (Button)findViewById(R.id.buttonSettings_SetFrequency);
        setAverage = (Button)findViewById(R.id.buttonSettings_SetAverage);
        txSlider = (SeekBar)findViewById(R.id.seekbarSettings_TxPower);
        valuesGenerator = (Switch)findViewById(R.id.switchSettings_FakeValues);
        hintGenerator = (TextView)findViewById(R.id.textSettings_FakeValuesHint);

        setDefValues();
        setMsButtonListener();
        setAverageButtonListener();
        setSliderListener();
        setGeneratorSwitchListener();
    }

    @Override
    public void onBackPressed() { this.finish(); }

    void setMsButtonListener(){
        setMs.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if (msVal.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(),
                            getText(R.string.toast_warning_empty_entry), Toast.LENGTH_SHORT).show();
                } else {
                    short ivest = Short.parseShort(msVal.getText().toString());
                    if (ivest < 250 || ivest > 5000 || msVal.getText() == null) {
                        Toast.makeText(getApplicationContext(),
                                getText(R.string.toast_warning_wrong_range), Toast.LENGTH_SHORT).show();
                    } else {
                        settings.setDelay(ivest);
                        //pakeista reiksme is kart issaugoma ateiciai
                        settings.saveDelay();
                        //patvirtinus ivesti, paslepiama klaviatura
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        msVal.clearFocus();
                        averageVal.clearFocus();
                        Toast.makeText(getApplicationContext(),
                                getText(R.string.toast_info_saved), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    void setAverageButtonListener(){
            setAverage.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    if (averageVal.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(),
                                getText(R.string.toast_warning_empty_entry), Toast.LENGTH_SHORT).show();
                    } else {
                        byte ivest = Byte.parseByte(averageVal.getText().toString());
                        if (ivest < 1 || ivest > 10 || averageVal.getText() == null) {
                            Toast.makeText(getApplicationContext(),
                                    getText(R.string.toast_warning_wrong_range), Toast.LENGTH_SHORT).show();
                        } else {
                            settings.setAverage(ivest);
                            //pakeista reiksme is kart issaugoma ateiciai
                            settings.saveAverage();
                            //patvirtinus ivesti, paslepiama klaviatura
                            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                            averageVal.clearFocus();
                            msVal.clearFocus();
                            Toast.makeText(getApplicationContext(),
                                    getText(R.string.toast_info_saved), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
    }

    void setSliderListener(){
        txSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                settings.setTxPow((byte) progress);
                txVal.setText(Byte.toString(settings.getTxPow()));
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                //pakeista reiksme is kart issaugoma ateiciai
                settings.saveTxPow();
            }
        });
    }

    void setGeneratorSwitchListener(){
        valuesGenerator.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.setGenerator(isChecked);
                settings.saveGenerator();
            }
        });
    }

    //Nustatomos "default" reiksmes
    //Jeigu programa leidziama ne pirma karta - nustatomos issaugotos reiksmes
    void setDefValues(){
        txSlider.setProgress(settings.getTxPow());
        txVal.setText(Byte.toString(settings.getTxPow()));
        averageVal.setText(Byte.toString(settings.getAverage()));
        msVal.setText(Integer.toString(settings.getDelay()));
        hintFrequency.setText(getText(R.string.settingsactivity_hint_frequency) + Short.toString(settings.getDefaultDelay()));
        valuesGenerator.setChecked(settings.getGenerator());
        hintGenerator.setText(getText(R.string.settingsactivity_hint_generator));
    }
}
