package com.example.btmatuoklis.classes;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.btmatuoklis.R;

import java.util.ArrayList;
import java.util.UUID;

public class _DebugDeviceControl {

    private Context context;
    private BluetoothGattCallback mGattCallback;
    private UUID customServices = UUID.fromString("a739aa00-f6cd-1692-994a-d66d9e0ce048");
    private UUID ledCharacteristic = UUID.fromString("a739fffd-f6cd-1692-994a-d66d9e0ce048");
    private byte[] enable = new byte[]{0x00, 0x01};
    private byte[] disable = new byte[]{0x00, 0x00};
    private byte[] command;
    private boolean available, switchDevice;
    private String enablingMsg, disablingMsg, enabledMsg, disabledMsg;
    private ArrayList<String> roomDeviceMACs, prevDeviceMACs;
    private int prevRoomIndex;
    private Handler handler;
    private Runnable toastPreparing, toastCompleted;

    public _DebugDeviceControl(Context context){
        this.context = context;
        this.command = this.disable;
        this.available = true;
        this.switchDevice = false;
        this.enablingMsg = this.context.getString(R.string.gatt_toast_function_enabling);
        this.disablingMsg = this.context.getString(R.string.gatt_toast_function_disabling);
        this.enabledMsg = this.context.getString(R.string.gatt_toast_function_enabled);
        this.disabledMsg = this.context.getString(R.string.gatt_toast_function_disabled);
        this.roomDeviceMACs = new ArrayList<String>();
        this.prevDeviceMACs = new ArrayList<String>();
        this.prevRoomIndex = -2;
        this.createGattCallback();
        this.createNotifications();
    }

    private void createGattCallback(){
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
                        available = true;
                        gatt.close();
                        Log.d("_GATT", "Connection Closed");
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
                }
            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                Log.d("_GATT", "Characteristic Write Success");
                handler.post(toastCompleted);
                gatt.disconnect();
            }
        };
    }

    private void createNotifications(){
        this.toastPreparing = new Runnable() {
            @Override
            public void run() { showPreparingAction(); }
        };
        this.toastCompleted = new Runnable() {
            @Override
            public void run() { showCompletedAction(); }
        };
    }

    private void showCompletedAction(){
        if (command == enable){
            Toast.makeText(this.context, enabledMsg, Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this.context, disabledMsg, Toast.LENGTH_SHORT).show();
        }
    }

    private void showPreparingAction(){
        if (command == enable){
            Toast.makeText(this.context, enablingMsg, Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this.context, disablingMsg, Toast.LENGTH_SHORT).show();
        }
    }

    public void checkDevices(RoomsArray created, int index){
        if (index > -2 && this.prevRoomIndex != index){
            this.prevDeviceMACs.clear();
            this.prevDeviceMACs.addAll(this.roomDeviceMACs);
            if (index == -1){ this.roomDeviceMACs.clear(); }
            else { this.roomDeviceMACs = created.getArray().get(index)._getDevicesMACList(); }
            if (!this.roomDeviceMACs.isEmpty()){
                this.command = this.enable;
                this.switchDevice = true;
            }
            else if (this.roomDeviceMACs.isEmpty() && !this.prevDeviceMACs.isEmpty()){
                this.command = this.disable;
                this.switchDevice = true;
            }
            else { this.switchDevice = false; }
            this.prevRoomIndex = index;
        }
        else { this.switchDevice = false; }
    }

    public void activateDevice(Handler handler, BluetoothAdapter mBluetoothAdapter){
        if (this.switchDevice){
            ArrayList<String> macs;
            if (this.command == this.enable){ macs = this.roomDeviceMACs; }
            else { macs = this.prevDeviceMACs; }
            this.handler = handler;
            for (int i = 0; i < macs.size() && i < 6; i++){
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(macs.get(i));
                if (device != null){
                    if (available){
                        this.handler.post(this.toastPreparing);
                        device.connectGatt(this.context, false, mGattCallback);
                    }
                    else { Log.d("_GATT", "UNAVAILABLE!"); }
                }
                else { Log.d("_GATT", "FAILED TO GET REMOTE DEVICE!"); }
            }
        }
    }

}
