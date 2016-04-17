package com.example.btmatuoklis.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.btmatuoklis.R;
import com.example.btmatuoklis.classes.Settings;
import com.example.btmatuoklis.helpers.BeaconInfoHelper;
import com.example.btmatuoklis.helpers.DialogBuildHelper;
import com.example.btmatuoklis.classes.Beacon;
import com.example.btmatuoklis.helpers.ChartHelper;
import com.example.btmatuoklis.classes.GlobalClass;
import com.example.btmatuoklis.helpers.MySQLiteHelper;
import com.example.btmatuoklis.classes.Room;
import com.jjoe64.graphview.GraphView;

import java.util.ArrayList;
import java.util.UUID;

public class BeaconActivity extends Activity {

    GlobalClass globalVariable;
    Settings settings;
    BluetoothAdapter mBluetoothAdapter;
    int roomID, beaconID;
    Room currentRoom;
    Beacon currentBeacon;
    MySQLiteHelper database;
    ArrayList<Byte> rssiArray;
    BeaconInfoHelper infohelper;
    byte rssiMax, rssiMin, rssiAverage;
    TextView displayRoomName, displayBeacon, displayRSSIList;
    TextView displayRSSINum, displayRSSIAverage, displayRSSIMax, displayRSSIMin;
    View displayArrayFrame;
    ImageView displayArrayArrow;

    boolean toogle = true;
    BluetoothGattCallback mGattCallback;
    UUID customServices = UUID.fromString("a739aa00-f6cd-1692-994a-d66d9e0ce048");
    UUID ledCharacteristic = UUID.fromString("a739fffd-f6cd-1692-994a-d66d9e0ce048");
    byte[] ledEnable = new byte[]{0x00, 0x01};
    byte[] ledDisable = new byte[]{0x00, 0x00};
    byte[] command;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon);
        getActionBar().setSubtitle(getString(R.string.subtitle_existing_beacon));
        displayRoomName = (TextView)findViewById(R.id.textBeacon_ActiveName);
        displayBeacon = (TextView)findViewById(R.id.textBeacon_Info);
        displayArrayFrame = findViewById(R.id.viewBeacon_array);
        displayRSSIList = (TextView)displayArrayFrame.findViewById(android.R.id.text2);
        displayArrayArrow = (ImageView)displayArrayFrame.findViewById(android.R.id.icon);
        displayRSSINum = (TextView)findViewById(R.id.textBeacon_ActiveRSSINum);
        displayRSSIAverage = (TextView)findViewById(R.id.textBeacon_ActiveAverage);
        displayRSSIMax = (TextView)findViewById(R.id.textBeacon_ActiveRSSIMax);
        displayRSSIMin = (TextView)findViewById(R.id.textBeacon_ActiveRSSIMin);

        setDefaultValues();
        setRSSIArrayListener();
        createBT();
        checkBT();
        createGattCallbak();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_beacon, menu);
        return true;
    }

    @Override
    public void onBackPressed(){
        this.finish();
    }

    public void onRemoveActionClick(MenuItem item) {
        removeBeaconConfirm();
    }

    public void onHelpActionClick(MenuItem item){
        //Toast.makeText(getApplicationContext(), "Not implemented.", Toast.LENGTH_SHORT).show();
        toggleLED();
    }

    public void onSettingsActionClick(MenuItem item){
        startActivity(new Intent(getBaseContext(), SettingsActivity.class));
    }

    void setDefaultValues(){
        globalVariable = (GlobalClass) getApplicationContext();
        roomID = getIntent().getExtras().getInt("roomID");
        beaconID = getIntent().getExtras().getInt("beaconID");
        currentRoom = globalVariable.getRoomsArray().getArray().get(roomID);
        currentBeacon = currentRoom.getBeacons().get(beaconID);
        database = new MySQLiteHelper(this);
        infohelper = new BeaconInfoHelper(this);
        rssiArray = currentBeacon.getFullRSSI();
        rssiMax = currentBeacon.getRSSIMax();
        rssiMin = currentBeacon.getRSSIMin();
        rssiAverage = currentBeacon.getRSSIAverage();
        displayRoomName.setText(currentRoom.getName());
        displayBeacon.setText(infohelper.getInfo(currentBeacon));
        displayRSSIList.setText(currentBeacon.getFullRSSI().toString());
        displayRSSINum.setText(Integer.toString(rssiArray.size()));
        displayRSSIAverage.setText(Byte.toString(rssiAverage));
        displayRSSIMax.setText(Byte.toString(rssiMax));
        displayRSSIMin.setText(Byte.toString(rssiMin));
        setChart(R.id.viewBeacon_chart1, currentBeacon);
    }

    //RSSI reiksmiu vaizdo keitimas tarp vienos elutes ir daugelio eiluciu
    void setRSSIArrayListener(){
        displayArrayFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (displayRSSIList.getMaxLines() == 1) {
                    displayRSSIList.setSingleLine(false);
                    displayRSSIList.setMaxLines(Integer.MAX_VALUE);
                    displayArrayArrow.setImageResource(android.R.drawable.arrow_up_float);
                    displayArrayFrame.setPressed(true);
                } else {
                    displayRSSIList.setSingleLine(true);
                    displayRSSIList.setMaxLines(1);
                    displayArrayArrow.setImageResource(android.R.drawable.arrow_down_float);
                    displayArrayFrame.setPressed(false);
                }
            }
        });
        displayArrayFrame.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                removeCalibrationConfirm();
                return false;
            }
        });
    }

    public void createBT(){
        BluetoothManager bluetoothManager =
                (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
    }

    void checkBT(){
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, settings.REQUEST_ENABLE_BT);
        }
    }

    void createGattCallbak(){
        mGattCallback = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
                    gatt.discoverServices();
                } else if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_DISCONNECTED) {
                    gatt.close();
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                BluetoothGattCharacteristic characteristic = gatt.getService(customServices).getCharacteristic(ledCharacteristic);
                if (characteristic != null){
                    characteristic.setValue(command);
                    boolean res = gatt.writeCharacteristic(characteristic);
                    if (res) { Log.d("GATT Characteristic", "write Success"); }
                    else { Log.d("GATT Characteristic", "write FAILED!"); }
                }
                else {
                    Log.d("GATT Characteristic", "Not Found!");
                    gatt.disconnect();
                }
            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                gatt.disconnect();
            }
        };
    }

    void toggleLED(){
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(currentBeacon.getMAC());
        if (device != null){
            if (toogle){
                command = ledEnable;
                Toast.makeText(getApplicationContext(), "LED ON", Toast.LENGTH_SHORT).show();
            }
            else {
                command = ledDisable;
                Toast.makeText(getApplicationContext(), "LED OFF", Toast.LENGTH_SHORT).show();
            }
            this.toogle = !toogle;
            device.connectGatt(this, false, mGattCallback);
        }
        else { Log.d("GATT Connect", "FAILED!"); }
    }

    void removeCalibration(){
        database.updateCalibration(currentRoom.getID(), currentBeacon.getID(), null);
        currentBeacon.getFullRSSI().clear();
        Toast.makeText(getApplicationContext(), getString(R.string.toast_info_removed), Toast.LENGTH_SHORT).show();
        BeaconActivity.this.finish();
    }

    void removeCalibrationConfirm() {
        DialogBuildHelper dialog = new DialogBuildHelper(BeaconActivity.this, getString(R.string.dialog_title_remove),
                getString(R.string.dialog_remove_calibration), android.R.drawable.ic_dialog_alert);
        dialog.getBuilder().setPositiveButton(getString(R.string.dialog_button_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeCalibration();
            }
        });
        dialog.setNegative(getString(R.string.dialog_button_cancel));
        dialog.showDialog();
    }

    void removeBeacon(){
        int id = database.getCalibrationID(currentRoom.getID(), currentBeacon.getID());
        database.deleteCalibration(id);
        database.deleteBeacon(currentBeacon.getID());
        currentRoom.getBeacons().remove(beaconID);
        if (currentRoom.getBeacons().isEmpty()){
            database.deleteRoom(currentRoom.getID());
            globalVariable.getRoomsArray().getArray().remove(roomID);
            if (globalVariable.getRoomsArray().getArray().isEmpty()){
                database.clearDB();
                globalVariable.getRoomsArray().getArray().clear();
            }
        }
        Toast.makeText(getApplicationContext(), getString(R.string.toast_info_removed), Toast.LENGTH_SHORT).show();
        BeaconActivity.this.finish();
    }

    void removeBeaconConfirm() {
        DialogBuildHelper dialog = new DialogBuildHelper(BeaconActivity.this, getString(R.string.dialog_title_remove),
                getString(R.string.dialog_remove_beacon), android.R.drawable.ic_dialog_alert);
        dialog.getBuilder().setPositiveButton(getString(R.string.dialog_button_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeBeacon();
            }
        });
        dialog.setNegative(getString(R.string.dialog_button_cancel));
        dialog.showDialog();
    }

    void setChart(int chartID, Beacon beacon){
        ChartHelper charthelper = new ChartHelper();
        GraphView graph = (GraphView)findViewById(chartID);
        charthelper.setFullSpacedChart(graph, beacon);
    }
}
