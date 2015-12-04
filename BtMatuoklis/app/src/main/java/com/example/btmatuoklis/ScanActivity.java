package com.example.btmatuoklis;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class ScanActivity extends AppCompatActivity {

    //Kas kiek laiko kartosis scan
    short delay = 1000; //Matuojant su maziau negu 300ms, po kurio laiko uzstringa

    //"Default" BLE irenginio stiprumas
    static byte txPow = 50;//Reiksme [1-100] intervale

    byte REQUEST_ENABLE_BT = 1;
    BluetoothAdapter mBluetoothAdapter;
    static ArrayList<DevInfo> btDevList;
    ArrayList<DevInfo> savedDevList;
    CustomInfoAdapter listAdapter;
    SharedPreferences preferences;
    SharedPreferences.Editor edit;

    TextView txVal, hintInfo, xKoord, yKoord;
    EditText msVal;
    Button setMs;
    SeekBar txSlider;
    ListView btInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        btInfo = (ListView)findViewById(R.id.listView);
        txVal = (TextView)findViewById(R.id.textView5);
        hintInfo = (TextView)findViewById(R.id.textView3);
        msVal = (EditText)findViewById(R.id.editText);
        setMs = (Button)findViewById(R.id.button2);
        txSlider = (SeekBar)findViewById(R.id.seekBar);
        xKoord = (TextView)findViewById(R.id.textView13);
        yKoord = (TextView)findViewById(R.id.textView14);

        preferences = getSharedPreferences("savedSettings", MODE_PRIVATE);
        edit = preferences.edit();

        setMsButtonListener();
        setSliderListener();
        setDefValues();
        createBT();
        checkBT();
        contScanStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    //Sukuriamas Bluetooth adapteris
    void createBT(){
        BluetoothManager bluetoothManager =
                (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        //bluetoothManager
        mBluetoothAdapter = bluetoothManager.getAdapter();
    }

    //Patikriname ar Bluetooth telefone yra ijungtas
    //Jei ne - paprasoma ijungti
    void checkBT(){
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

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
                        delay = ivest;
                        //pakeista reiksme is kart issaugoma ateiciai
                        edit.putInt("savedDelay", delay);
                        edit.apply();
                        //patvirtinus ivesti, paslepiama klaviatura
                        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        msVal.clearFocus();
                    }
                }
            }
        });
    }

    void setSliderListener(){
        txSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txPow = (byte) progress;
                txVal.setText(Byte.toString(txPow));
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                //pakeista reiksme is kart issaugoma ateiciai
                edit.putInt("savedTxPow", txPow);
                edit.apply();
            }
        });
    }

    //Nustatomos "default" reiksmes
    //Jeigu programa leidziama ne pirma karta - nustatomos issaugotos reiksmes
    void setDefValues(){
        txPow = (byte)preferences.getInt("savedTxPow", txPow);
        delay = (short)preferences.getInt("savedDelay", delay);
        txSlider.setProgress(txPow);
        msVal.setText(Integer.toString(delay));
        hintInfo.setText("Rekomenduotinos reikšmės intervale:\n[250; 5000], default - " + delay);
        btDevList = new ArrayList<DevInfo>();
        savedDevList = new ArrayList<DevInfo>();
        listAdapter = new CustomInfoAdapter(this, btDevList);
        btInfo.setAdapter(listAdapter);
    }

    //Jeigu randamas BLE irenginys, pastoviai gaunama jo RSSI reiksme
    //Per daug tikrinimu, reikia optimizuoti
    void startStopScan(){
        //Pradedamas scan
        mBluetoothAdapter.startLeScan(new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                byte numDev = 0;
                byte listSize = (byte) btDevList.size();
                byte currentRssi = (byte) rssi;
                if (listSize == 0) {
                    btDevList.add(new DevInfo(device.getName(), device.getAddress()));
                    btDevList.get(0).setRssi(currentRssi);
                } else {
                    if (btDevList.size() > 2)
                        koordinates();
                    for (byte i = 0; i < listSize; i++) {
                        if (btDevList.get(i).getMac().equals(device.getAddress())) {
                            btDevList.get(i).setRssi(currentRssi);
                        } else {
                            numDev++;
                        }
                    }
                    if (numDev > listSize - 1) {
                        btDevList.add(new DevInfo(device.getName(), device.getAddress()));
                        btDevList.get(numDev).setRssi(currentRssi);
                    }

                }
                listAdapter.notifyDataSetChanged();
                mBluetoothAdapter.stopLeScan(this); //Scan stabdomas
            }
        });
    }

    //Nuolatos pradedamas ir stabdomas scan
    void contScanStop(){
        final Handler h = new Handler();
        h.postDelayed(new Runnable() {
            public void run() {
                startStopScan();
                h.postDelayed(this, delay);
            }
        }, delay);
    }

    // Pradinė versija, dar turi kisti.
    void koordinates(){

        float x1 = btDevList.get(0).getX_val();
        float x2 = btDevList.get(1).getX_val();
        float x3 = btDevList.get(2).getX_val();
        float y1 = btDevList.get(0).getY_val();
        float y2 = btDevList.get(1).getY_val();
        float y3 = btDevList.get(2).getY_val();

        float r1 = btDevList.get(0).getDistance();
        float r2 = btDevList.get(1).getDistance();
        float r3 = btDevList.get(2).getDistance();

        float A=(-2*x1+ 2*x2);
        float B=(-2*y1 + 2* y2);
        float C=(r1*r1 -r2*r2 -x1*x1 + x2* x2 -y1 * y1 + y2 * y2);
        float D=(-2*x2 + 2*x3);
        float E=( -2*y2 + 2*y3);
        float F=(r2*r2 -r3*r3 -x2*x2 + x3*x3 -y2*y2 +x3 * y2);

        float x = (C*D - F*A)/(B*D - E*A );
        float y = (A*E - D*B)/(C*E - F*B);

        xKoord.setText(String.format("X koordinatė: %.2f", x ));
        yKoord.setText(String.format("Y koordinatė: %.2f", y ));
    }
}
