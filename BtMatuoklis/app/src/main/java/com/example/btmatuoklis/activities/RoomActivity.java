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
import com.example.btmatuoklis.classes.GlobalClass;
import com.example.btmatuoklis.classes.Room;
import com.example.btmatuoklis.classes.ScanTools;
import com.example.btmatuoklis.classes.Settings;

import java.util.ArrayList;

public class RoomActivity extends AppCompatActivity {

    ActionBar actionbar;
    TextView existingPavadinimas;

    ListView boundBtList;
    ArrayAdapter<String> listBoundAdapter;
    ArrayList<String> boundDevList;
    MenuItem actionProgress;
    Button calibrateButton;
    BluetoothAdapter mBluetoothAdapter;
    boolean scanning = false;
    Settings settings;
    ScanTools scantools = new ScanTools();
    Room currentRoom;

    int roomID;

    GlobalClass globalVariable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        actionbar = getSupportActionBar();
        actionbar.setTitle(getText(R.string.app_name));
        actionbar.setSubtitle(getText(R.string.subtitle_existing_room));
        globalVariable = (GlobalClass) getApplicationContext();
        roomID = getIntent().getExtras().getInt("roomID");
        existingPavadinimas = (TextView)findViewById(R.id.textSingleRoom_ActiveName);
        boundBtList = (ListView)findViewById(R.id.listSingleRoom_DevicesList);
        calibrateButton = (Button)findViewById(R.id.buttonSingleRoom_Calibrate);
        settings = MainActivity.settings;
        currentRoom = globalVariable.getRoomsArray().get(roomID);
        boundDevList = new ArrayList<String>();
        listBoundAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_checked, boundDevList);
        boundBtList.setAdapter(listBoundAdapter);
        existingPavadinimas.setText(currentRoom.getName());
        createBT();
        setChoiceListener();
        setCalibrateButtonListener();
        loadBoundDevices();
        checkCompleted();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_remove:
                removeRoomConfirm();
                return true;
            case R.id.action_help:
                //Work in progress
                Toast.makeText(getApplicationContext(), "Not implemented.", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_settings:
                startActivity(new Intent(getBaseContext(), SettingsActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBoundDevices();
        checkCalibratedDevices();
        if (currentRoom.isCalibrated()) {
            setListListener();
        }
        else {
            boundBtList.setOnItemClickListener(null);
        }
    }

    @Override
    public void onBackPressed(){
        scanning = false;
        this.finish();
    }

    void setCalibrateButtonListener(){
        calibrateButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if (!scanning) {
                    contScanStop();
                    actionProgress.setVisible(true);
                    calibrateButton.setText("Baigti");
                    calibrateButton.setEnabled(false);
                } else {
                    scanning = false;
                    actionProgress.setVisible(false);
                    calibrateButton.setEnabled(false);
                    setListListener();
                }
            }
        });
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
        builder2.setTitle(getText(R.string.dialog_remove_room));
        builder2.setIcon(android.R.drawable.ic_dialog_alert);

        builder2.setPositiveButton(getText(R.string.dialog_button_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                scanning = false;
                globalVariable.getRoomsArray().remove(roomID);
                globalVariable.getRoomsList().remove(roomID);
                Toast.makeText(getApplicationContext(),
                        getText(R.string.toast_info_removed), Toast.LENGTH_SHORT).show();
                RoomActivity.this.finish();
                startActivity(new Intent(getBaseContext(), AllRoomsActivity.class));
            }
        });

        builder2.setNegativeButton(getText(R.string.dialog_button_cancel), new DialogInterface.OnClickListener() {
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
        scanning = true;
        //Main Thread Runnable:
        //pranesa, kad reikia atnaujinti irenginiu sarasa
        final Runnable uiRunnable3 = new Runnable(){
            @Override
            public void run() {
                loadBoundDevices();
                checkCalibratedDevices();
                if (currentRoom.isCalibrated()){
                    calibrateButton.setEnabled(true);
                    setListListener();
                }
            }
        };
        //Background Runnable:
        //nustatytais intervalais daro scan ir paleidzia Main Thread Runnable
        Runnable backgroundRunnable3 = new Runnable(){
            @Override
            public void run() {
                if (scanning) {
                    startStopScan();
                    handler3.postDelayed(uiRunnable3, settings.getDelay());
                    handler3.postDelayed(this, settings.getDelay());
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
