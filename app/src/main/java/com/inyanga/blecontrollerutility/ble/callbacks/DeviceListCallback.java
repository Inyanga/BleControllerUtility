package com.inyanga.blecontrollerutility.ble.callbacks;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Pavel Shakhtarin on 04.11.2018.
 */
public interface DeviceListCallback {
    public void onDeviceFound(BluetoothDevice device);
}
