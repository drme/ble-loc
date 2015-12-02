package com.example.btmatuoklis;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.btmatuoklis.DevInfo;
import com.example.btmatuoklis.R;

import java.util.ArrayList;

public class CustomInfoAdapter extends ArrayAdapter<DevInfo> {
    public CustomInfoAdapter(Context context, ArrayList<DevInfo> devices) {
        super(context, 0, devices);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DevInfo device = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.bt_info_layout, parent, false);
        }
        // Lookup view for data population
        TextView name_value = (TextView) convertView.findViewById(R.id.name_value);
        TextView mac_value = (TextView) convertView.findViewById(R.id.mac_value);
        TextView rssi_value = (TextView) convertView.findViewById(R.id.rssi_value);
        // Populate the data into the template view using the data object
        name_value.setText(device.getName());
        mac_value.setText(device.getMac());
        rssi_value.setText(device.getRssi());
        // Return the completed view to render on screen
        return convertView;
    }
}
