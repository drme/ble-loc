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
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.btmatuoklis.R;
import com.example.btmatuoklis.classes.Beacon;
import com.example.btmatuoklis.classes.GlobalClass;
import com.example.btmatuoklis.classes.MySQLiteHelper;
import com.example.btmatuoklis.classes.Room;
import com.example.btmatuoklis.classes.ScanTools;
import com.example.btmatuoklis.classes.Settings;

import java.util.ArrayList;

public class NewRoomActivity extends AppCompatActivity {

    GlobalClass globalVariable;
    int roomID;
    long lastId;
    Settings settings;
    ScanTools scantools;
    Room currentRoom;
    MySQLiteHelper database;
    BluetoothAdapter mBluetoothAdapter;
    String roomName;
    ListView btInfo;
    ArrayList<Beacon> btDevList;
    ArrayList<String> savedDevList;
    ArrayAdapter<String> listAdapter;
    ArrayList<Integer> selectedDevices;
    Button acceptBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_room);
        getSupportActionBar().setSubtitle(getString(R.string.subtitle_new_room_beacons));
        btInfo = (ListView)findViewById(R.id.listNewRoom_DevicesList);
        acceptBtn = (Button)findViewById(R.id.buttonNewRoom_End);

        setDefaultValues();
        setListListener();
        createBT();
        contScanStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
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
        if (selectedDevices.size() > 0) {
            globalVariable.setScanning(false);
            createRoom();
            saveSelectedBeacons();
            NewRoomActivity.this.finish();
            startActivity(new Intent(getBaseContext(), AllRoomsActivity.class));
        } else {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.toast_warning_no_beacons), Toast.LENGTH_SHORT).show();
        }
    }

    void setDefaultValues(){
        globalVariable = (GlobalClass) getApplicationContext();
        roomName = getIntent().getExtras().getString("roomName");
        settings = MainActivity.settings;
        scantools = new ScanTools();
        database = new MySQLiteHelper(this);
        btDevList = new ArrayList<Beacon>();
        savedDevList = new ArrayList<String>();
        listAdapter = new ArrayAdapter<String>(this, R.layout.list_multiple_choice, savedDevList);
        selectedDevices = new ArrayList<Integer>();
        btInfo.setAdapter(listAdapter);
    }

    void setListListener(){
        btInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckedTextView checkedTextView = ((CheckedTextView) view);
                checkedTextView.setChecked(!checkedTextView.isChecked());
                if (checkedTextView.isChecked()) {
                    selectedDevices.add(position);
                } else {
                    selectedDevices.remove(selectedDevices.indexOf(position));
                }
            }
        });
    }

    void cancelCreationConfirm(){
        final AlertDialog.Builder builder3 = new AlertDialog.Builder(NewRoomActivity.this);
        builder3.setTitle(getString(R.string.dialog_cancel_room_creation));
        builder3.setIcon(android.R.drawable.ic_dialog_alert);

        builder3.setPositiveButton(getString(R.string.dialog_button_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                globalVariable.setScanning(false);
                Toast.makeText(getApplicationContext(),
                        getString(R.string.toast_info_cancelled), Toast.LENGTH_SHORT).show();
                NewRoomActivity.this.finish();
                startActivity(new Intent(getBaseContext(), AllRoomsActivity.class));
            }
        });

        builder3.setNegativeButton(getString(R.string.dialog_button_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder3.show();
    }

    //To-Do: patikrinti ar kambarys tokiu pavadinimu duombazeje jau neegzistuoja
    void createRoom(){
        globalVariable.getRoomsArray().add(new Room(roomName));
        globalVariable.getRoomsList().add(roomName);
        roomID = globalVariable.getRoomsArray().size() - 1;
        currentRoom = globalVariable.getRoomsArray().get(roomID);
        //createRoomInDatabase();
    }

    void saveSelectedBeacons(){
        createRoomInDatabase();
        for (int i = 0; i < selectedDevices.size(); i++){
            currentRoom.getBeacons().add(btDevList.get(selectedDevices.get(i)));
            saveBeaconsInDatabase(i);
        }
        notifyCreatedRoomAndBeacons();
        selectedDevices.clear();
    }

    void createRoomInDatabase(){
        database.addRoom(currentRoom);
        SQLiteDatabase dd = database.getReadableDatabase();
        Cursor c = dd.rawQuery("SELECT ROWID FROM rooms ORDER BY ROWID DESC LIMIT 1", null);
        if (c != null && c.moveToFirst()) {
            lastId = c.getLong(0); //The 0 is the column index, we only have 1 column, so the index is 0
        }
        int id = (int)lastId;
        currentRoom.setId(id);
    }

    void saveBeaconsInDatabase(int i){
        database.addBeacon(currentRoom.getBeacons().get(i));
        SQLiteDatabase dd = database.getReadableDatabase();
        Cursor c = dd.rawQuery("SELECT ROWID FROM beacons ORDER BY ROWID DESC LIMIT 1", null);
        if (c != null && c.moveToFirst()) {
            lastId = c.getLong(0); //The 0 is the column index, we only have 1 column, so the index is 0
        }
        int id = (int)lastId;
        currentRoom.getBeacons().get(i).setId(id);
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

    //Nuolatos pradedamas ir stabdomas scan
    void contScanStop(){
        final Handler handler2 = new Handler();
        globalVariable.setScanning(true);
        //Main Thread Runnable:
        //pranesa, kad reikia atnaujinti irenginiu sarasa
        final Runnable uiRunnable2 = new Runnable(){
            @Override
            public void run() {
                if (globalVariable.isScanning()) {
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
                    startStopScan();
                    handler2.postDelayed(this, settings.getDelay());
                    handler2.postDelayed(uiRunnable2, settings.getDelay()+1);
                }
            }
        };
        new Thread(backgroundRunnable2).start();
    }

    //Jeigu randamas BTLE irenginys, gaunama jo RSSI reiksme
    void startStopScan(){
        mBluetoothAdapter.startLeScan(new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                scantools.scanLogic(device, rssi, settings.getTxPow(), btDevList, savedDevList);
                mBluetoothAdapter.stopLeScan(this); //Scan stabdomas
            }
        });
    }
}
