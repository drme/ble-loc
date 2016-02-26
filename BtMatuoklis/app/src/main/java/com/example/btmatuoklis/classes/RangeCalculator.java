package com.example.btmatuoklis.classes;

import com.example.btmatuoklis.activities.MainActivity;

public class RangeCalculator {

    public RangeCalculator() {}

    public String getRange(byte txPower, byte rssi){
        byte maxRange = getMaxRange();
        String textInfo = "Apytikslis atstumas: ";
        float val = calculateAccuracy(txPower, rssi);
        if (val < maxRange)
            return String.format(textInfo+"%.2f m", val);
        else return (textInfo+">"+maxRange+" m");
    }

    private byte getMaxRange(){
        Settings settings = MainActivity.settings;
        return settings.getMaxRange();
    }

    //Funkcija rasta internete
    //Veikimo principas panasus i funkcija randama iOS?
    private float calculateAccuracy(byte txPower, float rssi) {
        if (rssi == 0) { return -1.0f; }
        float ratio = rssi*1.0f/txPower;
        if (ratio < 1.0) { return (float)Math.pow(ratio,10); }
        else { float accuracy =  (0.89976f)*(float)Math.pow(ratio,7.7095f) + 0.111f;
            return accuracy;
        }
    }
}
