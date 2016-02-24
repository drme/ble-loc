package com.example.btmatuoklis.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.btmatuoklis.R;
import com.example.btmatuoklis.classes.Beacon;
import com.example.btmatuoklis.classes.GlobalClass;
import com.example.btmatuoklis.classes.MySQLiteHelper;
import com.example.btmatuoklis.classes.Room;
import com.example.btmatuoklis.classes.Settings;

import java.util.ArrayList;

public class MainActivity extends Activity {

    GlobalClass globalVariable;
    public static Settings settings;
    BluetoothAdapter mBluetoothAdapter;
    MenuItem helpItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getActionBar().setSubtitle(getString(R.string.subtitle_main));

        setDefaultValues();
        createBT();
        checkBT();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_main, menu);
        helpItem = menu.findItem(R.id.action_help);
        return true;
    }

    public void onHelpActionClick(MenuItem item){
        //Work in progress
        Toast.makeText(getApplicationContext(), "Not implemented.", Toast.LENGTH_SHORT).show();
    }

    public void onSettingsActionClick(MenuItem item){
        startActivity(new Intent(getBaseContext(), SettingsActivity.class));
    }

    public void onScanButtonClick(View view){
        startActivity(new Intent(getBaseContext(), ScanActivity.class));
    }

    public void onAllRoomsButtonClick(View view){
        startActivity(new Intent(getBaseContext(), AllRoomsActivity.class));
    }

    void setDefaultValues(){
        globalVariable = (GlobalClass) getApplicationContext();
        settings = new Settings(getApplicationContext());
    }

    void loadRooms(){
        MySQLiteHelper dbhelper = new MySQLiteHelper(getApplicationContext());
        SQLiteDatabase db = dbhelper.getReadableDatabase();

        String query = "SELECT * FROM rooms";
        Cursor cursor = db.rawQuery(query, null);
        String roomName;
        int id;
        //beaconsArray = new ArrayList<Beacon>();

        while (cursor.moveToNext()) {
            id = cursor.getInt(0);
            roomName = cursor.getString(1);
            globalVariable.getRoomsArray().add(new Room(id, roomName));
            globalVariable.getRoomsList().add(roomName);
        }
        cursor.close();
    }

    void loadBeacons(){
        String beaconName;
        String beaconMac;
        String RSSI = null;
        int id;

        MySQLiteHelper dbhelper = new MySQLiteHelper(getApplicationContext());
        SQLiteDatabase db = dbhelper.getReadableDatabase();

        for (int i = 0; i < globalVariable.getRoomsArray().size(); i++) {
            Room currentRoom = globalVariable.getRoomsArray().get(i);
            String uzklausaSurinkimui = "SELECT beacons.id AS BeaconID, beacons.name AS BeaconName," +
                    "beacons.mac AS BeaconMac, calibrations.rssi AS RSSI " +
                    "FROM calibrations " +
                    "JOIN rooms ON (calibrations.roomid = rooms.id)"+
                    "JOIN beacons ON (calibrations.beaconid = beacons.id)"+
                    "WHERE roomid = " + Integer.toString(currentRoom.getID());

            Cursor cursor = db.rawQuery(uzklausaSurinkimui, null);

            if (cursor != null){
                while (cursor.moveToNext()) {
                    id = cursor.getInt(0);
                    beaconName = cursor.getString(1);
                    beaconMac = cursor.getString(2);
                    if (cursor.isLast()) {
                        RSSI = cursor.getString(3);
                    }
                    currentRoom.getBeacons().add(new Beacon(id, beaconName, beaconMac, loadRSSIS(RSSI)));
                }
            }
            cursor.close();
        }
    }

    ArrayList<Byte> loadRSSIS(String rssiArray){
        if (rssiArray == null){
            return new ArrayList<Byte>();
        }
        String onlyRSSI = rssiArray.replaceAll("[\\[\\]\\^]", "");
        String[] RSSIS = onlyRSSI.split(", ");
        ArrayList<Byte> arrays = new ArrayList<Byte>();
        for (String rssi : RSSIS) {
            byte lastrssi = Byte.parseByte(rssi.toString());
            arrays.add(lastrssi);
        }
        return arrays;
    }

    public void loadDatabase(MenuItem item){
        globalVariable.getRoomsArray().clear();
        globalVariable.getRoomsList().clear();
        loadRooms();
        loadBeacons();
        Toast.makeText(getApplicationContext(), "Duombazė užkrauta.", Toast.LENGTH_SHORT).show();
    }

    public void clearDatabase(MenuItem item){
        MySQLiteHelper database = new MySQLiteHelper(this);
        database.deleteAll("rooms");
        database.deleteAll("beacons");
        database.deleteAll("calibrations");
        globalVariable.getRoomsArray().clear();
        globalVariable.getRoomsList().clear();
        Toast.makeText(getApplicationContext(), "Duombazė išvalyta", Toast.LENGTH_SHORT).show();
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
}