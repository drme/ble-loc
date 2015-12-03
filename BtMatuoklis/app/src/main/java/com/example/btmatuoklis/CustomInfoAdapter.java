package com.example.btmatuoklis;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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
        rssi_value.setText(device.getRssi()+String.format(" Apytikslis atstumas: %.2f m", calculateAccuracy(ScanActivity.txPow, device.getRssi())));
        // Return the completed view to render on screen
        return convertView;
    }

    //Funkcija rasta internete
    //Veikimo principas panasus i funkcija randama iOS?
    double calculateAccuracy(int txPower, double rssi) {
        if (rssi == 0) {
            return -1.0; // if we cannot determine accuracy, return -1.
        }

        double ratio = rssi*1.0/txPower;
        if (ratio < 1.0) {
            return Math.pow(ratio,10);
        }
        else {
            double accuracy =  (0.89976)*Math.pow(ratio,7.7095) + 0.111;
            return accuracy;
        }
    }
}
