package com.example.btmatuoklis.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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
    }

    void createBTLECallBack(){
        mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                scantools.scanLogic(device, rssi, roomsArray, enviromentArray);
                mBluetoothAdapter.stopLeScan(this); //Scan stabdomas
            }
        };
    }

    //Nustatomos "default" reiksmes
    //Jeigu programa leidziama ne pirma karta - nustatomos issaugotos reiksmes
    void setDefaultValues(){
        globalVariable = (GlobalClass) getApplicationContext();
        settings = MainActivity.settings;
        scantools = new ScanTools();
        environment = new Room();
        roomsArray = new RoomsArray(globalVariable.getRoomsArray().getArray());
        enviromentArray = new RoomsArray();
        enviromentArray.getArray().add(new Room("Nepriskirti įrenginiai"));
        detector = new RoomDetector();
        adapter = new ScanAdapter(this, R.layout.list_scan_item, enviromentArray.getArray());
        displayBeaconsList.setAdapter(adapter);
    }

    void createThreads(){
        handler = new Handler();
        //Background Runnable:
        //nustatytu intervalu vykdo scan
        background = new Runnable() {
            @Override
            public void run() {
                if (globalVariable.isScanning()) {
                    new ScanTask().execute();
                    handler.postDelayed(this, settings.getFrequency());
                }
                else { Thread.currentThread().interrupt(); }
            }
        };
    }

    //Nuolatos pradedamas ir stabdomas scan
    void continuousScan(boolean enable){
        globalVariable.setScanning(enable);
        if (enable){ new Thread(background).start(); }
    }

    private class ScanTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            if (!settings.isGeneratorEnabled()){
                /*mBluetoothAdapter.startLeScan(mLeScanCallback);
                //Duodamos 100ms laiko aptikti bet kurio beacono signalui
                try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
                mBluetoothAdapter.stopLeScan(mLeScanCallback);*/
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            }
            else {
                scantools.fakeScanLogic(settings.getDebugBeacons(), settings.getDebugRSSIMin(),
                        settings.getDebugRSSIMax(), roomsArray, enviromentArray);
            }
            roomName = detector.getRoomName(roomsArray, enviromentArray);
            return true;
        }
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            adapter.notifyDataSetChanged();
            detectedRoom.setText(roomName);
        }
    }
}
