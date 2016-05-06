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
import com.example.btmatuoklis.classes.Room;
import com.example.btmatuoklis.helpers.DialogBuildHelper;
import com.example.btmatuoklis.classes.GlobalClass;
import com.example.btmatuoklis.helpers.MySQLiteHelper;
import com.example.btmatuoklis.classes.RoomsArray;

import java.util.ArrayList;

public class AllRoomsActivity extends Activity {

    GlobalClass globalVariable;
    ListView displayRoomsList;
    ArrayAdapter<String> listAdapter;
    RoomsArray roomsArray;
    ArrayList<String> savedRoomsList;
    DialogBuildHelper entryDialog;
    String room_key;

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
        savedRoomsList.clear();
        savedRoomsList.addAll(roomsArray.getNameList());
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

    public void onSettingsActionClick(MenuItem item){
        startActivity(new Intent(getBaseContext(), SettingsActivity.class));
    }

    void setListListener(){
        displayRoomsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), RoomActivity.class);
                intent.putExtra(room_key, position);
                startActivity(intent);
            }
        });
    }

    void setDefaultValues(){
        globalVariable = (GlobalClass) getApplicationContext();
        roomsArray = globalVariable.getRoomsArray();
        savedRoomsList = roomsArray.getNameList();
        room_key = getString(R.string.activity_key_room);
        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, savedRoomsList);
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
        String name = dialog.getInputText().trim();
        if (name.equals("")) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.toast_warning_empty_entry), Toast.LENGTH_SHORT).show();
            dialog.cancelInput();
        } else if (savedRoomsList.contains(name)) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.toast_warning_duplicate_entry), Toast.LENGTH_SHORT).show();
        }
        else {
            Room room = createRoom(name);
            createRoomInDatabase(room);
            notifyCreatedRoom(name);
            this.onResume();
        }
    }

    Room createRoom(String name){
        globalVariable.getRoomsArray().getArray().add(new Room(name));
        int roomIndex = globalVariable.getRoomsArray().getArray().size() - 1;
        return globalVariable.getRoomsArray().getArray().get(roomIndex);
    }

    void createRoomInDatabase(Room room){
        MySQLiteHelper database = new MySQLiteHelper(this);
        database.addRoom(room);
        room.setID(database.getLastRoomID());
    }

    void notifyCreatedRoom(String name){
        Toast.makeText(getApplicationContext(), getString(R.string.toast_info_created_room1)+
                        " \""+name+"\" "+getString(R.string.toast_info_created_room2),
                Toast.LENGTH_SHORT).show();
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
