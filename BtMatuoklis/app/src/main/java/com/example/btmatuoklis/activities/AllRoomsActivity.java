package com.example.btmatuoklis.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.btmatuoklis.R;
import com.example.btmatuoklis.classes.GlobalClass;
import com.example.btmatuoklis.classes.Room;

import java.util.ArrayList;

public class AllRoomsActivity extends Activity {

    GlobalClass globalVariable;
    ListView displayRoomsList;
    ArrayAdapter listAdapter;
    ArrayList<Room> roomsArray;
    ArrayList<String> savedRoomsList;
    String roomName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_rooms);
        getActionBar().setSubtitle(getString(R.string.subtitle_all_rooms));
        displayRoomsList = (ListView)findViewById(R.id.listAllRooms_BeaconsList);

        setDefaultValues();
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
    public void onBackPressed() { this.finish(); }

    public void onAddActionClick(MenuItem item){
        roomNameEntry();
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

    void roomNameEntry(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(AllRoomsActivity.this, AlertDialog.THEME_HOLO_DARK);
        builder.setTitle(getString(R.string.dialog_title_new_room));
        builder.setMessage(getString(R.string.dialog_new_room_name));
        builder.setIcon(android.R.drawable.ic_dialog_info);

        final EditText input = new EditText(AllRoomsActivity.this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setTextColor(Color.WHITE);
        builder.setView(input);

        builder.setPositiveButton(getString(R.string.dialog_button_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                roomName = input.getText().toString();
                if (roomName.equals("")) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.toast_warning_empty_entry), Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                } else {
                    Intent intent = new Intent(getBaseContext(), NewRoomActivity.class);
                    intent.putExtra("roomName", roomName);
                    AllRoomsActivity.this.finish();
                    startActivity(intent);
                }
            }
        });

        builder.setNegativeButton(getString(R.string.dialog_button_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count){}

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                } else {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }
            }
        });
    }

    void removeAllRoomsConfirm() {
        final AlertDialog.Builder builder1 = new AlertDialog.Builder(AllRoomsActivity.this, AlertDialog.THEME_HOLO_DARK);
        builder1.setTitle(getString(R.string.dialog_title_remove));
        builder1.setMessage(getString(R.string.dialog_remove_all_rooms));
        builder1.setIcon(android.R.drawable.ic_dialog_alert);

        builder1.setPositiveButton(getString(R.string.dialog_button_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                roomsArray.clear();
                savedRoomsList.clear();
                listAdapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(),
                        getString(R.string.toast_info_removed), Toast.LENGTH_SHORT).show();
            }
        });

        builder1.setNegativeButton(getString(R.string.dialog_button_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder1.show();
    }
}
