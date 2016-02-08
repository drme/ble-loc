package com.example.btmatuoklis.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.btmatuoklis.R;
import com.example.btmatuoklis.classes.Settings;

public class MainActivity extends AppCompatActivity {

    public static Settings settings;
    ActionBar actionbar;
    Button scan, allRoomsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        actionbar = getSupportActionBar();
        actionbar.setSubtitle(getText(R.string.main_subtitle));
        Context context = getApplicationContext();
        settings = new Settings(context);
        scan = (Button)findViewById(R.id.buttonMain_Scan);
        allRoomsBtn = (Button)findViewById(R.id.buttonMain_AllRooms);
        setScanListener();
        setAllRoomsListener();
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

    void setScanListener(){
        scan.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        startActivity(new Intent(getBaseContext(), ScanActivity.class));
                    }
                }
        );
    }

    void setAllRoomsListener(){
        allRoomsBtn.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        startActivity(new Intent(getBaseContext(), AllRoomsActivity.class));
                    }
                }
        );
    }
}