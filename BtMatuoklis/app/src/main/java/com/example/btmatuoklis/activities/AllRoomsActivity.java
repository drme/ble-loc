package com.example.btmatuoklis.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
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

public class AllRoomsActivity extends AppCompatActivity {

    ListView allRoomsList;
    ArrayAdapter roomsAdapter;
    ArrayList<Room> allRoomsArray;
    ArrayList<String> allRoomsStringList;
    String roomName;

    GlobalClass globalVariable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_rooms);
        getSupportActionBar().setSubtitle(getText(R.string.subtitle_all_rooms));
        allRoomsList = (ListView)findViewById(R.id.listAllRooms_DevicesList);
        allRoomsStringList = new ArrayList<String>();
        globalVariable = (GlobalClass) getApplicationContext();
        allRoomsArray = globalVariable.getRoomsArray();
        allRoomsStringList = globalVariable.getRoomsList();
        roomsAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, allRoomsStringList);
        allRoomsList.setAdapter(roomsAdapter);
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
        allRoomsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), RoomActivity.class);
                intent.putExtra("roomID", position);
                startActivity(intent);
            }
        });
    }

    void roomNameEntry(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(AllRoomsActivity.this);
        builder.setTitle(getText(R.string.dialog_new_room_name));
        builder.setIcon(android.R.drawable.ic_dialog_info);

        final EditText input = new EditText(AllRoomsActivity.this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton(getText(R.string.dialog_button_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                roomName = input.getText().toString();
                if (roomName.equals("")) {
                    Toast.makeText(getApplicationContext(),
                            getText(R.string.toast_warning_empty_entry), Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                } else {
                    Intent intent = new Intent(getBaseContext(), NewRoomActivity.class);
                    intent.putExtra("roomName", roomName);
                    AllRoomsActivity.this.finish();
                    startActivity(intent);
                }
            }
        });

        builder.setNegativeButton(getText(R.string.dialog_button_cancel), new DialogInterface.OnClickListener() {
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
        final AlertDialog.Builder builder1 = new AlertDialog.Builder(AllRoomsActivity.this);
        builder1.setTitle(getText(R.string.dialog_remove_all_rooms));
        builder1.setIcon(android.R.drawable.ic_dialog_alert);

        builder1.setPositiveButton(getText(R.string.dialog_button_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                allRoomsArray.clear();
                allRoomsStringList.clear();
                roomsAdapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(),
                        getText(R.string.toast_info_removed), Toast.LENGTH_SHORT).show();
            }
        });

        builder1.setNegativeButton(getText(R.string.dialog_button_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder1.show();
    }
}
