package com.example.btmatuoklis.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
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
import com.example.btmatuoklis.helpers.DialogBuildHelper;
import com.example.btmatuoklis.classes.Beacon;
import com.example.btmatuoklis.classes.Calibration;
import com.example.btmatuoklis.classes.GlobalClass;
import com.example.btmatuoklis.helpers.MySQLiteHelper;
import com.example.btmatuoklis.classes.Room;
import com.example.btmatuoklis.classes.ScanTools;
import com.example.btmatuoklis.classes.Settings;

import java.util.ArrayList;

public class NewRoomActivity extends Activity {

    GlobalClass globalVariable;
    int roomID;
    Settings settings;
    ScanTools scantools;
    Room environment, currentRoom;
    MySQLiteHelper database;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothAdapter.LeScanCallback mLeScanCallback;
    Handler handler;
    Runnable background;
    String roomName;
    ListView displayBeaconsList;
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
        checkBT();
        createBTLECallBack();
        createThread();
        continuousScan(true);
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
        continuousScan(false);
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
                    int checkIndex = selectedBeacons.indexOf(position);
                    if (!(checkIndex == -1)) {
                        selectedBeacons.remove(checkIndex);
                    }
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
        continuousScan(false);
        Toast.makeText(getApplicationContext(),
                getString(R.string.toast_info_cancelled), Toast.LENGTH_SHORT).show();
        NewRoomActivity.this.finish();
        startActivity(new Intent(getBaseContext(), AllRoomsActivity.class));
    }

    void cancelCreationConfirm(){
        DialogBuildHelper dialog = new DialogBuildHelper(NewRoomActivity.this, getString(R.string.dialog_title_cancel),
                getString(R.string.dialog_cancel_room_creation), android.R.drawable.ic_dialog_alert);
        dialog.getBuilder().setPositiveButton(getString(R.string.dialog_button_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cancelCreation();
            }
        });
        dialog.setNegative(getString(R.string.dialog_button_cancel));
        dialog.showDialog();
    }

    void createRoom(){
        globalVariable.getRoomsArray().getArray().add(new Room(roomName));
        globalVariable.getRoomsList().add(roomName);
        roomID = globalVariable.getRoomsArray().getArray().size() - 1;
        currentRoom = globalVariable.getRoomsArray().getArray().get(roomID);
    }

    void saveSelectedBeacons(){
        createRoomInDatabase();
        for (int i = 0; i < selectedBeacons.size(); i++){
            Beacon beacon = environment.getBeacons().get(selectedBeacons.get(i));
            currentRoom.getBeacons().add(new Beacon(beacon.getName(), beacon.getMAC()));
            saveBeaconsInDatabase(currentRoom.getBeacons().get(i));
        }
        notifyCreatedRoomAndBeacons();
    }

    void createRoomInDatabase(){
        MySQLiteHelper database = new MySQLiteHelper(this);
        database.addRoom(currentRoom);
        currentRoom.setID(database.getLastRoomID());
    }

    void saveBeaconsInDatabase(Beacon beacon){
        MySQLiteHelper database = new MySQLiteHelper(this);
        database.addBeacon(beacon);
        int id = database.getLastBeaconID();
        beacon.setID(id);
        database.addCalibration(new Calibration(currentRoom.getID(), id, null));
    }

    void notifyCreatedRoomAndBeacons(){
        Toast.makeText(getApplicationContext(), getString(R.string.toast_info_created_room1)+
                " \""+roomName+"\" "+getString(R.string.toast_info_created_room2)+"\n"+
                getString(R.string.toast_info_created_room3)+" "+ currentRoom.getBeacons().size(),
                Toast.LENGTH_SHORT).show();
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

    void createBTLECallBack(){
        mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                scantools.assignLogic(device, rssi, environment);
                mBluetoothAdapter.stopLeScan(this); //Scan stabdomas
            }
        };
    }

    void createThread(){
        handler = new Handler();
        //Background Runnable:
        //nustatytu intervalu vykdo scan
        background = new Runnable() {
            @Override
            public void run() {
                if (globalVariable.isScanning()) {
                    new ScanTask().execute();
                    handler.postDelayed(this, settings.getFrequency());
                }
                else { Thread.currentThread().interrupt(); }
            }
        };
    }

    //Nuolatos pradedamas ir stabdomas scan
    void continuousScan(boolean enable){
        globalVariable.setScanning(enable);
        if (enable){ new Thread(background).start(); }
    }

    private class ScanTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            if (!settings.isGeneratorEnabled()) {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            }
            else{
                scantools.fakeAssignLogic(settings.getDebugBeacons(), settings.getDebugRSSIMin(),
                        settings.getDebugRSSIMax(), environment);
            }
            return true;
        }
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            beaconsList.clear();
            beaconsList.addAll(environment.getCurrentInfoList());
            listAdapter.notifyDataSetChanged();
        }
    }
}
