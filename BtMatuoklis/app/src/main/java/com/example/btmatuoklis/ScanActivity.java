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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class ScanActivity extends AppCompatActivity {

    //Kas kiek laiko kartosis scan
    int delay = 500; //Matuojant su maziau negu 300ms, po kurio laiko uzstringa

    //Default BLE irenginio stiprumas
    int txPow = 50; //Reiksme [1-100] intervale

    private final static int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter mBluetoothAdapter;

    TextView btInfo, txVal, hintInfo;
    EditText msVal;
    Button setMs;
    SeekBar txSlider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        btInfo = (TextView)findViewById(R.id.textView2);
        txVal = (TextView)findViewById(R.id.textView5);
        hintInfo = (TextView)findViewById(R.id.textView3);
        msVal = (EditText)findViewById(R.id.editText);
        setMs = (Button)findViewById(R.id.button2);
        txSlider = (SeekBar)findViewById(R.id.seekBar);

        setMs.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if (msVal.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(),
                            "Neįvesta reikšmė!", Toast.LENGTH_SHORT).show();
                }
                else {
                    int ivest = Integer.parseInt(msVal.getText().toString());
                    if (ivest < 250 || ivest > 5000 || msVal.getText() == null) {
                        Toast.makeText(getApplicationContext(),
                                "Netinkamas intervalas!", Toast.LENGTH_SHORT).show();
                    } else {
                        delay = ivest;
                    }
                }
            }
        });

        txSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress;
                txPow = progressChanged;
                txVal.setText(Integer.toString(txPow));
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        txSlider.setProgress(txPow);
        msVal.setText(Integer.toString(delay));
        hintInfo.setText("Rekomenduotinos reikšmės intervale:\n[250; 5000], default - " + delay);

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

    //Jeigu randamas BLE irenginys, pastoviai gaunama jo RSSI reiksme
    void startStopScan(){
        //Pradedamas scan
        mBluetoothAdapter.startLeScan(new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                btInfo.setText("Įrenginys: " + device.getName() +
                        "\nMAC: " + device.getAddress() +
                        "\nRSSI: " + rssi + "\n" +
                        String.format("Apytikslis atstumas?: %.2f", calculateAccuracy(txPow, rssi)) + " m");
                mBluetoothAdapter.stopLeScan(this); //Scan stabdomas
            }
        });
    }

    //Nuolatos pradedamas ir stabdomas scan
    void contScanStop(){
        final Handler h = new Handler();
        h.postDelayed(new Runnable(){
            public void run(){
                startStopScan();
                h.postDelayed(this, delay);
            }
        }, delay);
    }

    //Funkcija rasta internete
    //Veikimo principas panasus i funkcija randama iOS?
    protected static double calculateAccuracy(int txPower, double rssi) {
        if (rssi == 0) {
            return -1.0; // if we cannot determine accuracy, return -1.
        }

        double ratio = rssi*1.0/txPower;
        if (ratio < 1.0) {
            return Math.pow(ratio,10);
        }
        else {
            double accuracy =  (0.89976)*Math.pow(ratio,7.7095) + 0.111;
            return accuracy;
        }
    }
}
