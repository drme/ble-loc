package com.example.btmatuoklis.classes;

import android.content.Context;
import android.os.Environment;

import com.example.btmatuoklis.R;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ExportCSVHelper {

    Context context;
    String[] columns = new String[3];

    public ExportCSVHelper(Context context){
        this.context = context;
        columns[0] = context.getString(R.string.exportcsv_column_beacon);
        columns[1] = context.getString(R.string.exportcsv_column_mac);
        columns[2] = context.getString(R.string.exportcsv_column_rssi);
    }

    public String[] exportRoomCSV(Room room) {
        String[] res = new String[2];
        res[1] = getExternalStorageDirectory(context.getString(R.string.app_name));
        File exportDir = new File(res[1], "");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
        res[0] = context.getString(R.string.exportcsv_name_calibrate)+"."+room.getName()+".csv";
        File file = new File(exportDir, res[0]);
        try {
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            ArrayList<Beacon> beacons = room.getBeacons();
            csvWrite.writeNext(columns);
            for (int i = 0; i < beacons.size(); i++){
                String arrStr[] = {beacons.get(i).getName(), beacons.get(i).getMAC(), beacons.get(i).getFullRSSI().toString()};
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    private String getExternalStorageDirectory(String folder){
        String sdpath="/storage/extSdCard/";
        String sd1path="/storage/sdcard1/";
        //String sd2path="/storage/external_SD/";
        String usbdiskpath="/storage/usbcard1/";
        String sd0path="/storage/sdcard0/";
        if(new File(sdpath).exists()) { return sdpath+folder; }
        else if(new File(sd1path).exists()) { return sd1path+folder; }
        //else if(new File(sd2path).exists()) { return sd2path+folder; }
        else if(new File(usbdiskpath).exists()) { return usbdiskpath+folder; }
        else if(new File(sd0path).exists()) { return sd0path+folder; }
        else return Environment.getExternalStorageDirectory().toString()+folder;
    }
}
