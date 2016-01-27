package com.example.btmatuoklis.activities;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.btmatuoklis.R;

import java.util.ArrayList;

public class AllRoomsActivity extends AppCompatActivity {

    ActionBar actionbar;
    ListView allRoomsList;
    static ArrayList<String> roomsList;
    ArrayAdapter roomsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_rooms);
        actionbar = getSupportActionBar();
        actionbar.setSubtitle(getText(R.string.all_rooms_name));
        allRoomsList = (ListView)findViewById(R.id.listAllRooms_DevicesList);
        roomsList = new ArrayList<String>();
        roomsAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, roomsList);
        allRoomsList.setAdapter(roomsAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar, menu);
        menu.findItem(R.id.action_add_room).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(AllRoomsActivity.this, SettingsActivity.class));
                return true;
            case R.id.action_add_room:
                startActivity(new Intent(AllRoomsActivity.this, SingleRoomActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() { this.finish(); }

    @Override
    public void onResume(){
        super.onResume();
        roomsAdapter.notifyDataSetChanged();
    }

}
