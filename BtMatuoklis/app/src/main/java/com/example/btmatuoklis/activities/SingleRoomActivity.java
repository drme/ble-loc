package com.example.btmatuoklis.activities;

import android.app.Activity;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.btmatuoklis.R;
import com.example.btmatuoklis.classes.DeviceInfo;
import com.example.btmatuoklis.classes.Settings;

import java.util.ArrayList;

public class SingleRoomActivity extends AppCompatActivity {

    ActionBar actionbar;
    Button acceptBtn;
    EditText newPavadinimas;
    TextView existingPavadinimas;

    //-----
    BluetoothAdapter mBluetoothAdapter;
    ListView btInfo;
    ListView boundBtList;
    boolean scanning = false;
    ArrayAdapter<String> listAdapter;
    ArrayAdapter<String> listBoundAdapter;
    static ArrayList<DeviceInfo> btDevList;
    ArrayList<String> savedDevList;
    ArrayList<String> boundDevList;
    MenuItem actionProgress;
    Settings settings;
    int selectedDevices = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        newFirstStep();
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
                startActivity(new Intent(SingleRoomActivity.this, SettingsActivity.class));
                return true;
            case R.id.action_remove_room:
                Toast.makeText(getApplicationContext(),
                        "Need to implement.", Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() { this.finish(); }

    void newFirstStep(){
        setContentView(R.layout.activity_new_room_name);
        actionbar = getSupportActionBar();
        actionbar.setTitle(getText(R.string.new_room_title));
        actionbar.setSubtitle(getText(R.string.new_room_name_subtitle));
        newPavadinimas = (EditText)findViewById(R.id.editNewRoomName_Name);
        acceptBtn = (Button)findViewById(R.id.buttonNewRoomName_Set);
        setFirstAcceptListener();
    }

    void setFirstAcceptListener(){
        acceptBtn.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        if (newPavadinimas.getText().toString().equals("")) {
                            Toast.makeText(getApplicationContext(),
                                    "Neįvestas pavadinimas!", Toast.LENGTH_SHORT).show();
                        } else {
                            AllRoomsActivity.roomsList.add(newPavadinimas.getText().toString());
                            //patvirtinus ivesti, paslepiama klaviatura
                            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                            acceptBtn.clearFocus();
                            newSecondStep();
                        }
                    }
                }
        );
    }

    void newSecondStep(){
        setContentView(R.layout.activity_new_room_devices);
        actionbar.setSubtitle(getText(R.string.new_room_devices_subtitle));
        acceptBtn = (Button)findViewById(R.id.buttonNewRoomDevices_End);
        setSecondAcceptListener();

        //Bind devices code here
        createBT();
        settings = MainActivity.settings;
        btInfo = (ListView)findViewById(R.id.listNewRoom_DevicesList);
        btDevList = new ArrayList<DeviceInfo>();
        savedDevList = new ArrayList<String>();
        boundDevList = new ArrayList<String>();
        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, savedDevList);
        btInfo.setAdapter(listAdapter);
        setCustomList();
        actionProgress.setVisible(true);
        contScanStop();
    }

    void setSecondAcceptListener(){
        acceptBtn.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        if (selectedDevices > 0){
                            actionProgress.setVisible(true);
                            scanning = false;
                            existingRoom();
                        }
                        else {
                            Toast.makeText(getApplicationContext(),
                                    "Nepasirinktas nei vienas įrenginys!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    void existingRoom(){
        setContentView(R.layout.activity_single_room);
        actionbar = getSupportActionBar();
        actionbar.setTitle(getText(R.string.app_name));
        actionbar.setSubtitle(getText(R.string.existing_room));
        existingPavadinimas = (TextView)findViewById(R.id.textSingleRoom_ActiveName);
        if (!newPavadinimas.getText().toString().equals("")){
            existingPavadinimas.setText(AllRoomsActivity.roomsList.get(AllRoomsActivity.roomsList.size()-1));
        }
        //---
        boundBtList = (ListView)findViewById(R.id.listSingleRoom_DevicesList);
        listBoundAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, boundDevList);
        boundBtList.setAdapter(listBoundAdapter);
        //---
    }


    //----------
    //Sukuriamas Bluetooth adapteris
    public void createBT(){
        BluetoothManager bluetoothManager =
                (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
    }

    void setCustomList(){
        btInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // change the checkbox state
                CheckedTextView checkedTextView = ((CheckedTextView) view);
                checkedTextView.setChecked(!checkedTextView.isChecked());
                if (checkedTextView.isChecked()){
                    boundDevList.add(btDevList.get(position).getInfo());
                    selectedDevices++;
                }
                else {
                    boundDevList.remove(boundDevList.size()-1);
                    selectedDevices--;
                }
            }
        });
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
            public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                byte numDev = 0;
                byte listSize = (byte) btDevList.size();
                byte currentRssi = (byte) rssi;
                if (listSize == 0) {
                    btDevList.add(new DeviceInfo(device.getName(), device.getAddress()));
                    btDevList.get(0).setRSSI(currentRssi);
                    savedDevList.add(btDevList.get(0).getCurrentInfo(settings.getTxPow()));
                } else {
                    for (byte i = 0; i < listSize; i++) {
                        if (btDevList.get(i).getMAC().equals(device.getAddress())) {
                            btDevList.get(i).setRSSI(currentRssi);
                            savedDevList.set(i, btDevList.get(i).getCurrentInfo(settings.getTxPow()));
                        } else {
                            numDev++;
                        }
                    }
                    if (numDev > listSize - 1) {
                        btDevList.add(new DeviceInfo(device.getName(), device.getAddress()));
                        btDevList.get(numDev).setRSSI(currentRssi);
                        savedDevList.add(numDev, btDevList.get(numDev).getCurrentInfo(settings.getTxPow()));
                    }
                }
                mBluetoothAdapter.stopLeScan(this); //Scan stabdomas
            }
        });
    }
}
