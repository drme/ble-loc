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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ScanActivity extends AppCompatActivity {

    //Kas kiek laiko kartosis scan
    int delay = 500; //Matuojant su maziau negu 300ms, po kurio laiko uzstringa

    //Default BLE irenginio stiprumas
    int txPow = 50; //Reiksme [1-100] intervale

    private final static int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter mBluetoothAdapter;
    List<DevInfo> btDevList = new ArrayList<DevInfo>();

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
    //Per daug tikrinimu, reikia optimizuoti
    void startStopScan(){
        //Pradedamas scan
        mBluetoothAdapter.startLeScan(new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                Integer numDev = 0;
                if (btDevList.isEmpty()) {
                    btDevList.add(new DevInfo(device.getName(), device.getAddress(), rssi));
                } else {
                    for (int i = 0; i < btDevList.size(); i++) {
                        if (btDevList.get(i).getMac().equals(device.getAddress())) {
                            btDevList.get(i).updateRssi(rssi);
                        }
                        else {
                            numDev++;
                        }
                    }
                    if (numDev > btDevList.size() - 1){
                        btDevList.add(new DevInfo(device.getName(), device.getAddress(), rssi));
                    }
                }
                convertList();
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

    //Reiksmes dedamos is List i Listview
    //Kodas visiskai neoptimalus - reikes keisti
    void convertList(){
        ArrayAdapter<String> listAdapter;
        ArrayList<String> convertedList = new ArrayList<String>();
        for (int j = 0; j < btDevList.size(); j++){
            convertedList.add(btDevList.get(j).getInfo()+"\n"+String.format(
                    "Apytikslis atstumas?: %.2f", calculateAccuracy(txPow, btDevList.get(j).getRssi()))+" m");
        }
        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, convertedList);
        btInfo.setAdapter(listAdapter);
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

    void setMsButtonListener(){
        setMs.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if (msVal.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(),
                            "Neįvesta reikšmė!", Toast.LENGTH_SHORT).show();
                } else {
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
    }

    void setSliderListener(){
        txSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress;
                txPow = progressChanged;
                txVal.setText(Integer.toString(txPow));
            }

            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }
}
