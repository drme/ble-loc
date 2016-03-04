package com.example.btmatuoklis.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.btmatuoklis.R;
import com.example.btmatuoklis.classes.AlertDialogBuilder;
import com.example.btmatuoklis.classes.Calibration;
import com.example.btmatuoklis.classes.GlobalClass;
import com.example.btmatuoklis.classes.MySQLiteHelper;
import com.example.btmatuoklis.classes.Room;
import com.example.btmatuoklis.classes.ScanTools;
import com.example.btmatuoklis.classes.Settings;

import java.util.ArrayList;

public class NewRoomActivity extends Activity {

    GlobalClass globalVariable;
    int roomID;
    long lastID;
    Settings settings;
    ScanTools scantools;
    Room environment, currentRoom;
    MySQLiteHelper database;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothAdapter.LeScanCallback mLeScanCallback;
    String roomName;
    ListView displayBeaconsList;
    ArrayList<String> savedBeaconsList;
    ArrayList<String> beaconsList;
    ArrayAdapter<String> listAdapter;
    ArrayList<Integer> selectedBeacons;
    Button buttonAccept;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_room);
        getActionBar().setSubtitle(getString(R.string.subtitle_new_room_beacons));
        displayBeaconsList = (ListView)findViewById(R.id.listNewRoom_BeaconsList);
        buttonAccept = (Button)findViewById(R.id.buttonNewRoom_End);

        setDefaultValues();
        setListListener();
        createBT();
        createBTLECallBack();
        continuousScan();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getActionBar().setDisplayShowCustomEnabled(true);
        getActionBar().setCustomView(R.layout.action_view_progress);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_newroom, menu);
        return true;
    }

    @Override
    public void onBackPressed(){
        cancelCreationConfirm();
    }

    public void onCancelActionClick(MenuItem item){
        cancelCreationConfirm();
    }

    public void onHelpActionClick(MenuItem item){
        //Work in progress
        Toast.makeText(getApplicationContext(), "Not implemented.", Toast.LENGTH_SHORT).show();
    }

    public void onSettingsActionClick(MenuItem item){
        startActivity(new Intent(getBaseContext(), SettingsActivity.class));
    }

    public void onAcceptButtonClick(View view){
        globalVariable.setScanning(false);
        createRoom();
        saveSelectedBeacons();
        NewRoomActivity.this.finish();
        startActivity(new Intent(getBaseContext(), AllRoomsActivity.class));
    }

    void setDefaultValues(){
        globalVariable = (GlobalClass) getApplicationContext();
        roomName = getIntent().getExtras().getString("roomName");
        settings = MainActivity.settings;
        scantools = new ScanTools();
        environment = new Room();
        database = new MySQLiteHelper(this);
        savedBeaconsList = new ArrayList<String>();
        beaconsList = new ArrayList<String>();
        listAdapter = new ArrayAdapter<String>(this, R.layout.list_multiple_choice, beaconsList);
        selectedBeacons = new ArrayList<Integer>();
        displayBeaconsList.setAdapter(listAdapter);
    }

    void setListListener(){
        displayBeaconsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckedTextView checkedTextView = ((CheckedTextView) view);
                checkedTextView.setChecked(!checkedTextView.isChecked());
                if (checkedTextView.isChecked()) {
                    selectedBeacons.add(position);
                } else {
                    selectedBeacons.remove(selectedBeacons.indexOf(position));
                }
                enableFinishButton();
            }
        });
    }

    void enableFinishButton(){
        if (selectedBeacons.size() > 0){ buttonAccept.setEnabled(true); }
        else { buttonAccept.setEnabled(false); }
    }

    void cancelCreation(){
        globalVariable.setScanning(false);
        Toast.makeText(getApplicationContext(),
                getString(R.string.toast_info_cancelled), Toast.LENGTH_SHORT).show();
        NewRoomActivity.this.finish();
        startActivity(new Intent(getBaseContext(), AllRoomsActivity.class));
    }

    void cancelCreationConfirm(){
        AlertDialogBuilder dialog = new AlertDialogBuilder(NewRoomActivity.this, getString(R.string.dialog_title_cancel),
                getString(R.string.dialog_cancel_room_creation), android.R.drawable.ic_dialog_alert);
        dialog.getBuilder().setPositiveButton(getString(R.string.dialog_button_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cancelCreation();
            }
        });
        dialog.setNegatvie(getString(R.string.dialog_button_cancel));
        dialog.showDialog();
    }

    //To-Do: patikrinti ar kambarys tokiu pavadinimu duombazeje jau neegzistuoja
    void createRoom(){
        globalVariable.getRoomsArray().add(new Room(roomName));
        globalVariable.getRoomsList().add(roomName);
        roomID = globalVariable.getRoomsArray().size() - 1;
        currentRoom = globalVariable.getRoomsArray().get(roomID);
    }

    void saveSelectedBeacons(){
        createRoomInDatabase();
        for (int i = 0; i < selectedBeacons.size(); i++){
            currentRoom.getBeacons().add(environment.getBeacons().get(i));
            saveBeaconsInDatabase(i);
        }
        notifyCreatedRoomAndBeacons();
    }

    void createRoomInDatabase(){
        database.addRoom(currentRoom);
        SQLiteDatabase dd = database.getReadableDatabase();
        Cursor c = dd.rawQuery("SELECT ROWID FROM rooms ORDER BY ROWID DESC LIMIT 1", null);
        if (c != null && c.moveToFirst()) {
            lastID = c.getLong(0); //The 0 is the column index, we only have 1 column, so the index is 0
        }
        int id = (int)lastID;
        currentRoom.setID(id);
    }

    void saveBeaconsInDatabase(int i){
        database.addBeacon(currentRoom.getBeacons().get(i));
        SQLiteDatabase dd = database.getReadableDatabase();
        Cursor c = dd.rawQuery("SELECT ROWID FROM beacons ORDER BY ROWID DESC LIMIT 1", null);
        if (c != null && c.moveToFirst()) {
            lastID = c.getLong(0); //The 0 is the column index, we only have 1 column, so the index is 0
        }
        int id = (int)lastID;
        currentRoom.getBeacons().get(i).setID(id);
        database.addCalibration(new Calibration(currentRoom.getID(), id, null));
    }

    void notifyCreatedRoomAndBeacons(){
        Toast.makeText(getApplicationContext(), getString(R.string.toast_info_created_room1)+
                roomName+getString(R.string.toast_info_created_room2)+
                getString(R.string.toast_info_created_room3)+
                currentRoom.getBeacons().size(), Toast.LENGTH_SHORT).show();
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
                //scantools.scanLogic(device, rssi, beaconsArray, savedBeaconsList);
                scantools.scanLogic(device, rssi, environment);
                mBluetoothAdapter.stopLeScan(this); //Scan stabdomas
            }
        };
    }

    //Nuolatos pradedamas ir stabdomas scan
    void continuousScan(){
        final Handler handler2 = new Handler();
        globalVariable.setScanning(true);
        //Main Thread Runnable:
        //pranesa, kad reikia atnaujinti irenginiu sarasa
        final Runnable uiRunnable2 = new Runnable(){
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
        Runnable backgroundRunnable2 = new Runnable(){
            @Override
            public void run() {
                if (globalVariable.isScanning()) {
                    startBTLEScan();
                    handler2.postDelayed(this, settings.getFrequency());
                    handler2.postDelayed(uiRunnable2, settings.getFrequency()+1);
                }
            }
        };
        new Thread(backgroundRunnable2).start();
    }

    //Jeigu randamas BTLE irenginys, gaunama jo RSSI reiksme
    void startBTLEScan(){
        if (!settings.isGeneratorEnabled()) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        }
        else{
            scantools.fakeScanLogic(settings.getDebugBeacons(), settings.getDebugRSSIMin(),
                    settings.getDebugRSSIMax(), environment);
        }
        savedBeaconsList = environment.getCurrentInfoList();
    }
}
