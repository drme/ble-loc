package com.example.btmatuoklis.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.btmatuoklis.R;
import com.example.btmatuoklis.helpers.BeaconInfoHelper;
import com.example.btmatuoklis.helpers.DialogBuildHelper;
import com.example.btmatuoklis.classes.Beacon;
import com.example.btmatuoklis.helpers.ChartHelper;
import com.example.btmatuoklis.classes.GlobalClass;
import com.example.btmatuoklis.helpers.MySQLiteHelper;
import com.example.btmatuoklis.classes.Room;
import com.jjoe64.graphview.GraphView;

import java.util.ArrayList;

public class BeaconActivity extends Activity {

    GlobalClass globalVariable;
    int roomIndex, beaconIndex;
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
    String room_key, beacon_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon);
        getActionBar().setSubtitle(getString(R.string.subtitle_existing_beacon));
        displayRoomName = (TextView)findViewById(R.id.textBeacon_Name);
        displayBeacon = (TextView)findViewById(R.id.textBeacon_Info);
        displayArrayFrame = findViewById(R.id.viewBeacon_array);
        displayRSSIList = (TextView)displayArrayFrame.findViewById(android.R.id.text2);
        displayArrayArrow = (ImageView)displayArrayFrame.findViewById(android.R.id.icon);
        displayRSSINum = (TextView)findViewById(R.id.textBeacon_RSSINum);
        displayRSSIAverage = (TextView)findViewById(R.id.textBeacon_Average);
        displayRSSIMax = (TextView)findViewById(R.id.textBeacon_RSSIMax);
        displayRSSIMin = (TextView)findViewById(R.id.textBeacon_RSSIMin);

        setDefaultValues();
        setRSSIArrayListener();
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
        Toast.makeText(getApplicationContext(), "Not implemented.", Toast.LENGTH_SHORT).show();
    }

    public void onSettingsActionClick(MenuItem item){
        startActivity(new Intent(getBaseContext(), SettingsActivity.class));
    }

    void setDefaultValues(){
        globalVariable = (GlobalClass) getApplicationContext();
        room_key = getString(R.string.activity_key_room);
        beacon_key = getString(R.string.activity_key_beacon);
        roomIndex = getIntent().getExtras().getInt(room_key);
        beaconIndex = getIntent().getExtras().getInt(beacon_key);
        currentRoom = globalVariable.getRoomsArray().getArray().get(roomIndex);
        currentBeacon = currentRoom.getBeacons().get(beaconIndex);
        database = new MySQLiteHelper(this);
        infohelper = new BeaconInfoHelper(this);
        rssiArray = currentBeacon.getFullRSSI();
        rssiMax = currentBeacon.getRSSIMax();
        rssiMin = currentBeacon.getRSSIMin();
        rssiAverage = currentBeacon.getRSSIAverage();
        displayRoomName.setText(getString(R.string.beaconactivity_text_name)+" "+currentRoom.getName());
        displayBeacon.setText(infohelper.getInfo(currentBeacon));
        displayRSSIList.setText(currentBeacon.getFullRSSI().toString());
        displayRSSINum.setText(getString(R.string.beaconactivity_text_rssi_num)+" "+Integer.toString(rssiArray.size()));
        displayRSSIAverage.setText(getString(R.string.beaconactivity_text_rssi_average)+" "+Byte.toString(rssiAverage));
        displayRSSIMax.setText(getString(R.string.beaconactivity_text_rssi_max)+" "+Byte.toString(rssiMax));
        displayRSSIMin.setText(getString(R.string.beaconactivity_text_rssi_min)+" "+Byte.toString(rssiMin));
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
        currentRoom.getBeacons().remove(beaconIndex);
        if (currentRoom.getBeacons().isEmpty()){
            database.deleteRoom(currentRoom.getID());
            globalVariable.getRoomsArray().getArray().remove(roomIndex);
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
