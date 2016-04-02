package com.example.btmatuoklis.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.btmatuoklis.R;
import com.example.btmatuoklis.helpers.DialogBuildHelper;
import com.example.btmatuoklis.classes.GlobalClass;
import com.example.btmatuoklis.helpers.MySQLiteHelper;
import com.example.btmatuoklis.classes.RoomsArray;

import java.util.ArrayList;

public class AllRoomsActivity extends Activity {

    GlobalClass globalVariable;
    ListView displayRoomsList;
    ArrayAdapter listAdapter;
    RoomsArray roomsArray;
    ArrayList<String> savedRoomsList;
    String roomName;
    DialogBuildHelper entryDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_rooms);
        getActionBar().setSubtitle(getString(R.string.subtitle_all_rooms));
        displayRoomsList = (ListView)findViewById(R.id.listAllRooms_BeaconsList);

        setDefaultValues();
        setListListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_allrooms, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() { this.finish(); }

    public void onAddActionClick(MenuItem item){
        roomNameEntryConfirm();
    }

    public void onRemoveActionClick(MenuItem item){
        removeAllRoomsConfirm();
    }

    public void onHelpActionClick(MenuItem item){
        //Work in progress
        Toast.makeText(getApplicationContext(), "Not implemented.", Toast.LENGTH_SHORT).show();
    }

    public void onSettingsActionClick(MenuItem item){
        startActivity(new Intent(getBaseContext(), SettingsActivity.class));
    }

    void setListListener(){
        displayRoomsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), RoomActivity.class);
                intent.putExtra("roomID", position);
                startActivity(intent);
            }
        });
    }

    void setDefaultValues(){
        globalVariable = (GlobalClass) getApplicationContext();
        savedRoomsList = new ArrayList<String>();
        roomsArray = globalVariable.getRoomsArray();
        savedRoomsList = globalVariable.getRoomsList();
        listAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, savedRoomsList);
        displayRoomsList.setAdapter(listAdapter);
    }

    void roomNameEntryConfirm(){
        DialogBuildHelper dialog = new DialogBuildHelper(AllRoomsActivity.this, getString(R.string.dialog_title_new_room),
                getString(R.string.dialog_new_room_name), android.R.drawable.ic_dialog_info);
        entryDialog = dialog;
        dialog.setInput();
        dialog.getBuilder().setPositiveButton(getString(R.string.dialog_button_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                roomNameEntry(entryDialog); }
        });
        dialog.setNegative(getString(R.string.dialog_button_cancel));
        dialog.showDialog();
        dialog.setInputListener();
    }

    void roomNameEntry(DialogBuildHelper dialog){
        roomName = dialog.getInputText().trim();
        if (roomName.equals("")) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.toast_warning_empty_entry), Toast.LENGTH_SHORT).show();
            dialog.cancelInput();
        } else if (savedRoomsList.contains(roomName)) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.toast_warning_duplicate_entry), Toast.LENGTH_SHORT).show();
        }
        else {
            Intent intent = new Intent(getBaseContext(), NewRoomActivity.class);
            intent.putExtra("roomName", roomName);
            AllRoomsActivity.this.finish();
            startActivity(intent);
        }
    }

    void removeAllRoomsConfirm() {
        DialogBuildHelper dialog = new DialogBuildHelper(AllRoomsActivity.this, getString(R.string.dialog_title_remove),
                getString(R.string.dialog_remove_all_rooms), android.R.drawable.ic_dialog_alert);
        dialog.getBuilder().setPositiveButton(getString(R.string.dialog_button_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { removeAllRooms(); }});
        dialog.setNegative(getString(R.string.dialog_button_cancel));
        dialog.showDialog();
    }

    void removeAllRooms(){
        MySQLiteHelper database = new MySQLiteHelper(this);
        database.clearDB();
        roomsArray.getArray().clear();
        savedRoomsList.clear();
        listAdapter.notifyDataSetChanged();
        Toast.makeText(getApplicationContext(),
                getString(R.string.toast_info_removed), Toast.LENGTH_SHORT).show();
    }
}
