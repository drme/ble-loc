package com.example.btmatuoklis.activities;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.btmatuoklis.R;
import com.example.btmatuoklis.classes.DeviceInfo;
import com.example.btmatuoklis.classes.GlobalClass;

import java.util.ArrayList;

public class SingleRoomActivity extends AppCompatActivity {

    ActionBar actionbar;
    TextView existingPavadinimas;

    ListView boundBtList;
    ArrayAdapter<String> listBoundAdapter;
    ArrayList<String> boundDevList;
    MenuItem actionProgress;

    int roomID;

    GlobalClass globalVariable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_room);
        actionbar = getSupportActionBar();
        actionbar.setTitle(getText(R.string.app_name));
        actionbar.setSubtitle(getText(R.string.existing_room));
        globalVariable = (GlobalClass) getApplicationContext();
        roomID = getIntent().getExtras().getInt("roomID");
        existingPavadinimas = (TextView)findViewById(R.id.textSingleRoom_ActiveName);
        boundBtList = (ListView)findViewById(R.id.listSingleRoom_DevicesList);
        boundDevList = new ArrayList<String>();
        listBoundAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, boundDevList);
        boundBtList.setAdapter(listBoundAdapter);
        existingPavadinimas.setText(globalVariable.getRoomsArray().get(roomID).getName());
        loadBoundDevices();
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
                startActivity(new Intent(getBaseContext(), SettingsActivity.class));
                return true;
            case R.id.action_remove_room:
                globalVariable.getRoomsArray().remove(roomID);
                globalVariable.getRoomsList().remove(roomID);
                Toast.makeText(getApplicationContext(), "Kambarys pašalintas.", Toast.LENGTH_SHORT).show();
                this.finish();
                startActivity(new Intent(getBaseContext(), AllRoomsActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() { this.finish(); }

    void loadBoundDevices(){
        ArrayList<DeviceInfo> btDevList = globalVariable.getRoomsArray().get(roomID).getDevices();
        for (int i=0; i < btDevList.size(); i++){
            boundDevList.add(btDevList.get(i).getInfo());
        }
    }

}
