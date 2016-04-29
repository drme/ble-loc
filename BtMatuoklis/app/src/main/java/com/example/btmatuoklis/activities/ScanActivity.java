package com.example.btmatuoklis.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.btmatuoklis.R;
import com.example.btmatuoklis.classes.BeaconGenerator;
import com.example.btmatuoklis.classes.RoomsArray;
import com.example.btmatuoklis.adapters.ScanAdapter;
import com.example.btmatuoklis.classes.GlobalClass;
import com.example.btmatuoklis.classes.Room;
import com.example.btmatuoklis.classes.RoomDetector;
import com.example.btmatuoklis.classes.ScanTools;
import com.example.btmatuoklis.classes.Settings;

public class ScanActivity extends Activity {

    GlobalClass globalVariable;
    Settings settings;
    ScanTools scantools;

    BluetoothAdapter mBluetoothAdapter;
    BluetoothAdapter.LeScanCallback mLeScanCallback;

    BluetoothLeScanner mLEScanner;
    ScanCallback mScanCallback;

    BeaconGenerator generator;

    short sleepMin, sleepMax, sampleTime;

    Handler handler;
    Runnable background;
    Room environment;
    RoomsArray roomsArray, enviromentArray;
    RoomDetector detector;
    ScanAdapter adapter;
    TextView detectedRoom;
    ExpandableListView displayBeaconsList;
    String roomName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        getActionBar().setSubtitle(getString(R.string.subtitle_scan));
        detectedRoom = (TextView)findViewById(R.id.textScan_DetectedRoom);
        displayBeaconsList = (ExpandableListView)findViewById(R.id.listScan_BeaconsList);

        setDefaultValues();
        createBT();
        checkBT();
        createBTLECallBack();
        createThreads();
        continuousScan(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getActionBar().setDisplayShowCustomEnabled(true);
        getActionBar().setCustomView(R.layout.action_view_progress);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_scan, menu);
        return true;
    }

    @Override
    public void onBackPressed(){
        continuousScan(false);
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
            //To-do: add settings filter?
        }
    }

    void createBTLECallBack(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                    if (settings.showNullDevices() | device.getName() != null){
                        scantools.scanSample(device.getName(), device.getAddress(), (byte)rssi);
                    }
                }
            };
        }
        else {
            mScanCallback = new ScanCallback() {
                @Override
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                public void onScanResult(int callbackType, ScanResult result) {
                    if (settings.showNullDevices() | result.getDevice().getName() != null){
                        scantools.scanSample(result.getDevice().getName(), result.getDevice().getAddress(), (byte)result.getRssi());
                    }
                }
            };
        }
    }

    //Nustatomos "default" reiksmes
    //Jeigu programa leidziama ne pirma karta - nustatomos issaugotos reiksmes
    void setDefaultValues(){
        globalVariable = (GlobalClass) getApplicationContext();
        settings = MainActivity.settings;
        scantools = new ScanTools();
        environment = new Room();
        roomsArray = globalVariable.getRoomsArray();
        enviromentArray = new RoomsArray();
        enviromentArray.getArray().add(new Room("Nepriskirti įrenginiai"));
        detector = new RoomDetector();

        generator = new BeaconGenerator(this);

        sleepMin = (short)getResources().getInteger(R.integer.sleep_min);
        sleepMax = (short)getResources().getInteger(R.integer.sleep_max);
        sampleTime = (short)getResources().getInteger(R.integer.scan_min);

        adapter = new ScanAdapter(this, roomsArray, enviromentArray);
        displayBeaconsList.setAdapter(adapter);
    }

    void createThreads(){
        handler = new Handler();
        //Background Runnable - paleidziamas scanAppend AsyncTask
        background = new Runnable() {
            @Override
            public void run() {
                if (globalVariable.isScanning()) { new ScanTask().execute(); }
                else { Thread.currentThread().interrupt(); }
            }
        };
    }

    //Nuolatos pradedamas ir stabdomas scanAppend
    void continuousScan(boolean enable){
        globalVariable.setScanning(enable);
        if (enable){ new Thread(background).start(); }
    }

    private class ScanTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            scanLogic();
            scantools.scanAppend(roomsArray, enviromentArray);
            roomName = detector.getRoomName(roomsArray, enviromentArray);
            return true;
        }
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            //To-do: "adaptyvus" delayed laiko paskaiciavimas pagal aptiktu beaconu kieki
            handler.postDelayed(background, sleepMin);
            adapter.notifyDataSetChanged();
            detectedRoom.setText(roomName);
        }
    }

    private void scanLogic(){
        if (!settings.isGeneratorEnabled()){
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
                mBluetoothAdapter.startLeScan(mLeScanCallback);
                //To-do: "adaptyvus" sleep laiko paskaiciavimas pagal aptiktu beaconu kieki
                threadSleep(sampleTime);
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
            else {
                mLEScanner.startScan(mScanCallback);
                threadSleep(sampleTime);
                mLEScanner.stopScan(mScanCallback);
            }
        }
        else {
            int cycles = generator.numGen(0, settings.getDebugBeacons()*5);
            for (int i = 0; i < cycles; i++){
                generator.generate(settings.getDebugBeacons(), settings.getDebugRSSIMin(), settings.getDebugRSSIMax());
                scantools.scanSample(generator.getName(), generator.getMAC(), generator.getRSSI());
            }
            threadSleep(sampleTime);
        }
    }

    private void threadSleep(short time){
        try { Thread.sleep(time); }
        catch (InterruptedException e) { e.printStackTrace(); }
    }
}
