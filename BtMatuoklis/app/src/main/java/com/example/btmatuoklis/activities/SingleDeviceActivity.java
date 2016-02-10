package com.example.btmatuoklis.activities;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.btmatuoklis.R;
import com.example.btmatuoklis.classes.GlobalClass;
import com.example.btmatuoklis.classes.Room;

public class SingleDeviceActivity extends AppCompatActivity {

    ActionBar actionbar;
    TextView roomPavadinimas, deviceInfo, rssiArray;
    GlobalClass globalVariable;
    Room currentRoom;
    int roomID, deviceID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_device);
        actionbar = getSupportActionBar();
        actionbar.setTitle(getText(R.string.app_name));
        //actionbar.setSubtitle(getText(R.string.existing_room));
        actionbar.setSubtitle("Įrenginys");
        globalVariable = (GlobalClass) getApplicationContext();
        roomID = getIntent().getExtras().getInt("roomID");
        deviceID = getIntent().getExtras().getInt("deviceID");
        roomPavadinimas = (TextView)findViewById(R.id.textSingleDevice_ActiveName);
        deviceInfo = (TextView)findViewById(R.id.textSingleDevice_Info);
        rssiArray = (TextView)findViewById(R.id.textSingleDevice_ActiveArray);
        currentRoom = globalVariable.getRoomsArray().get(roomID);
        roomPavadinimas.setText(currentRoom.getName());
        deviceInfo.setText(currentRoom.getDevices().get(deviceID).getInfo());
        rssiArray.setText(currentRoom.getDevices().get(deviceID).getCalibratedRSSI().toString());
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
}
