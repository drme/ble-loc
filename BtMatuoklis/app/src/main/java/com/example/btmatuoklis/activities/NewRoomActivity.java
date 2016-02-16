package com.example.btmatuoklis.activities;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBar;
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
import com.example.btmatuoklis.classes.Room;
import com.example.btmatuoklis.classes.ScanTools;
import com.example.btmatuoklis.classes.Settings;

import java.util.ArrayList;

public class NewRoomActivity extends AppCompatActivity {

    ActionBar actionbar;
    Button acceptBtn;

    BluetoothAdapter mBluetoothAdapter;
    ListView btInfo;
    ArrayAdapter<String> listAdapter;
    ArrayList<Beacon> btDevList;
    ArrayList<String> savedDevList;
    Settings settings;
    ScanTools scantools = new ScanTools();

    ArrayList<Integer> selectedDevices;
    int roomID = 0;

    String roomName;

    GlobalClass globalVariable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_room);
        actionbar = getSupportActionBar();
        actionbar.setSubtitle(getText(R.string.subtitle_new_room_beacons));
        acceptBtn = (Button)findViewById(R.id.buttonNewRoom_End);
        roomName = getIntent().getExtras().getString("roomName");
        createBT();
        settings = MainActivity.settings;
        globalVariable = (GlobalClass) getApplicationContext();
        btInfo = (ListView)findViewById(R.id.listNewRoom_DevicesList);
        selectedDevices = new ArrayList<Integer>();
        btDevList = new ArrayList<Beacon>();
        savedDevList = new ArrayList<String>();
        listAdapter = new ArrayAdapter<String>(this, R.layout.list_multiple_choice, savedDevList);
        btInfo.setAdapter(listAdapter);
        setListListener();
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
            saveSelected();
            NewRoomActivity.this.finish();
            startActivity(new Intent(getBaseContext(), AllRoomsActivity.class));
        } else {
            Toast.makeText(getApplicationContext(),
                    getText(R.string.toast_warning_no_beacons), Toast.LENGTH_SHORT).show();
        }
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
        builder3.setTitle(getText(R.string.dialog_cancel_room_creation));
        builder3.setIcon(android.R.drawable.ic_dialog_alert);

        builder3.setPositiveButton(getText(R.string.dialog_button_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                globalVariable.setScanning(false);
                Toast.makeText(getApplicationContext(),
                        getText(R.string.toast_info_cancelled), Toast.LENGTH_SHORT).show();
                NewRoomActivity.this.finish();
                startActivity(new Intent(getBaseContext(), AllRoomsActivity.class));
            }
        });

        builder3.setNegativeButton(getText(R.string.dialog_button_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder3.show();
    }

    void createRoom(){
        globalVariable.getRoomsArray().add(new Room(roomName));
        globalVariable.getRoomsList().add(roomName);
        roomID = globalVariable.getRoomsArray().size() - 1;
    }

    void saveSelected(){
        for (int i = 0; i < selectedDevices.size(); i++){
            globalVariable.getRoomsArray().get(roomID).getBeacons().add(btDevList.get(selectedDevices.get(i)));
        }
        selectedDevices.clear();
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
