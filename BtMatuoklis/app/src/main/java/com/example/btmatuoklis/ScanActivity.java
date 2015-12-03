package com.example.btmatuoklis;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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

    //Default BLE irenginio stiprumas
    static short txPow = 50; //Reiksme [1-100] intervale

    private final static short REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter mBluetoothAdapter;
    ArrayList<DevInfo> btDevList;
    CustomInfoAdapter listAdapter;

    TextView txVal, hintInfo;
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

        setMsButtonListener();
        setSliderListener();
        setDefValues();
        createBT();
        checkBT();
        contScanStop();
    }

    //Sukuriamas Bluetooth adapteris
    void createBT(){
        final BluetoothManager bluetoothManager =
                (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
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
                    }
                }
            }
        });
    }

    void setSliderListener(){
        txSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            short progressChanged = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = (short)progress;
                txPow = progressChanged;
                txVal.setText(Short.toString(txPow));
            }

            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    void setDefValues(){
        txSlider.setProgress(txPow);
        msVal.setText(Short.toString(delay));
        hintInfo.setText("Rekomenduotinos reikšmės intervale:\n[250; 5000], default - " + delay);
        btDevList = new ArrayList<DevInfo>();
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
                short numDev = 0;
                if (btDevList.isEmpty()) {
                    btDevList.add(new DevInfo(device.getName(), device.getAddress(), (short)rssi));
                } else {
                    for (short i = 0; i < btDevList.size(); i++) {
                        if (btDevList.get(i).getMac().equals(device.getAddress())) {
                            btDevList.get(i).updateRssi((short)rssi);
                        } else {
                            numDev++;
                        }
                    }
                    if (numDev > btDevList.size() - 1) {
                        btDevList.add(new DevInfo(device.getName(), device.getAddress(), (short)rssi));
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
}
