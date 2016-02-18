package com.example.btmatuoklis.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.btmatuoklis.R;
import com.example.btmatuoklis.classes.GlobalClass;
import com.example.btmatuoklis.classes.Room;

import java.util.ArrayList;
import java.util.Collections;

public class BeaconActivity extends AppCompatActivity {

    GlobalClass globalVariable;
    int roomID, beaconID;
    Room currentRoom;
    ArrayList<Byte> rssiArray;
    TextView displayRoomName, displayBeacon, displayRSSIList;
    TextView displayRSSINum, displayRSSIAverage, displayRSSIMax, displayRSSIMin;
    View displayArrayFrame;
    ImageView displayArrayArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon);
        getSupportActionBar().setSubtitle(getString(R.string.subtitle_existing_beacon));
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
        rssiArray = currentRoom.getBeacons().get(beaconID).getCalibratedRSSI();
        displayRoomName.setText(currentRoom.getName());
        displayBeacon.setText(currentRoom.getBeacons().get(beaconID).getInfo());
        displayRSSIList.setText(currentRoom.getBeacons().get(beaconID).getCalibratedRSSI().toString());
        displayRSSINum.setText(Integer.toString(rssiArray.size()));
        displayRSSIAverage.setText(Byte.toString(calculateAverage(rssiArray)));
        displayRSSIMax.setText(Byte.toString(Collections.max(rssiArray)));
        displayRSSIMin.setText(Byte.toString(Collections.min(rssiArray)));
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

    void removeCalibrationConfirm() {
        final AlertDialog.Builder builder4 = new AlertDialog.Builder(BeaconActivity.this);
        builder4.setTitle(getString(R.string.dialog_remove_calibration));
        builder4.setIcon(android.R.drawable.ic_dialog_alert);

        builder4.setPositiveButton(getString(R.string.dialog_button_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                currentRoom.getBeacons().get(beaconID).getCalibratedRSSI().clear();
                Toast.makeText(getApplicationContext(), getString(R.string.toast_info_removed), Toast.LENGTH_SHORT).show();
                BeaconActivity.this.finish();
            }
        });

        builder4.setNegativeButton(getString(R.string.dialog_button_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder4.show();
    }

    void removeBeaconConfirm() {
        final AlertDialog.Builder builder5 = new AlertDialog.Builder(BeaconActivity.this);
        builder5.setTitle(getString(R.string.dialog_remove_beacon));
        builder5.setIcon(android.R.drawable.ic_dialog_alert);

        builder5.setPositiveButton(getString(R.string.dialog_button_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                currentRoom.getBeacons().remove(beaconID);
                Toast.makeText(getApplicationContext(), getString(R.string.toast_info_removed), Toast.LENGTH_SHORT).show();
                BeaconActivity.this.finish();
            }
        });

        builder5.setNegativeButton(getString(R.string.dialog_button_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder5.show();
    }

    private byte calculateAverage(ArrayList<Byte> array){
        int sum = 0;
        int size = array.size();
        if(!array.isEmpty()){
            for (int i = 0; i < size; i++){
                sum += array.get(i);
            }
            return (byte)(sum/size);
        }
        return (byte)sum;
    }
}
