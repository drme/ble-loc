package com.example.btmatuoklis.classes;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.btmatuoklis.R;

import java.util.UUID;

public class _DebugDeviceControl {

    private Context context;
    private RoomDetector detector;
    private BluetoothGattCallback mGattCallback;
    private String mac = "68:9E:19:15:1B:ED";
    private UUID customServices = UUID.fromString("a739aa00-f6cd-1692-994a-d66d9e0ce048");
    private UUID ledCharacteristic = UUID.fromString("a739fffd-f6cd-1692-994a-d66d9e0ce048");
    byte[] enable = new byte[]{0x00, 0x01};
    byte[] disable = new byte[]{0x00, 0x00};
    byte[] command;
    private boolean available, switchDevice;
    private String enabledMsg, disabledMsg;
    private int deviceRoomIndex, prevRoomIndex;
    private Runnable showToast;

    public _DebugDeviceControl(Context context, RoomDetector detector){
        this.context = context;
        this.detector = detector;
        this.command = this.disable;
        this.available = true;
        this.switchDevice = false;
        this.enabledMsg = this.context.getString(R.string.gatt_toast_function_enabled);
        this.disabledMsg = this.context.getString(R.string.gatt_toast_function_disabled);
        //this.createNotifications();
        this.createGattCallbak();
    }

    private void createGattCallbak(){
        mGattCallback = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    if (newState == BluetoothProfile.STATE_CONNECTED){
                        available = false;
                        gatt.discoverServices();
                        Log.d("_GATT", "Discovering Services...");
                    }
                    else if (newState == BluetoothProfile.STATE_DISCONNECTED){
                        gatt.close();
                        Log.d("_GATT", "Connection Closed");
                        available = true;
                    }
                }
                else { Log.d("_GATT", "OPERATION FAILED!"); }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                BluetoothGattCharacteristic characteristic = gatt.getService(customServices).getCharacteristic(ledCharacteristic);
                if (characteristic != null){
                    characteristic.setValue(command);
                    gatt.writeCharacteristic(characteristic);
                }
                else {
                    Log.d("_GATT", "CHARACTERISTIC NOT FOUND!");
                    gatt.disconnect();
                    //continuousScan(true);
                }
            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                Log.d("_GATT", "Characteristic Write Success");
                //To-do: Display toast when function enabled/disabled
                //runOnUiThread(showToast);
                gatt.disconnect();
            }
        };
    }

    private void createNotifications(){
        this.showToast = new Runnable() {
            @Override
            public void run() { showCommand(); }
        };
    }

    private void showCommand(){
        if (command == enable){
            Toast.makeText(this.context, enabledMsg, Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this.context, disabledMsg, Toast.LENGTH_SHORT).show();
        }
    }

    public void findRoomDeviceIndex(RoomsArray created){
        this.deviceRoomIndex = created.findRoomIndex(this.mac);
    }

    public void checkDevice(RoomsArray created, RoomsArray enviroment){
        int roomIndex = this.detector.getDetectedRoomIndex(created, enviroment);
        if (this.deviceRoomIndex > -1 && roomIndex > -2 && this.prevRoomIndex != roomIndex){
            if (this.deviceRoomIndex == roomIndex){
                command = enable;
                this.switchDevice = true;
            }
            else if (this.deviceRoomIndex == this.prevRoomIndex) {
                command = disable;
                this.switchDevice = true;
            }
            else { this.switchDevice = false; }
            this.prevRoomIndex = roomIndex;
        }
        else { this.switchDevice = false; }
    }

    public void activateDevice(BluetoothAdapter mBluetoothAdapter){
        if (this.switchDevice){
            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(this.mac);
            if (device != null){
                if (available){ device.connectGatt(this.context, false, mGattCallback);}
                else { Log.d("_GATT", "UNAVAILABLE!"); }
            }
            else { Log.d("_GATT", "FAILED TO GET REMOTE DEVICE!"); }
        }
    }

}
