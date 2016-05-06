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
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.btmatuoklis.R;
import com.example.btmatuoklis.adapters.LinkAdapter;
import com.example.btmatuoklis.classes._DebugBeaconGenerator;
import com.example.btmatuoklis.classes.RoomsArray;
import com.example.btmatuoklis.helpers.DialogBuildHelper;
import com.example.btmatuoklis.helpers.CSVExportHelper;
import com.example.btmatuoklis.classes.GlobalClass;
import com.example.btmatuoklis.helpers.MySQLiteHelper;
import com.example.btmatuoklis.classes.Room;
import com.example.btmatuoklis.classes.ScanTools;
import com.example.btmatuoklis.classes.Settings;

import java.util.ArrayList;

public class RoomActivity extends Activity {

    GlobalClass globalVariable;
    int roomIndex;
    Settings settings;
    ScanTools scantools;
    Room currentRoom;
    ArrayList<String> roomMACs;
    RoomsArray roomArray;
    MySQLiteHelper database;
    CSVExportHelper exportCSV;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothAdapter.LeScanCallback mLeScanCallback;
    BluetoothLeScanner mLEScanner;
    ScanCallback mScanCallback;
    _DebugBeaconGenerator _generator;
    short sleepTime, sampleTime;
    Handler handler;
    Runnable background;
    MenuItem assignItem, exportItem;
    LinkAdapter listAdapter;
    ExpandableListView displayBeaconsList;
    TextView displayRoomName;
    Button buttonCalibrate;
    String room_key, beacon_key, simple_beacon_key;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        getActionBar().setSubtitle(getString(R.string.subtitle_existing_room));
        displayRoomName = (TextView)findViewById(R.id.textSingleRoom_Name);
        displayBeaconsList = (ExpandableListView)findViewById(R.id.listSingleRoom_BeaconsList);
        buttonCalibrate = (Button)findViewById(R.id.buttonSingleRoom_Calibrate);

        setDefaultValues();
        checkBeacons();
        createBT();
        checkBT();
        createBTLECallBack();
        createThread();
        setListListener(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getActionBar().setDisplayShowCustomEnabled(true);
        getActionBar().setCustomView(R.layout.action_view_progress);
        getActionBar().getCustomView().setVisibility(View.INVISIBLE);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_room, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        assignItem = menu.findItem(R.id.action_add);
        exportItem = menu.findItem(R.id.action_export);
        if (currentRoom.getBeacons().isEmpty()){ this.finish(); }
        else if (!currentRoom.isCalibrationStarted()){ restoreCalibrateButton(); }
        else { resumeCalibrateButton(); }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed(){
        continuousScan(false);
        this.finish();
    }

    public void onAddActionClick(MenuItem item){
        startBeaconAssignConfirm();
    }

    public void onExportActionClick(MenuItem item) { exportRoomCSVConfirm(); }

    public void onAddDeviceActionClick(MenuItem item) { startDeviceAssignConfirm(); }

    public void onRemoveActionClick(MenuItem item){ removeRoomConfirm(); }

    public void onSettingsActionClick(MenuItem item){
        startActivity(new Intent(getBaseContext(), SettingsActivity.class));
    }

    public void onCalibrateButtonClick(View view){
        if (!globalVariable.isScanning()) { startCalibration(); }
        else { finishCalibration(); }
    }

    void setDefaultValues(){
        globalVariable = (GlobalClass) getApplicationContext();
        room_key = getString(R.string.activity_key_room);
        beacon_key = getString(R.string.activity_key_beacon);
        simple_beacon_key = getString(R.string.key_simple_beacon);
        roomIndex = getIntent().getExtras().getInt(room_key);
        settings = MainActivity.settings;
        scantools = new ScanTools();
        roomArray = new RoomsArray();
        currentRoom = globalVariable.getRoomsArray().getArray().get(roomIndex);
        roomMACs = currentRoom.getMACList();
        roomArray.getArray().add(new Room(getString(R.string.category_assigned_beacons), currentRoom.getBeacons()));

        if (!currentRoom._getDevices().isEmpty()){
            roomArray.getArray().add(new Room(getString(R.string.category_unassigned_devices), currentRoom._getDevices()));
        }

        database = new MySQLiteHelper(this);
        exportCSV = new CSVExportHelper(this);
        _generator = new _DebugBeaconGenerator(this);
        sleepTime = (short)getResources().getInteger(R.integer.sleep_fast);
        sampleTime = (short)getResources().getInteger(R.integer.scan_sample_min);
        listAdapter = new LinkAdapter(this, roomArray);
        displayBeaconsList.setAdapter(listAdapter);
        displayRoomName.setText(getString(R.string.roomactivity_text_name) + " " + currentRoom.getName());
    }

    void checkBeacons(){
        if (currentRoom.getBeacons().isEmpty()){ startBeaconAssign(); }
    }

    void startBeaconAssignConfirm() {
        DialogBuildHelper dialog = new DialogBuildHelper(RoomActivity.this, getString(R.string.dialog_title_beacon_assign),
                getString(R.string.dialog_assign_beacons), android.R.drawable.ic_dialog_info);
        dialog.getBuilder().setPositiveButton(getString(R.string.dialog_button_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { startBeaconAssign(); }});
        dialog.setNegative(getString(R.string.dialog_button_cancel));
        dialog.showDialog();
    }

    void startBeaconAssign(){
        Intent intent = new Intent(getBaseContext(), BeaconAssignActivity.class);
        intent.putExtra(room_key, roomIndex);
        intent.putExtra(simple_beacon_key, true);
        this.finish();
        startActivity(intent);
    }

    void startDeviceAssignConfirm() {
        DialogBuildHelper dialog = new DialogBuildHelper(RoomActivity.this, getString(R.string.dialog_title_device_assign),
                getString(R.string.dialog_assign_devices), android.R.drawable.ic_dialog_info);
        dialog.getBuilder().setPositiveButton(getString(R.string.dialog_button_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { startDeviceAssign(); }});
        dialog.setNegative(getString(R.string.dialog_button_cancel));
        dialog.showDialog();
    }

    void startDeviceAssign() {
        Intent intent = new Intent(getBaseContext(), BeaconAssignActivity.class);
        intent.putExtra(room_key, roomIndex);
        intent.putExtra(simple_beacon_key, false);
        this.finish();
        startActivity(intent);
    }

    //Veiksmai kalibracijai pradeti
    void startCalibration(){
        getActionBar().getCustomView().setVisibility(View.VISIBLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setListListener(false);
        buttonCalibrate.setText(getString(R.string.roomactivity_button_finish_calib));
        enableMenuItem(assignItem, false);
        enableMenuItem(exportItem, false);
        scantools.calibratePrepare(currentRoom);
        continuousScan(true);
    }

    //Veiksmai veiksmai kalibracijai baigti
    void finishCalibration(){
        getActionBar().getCustomView().setVisibility(View.INVISIBLE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        buttonCalibrate.setText(getString(R.string.roomactivity_button_resume_calib));
        setListListener(true);
        saveRSSIInDatabase();
        enableMenuItem(assignItem, true);
        enableMenuItem(exportItem, true);
        continuousScan(false);
    }

    void saveRSSIInDatabase(){
        int roomdID = currentRoom.getID();
        int beaconID;
        ArrayList<Boolean> calibratedDevices = currentRoom.getCalibratedBeacons();
        String rssi;
        for (int i = 0; i < calibratedDevices.size(); i++){
            rssi = currentRoom.getBeacons().get(i).getFullRSSI().toString();
            beaconID = currentRoom.getBeacons().get(i).getID();
            database.updateCalibration(roomdID, beaconID, rssi);
        }
    }

    void exportRoomCSV(){
        String[] res = exportCSV.exportRoomCSV(currentRoom);
        Toast.makeText(getApplicationContext(), getString(R.string.toast_info_created_file1)+" "+
                        res[0]+" "+getString(R.string.toast_info_created_file2)+"\n"+res[1],
                Toast.LENGTH_LONG).show();
    }

    //Veiksmai pradinei mygtuko isvaizdai atstayti, kai nera kalibraciniu reiksmiu
    void restoreCalibrateButton(){
        continuousScan(false);
        buttonCalibrate.setText(getString(R.string.roomactivity_button_calibrate));
        setListListener(true);
        enableMenuItem(assignItem, true);
        enableMenuItem(exportItem, false);
    }

    //Veiksmai mygtuko isvaizdai nustatyti, kai yra kalibraciniu reiksmiu
    void resumeCalibrateButton(){
        continuousScan(false);
        buttonCalibrate.setText(getString(R.string.roomactivity_button_resume_calib));
        setListListener(true);
        enableMenuItem(assignItem, true);
        enableMenuItem(exportItem, true);
    }

    void enableMenuItem(MenuItem item, boolean enabled){
        if (enabled){ item.getIcon().setAlpha(255); }
        else { item.getIcon().setAlpha(128); }
        item.setEnabled(enabled);
    }

    void setListListener(boolean enabled){
        if (enabled){
            displayBeaconsList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                    Intent intent = new Intent(getBaseContext(), BeaconActivity.class);
                    intent.putExtra(room_key, roomIndex);
                    intent.putExtra(simple_beacon_key, groupPosition < 1);
                    intent.putExtra(beacon_key, childPosition);
                    startActivity(intent);
                    return true;
                }
            });
        }
        else { displayBeaconsList.setOnChildClickListener(null); }
    }

    void removeRoom(){
        continuousScan(false);
        database.deleteBeacons(currentRoom.getBeaconsIDs());
        database.deleteRoom(currentRoom.getID());
        database.deleteCalibrations(currentRoom.getID());
        globalVariable.getRoomsArray().getArray().remove(roomIndex);
        if (globalVariable.getRoomsArray().getArray().isEmpty()){
            database.clearDB();
            globalVariable.getRoomsArray().getArray().clear();
        }
        Toast.makeText(getApplicationContext(),
                getString(R.string.toast_info_removed), Toast.LENGTH_SHORT).show();
        RoomActivity.this.finish();
    }

    void exportRoomCSVConfirm() {
        DialogBuildHelper dialog = new DialogBuildHelper(RoomActivity.this, getString(R.string.dialog_title_export),
                getString(R.string.dialog_export_room_csv), android.R.drawable.ic_dialog_info);
        dialog.getBuilder().setPositiveButton(getString(R.string.dialog_button_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { exportRoomCSV(); }});
        dialog.setNegative(getString(R.string.dialog_button_cancel));
        dialog.showDialog();
    }

    void removeRoomConfirm() {
        DialogBuildHelper dialog = new DialogBuildHelper(RoomActivity.this, getString(R.string.dialog_title_remove),
                getString(R.string.dialog_remove_room), android.R.drawable.ic_dialog_alert);
        dialog.getBuilder().setPositiveButton(getString(R.string.dialog_button_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeRoom();
            }
        });
        dialog.setNegative(getString(R.string.dialog_button_cancel));
        dialog.showDialog();
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
                        scantools.calibrateSample(device.getAddress(), (byte)rssi, roomMACs);
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
                        scantools.calibrateSample(result.getDevice().getAddress(), (byte)result.getRssi(), roomMACs);
                    }
                }
            };
        }
    }

    void createThread(){
        handler = new Handler();
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

    private class ScanTask extends AsyncTask<Void, Void, Boolean>{
        @Override
        protected Boolean doInBackground(Void... params) {
            calibrateLogic();
            scantools.calibrateAppend(currentRoom);
            return true;
        }
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            handler.postDelayed(background, sleepTime);
            listAdapter.notifyDataSetChanged();
        }
    }

    private void calibrateLogic(){
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
        else {
            int cycles = _generator.numGen(0, settings.getDebugBeacons()*5);
            for (int i = 0; i < cycles; i++) {
                _generator.generate(settings.getDebugBeacons(), settings.getDebugRSSIMin(), settings.getDebugRSSIMax());
                scantools.calibrateSample(_generator.getMAC(), _generator.getRSSI(), roomMACs);
            }
            threadSleep(sampleTime);
        }
    }

    private void threadSleep(short time){
        try { Thread.sleep(time); }
        catch (InterruptedException e) { e.printStackTrace(); }
    }
}
