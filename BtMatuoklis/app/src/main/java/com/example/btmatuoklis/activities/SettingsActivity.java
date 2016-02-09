package com.example.btmatuoklis.activities;

import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.btmatuoklis.R;
import com.example.btmatuoklis.classes.Settings;

public class SettingsActivity extends AppCompatActivity {

    Settings settings;
    TextView txVal, hintInfo;
    EditText msVal;
    Button setMs;
    SeekBar txSlider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setSubtitle(getText(R.string.settings_name));
        settings = MainActivity.settings;

        txVal = (TextView)findViewById(R.id.textSettings_ActiveTxPower);
        hintInfo = (TextView)findViewById(R.id.textSettings_FrequencyHint);
        msVal = (EditText)findViewById(R.id.editSettings_Frequency);
        setMs = (Button)findViewById(R.id.buttonSettings_SetFrequency);
        txSlider = (SeekBar)findViewById(R.id.seekbarSettings_TxPower);

        setDefValues();
        setMsButtonListener();
        setSliderListener();
    }

    @Override
    public void onBackPressed() { this.finish(); }

    void setMsButtonListener(){
        setMs.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if (msVal.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(),
                            "Neįvesta reikšmė!", Toast.LENGTH_SHORT).show();
                } else {
                    short ivest = Short.parseShort(msVal.getText().toString());
                    if (ivest < 250 || ivest > 5000 || msVal.getText() == null) {
                        Toast.makeText(getApplicationContext(),
                                "Netinkamas intervalas!", Toast.LENGTH_SHORT).show();
                    } else {
                        settings.setDelay(ivest);
                        //pakeista reiksme is kart issaugoma ateiciai
                        settings.saveDelay();
                        //patvirtinus ivesti, paslepiama klaviatura
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        msVal.clearFocus();
                        Toast.makeText(getApplicationContext(),
                                "Išsaugota.", Toast.LENGTH_SHORT).show();
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

    //Nustatomos "default" reiksmes
    //Jeigu programa leidziama ne pirma karta - nustatomos issaugotos reiksmes
    void setDefValues(){
        txSlider.setProgress(settings.getTxPow());
        txVal.setText(Byte.toString(settings.getTxPow()));
        msVal.setText(Integer.toString(settings.getDelay()));
        hintInfo.setText("Rekomenduotinos reikšmės intervale:\n[250; 5000], default - " + settings.getDefaultDelay());
    }
}
