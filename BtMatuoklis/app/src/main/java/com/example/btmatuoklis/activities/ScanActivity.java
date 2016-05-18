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
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.example.btmatuoklis.R;
import com.example.btmatuoklis.classes.RoomsArray;
import com.example.btmatuoklis.adapters.ScanAdapter;
import com.example.btmatuoklis.classes.GlobalClass;
import com.example.btmatuoklis.classes.Room;
import com.example.btmatuoklis.classes.RoomDetector;
import com.example.btmatuoklis.classes.ScanTools;
import com.example.btmatuoklis.classes.Settings;
import com.example.btmatuoklis.classes._DebugBeaconGenerator;
import com.example.btmatuoklis.classes._DebugDeviceControl;

public class ScanActivity extends Activity {

    GlobalClass globalVariable;
    Settings settings;
    ScanTools scantools;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothAdapter.LeScanCallback mLeScanCallback;
    BluetoothLeScanner mLEScanner;
    ScanCallback mScanCallback;
    _DebugBeaconGenerator _generator;
    _DebugDeviceControl _control;
    short sleepMin, sleepMax, sleepFast, sampleTime;
    Handler handler;
    Runnable background;
    Room environment;
    RoomsArray roomsArray, scanArray;
    RoomDetector detector;
    ScanAdapter adapter;
    TextView detectedRoom;
    ExpandableListView displayBeaconsList;
    String roomName;
    boolean callbackCreated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        getActionBar().setSubtitle(getString(R.string.subtitle_scan));
        detectedRoom = (TextView)findViewById(R.id.textScan_DetectedRoom);
        displayBeaconsList = (ExpandableListView)findViewById(R.id.listScan_BeaconsList);
        
        setDefaultValues();
        createBT();
        createThreads();
        continuousScan(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getActionBar().setDisplayShowCustomEnabled(true);
        getActionBar().setCustomView(R.layout.action_view_progress);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        return true;
    }

    @Override
    public void onBackPressed(){
        continuousScan(false);
        this.finish();
    }

    public void onSettingsActionClick(MenuItem item){
        startActivity(new Intent(getBaseContext(), SettingsActivity.class));
    }

    public void createBT(){
        BluetoothManager bluetoothManager =
                (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
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
            mLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
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
        callbackCreated = true;
    }

    void setDefaultValues(){
        globalVariable = (GlobalClass) getApplicationContext();
        settings = MainActivity.settings;
        scantools = new ScanTools();
        environment = new Room();
        roomsArray = globalVariable.getRoomsArray();
        scanArray = new RoomsArray();
        scanArray.getArray().add(new Room(getString(R.string.category_unassigned_beacons)));
        detector = new RoomDetector(this);
        _generator = new _DebugBeaconGenerator(this);
        _control = new _DebugDeviceControl(ScanActivity.this);
        sleepMin = (short)getResources().getInteger(R.integer.scan_sleep_min);
        sleepMax = (short)getResources().getInteger(R.integer.scan_sleep_max);
        sleepFast = (short)getResources().getInteger(R.integer.sleep_fast);
        sampleTime = (short)getResources().getInteger(R.integer.scan_sample_min);
        callbackCreated = false;
        adapter = new ScanAdapter(this, roomsArray, scanArray);
        displayBeaconsList.setAdapter(adapter);
    }

    void createThreads(){
        handler = new Handler();
        background = new Runnable() {
            @Override
            public void run() {
                if (globalVariable.isScanning()) { new ScanTask().execute(); }
                else { Thread.currentThread().interrupt(); }
            }
        };
    }

    void continuousScan(boolean enable){
        globalVariable.setScanning(enable);
        if (enable){ new Thread(background).start(); }
    }

    private class ScanTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            scanLogic();
            scantools.scanAppend(roomsArray, scanArray);
            int index = detector.getDetectedRoomIndex(roomsArray, scanArray);
            roomName = detector.getDetectedRoomName(roomsArray, index);
            _control.checkDevices(roomsArray, index);
            return true;
        }
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            adapter.notifyDataSetChanged();
            detectedRoom.setText(roomName);
            _control.activateDevice(handler, mBluetoothAdapter);
            //To-do: "adaptyvus" delayed laiko paskaiciavimas pagal aptiktu beaconu kieki
            handler.postDelayed(background, getSleepTime());
        }
    }

    private void scanLogic(){
        if (!settings.isGeneratorEnabled() && mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()){
            if (!callbackCreated){ createBTLECallBack(); }
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
            int cycles = _generator.numGen(0, settings.getDebugBeacons()*5);
            for (int i = 0; i < cycles; i++){
                _generator.generate(settings.getDebugBeacons(), settings.getDebugRSSIMin(), settings.getDebugRSSIMax());
                scantools.scanSample(_generator.getName(), _generator.getMAC(), _generator.getRSSI());
            }
            threadSleep(sampleTime);
        }
    }

    private short getSleepTime(){
        short time;
        if (settings.isFastSleep()){ time = sleepFast; }
        else { time = sleepMin; }
        return time;
    }

    private void threadSleep(short time) {
        try { Thread.sleep(time); }
        catch (InterruptedException e) { e.printStackTrace(); }
    }
}
