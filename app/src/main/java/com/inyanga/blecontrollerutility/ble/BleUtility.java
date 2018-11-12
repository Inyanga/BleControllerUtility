package com.inyanga.blecontrollerutility.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCallback;
import android.content.Context;
import android.content.pm.PackageManager;

import com.inyanga.blecontrollerutility.R;
import com.inyanga.blecontrollerutility.ble.callbacks.BleUtilityCallback;

/**
 * Created by Pavel Shakhtarin on 02.11.2018.
 */
public class BleUtility extends BluetoothGattCallback {

    private Context context;
    private BluetoothAdapter bluetoothAdapter;
    private BleUtilityCallback callback;




    public BleUtility(Context context, BluetoothAdapter bluetoothAdapter, BleUtilityCallback callback) {
        this.context = context;
        this.bluetoothAdapter = bluetoothAdapter;
        this.callback = callback;
    }

    public boolean initBluetooth() {
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            callback.onBleMsg(context.getResources().getString(R.string.ble_not_supported));
            return false;
        }
        if (!bluetoothAdapter.isEnabled()) {
            callback.onBluetoothEnable();
            return false;
        } else {
            return true;
        }

    }


}
