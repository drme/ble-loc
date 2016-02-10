package com.example.btmatuoklis.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.btmatuoklis.R;
import com.example.btmatuoklis.classes.DeviceInfo;
import com.example.btmatuoklis.classes.GlobalClass;
import com.example.btmatuoklis.classes.Room;
import com.example.btmatuoklis.classes.ScanTools;
import com.example.btmatuoklis.classes.Settings;

import java.util.ArrayList;

public class SingleRoomActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_single_room);
        actionbar = getSupportActionBar();
        actionbar.setTitle(getText(R.string.app_name));
        actionbar.setSubtitle(getText(R.string.existing_room));
        globalVariable = (GlobalClass) getApplicationContext();
        roomID = getIntent().getExtras().getInt("roomID");
        existingPavadinimas = (TextView)findViewById(R.id.textSingleRoom_ActiveName);
        boundBtList = (ListView)findViewById(R.id.listSingleRoom_DevicesList);
        boundBtList.setChoiceMode(boundBtList.CHOICE_MODE_MULTIPLE);
        calibrateButton = (Button)findViewById(R.id.buttonSingleRoom_Calibrate);
        settings = MainActivity.settings;
        currentRoom = globalVariable.getRoomsArray().get(roomID);
        boundDevList = new ArrayList<String>();
        listBoundAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_checked, boundDevList);
        boundBtList.setAdapter(listBoundAdapter);
        existingPavadinimas.setText(currentRoom.getName());
        loadBoundDevices();
        createBT();
        setCalibrateButtonListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar, menu);
        menu.findItem(R.id.action_remove_room).setVisible(true);
        actionProgress = menu.findItem(R.id.action_progress);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(getBaseContext(), SettingsActivity.class));
                return true;
            case R.id.action_remove_room:
                globalVariable.getRoomsArray().remove(roomID);
                globalVariable.getRoomsList().remove(roomID);
                Toast.makeText(getApplicationContext(), "Kambarys pašalintas.", Toast.LENGTH_SHORT).show();
                this.finish();
                startActivity(new Intent(getBaseContext(), AllRoomsActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
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
                }
            }
        });
    }

    //Sukuriamas Bluetooth adapteris
    public void createBT(){
        BluetoothManager bluetoothManager =
                (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
    }

    void loadBoundDevices(){
        boundDevList.clear();
        boundDevList.addAll(globalVariable.getRoomsArray().get(roomID).getDevicesCalibrationCount());
    }

    void checkCalibratedDevices(){
        ArrayList<Boolean> calibratedDevices = currentRoom.getCalibratedDevices();
        for (int i = 0; i < calibratedDevices.size(); i++){
            boundBtList.setItemChecked(i, calibratedDevices.get(i));
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
                listBoundAdapter.notifyDataSetChanged();
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
                if (currentRoom.isCalibrated()){
                    calibrateButton.setEnabled(true);
                }
                mBluetoothAdapter.stopLeScan(this); //Scan stabdomas
            }
        });
    }

}
