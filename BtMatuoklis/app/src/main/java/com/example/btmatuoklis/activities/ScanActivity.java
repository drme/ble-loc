package com.example.btmatuoklis.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;

import com.example.btmatuoklis.classes.DeviceInfo;
import com.example.btmatuoklis.R;
import com.example.btmatuoklis.classes.Settings;

import java.util.ArrayList;

public class ScanActivity extends AppCompatActivity {

    Settings settings;
    ActionBar actionbar;

    boolean scanning = false;

    BluetoothAdapter mBluetoothAdapter;
    ArrayList<DeviceInfo> btDevList;
    ArrayList<String> savedDevList;
    ArrayAdapter<String> listAdapter;

    ListView btInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        actionbar = getSupportActionBar();
        actionbar.setSubtitle(getText(R.string.scan_name));
        settings = MainActivity.settings;
        btInfo = (ListView)findViewById(R.id.listScan_DevicesList);

        setDefValues();
        createBT();
        checkBT();
        //---
        setCustomList();
        //---
        contScanStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar, menu);
        menu.findItem(R.id.action_progress).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(ScanActivity.this, SettingsActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed(){
        scanning = false;
        this.finish();
    }

    @Override
    public void setSupportProgressBarIndeterminateVisibility(boolean visible) {
        getSupportActionBar().getCustomView().setVisibility(visible ? View.VISIBLE : View.GONE);
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

    //Jei atsisakoma ijungti Bluetooth - griztama i pradzia
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_CANCELED) {
            this.finish();
        }
    }

    //Nustatomos "default" reiksmes
    //Jeigu programa leidziama ne pirma karta - nustatomos issaugotos reiksmes
    void setDefValues(){
        btDevList = new ArrayList<DeviceInfo>();
        savedDevList = new ArrayList<String>();
        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, savedDevList);
        btInfo.setAdapter(listAdapter);
    }

    void setCustomList(){
        btInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // change the checkbox state
                CheckedTextView checkedTextView = ((CheckedTextView) view);
                checkedTextView.setChecked(!checkedTextView.isChecked());
            }
        });
    }

    //Nuolatos pradedamas ir stabdomas scan
    void contScanStop(){
        final Handler handler = new Handler();
        scanning = true;
        //Main Thread Runnable:
        //pranesa, kad reikia atnaujinti irenginiu sarasa
        final Runnable uiRunnable = new Runnable(){
            @Override
            public void run() {
                listAdapter.notifyDataSetChanged();
            }
        };
        //Background Runnable:
        //nustatytais intervalais daro scan ir paleidzia Main Thread Runnable
        Runnable backgroundRunnable = new Runnable(){
            @Override
            public void run() {
                if (scanning) {
                    startStopScan();
                    handler.postDelayed(uiRunnable, settings.getDelay());
                    handler.postDelayed(this, settings.getDelay());
                }
            }
        };
        new Thread(backgroundRunnable).start();
    }

    //Jeigu randamas BTLE irenginys, gaunama jo RSSI reiksme
    void startStopScan(){
        mBluetoothAdapter.startLeScan(new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                byte numDev = 0;
                byte listSize = (byte)btDevList.size();
                byte currentRssi = (byte)rssi;
                if (listSize == 0) {
                    btDevList.add(new DeviceInfo(device.getName(), device.getAddress()));
                    btDevList.get(0).setRSSI(currentRssi);
                    savedDevList.add(btDevList.get(0).getCurrentInfo(settings.getTxPow()));
                } else {
                    for (byte i = 0; i < listSize; i++) {
                        if (btDevList.get(i).getMAC().equals(device.getAddress())) {
                            btDevList.get(i).setRSSI(currentRssi);
                            savedDevList.set(i, btDevList.get(i).getCurrentInfo(settings.getTxPow()));
                        } else {
                            numDev++;
                        }
                    }
                    if (numDev > listSize - 1) {
                        btDevList.add(new DeviceInfo(device.getName(), device.getAddress()));
                        btDevList.get(numDev).setRSSI(currentRssi);
                        savedDevList.add(numDev, btDevList.get(numDev).getCurrentInfo(settings.getTxPow()));
                    }
                }
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

    private class asyncScan extends AsyncTask<Void, Void, ArrayList<DeviceInfo>>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<DeviceInfo> doInBackground(Void... params) {
            mBluetoothAdapter.startLeScan(new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    byte numDev = 0;
                    byte listSize = (byte)btDevList.size();
                    byte currentRssi = (byte)rssi;
                    if (listSize == 0) {
                        btDevList.add(new DeviceInfo(device.getName(), device.getAddress()));
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
                            btDevList.add(new DeviceInfo(device.getName(), device.getAddress()));
                            btDevList.get(numDev).setRssi(currentRssi);
                        }
                    }
                    mBluetoothAdapter.stopLeScan(this); //Scan stabdomas
                }
            });
            return btDevList;
        }

        @Override
        protected void onPostExecute(ArrayList<DeviceInfo> devList) {
            super.onPostExecute(devList);
        }
    }*/
}
