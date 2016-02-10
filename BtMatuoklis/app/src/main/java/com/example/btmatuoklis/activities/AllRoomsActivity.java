package com.example.btmatuoklis.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
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

public class AllRoomsActivity extends AppCompatActivity {

    ActionBar actionbar;
    ListView allRoomsList;
    ArrayAdapter roomsAdapter;
    String roomName;

    GlobalClass globalVariable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_rooms);
        actionbar = getSupportActionBar();
        actionbar.setSubtitle(getText(R.string.all_rooms_name));
        allRoomsList = (ListView)findViewById(R.id.listAllRooms_DevicesList);
        globalVariable = (GlobalClass) getApplicationContext();
        roomsAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, globalVariable.getRoomsList());
        allRoomsList.setAdapter(roomsAdapter);
        setListListener();
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
                startActivity(new Intent(getBaseContext(), SettingsActivity.class));
                return true;
            case R.id.action_add_room:
                roomNameEntry();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() { this.finish(); }

    void setListListener(){
        allRoomsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), SingleRoomActivity.class);
                intent.putExtra("roomID", position);
                startActivity(intent);
            }
        });
    }

    void roomNameEntry(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(AllRoomsActivity.this);
        builder.setTitle("Naujos patalpos pavadinimas:");

        final EditText input = new EditText(AllRoomsActivity.this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                roomName = input.getText().toString();
                if (roomName.equals("")) {
                    Toast.makeText(getApplicationContext(),
                            "Neįvestas pavadinimas!", Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                } else {
                    Intent intent = new Intent(getBaseContext(), NewRoomActivity.class);
                    intent.putExtra("roomName", roomName);
                    AllRoomsActivity.this.finish();
                    startActivity(intent);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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

}
