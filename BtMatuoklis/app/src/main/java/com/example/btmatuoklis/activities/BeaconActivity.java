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
    TextView roomPavadinimas, deviceInfo, rssiList, rssiNum, rssiAverage, rssiMax, rssiMin;
    View arrayFrame;
    ImageView listArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon);
        getSupportActionBar().setSubtitle(getString(R.string.subtitle_existing_beacon));
        roomPavadinimas = (TextView)findViewById(R.id.textBeacon_ActiveName);
        deviceInfo = (TextView)findViewById(R.id.textBeacon_Info);
        arrayFrame = findViewById(R.id.viewBeacon_array);
        rssiList = (TextView)arrayFrame.findViewById(android.R.id.text2);
        listArrow = (ImageView)arrayFrame.findViewById(android.R.id.icon);
        rssiNum = (TextView)findViewById(R.id.textBeacon_ActiveRSSINum);
        rssiAverage = (TextView)findViewById(R.id.textBeacon_ActiveAverage);
        rssiMax = (TextView)findViewById(R.id.textBeacon_ActiveRSSIMax);
        rssiMin = (TextView)findViewById(R.id.textBeacon_ActiveRSSIMin);

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
        roomPavadinimas.setText(currentRoom.getName());
        deviceInfo.setText(currentRoom.getBeacons().get(beaconID).getInfo());
        rssiList.setText(currentRoom.getBeacons().get(beaconID).getCalibratedRSSI().toString());
        rssiNum.setText(Integer.toString(rssiArray.size()));
        rssiAverage.setText(Byte.toString(calculateAverage(rssiArray)));
        rssiMax.setText(Byte.toString(Collections.min(rssiArray)));
        rssiMin.setText(Byte.toString(Collections.max(rssiArray)));
    }

    //RSSI reiksmiu vaizdo keitimas tarp vienos elutes daugelio eiluciu
    void setRSSIArrayListener(){
        arrayFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rssiList.getMaxLines() == 1) {
                    rssiList.setSingleLine(false);
                    rssiList.setMaxLines(Integer.MAX_VALUE);
                    listArrow.setImageResource(android.R.drawable.arrow_up_float);
                    arrayFrame.setPressed(true);
                } else {
                    rssiList.setSingleLine(true);
                    rssiList.setMaxLines(1);
                    listArrow.setImageResource(android.R.drawable.arrow_down_float);
                    arrayFrame.setPressed(false);
                }
            }
        });
        arrayFrame.setOnLongClickListener(new View.OnLongClickListener() {
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
