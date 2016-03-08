package com.example.btmatuoklis.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.btmatuoklis.R;
import com.example.btmatuoklis.classes.AlertDialogBuilder;
import com.example.btmatuoklis.classes.Beacon;
import com.example.btmatuoklis.classes.Calibration;
import com.example.btmatuoklis.classes.ExportCSVHelper;
import com.example.btmatuoklis.classes.GlobalClass;
import com.example.btmatuoklis.classes.MySQLiteHelper;
import com.example.btmatuoklis.classes.Room;
import com.example.btmatuoklis.classes.ScanTools;
import com.example.btmatuoklis.classes.Settings;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

public class RoomActivity extends Activity {

    GlobalClass globalVariable;
    int roomID;
    Settings settings;
    ScanTools scantools;
    Room currentRoom;
    MySQLiteHelper database;
    ExportCSVHelper exportCSV;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothAdapter.LeScanCallback mLeScanCallback;
    MenuItem exportItem;
    ArrayList<String> boundBeaconsList;
    ArrayAdapter<String> listAdapter;
    ListView displayBeaconsList;
    TextView displayRoomName;
    Button buttonCalibrate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        getActionBar().setSubtitle(getString(R.string.subtitle_existing_room));
        displayRoomName = (TextView)findViewById(R.id.textSingleRoom_ActiveName);
        displayBeaconsList = (ListView)findViewById(R.id.listSingleRoom_BeaconsList);
        buttonCalibrate = (Button)findViewById(R.id.buttonSingleRoom_Calibrate);

        setDefaultValues();
        reloadBoundDevices();
        setChoiceListener();
        checkCompleted();
        createBT();
        createBTLECallBack();
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
        reloadBoundDevices();
        checkCalibratedDevices();
    }

    @Override
    public void onBackPressed(){
        globalVariable.setScanning(false);
        displayBeaconsList.setMultiChoiceModeListener(null);
        this.finish();
    }

    public void onRemoveActionClick(MenuItem item){ removeRoomConfirm(); }

    public void onExportActionClick(MenuItem item) { ExportRoomCSVConfirm(); }

    public void onHelpActionClick(MenuItem item){
        //Work in progress
        Toast.makeText(getApplicationContext(), "Not implemented.", Toast.LENGTH_SHORT).show();
    }

    public void onSettingsActionClick(MenuItem item){
        startActivity(new Intent(getBaseContext(), SettingsActivity.class));
    }

    public void onCalibrateButtonClick(View view){
        if (!globalVariable.isScanning() && !currentRoom.isCalibrated()) {
            startCalibration();
        } else if (globalVariable.isScanning() && currentRoom.isCalibrated()) {
            finishCalibration();
        }
        else {
            startCalibration();
            buttonCalibrate.setEnabled(true);
        }
    }

    void setDefaultValues(){
        globalVariable = (GlobalClass) getApplicationContext();
        roomID = getIntent().getExtras().getInt("roomID");
        settings = MainActivity.settings;
        scantools = new ScanTools();
        currentRoom = globalVariable.getRoomsArray().get(roomID);
        database = new MySQLiteHelper(this);
        exportCSV = new ExportCSVHelper(this);
        boundBeaconsList = new ArrayList<String>();
        listAdapter = new ArrayAdapter<String>(this, R.layout.list_checked, boundBeaconsList);
        displayBeaconsList.setAdapter(listAdapter);
        displayRoomName.setText(currentRoom.getName());
    }

    //Veiksmai kalibracijai pradeti
    void startCalibration(){
        globalVariable.setScanning(true);
        getActionBar().getCustomView().setVisibility(View.VISIBLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        displayBeaconsList.setOnItemClickListener(null);
        buttonCalibrate.setText(getString(R.string.roomactivity_button_finish_calib));
        buttonCalibrate.setEnabled(false);
        enableMenuItem(exportItem, false);
        continuousScan();
    }

    //Veiksmai veiksmai kalibracijai baigti
    void finishCalibration(){
        globalVariable.setScanning(false);
        getActionBar().getCustomView().setVisibility(View.INVISIBLE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        buttonCalibrate.setText(getString(R.string.roomactivity_button_resume_calib));
        buttonCalibrate.setEnabled(true);
        setListListener();
        saveRSSIInDatabase();
        enableMenuItem(exportItem, true);
    }

    void saveRSSIInDatabase(){
        int roomdID = currentRoom.getID();
        int beaconID;
        ArrayList<Boolean> calibratedDevices = currentRoom.getCalibratedBeacons();
        String rssi;
        for (int i = 0; i < calibratedDevices.size(); i++){
            displayBeaconsList.setItemChecked(i, calibratedDevices.get(i));
            rssi = currentRoom.getBeacons().get(i).getFullRSSI().toString();
            beaconID = currentRoom.getBeacons().get(i).getID();
            Calibration calibation = new Calibration(roomdID, beaconID, rssi);
            database.updateCalibration(calibation);
        }
    }

    void exportRoomCSV(){
        String[] res = exportCSV.exportRoomCSV(currentRoom);
        Toast.makeText(getApplicationContext(), getString(R.string.toast_info_created_file1)+
                        res[0]+ getString(R.string.toast_info_created_file2)+
                        getString(R.string.toast_info_created_file3)+res[1],
                Toast.LENGTH_LONG).show();
    }

    //Veiksmai pradinei mygtuko isvaizdai atstayti, kai nera kalibraciniu reiksmiu
    void restoreCalibrateButton(){
        globalVariable.setScanning(false);
        buttonCalibrate.setText(getString(R.string.roomactivity_button_calibrate));
        buttonCalibrate.setEnabled(true);
        displayBeaconsList.setOnItemClickListener(null);
        enableMenuItem(exportItem, false);
        //Grizus i Activity arba rotate screen - reiktu vel atstatyti CSV export menu item
    }

    //Veiksmai mygtuko isvaizdai nustatyti, kai yra kalibraciniu reiksmiu
    void resumeCalibrateButton(){
        globalVariable.setScanning(false);
        buttonCalibrate.setText(getString(R.string.roomactivity_button_resume_calib));
        buttonCalibrate.setEnabled(true);
        setListListener();
        enableMenuItem(exportItem, true);
        //Grizus i Activity arba rotate screen - reiktu vel atstatyti CSV export menu item
    }

    void enableMenuItem(MenuItem item, boolean enabled){
        if (enabled){ item.getIcon().setAlpha(255); }
        else { item.getIcon().setAlpha(128); }
        item.setEnabled(enabled);
    }

    void setListListener(){
        displayBeaconsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), BeaconActivity.class);
                intent.putExtra("roomID", roomID);
                intent.putExtra("beaconID", position);
                startActivity(intent);
            }
        });
    }

    //Neleidzia rankiniu budu keisti "checkmark" busenu
    void setChoiceListener(){
        displayBeaconsList.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
            }
        });
    }

    void removeRoom(){
        globalVariable.setScanning(false);
        database.deleteRoom(currentRoom.getID());
        globalVariable.getRoomsArray().remove(roomID);
        globalVariable.getRoomsList().remove(roomID);
        if (globalVariable.getRoomsArray().isEmpty()){
            database.clearDB();
            globalVariable.getRoomsArray().clear();
            globalVariable.getRoomsList().clear();
        }
        Toast.makeText(getApplicationContext(),
                getString(R.string.toast_info_removed), Toast.LENGTH_SHORT).show();
        RoomActivity.this.finish();
    }

    void removeRoomConfirm() {
        AlertDialogBuilder dialog = new AlertDialogBuilder(RoomActivity.this, getString(R.string.dialog_title_remove),
                getString(R.string.dialog_remove_room), android.R.drawable.ic_dialog_alert);
        dialog.getBuilder().setPositiveButton(getString(R.string.dialog_button_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeRoom();
            }
        });
        dialog.setNegatvie(getString(R.string.dialog_button_cancel));
        dialog.showDialog();
    }

    void ExportRoomCSVConfirm() {
        AlertDialogBuilder dialog = new AlertDialogBuilder(RoomActivity.this, getString(R.string.dialog_title_export),
                getString(R.string.dialog_export_room_csv), android.R.drawable.ic_dialog_info);
        dialog.getBuilder().setPositiveButton(getString(R.string.dialog_button_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { exportRoomCSV(); }});
        dialog.setNegatvie(getString(R.string.dialog_button_cancel));
        dialog.showDialog();
    }

    //Sukuriamas Bluetooth adapteris
    public void createBT(){
        BluetoothManager bluetoothManager =
                (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
    }

    void createBTLECallBack(){
        mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                scantools.calibrateLogic(device, rssi, currentRoom);
                mBluetoothAdapter.stopLeScan(this); //Scan stabdomas
            }
        };
    }

    void reloadBoundDevices(){
        boundBeaconsList.clear();
        boundBeaconsList.addAll(currentRoom.getBeaconsCalibrationCount());
    }

    void checkCalibratedDevices(){
        ArrayList<Boolean> calibratedDevices = currentRoom.getCalibratedBeacons();
        for (int i = 0; i < calibratedDevices.size(); i++){
            displayBeaconsList.setItemChecked(i, calibratedDevices.get(i));
        }
    }

    void checkCompleted(){
        if (currentRoom.isCalibrated()){
            checkCalibratedDevices();
            setListListener();
        }
    }

    //Nuolatos pradedamas ir stabdomas scan
    void continuousScan(){
        final Handler handler3 = new Handler();
        globalVariable.setScanning(true);
        //Main Thread Runnable:
        //pranesa, kad reikia atnaujinti irenginiu sarasa
        final Runnable uiRunnable3 = new Runnable(){
            @Override
            public void run() {
                if (globalVariable.isScanning()) {
                    reloadBoundDevices();
                    checkCalibratedDevices();
                    if (currentRoom.isCalibrated()) { buttonCalibrate.setEnabled(true); }
                }
            }
        };
        //Background Runnable:
        //nustatytais intervalais daro scan ir paleidzia Main Thread Runnable
        Runnable backgroundRunnable3 = new Runnable(){
            @Override
            public void run() {
                if (globalVariable.isScanning()) {
                    startBTLEScan();
                    handler3.postDelayed(this, settings.getFrequency());
                    handler3.postDelayed(uiRunnable3, settings.getFrequency()+1);
                }
            }
        };
        new Thread(backgroundRunnable3).start();
    }

    void startBTLEScan(){
        if (!settings.isGeneratorEnabled()) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        }
        else {
            scantools.fakeCalibrateLogic(settings.getDebugBeacons(),
                    settings.getDebugRSSIMin(), settings.getDebugRSSIMax(), currentRoom);
        }
    }
}
