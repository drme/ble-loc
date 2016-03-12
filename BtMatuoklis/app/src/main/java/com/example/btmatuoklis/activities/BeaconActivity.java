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
import com.example.btmatuoklis.classes.AlertDialogBuilder;
import com.example.btmatuoklis.classes.Beacon;
import com.example.btmatuoklis.classes.Calibration;
import com.example.btmatuoklis.classes.ChartHelper;
import com.example.btmatuoklis.classes.GlobalClass;
import com.example.btmatuoklis.classes.MySQLiteHelper;
import com.example.btmatuoklis.classes.Room;

import java.util.ArrayList;

public class BeaconActivity extends Activity {

    GlobalClass globalVariable;
    int roomID, beaconID;
    Room currentRoom;
    Beacon currentBeacon;
    MySQLiteHelper database;
    ArrayList<Byte> rssiArray;
    byte rssiMax, rssiMin, rssiAverage;
    TextView displayRoomName, displayBeacon, displayRSSIList;
    TextView displayRSSINum, displayRSSIAverage, displayRSSIMax, displayRSSIMin;
    View displayArrayFrame;
    ImageView displayArrayArrow;

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_beacon, menu);
        return true;
    }

    @Override
    public void onBackPressed(){
        this.finish();
    }

    public void onRemoveActionClick(MenuItem item){
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
        roomID = getIntent().getExtras().getInt("roomID");
        beaconID = getIntent().getExtras().getInt("beaconID");
        currentRoom = globalVariable.getRoomsArray().get(roomID);
        currentBeacon = currentRoom.getBeacons().get(beaconID);
        database = new MySQLiteHelper(this);
        rssiArray = currentBeacon.getFullRSSI();
        rssiMax = currentBeacon.getRSSIMax();
        rssiMin = currentBeacon.getRSSIMin();
        rssiAverage = currentBeacon.getRSSIAverage();
        displayRoomName.setText(currentRoom.getName());
        displayBeacon.setText(currentBeacon.getInfo(""));
        displayRSSIList.setText(currentBeacon.getFullRSSI().toString());
        displayRSSINum.setText(Integer.toString(rssiArray.size()));
        displayRSSIAverage.setText(Byte.toString(rssiAverage));
        displayRSSIMax.setText(Byte.toString(rssiMax));
        displayRSSIMin.setText(Byte.toString(rssiMin));

        displayChart(R.id.viewBeacon_chart1, 0);
        displayChart(R.id.viewBeacon_chart2, 1);
    }

    //RSSI reiksmiu vaizdo keitimas tarp vienos elutes daugelio eiluciu
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
        database.updateCalibration(new Calibration(currentRoom.getID(), currentBeacon.getID(), null));
        currentBeacon.getFullRSSI().clear();
        Toast.makeText(getApplicationContext(), getString(R.string.toast_info_removed), Toast.LENGTH_SHORT).show();
        BeaconActivity.this.finish();
    }

    void removeCalibrationConfirm() {
        AlertDialogBuilder dialog = new AlertDialogBuilder(BeaconActivity.this, getString(R.string.dialog_title_remove),
                getString(R.string.dialog_remove_calibration), android.R.drawable.ic_dialog_alert);
        dialog.getBuilder().setPositiveButton(getString(R.string.dialog_button_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeCalibration();
            }
        });
        dialog.setNegatvie(getString(R.string.dialog_button_cancel));
        dialog.showDialog();
    }

    void removeBeacon(){
        int id = database.getCalibrationID(new Calibration(currentRoom.getID(), currentBeacon.getID()));
        database.deleteCalibration(id);
        database.deleteBeacon(currentBeacon.getID());
        currentRoom.getBeacons().remove(beaconID);
        if (currentRoom.getBeacons().isEmpty()){
            database.deleteRoom(currentRoom.getID());
            globalVariable.getRoomsArray().remove(roomID);
            globalVariable.getRoomsList().remove(roomID);
            if (globalVariable.getRoomsArray().isEmpty()){
                database.clearDB();
                globalVariable.getRoomsArray().clear();
                globalVariable.getRoomsList().clear();
            }
        }
        Toast.makeText(getApplicationContext(), getString(R.string.toast_info_removed), Toast.LENGTH_SHORT).show();
        BeaconActivity.this.finish();
    }

    void removeBeaconConfirm() {
        AlertDialogBuilder dialog = new AlertDialogBuilder(BeaconActivity.this, getString(R.string.dialog_title_remove),
                getString(R.string.dialog_remove_beacon), android.R.drawable.ic_dialog_alert);
        dialog.getBuilder().setPositiveButton(getString(R.string.dialog_button_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeBeacon();
            }
        });
        dialog.setNegatvie(getString(R.string.dialog_button_cancel));
        dialog.showDialog();
    }

    void displayChart(int id, int type){
        ChartHelper chart = new ChartHelper();
        switch (type) {
            case 1:
                chart.setRangeChart(this, currentBeacon, id);
                break;
            default:
                chart.setFullChart(this, currentBeacon, id);
                break;
        }
    }
}
