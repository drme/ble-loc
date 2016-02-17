package com.example.btmatuoklis.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.btmatuoklis.classes.Beacon;
import com.example.btmatuoklis.R;
import com.example.btmatuoklis.classes.GlobalClass;
import com.example.btmatuoklis.classes.ScanTools;
import com.example.btmatuoklis.classes.Settings;

import java.util.ArrayList;

public class ScanActivity extends AppCompatActivity {

    Settings settings;
    ScanTools scantools = new ScanTools();

    BluetoothAdapter mBluetoothAdapter;
    ArrayList<Beacon> btDevList;
    ArrayList<String> savedDevList;
    ArrayAdapter<String> listAdapter;
    ListView btInfo;
    GlobalClass globalVariable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        getSupportActionBar().setSubtitle(getText(R.string.subtitle_scan));
        globalVariable = (GlobalClass) getApplicationContext();
        settings = MainActivity.settings;
        btInfo = (ListView)findViewById(R.id.listScan_DevicesList);

        setDefValues();
        createBT();
        checkBT();
        contScanStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_scan, menu);
        menu.findItem(R.id.action_progress).setVisible(true);
        return true;
    }

    @Override
    public void onBackPressed(){
        globalVariable.setScanning(false);
        this.finish();
    }

    public void onHelpActionClick(MenuItem item){
        //Work in progress
        Toast.makeText(getApplicationContext(), "Not implemented.", Toast.LENGTH_SHORT).show();
    }

    public void onSettingsActionClick(MenuItem item){
        startActivity(new Intent(getBaseContext(), SettingsActivity.class));
    }

    //Sukuriamas Bluetooth adapteris
    public void createBT(){
        BluetoothManager bluetoothManager =
                (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
    }

    //Patikriname ar Bluetooth telefone yra ijungtas
    //Jei ne - paprasoma ijungti
    void checkBT(){
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, settings.REQUEST_ENABLE_BT);
        }
    }

    //Nustatomos "default" reiksmes
    //Jeigu programa leidziama ne pirma karta - nustatomos issaugotos reiksmes
    void setDefValues(){
        btDevList = new ArrayList<Beacon>();
        savedDevList = new ArrayList<String>();
        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, savedDevList);
        btInfo.setAdapter(listAdapter);
    }

    //Nuolatos pradedamas ir stabdomas scan
    void contScanStop(){
        final Handler handler = new Handler();
        globalVariable.setScanning(true);
        //Main Thread Runnable:
        //pranesa, kad reikia atnaujinti irenginiu sarasa
        final Runnable uiRunnable = new Runnable(){
            @Override
            public void run() {
                if (globalVariable.isScanning()) {
                    listAdapter.notifyDataSetChanged();
                }
            }
        };
        //Background Runnable:
        //nustatytais intervalais daro scan ir paleidzia Main Thread Runnable
        Runnable backgroundRunnable = new Runnable(){
            @Override
            public void run() {
                if (globalVariable.isScanning()) {
                    startStopScan();
                    handler.postDelayed(this, settings.getDelay());
                    handler.postDelayed(uiRunnable, settings.getDelay()+1);
                }
            }
        };
        new Thread(backgroundRunnable).start();
    }

    //Jeigu randamas BTLE irenginys, gaunama jo RSSI reiksme
    void startStopScan(){
        mBluetoothAdapter.startLeScan(new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                scantools.scanLogic(device, rssi, settings.getTxPow(), btDevList, savedDevList);
                mBluetoothAdapter.stopLeScan(this); //Scan stabdomas
            }
        });
    }

    //AsyncScan - turetu tikti kalibravimo (n kiekio reiksmiu gavimui)
    /*void startStopScan(){
        //Pradedamas scan
        new asyncScan().execute();
    }

    //Nuolatos pradedamas ir stabdomas scan
    void contScanStop(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startStopScan();
                listAdapter.notifyDataSetChanged();
                handler.postDelayed(this, settings.getDelay());
            }
        }, settings.getDelay());
    }

    private class asyncScan extends AsyncTask<Void, Void, ArrayList<Beacon>>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Beacon> doInBackground(Void... params) {
            mBluetoothAdapter.startLeScan(new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                    byte numDev = 0;
                    byte listSize = (byte)btDevList.size();
                    byte currentRssi = (byte)rssi;
                    if (listSize == 0) {
                        btDevList.add(new Beacon(device.getName(), device.getAddress()));
                        btDevList.get(0).setRssi(currentRssi);
                    } else {
                        for (byte i = 0; i < listSize; i++) {
                            if (btDevList.get(i).getMac().equals(device.getAddress())) {
                                btDevList.get(i).setRssi(currentRssi);
                            } else {
                                numDev++;
                            }
                        }
                        if (numDev > listSize - 1) {
                            btDevList.add(new Beacon(device.getName(), device.getAddress()));
                            btDevList.get(numDev).setRssi(currentRssi);
                        }
                    }
                    mBluetoothAdapter.stopLeScan(this); //Scan stabdomas
                }
            });
            return btDevList;
        }

        @Override
        protected void onPostExecute(ArrayList<Beacon> devList) {
            super.onPostExecute(devList);
        }
    }*/
}
