package com.example.btmatuoklis.activities;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.btmatuoklis.R;
import com.example.btmatuoklis.classes.Calibration;
import com.example.btmatuoklis.classes.GlobalClass;
import com.example.btmatuoklis.classes.MySQLiteHelper;
import com.example.btmatuoklis.classes.Room;
import com.example.btmatuoklis.classes.ScanTools;
import com.example.btmatuoklis.classes.Settings;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

public class RoomActivity extends AppCompatActivity {

    GlobalClass globalVariable;
    int roomID;
    Settings settings;
    ScanTools scantools;
    Room currentRoom;
    MySQLiteHelper database;
    BluetoothAdapter mBluetoothAdapter;
    MenuItem actionProgress;
    ArrayList<String> boundDevList;
    ArrayAdapter<String> listBoundAdapter;
    ListView boundBtList;
    TextView existingPavadinimas;
    Button calibrateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        getSupportActionBar().setSubtitle(getString(R.string.subtitle_existing_room));
        existingPavadinimas = (TextView)findViewById(R.id.textSingleRoom_ActiveName);
        boundBtList = (ListView)findViewById(R.id.listSingleRoom_DevicesList);
        calibrateButton = (Button)findViewById(R.id.buttonSingleRoom_Calibrate);

        setDefaultValues();
        loadBoundDevices();
        checkCompleted();
        setChoiceListener();
        createBT();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_room, menu);
        actionProgress = menu.findItem(R.id.action_progress);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBoundDevices();
        checkCalibratedDevices();
        if ((currentRoom.getBeacons().size() == 0) | !currentRoom.isCalibrationStarted()){
            restoreCalibrateButton();
        }
        else {
            resumeCalibrateButton();
        }
    }

    @Override
    public void onBackPressed(){
        globalVariable.setScanning(false);
        this.finish();
    }

    public void onRemoveActionClick(MenuItem item){ removeRoomConfirm(); }

    public void onHelpActionClick(MenuItem item){
        //Work in progress
        Toast.makeText(getApplicationContext(), "Not implemented.", Toast.LENGTH_SHORT).show();
    }

    public void onSettingsActionClick(MenuItem item){
        startActivity(new Intent(getBaseContext(), SettingsActivity.class));
    }

    public void onCalibrateButtonClick(View view){
        if (!globalVariable.isScanning() && !currentRoom.isCalibrated()) {
            StartCalibration();
        } else if (globalVariable.isScanning() && currentRoom.isCalibrated()) {
            finishCalibration();
        }
        else {
            StartCalibration();
        }
    }

    void setDefaultValues(){
        globalVariable = (GlobalClass) getApplicationContext();
        roomID = getIntent().getExtras().getInt("roomID");
        settings = MainActivity.settings;
        scantools = new ScanTools();
        currentRoom = globalVariable.getRoomsArray().get(roomID);
        database = new MySQLiteHelper(this);
        boundDevList = new ArrayList<String>();
        listBoundAdapter = new ArrayAdapter<String>(this, R.layout.list_checked, boundDevList);
        boundBtList.setAdapter(listBoundAdapter);
        existingPavadinimas.setText(currentRoom.getName());
    }

    //Veiksmai kalibracijai pradeti
    void StartCalibration(){
        globalVariable.setScanning(true);
        actionProgress.setVisible(true);
        calibrateButton.setText(getString(R.string.roomactivity_button_finish_calib));
        calibrateButton.setEnabled(false);
        contScanStop();
    }

    //Veiksmai veiksmai kalibracijai baigti
    void finishCalibration(){
        globalVariable.setScanning(false);
        actionProgress.setVisible(false);
        calibrateButton.setText(getString(R.string.roomactivity_button_resume_calib));
        calibrateButton.setEnabled(true);
        setListListener();
        saveRSSIInDatabase();
        exportDB();
    }

    void saveRSSIInDatabase(){
        int roomdID = currentRoom.getId();
        int beaconID;
        ArrayList<Boolean> calibratedDevices = currentRoom.getCalibratedBeacons();
        String rssi;
        for (int i = 0; i < calibratedDevices.size(); i++){
            boundBtList.setItemChecked(i, calibratedDevices.get(i));
            rssi = currentRoom.getBeacons().get(i).getCalibratedRSSI().toString();
            beaconID = currentRoom.getBeacons().get(i).getId();
            database.addCalibration(new Calibration(roomdID, beaconID, rssi));
        }
    }

    private void exportDB() {
        //File dbFile = getDatabasePath("CalibrationDB.db");
        MySQLiteHelper dbhelper = new MySQLiteHelper(getApplicationContext());
        //String directory = Environment.getExternalStorageDirectory()+"/"+getString(R.string.app_name);
        String directory = getExternalStorageDirectory(getString(R.string.app_name));
        File exportDir = new File(directory, "");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }

        String fileName = getString(R.string.generic_calibrate)+currentRoom.getName()+".csv";
        File file = new File(exportDir, fileName);
        try {
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            SQLiteDatabase db = dbhelper.getReadableDatabase();
            String uzklausaSurinkimui = "SELECT rooms.name AS RoomName, beacons.name AS BeaconName," +
                    "beacons.mac AS BeaconMac, calibrations.rssi AS RSSI " +
                    "FROM calibrations " +
                    "JOIN rooms ON (calibrations.roomid = rooms.id)"+
                    "JOIN beacons ON (calibrations.beaconid = beacons.id)"+
                    "WHERE roomid = " + Integer.toString(currentRoom.getId());
            Cursor curCSV = db.rawQuery(uzklausaSurinkimui, null);
            csvWrite.writeNext(curCSV.getColumnNames());
            while (curCSV.moveToNext()) {
                //Which column you want to exprort
                String arrStr[] = {curCSV.getString(0), curCSV.getString(1), curCSV.getString(2), curCSV.getString(3)};
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();
            notifyExportedCSV(fileName, directory);
        } catch (Exception sqlEx) {
            //Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
        }
    }

    void notifyExportedCSV(String fileName, String directory){
        Toast.makeText(getApplicationContext(), getString(R.string.toast_info_created_file1)+
                        fileName+ getString(R.string.toast_info_created_file2)+
                        getString(R.string.toast_info_created_file3)+directory,
                Toast.LENGTH_LONG).show();
    }

    String getExternalStorageDirectory(String folder){
        String sdpath="/storage/extSdCard/";
        String sd1path="/storage/sdcard1/";
        String usbdiskpath="/storage/usbcard1/";
        String sd0path="/storage/sdcard0/";
        if(new File(sdpath).exists()) { return sdpath+folder; }
        else if(new File(sd1path).exists()) { return sd1path+folder; }
        else if(new File(usbdiskpath).exists()) { return usbdiskpath+folder; }
        else if(new File(sd0path).exists()) { return sd0path+folder; }
        else return Environment.getExternalStorageDirectory().toString()+folder;
    }

    //Veiksmai pradinei mygtuko isvaizdai atstayti, kai nera kalibraciniu reiksmiu
    void restoreCalibrateButton(){
        globalVariable.setScanning(false);
        calibrateButton.setText(getString(R.string.roomactivity_button_calibrate));
        calibrateButton.setEnabled(true);
        boundBtList.setOnItemClickListener(null);
    }

    //Veiksmai mygtuko isvaizdai nustatyti, kai yra kalibraciniu reiksmiu
    void resumeCalibrateButton(){
        globalVariable.setScanning(false);
        calibrateButton.setText(getString(R.string.roomactivity_button_resume_calib));
        calibrateButton.setEnabled(true);
        setListListener();
    }

    void setListListener(){
        boundBtList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
        boundBtList.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
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

    void removeRoomConfirm() {
        final AlertDialog.Builder builder2 = new AlertDialog.Builder(RoomActivity.this);
        builder2.setTitle(getString(R.string.dialog_remove_room));
        builder2.setIcon(android.R.drawable.ic_dialog_alert);

        builder2.setPositiveButton(getString(R.string.dialog_button_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                globalVariable.setScanning(false);
                globalVariable.getRoomsArray().remove(roomID);
                globalVariable.getRoomsList().remove(roomID);
                Toast.makeText(getApplicationContext(),
                        getString(R.string.toast_info_removed), Toast.LENGTH_SHORT).show();
                RoomActivity.this.finish();
                startActivity(new Intent(getBaseContext(), AllRoomsActivity.class));
            }
        });

        builder2.setNegativeButton(getString(R.string.dialog_button_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder2.show();
    }

    //Sukuriamas Bluetooth adapteris
    public void createBT(){
        BluetoothManager bluetoothManager =
                (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
    }

    void loadBoundDevices(){
        boundDevList.clear();
        boundDevList.addAll(globalVariable.getRoomsArray().get(roomID).getBeaconsCalibrationCount());
    }

    void checkCalibratedDevices(){
        ArrayList<Boolean> calibratedDevices = currentRoom.getCalibratedBeacons();
        for (int i = 0; i < calibratedDevices.size(); i++){
            boundBtList.setItemChecked(i, calibratedDevices.get(i));
        }
    }

    void checkCompleted(){
        if (currentRoom.isCalibrated()){
            checkCalibratedDevices();
            calibrateButton.setText("Baigti");
            calibrateButton.setEnabled(false);
            setListListener();
        }
    }

    //Nuolatos pradedamas ir stabdomas scan
    void contScanStop(){
        final Handler handler3 = new Handler();
        globalVariable.setScanning(true);
        //Main Thread Runnable:
        //pranesa, kad reikia atnaujinti irenginiu sarasa
        final Runnable uiRunnable3 = new Runnable(){
            @Override
            public void run() {
                if (globalVariable.isScanning()) {
                    loadBoundDevices();
                    checkCalibratedDevices();
                    if (currentRoom.isCalibrated()) {
                        calibrateButton.setEnabled(true);
                        setListListener();
                    }
                }
            }
        };
        //Background Runnable:
        //nustatytais intervalais daro scan ir paleidzia Main Thread Runnable
        Runnable backgroundRunnable3 = new Runnable(){
            @Override
            public void run() {
                if (globalVariable.isScanning()) {
                    startStopScan();
                    handler3.postDelayed(this, settings.getDelay());
                    handler3.postDelayed(uiRunnable3, settings.getDelay()+1);
                }
            }
        };
        new Thread(backgroundRunnable3).start();
    }

    void startStopScan(){
        mBluetoothAdapter.startLeScan(new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                scantools.calibrateLogic(device, rssi, currentRoom);
                mBluetoothAdapter.stopLeScan(this); //Scan stabdomas
            }
        });
    }

}
