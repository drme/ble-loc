package com.example.btmatuoklis.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.btmatuoklis.classes.Beacon;
import com.example.btmatuoklis.R;
import com.example.btmatuoklis.classes.GlobalClass;
import com.example.btmatuoklis.classes.ScanTools;
import com.example.btmatuoklis.classes.Settings;

import java.util.ArrayList;

public class ScanActivity extends Activity {

    GlobalClass globalVariable;
    Settings settings;
    ScanTools scantools;
    BluetoothAdapter mBluetoothAdapter;
    ArrayList<Beacon> beaconsArray;
    ArrayList<String> savedBeaconsList;
    ArrayList<String> beaconsList;
    ArrayAdapter<String> listAdapter;
    ListView displayBeaconsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        getActionBar().setSubtitle(getString(R.string.subtitle_scan));
        displayBeaconsList = (ListView)findViewById(R.id.listScan_BeaconsList);

        setDefaultValues();
        createBT();
        continuousScan();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getActionBar().setDisplayShowCustomEnabled(true);
        getActionBar().setCustomView(R.layout.action_view_progress);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_scan, menu);
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
        getActionBar().getCustomView().setVisibility(View.INVISIBLE);
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

    //Nustatomos "default" reiksmes
    //Jeigu programa leidziama ne pirma karta - nustatomos issaugotos reiksmes
    void setDefaultValues(){
        globalVariable = (GlobalClass) getApplicationContext();
        settings = MainActivity.settings;
        scantools = new ScanTools();
        beaconsArray = new ArrayList<Beacon>();
        savedBeaconsList = new ArrayList<String>();
        beaconsList = new ArrayList<String>();
        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, beaconsList);
        displayBeaconsList.setAdapter(listAdapter);
    }

    //Nuolatos pradedamas ir stabdomas scan
    void continuousScan(){
        final Handler handler = new Handler();
        globalVariable.setScanning(true);
        //Main Thread Runnable:
        //pranesa, kad reikia atnaujinti irenginiu sarasa
        final Runnable uiRunnable = new Runnable(){
            @Override
            public void run() {
                if (globalVariable.isScanning()) {
                    beaconsList.clear();
                    beaconsList.addAll(savedBeaconsList);
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
                    startBTLEScan();
                    handler.postDelayed(this, settings.getDelay());
                    handler.postDelayed(uiRunnable, settings.getDelay() + 1);
                }
            }
        };
        new Thread(backgroundRunnable).start();
    }

    //Jeigu randamas BTLE irenginys, gaunama jo RSSI reiksme
    void startBTLEScan(){
        if (!settings.getGenerator()){
            mBluetoothAdapter.startLeScan(new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                    scantools.scanLogic(device, rssi, settings.getTXPower(), beaconsArray, savedBeaconsList);
                    mBluetoothAdapter.stopLeScan(this); //Scan stabdomas
                }
            });
        }
        else {
            scantools.fakeScanLogic(settings.getDebugBeacons(),
                    settings.getDebugRSSIMin(), settings.getDebugRSSIMax(),
                    settings.getTXPower(), beaconsArray, savedBeaconsList);
        }
    }
}
