package com.example.btmatuoklis.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.btmatuoklis.R;
import com.example.btmatuoklis.classes.GlobalClass;
import com.example.btmatuoklis.classes.Room;

import java.util.ArrayList;
import java.util.Collections;

public class BeaconActivity extends AppCompatActivity {

    ActionBar actionbar;
    TextView roomPavadinimas, deviceInfo, rssiList, rssiNum, rssiAverage, rssiMax, rssiMin;
    FrameLayout arrayFrame;
    GlobalClass globalVariable;
    Room currentRoom;
    ArrayList<Byte> rssiArray;
    int roomID, beaconID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon);
        actionbar = getSupportActionBar();
        actionbar.setTitle(getText(R.string.app_name));
        actionbar.setSubtitle(getText(R.string.subtitle_existing_beacon));
        globalVariable = (GlobalClass) getApplicationContext();
        roomID = getIntent().getExtras().getInt("roomID");
        beaconID = getIntent().getExtras().getInt("beaconID");
        roomPavadinimas = (TextView)findViewById(R.id.textBeacon_ActiveName);
        deviceInfo = (TextView)findViewById(R.id.textBeacon_Info);
        rssiList = (TextView)findViewById(R.id.textBeacon_ActiveArray);
        arrayFrame = (FrameLayout)findViewById(R.id.frameBeacon_RSSIArray);
        rssiNum = (TextView)findViewById(R.id.textBeacon_ActiveRSSINum);
        rssiAverage = (TextView)findViewById(R.id.textBeacon_ActiveAverage);
        rssiMax = (TextView)findViewById(R.id.textBeacon_ActiveRSSIMax);
        rssiMin = (TextView)findViewById(R.id.textBeacon_ActiveRSSIMin);
        currentRoom = globalVariable.getRoomsArray().get(roomID);
        rssiArray = currentRoom.getBeacons().get(beaconID).getCalibratedRSSI();
        roomPavadinimas.setText(currentRoom.getName());
        deviceInfo.setText(currentRoom.getBeacons().get(beaconID).getInfo());
        setRSSIArrayListener();
        setInfoValues();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar, menu);
        menu.findItem(R.id.action_remove).setTitle(getText(R.string.bartext_existing_calibration_remove));
        menu.findItem(R.id.action_remove).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(getBaseContext(), SettingsActivity.class));
                return true;
            case R.id.action_remove:
                removeBeaconConfirm();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed(){
        this.finish();
    }

    //RSSI reiksmiu vaizdo keitimas tarp vienos elutes daugelio eiluciu
    void setRSSIArrayListener(){
        arrayFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rssiList.getMaxLines() == 1) {
                    rssiList.setSingleLine(false);
                    rssiList.setMaxLines(Integer.MAX_VALUE);
                    rssiList.setEllipsize(null);
                    arrayFrame.setPressed(true);
                } else {
                    rssiList.setSingleLine(true);
                    rssiList.setMaxLines(1);
                    rssiList.setEllipsize(TextUtils.TruncateAt.END);
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
        builder4.setTitle(getText(R.string.dialog_remove_calibration));
        builder4.setIcon(android.R.drawable.ic_dialog_alert);

        builder4.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                currentRoom.getBeacons().get(beaconID).getCalibratedRSSI().clear();
                Toast.makeText(getApplicationContext(), getText(R.string.toast_info_removed), Toast.LENGTH_SHORT).show();
                BeaconActivity.this.finish();
            }
        });

        builder4.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder4.show();
    }

    void removeBeaconConfirm() {
        final AlertDialog.Builder builder5 = new AlertDialog.Builder(BeaconActivity.this);
        builder5.setTitle(getText(R.string.dialog_remove_beacon));
        builder5.setIcon(android.R.drawable.ic_dialog_alert);

        builder5.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                currentRoom.getBeacons().remove(beaconID);
                Toast.makeText(getApplicationContext(), getText(R.string.dialog_remove_calibration), Toast.LENGTH_SHORT).show();
                BeaconActivity.this.finish();
            }
        });

        builder5.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder5.show();
    }

    void setInfoValues(){
        rssiList.setText(currentRoom.getBeacons().get(beaconID).getCalibratedRSSI().toString());
        rssiNum.setText(Integer.toString(rssiArray.size()));
        rssiAverage.setText(Byte.toString(calculateAverage(rssiArray)));
        rssiMax.setText(Byte.toString(Collections.min(rssiArray)));
        rssiMin.setText(Byte.toString(Collections.max(rssiArray)));
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
