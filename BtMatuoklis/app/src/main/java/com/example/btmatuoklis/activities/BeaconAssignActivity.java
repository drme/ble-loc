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
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.example.btmatuoklis.R;
import com.example.btmatuoklis.adapters.AssignAdapter;
import com.example.btmatuoklis.classes._DebugBeaconGenerator;
import com.example.btmatuoklis.classes.RoomsArray;
import com.example.btmatuoklis.helpers.DialogBuildHelper;
import com.example.btmatuoklis.classes.Beacon;
import com.example.btmatuoklis.classes.GlobalClass;
import com.example.btmatuoklis.helpers.MySQLiteHelper;
import com.example.btmatuoklis.classes.Room;
import com.example.btmatuoklis.classes.ScanTools;
import com.example.btmatuoklis.classes.Settings;

import java.util.ArrayList;

public class BeaconAssignActivity extends Activity {

    GlobalClass globalVariable;
    int roomIndex;
    Settings settings;
    ScanTools scantools;
    Room currentRoom;
    ArrayList<String> allMACs;
    RoomsArray enviromentArray;
    MySQLiteHelper database;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothAdapter.LeScanCallback mLeScanCallback;
    BluetoothLeScanner mLEScanner;
    ScanCallback mScanCallback;
    _DebugBeaconGenerator _generator;
    short sleepTime, sampleTime;
    Handler handler;
    Runnable background;
    ExpandableListView displayBeaconsList;
    AssignAdapter adapter;
    ArrayList<Integer> selectedBeacons;
    Button buttonAccept;
    String room_key, simple_beacon_key, sql_device_key;
    boolean simple_beacon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_assign);
        getActionBar().setSubtitle(getString(R.string.subtitle_new_room_beacons));
        displayBeaconsList = (ExpandableListView)findViewById(R.id.listNewRoom_BeaconsList);
        buttonAccept = (Button)findViewById(R.id.buttonNewRoom_End);

        setDefaultValues();
        setListListener();
        createBT();
        checkBT();
        createBTLECallBack();
        createThread();
        continuousScan(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getActionBar().setDisplayShowCustomEnabled(true);
        getActionBar().setCustomView(R.layout.action_view_progress);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_beaconassign, menu);
        return true;
    }

    @Override
    public void onBackPressed(){
        cancelCreationConfirm();
    }

    public void onCancelActionClick(MenuItem item){
        cancelCreationConfirm();
    }

    public void onSettingsActionClick(MenuItem item){
        startActivity(new Intent(getBaseContext(), SettingsActivity.class));
    }

    public void onAcceptButtonClick(View view){
        continuousScan(false);
        saveSelectedBeacons(simple_beacon);
        BeaconAssignActivity.this.finish();
        startActivity(new Intent(getBaseContext(), AllRoomsActivity.class));
    }

    void setDefaultValues(){
        globalVariable = (GlobalClass) getApplicationContext();
        room_key = getString(R.string.activity_key_room);
        simple_beacon_key = getString(R.string.key_simple_beacon);
        sql_device_key = getString(R.string.key_sql_device);
        roomIndex = getIntent().getExtras().getInt(room_key);
        simple_beacon = getIntent().getExtras().getBoolean(simple_beacon_key);
        settings = MainActivity.settings;
        scantools = new ScanTools();
        allMACs = globalVariable.getRoomsArray().getFullMACList();
        allMACs.addAll(globalVariable.getRoomsArray()._getFullDeviceMACList());
        enviromentArray = new RoomsArray();
        enviromentArray.getArray().add(new Room(getString(R.string.category_assigned_beacons)));
        enviromentArray.getArray().add(new Room(getString(R.string.category_unassigned_beacons)));
        currentRoom = globalVariable.getRoomsArray().getArray().get(roomIndex);
        database = new MySQLiteHelper(this);
        _generator = new _DebugBeaconGenerator(this);
        sleepTime = (short)getResources().getInteger(R.integer.sleep_fast);
        sampleTime = (short)getResources().getInteger(R.integer.scan_sample_min);
        selectedBeacons = new ArrayList<Integer>();
        adapter = new AssignAdapter(this, enviromentArray, selectedBeacons);
        displayBeaconsList.setAdapter(adapter);
    }

    void setListListener(){
        displayBeaconsList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View view, int groupPosition, int childPosition, long id) {
                if (groupPosition > 0) {
                    CheckedTextView checkedTextView = ((CheckedTextView) view);
                    checkedTextView.setChecked(!checkedTextView.isChecked());
                    if (checkedTextView.isChecked()) {
                        selectedBeacons.add(childPosition);
                    } else {
                        int checkIndex = selectedBeacons.indexOf(childPosition);
                        if (!(checkIndex == -1)) {
                            selectedBeacons.remove(checkIndex);
                        }
                    }
                    buttonAccept.setEnabled(!selectedBeacons.isEmpty());
                }
                return true;
            }
        });
    }

    void cancelCreation(){
        continuousScan(false);
        Toast.makeText(getApplicationContext(),
                getString(R.string.toast_info_cancelled), Toast.LENGTH_SHORT).show();
        BeaconAssignActivity.this.finish();
        startActivity(new Intent(getBaseContext(), AllRoomsActivity.class));
    }

    void cancelCreationConfirm(){
        DialogBuildHelper dialog = new DialogBuildHelper(BeaconAssignActivity.this, getString(R.string.dialog_title_cancel),
                getString(R.string.dialog_cancel_room_creation), android.R.drawable.ic_dialog_alert);
        dialog.getBuilder().setPositiveButton(getString(R.string.dialog_button_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cancelCreation();
            }
        });
        dialog.setNegative(getString(R.string.dialog_button_cancel));
        dialog.showDialog();
    }

    void saveSelectedBeacons(boolean simpleBeacon){
        for (int i = 0; i < selectedBeacons.size(); i++){
            Beacon temp = enviromentArray.getArray().get(1).getBeacons().get(selectedBeacons.get(i));
            Beacon beacon = new Beacon(temp.getName(), temp.getMAC());
            if (simpleBeacon){ currentRoom.getBeacons().add(beacon); }
            else { currentRoom._getDevices().add(beacon); }
            saveBeaconInDatabase(simpleBeacon, beacon);
        }
        notifyAssignedBeacons();
    }

    void saveBeaconInDatabase(boolean simpleBeacon, Beacon beacon){
        MySQLiteHelper database = new MySQLiteHelper(this);
        database.addBeacon(beacon);
        int id = database.getLastBeaconID();
        beacon.setID(id);
        if (simpleBeacon){ database.addCalibration(currentRoom.getID(), id, null); }
        else { database.addCalibration(currentRoom.getID(), id, sql_device_key); }
    }

    void notifyAssignedBeacons(){
        Toast.makeText(getApplicationContext(),
                getString(R.string.toast_info_created_room3)+" "+currentRoom.getBeacons().size(),
                Toast.LENGTH_SHORT).show();
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
        }
    }

    void createBTLECallBack(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                    if (settings.showNullDevices() | device.getName() != null){
                        scantools.assignSample(device.getName(), device.getAddress(), (byte)rssi);
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
                        scantools.assignSample(result.getDevice().getName(), result.getDevice().getAddress(), (byte)result.getRssi());
                    }
                }
            };
        }
    }

    void createThread(){
        handler = new Handler();
        //Background Runnable:
        //nustatytu intervalu vykdo scanAppend
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
            assignLogic();
            scantools.assignAppend(allMACs, enviromentArray);
            return true;
        }
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            handler.postDelayed(background, sleepTime);
            adapter.notifyDataSetChanged();
        }
    }

    private void assignLogic(){
        if (!settings.isGeneratorEnabled()) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
                mBluetoothAdapter.startLeScan(mLeScanCallback);
                threadSleep(sampleTime);
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
            else {
                mLEScanner.startScan(mScanCallback);
                threadSleep(sampleTime);
                mLEScanner.stopScan(mScanCallback);
            }
        }
        else{
            int cycles = _generator.numGen(0, settings.getDebugBeacons()*5);
            for (int i = 0; i < cycles; i++) {
                _generator.generate(settings.getDebugBeacons(), settings.getDebugRSSIMin(), settings.getDebugRSSIMax());
                scantools.assignSample(_generator.getName(), _generator.getMAC(), _generator.getRSSI());
            }
            threadSleep(sampleTime);
        }
    }

    private void threadSleep(short time){
        try { Thread.sleep(time); }
        catch (InterruptedException e) { e.printStackTrace(); }
    }
}
