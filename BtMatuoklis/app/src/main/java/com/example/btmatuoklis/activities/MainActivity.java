package com.example.btmatuoklis.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.btmatuoklis.R;
import com.example.btmatuoklis.classes.GlobalClass;
import com.example.btmatuoklis.helpers.MySQLiteHelper;
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
        loadDatabase(null);
    }

    void loadRooms(){
        MySQLiteHelper database = new MySQLiteHelper(this);
        ArrayList<Room> rooms = database.getAllRooms();
        for (int i = 0; i < rooms.size(); i++){
            globalVariable.getRoomsArray().getArray().add(rooms.get(i));
        }
    }

    void loadBeacons(){
        MySQLiteHelper database = new MySQLiteHelper(this);
        database.loadAllBeacons(globalVariable.getRoomsArray().getArray());
    }

    public void loadDatabase(MenuItem item){
        globalVariable.getRoomsArray().getArray().clear();
        loadRooms();
        loadBeacons();
        Toast.makeText(getApplicationContext(), "Duombazė užkrauta.", Toast.LENGTH_SHORT).show();
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