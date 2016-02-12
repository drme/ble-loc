package com.example.btmatuoklis.activities;

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

import com.example.btmatuoklis.R;
import com.example.btmatuoklis.classes.GlobalClass;
import com.example.btmatuoklis.classes.Room;

import java.util.ArrayList;
import java.util.Collections;

public class SingleDeviceActivity extends AppCompatActivity {

    ActionBar actionbar;
    TextView roomPavadinimas, deviceInfo, rssiList, rssiNum, rssiAverage, rssiMax, rssiMin;
    FrameLayout arrayFrame;
    GlobalClass globalVariable;
    Room currentRoom;
    ArrayList<Byte> rssiArray;
    int roomID, deviceID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_device);
        actionbar = getSupportActionBar();
        actionbar.setTitle(getText(R.string.app_name));
        actionbar.setSubtitle(getText(R.string.existing_device));
        globalVariable = (GlobalClass) getApplicationContext();
        roomID = getIntent().getExtras().getInt("roomID");
        deviceID = getIntent().getExtras().getInt("deviceID");
        roomPavadinimas = (TextView)findViewById(R.id.textSingleDevice_ActiveName);
        deviceInfo = (TextView)findViewById(R.id.textSingleDevice_Info);
        rssiList = (TextView)findViewById(R.id.textSingleDevice_ActiveArray);
        arrayFrame = (FrameLayout)findViewById(R.id.frameSingleDevice_RSSIArray);
        rssiNum = (TextView)findViewById(R.id.textSingleDevice_ActiveRSSINum);
        rssiAverage = (TextView)findViewById(R.id.textSingleDevice_ActiveAverage);
        rssiMax = (TextView)findViewById(R.id.textSingleDevice_ActiveRSSIMax);
        rssiMin = (TextView)findViewById(R.id.textSingleDevice_ActiveRSSIMin);
        currentRoom = globalVariable.getRoomsArray().get(roomID);
        rssiArray = currentRoom.getDevices().get(deviceID).getCalibratedRSSI();
        roomPavadinimas.setText(currentRoom.getName());
        deviceInfo.setText(currentRoom.getDevices().get(deviceID).getInfo());
        rssiList.setText(currentRoom.getDevices().get(deviceID).getCalibratedRSSI().toString());
        setRSSIArrayListener();
        setInfoValues();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(getBaseContext(), SettingsActivity.class));
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
                } else {
                    rssiList.setSingleLine(true);
                    rssiList.setMaxLines(1);
                    rssiList.setEllipsize(TextUtils.TruncateAt.END);
                }
            }
        });
    }

    void setInfoValues(){
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
