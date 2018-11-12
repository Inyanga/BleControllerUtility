package com.inyanga.blecontrollerutility.ble.callbacks;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;

public interface BleScannerCallback {
    void onScan(boolean start);
    public void onDeviceFound(ScanResult result);
}
