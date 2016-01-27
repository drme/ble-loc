package com.example.btmatuoklis.classes;

import android.content.res.Resources;

import com.example.btmatuoklis.R;

public class RangeCalculator {

    private static byte maxRange;

    public RangeCalculator() {}

    public static String getRange(byte txPower, byte rssi){
        maxRange = Settings.maxRange;
        //String textInfo = Resources.getSystem().getString(R.string.device_range);
        String textInfo = "Apytikslis atstumas: ";
        float val = calculateAccuracy(txPower, rssi);
        if (val < maxRange)
            return String.format(textInfo+"%.2f m", val);
        else return (textInfo+">"+maxRange+" m");
    }

    //Funkcija rasta internete
    //Veikimo principas panasus i funkcija randama iOS?
    private static float calculateAccuracy(byte txPower, float rssi) {
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
}
