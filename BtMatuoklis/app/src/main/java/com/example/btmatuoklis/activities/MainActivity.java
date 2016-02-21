package com.example.btmatuoklis.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.btmatuoklis.R;
import com.example.btmatuoklis.classes.MySQLiteHelper;
import com.example.btmatuoklis.classes.Settings;

public class MainActivity extends Activity {

    public static Settings settings;
    MySQLiteHelper database;
    BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getActionBar().setSubtitle(getString(R.string.subtitle_main));

        setDefaultValues();
        createBT();
        checkBT();
        manageDatabase();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_main, menu);
        return true;
    }

    public void onHelpActionClick(MenuItem item){
        //Work in progress
        String res = "empty";
        res = Short.toString(settings.getDelay());
        Toast.makeText(getApplicationContext(), res, Toast.LENGTH_SHORT).show();
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
        settings = new Settings(getApplicationContext());
    }

    void manageDatabase(){
        database = new MySQLiteHelper(this);
        database.deleteAll("rooms");
        database.deleteAll("beacons");
        database.deleteAll("calibrations");
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