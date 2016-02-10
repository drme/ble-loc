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
import android.widget.Toast;

import com.example.btmatuoklis.R;
import com.example.btmatuoklis.classes.DeviceInfo;
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
    boolean scanning = false;
    ArrayAdapter<String> listAdapter;
    ArrayList<DeviceInfo> btDevList;
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
        actionbar.setSubtitle(getText(R.string.new_room_devices_subtitle));
        acceptBtn = (Button)findViewById(R.id.buttonNewRoom_End);
        roomName = getIntent().getExtras().getString("roomName");
        setAcceptListener();
        createBT();
        settings = MainActivity.settings;
        globalVariable = (GlobalClass) getApplicationContext();
        btInfo = (ListView)findViewById(R.id.listNewRoom_DevicesList);
        selectedDevices = new ArrayList<Integer>();
        btDevList = new ArrayList<DeviceInfo>();
        savedDevList = new ArrayList<String>();
        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, savedDevList);
        btInfo.setAdapter(listAdapter);
        setListListener();
        contScanStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar, menu);
        menu.findItem(R.id.action_progress).setVisible(true);
        menu.findItem(R.id.action_remove_room).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(getBaseContext(), SettingsActivity.class));
                return true;
            case R.id.action_remove_room:
                Toast.makeText(getApplicationContext(), "Atšaukta.", Toast.LENGTH_SHORT).show();
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

    void setAcceptListener() {
        acceptBtn.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        if (selectedDevices.size() > 0) {
                            scanning = false;
                            createRoom();
                            saveSelected();
                            NewRoomActivity.this.finish();
                            startActivity(new Intent(getBaseContext(), AllRoomsActivity.class));
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Nepasirinktas nei vienas įrenginys!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    //Sukuriamas Bluetooth adapteris
    public void createBT(){
        BluetoothManager bluetoothManager =
                (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
    }

    void setListListener(){
        btInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckedTextView checkedTextView = ((CheckedTextView) view);
                checkedTextView.setChecked(!checkedTextView.isChecked());
                if (checkedTextView.isChecked()){
                    selectedDevices.add(position);
                }
                else {
                    selectedDevices.remove(selectedDevices.indexOf(position));
                }
            }
        });
    }

    void createRoom(){
        globalVariable.getRoomsArray().add(new Room(roomName));
        globalVariable.getRoomsList().add(roomName);
        roomID = globalVariable.getRoomsArray().size() - 1;
    }

    void saveSelected(){
        for (int i = 0; i < selectedDevices.size(); i++){
            globalVariable.getRoomsArray().get(roomID).getDevices().add(btDevList.get(selectedDevices.get(i)));
        }
        selectedDevices.clear();
    }

    //Nuolatos pradedamas ir stabdomas scan
    void contScanStop(){
        final Handler handler2 = new Handler();
        scanning = true;
        //Main Thread Runnable:
        //pranesa, kad reikia atnaujinti irenginiu sarasa
        final Runnable uiRunnable2 = new Runnable(){
            @Override
            public void run() {
                listAdapter.notifyDataSetChanged();
            }
        };
        //Background Runnable:
        //nustatytais intervalais daro scan ir paleidzia Main Thread Runnable
        Runnable backgroundRunnable2 = new Runnable(){
            @Override
            public void run() {
                if (scanning) {
                    startStopScan();
                    handler2.postDelayed(uiRunnable2, settings.getDelay());
                    handler2.postDelayed(this, settings.getDelay());
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
