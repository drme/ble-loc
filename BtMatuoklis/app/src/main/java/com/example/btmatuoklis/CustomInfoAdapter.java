package com.example.btmatuoklis;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class CustomInfoAdapter extends ArrayAdapter<DevInfo> {

    byte maxRange = 100; //Maksimalus teorinis BLE aptikimo atstumas metrais
    boolean correctCoord = false;

    public CustomInfoAdapter(Context context, ArrayList<DevInfo> devices) {
        super(context, 0, devices);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DevInfo device = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.bt_info_layout, parent, false);
        }
        // Lookup view for data population
        TextView name_value = (TextView) convertView.findViewById(R.id.name_value);
        TextView mac_value = (TextView) convertView.findViewById(R.id.mac_value);
        TextView rssi_value = (TextView) convertView.findViewById(R.id.rssi_value);
        TextView coord_x = (TextView) convertView.findViewById(R.id.coord_x);
        TextView coord_y = (TextView) convertView.findViewById(R.id.coord_y);
        TextView title_x = (TextView) convertView.findViewById(R.id.textView8);
        TextView title_y = (TextView) convertView.findViewById(R.id.textView11);
        FrameLayout infoOverlay = (FrameLayout) convertView.findViewById(R.id.frame_layout);
        CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);

        setOverlayListener(position, infoOverlay, coord_x, coord_y, title_x, title_y,
                checkBox, R.id.textView7, R.id.textView8);

        // Populate the data into the template view using the data object
        name_value.setText(device.getName());
        mac_value.setText(device.getMac());
        rssi_value.setText(device.getRssi()+formatAccuracy(
                maxRange, ScanActivity.txPow, device.getRssi()));
        if (device.coordIsSet()){
            coord_x.setText(Float.toString(device.getX_val())+" m");
            coord_y.setText(Float.toString(device.getY_val())+" m");
        }
        // Return the completed view to render on screen
        return convertView;
    }

    //Pazymejus (issaugojimui) iregini, iskleidziama papildoma informacijos eilute
    void expandInfo(TextView tv1, TextView tv2, TextView tv3,
                    TextView tv4, int visib, CheckBox cb, int base){
        tv1.setVisibility(visib);
        tv2.setVisibility(visib);
        tv3.setVisibility(visib);
        tv4.setVisibility(visib);
        RelativeLayout.LayoutParams cb_p = (RelativeLayout.LayoutParams)cb.getLayoutParams();
        cb_p.addRule(RelativeLayout.ALIGN_BOTTOM, base);
        cb.setLayoutParams(cb_p);
    }

    void setOverlayListener(final int position, FrameLayout io, final TextView tv1, final TextView tv2,
                            final TextView tv3, final TextView tv4,
                            final CheckBox cb, final int base1, final int base2){
        io.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb.isChecked()) {
                    cb.setChecked(false);
                    //ScanActivity.btDevList.get(position).setCoords(0, 0);
                    expandInfo(tv1, tv2, tv3, tv4, View.GONE, cb, base1);
                } else {
                    showCoordDialog(position);
                    if (correctCoord){
                        cb.setChecked(true);
                        expandInfo(tv1, tv2, tv3, tv4, View.VISIBLE, cb, base2);
                    }
                }
            }
        });
    }

    void showCoordDialog(final int position){
        LayoutInflater layoutInflater = LayoutInflater.from(this.getContext());
        View promptView = layoutInflater.inflate(R.layout.coord_input_layout, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.getContext());
        alertDialogBuilder.setView(promptView);

        final EditText xCoordEdit = (EditText) promptView.findViewById(R.id.editText2);
        final EditText yCoordEdit = (EditText) promptView.findViewById(R.id.editText3);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (xCoordEdit.getText().toString().equals("") || yCoordEdit.getText().toString().equals("")) {
                            Toast.makeText(getContext(),
                                    "Neįvesta reikšmė!", Toast.LENGTH_SHORT).show();
                            correctCoord = false;
                        } else {
                            float value_x = Float.parseFloat(xCoordEdit.getText().toString());
                            float value_y = Float.parseFloat(yCoordEdit.getText().toString());
                            if (value_x > 100.00f || value_x < -100.00f || value_y > 100.00f || value_y < -100.00f) {
                                Toast.makeText(getContext(),
                                        "Netinkamas intervalas!", Toast.LENGTH_SHORT).show();
                                correctCoord = false;
                            } else {
                                ScanActivity.btDevList.get(position).setCoords(value_x, value_y);
                                correctCoord = true;
                            }
                        }
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    //Funkcija rasta internete
    //Veikimo principas panasus i funkcija randama iOS?
    float calculateAccuracy(byte txPower, float rssi) {
        if (rssi == 0) {
            return -1.0f; // if we cannot determine accuracy, return -1.
        }

        float ratio = rssi*1.0f/txPower;
        if (ratio < 1.0) {
            return (float)Math.pow(ratio,10);
        }
        else {
            float accuracy =  (0.89976f)*(float)Math.pow(ratio,7.7095f) + 0.111f;
            return accuracy;
        }
    }

    //Gauto rezultato formatavimas
    String formatAccuracy(byte maxRange, byte txPower, float rssi){
        float val = calculateAccuracy(txPower, rssi);
        if (val < maxRange)
            return String.format(" Apytikslis atstumas: %.2f m", val);
        else return " Apytikslis atstumas: >"+maxRange+" m";
    }
}
