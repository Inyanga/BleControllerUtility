package com.inyanga.blecontrollerutility.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Handler;

import com.inyanga.blecontrollerutility.ble.callbacks.BleScannerCallback;

public class BleScanner {

    private static final int SCAN_MAX_TIME = 5000;

    private BleScannerCallback scannerUiCallback;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner btScanner;

    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            scannerUiCallback.onDeviceFound(result);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };


    public BleScanner(BleScannerCallback scannerUiCallback, BluetoothAdapter bluetoothAdapter) {
        this.scannerUiCallback = scannerUiCallback;
        this.bluetoothAdapter = bluetoothAdapter;
    }

    public void startScan() {
        btScanner = bluetoothAdapter.getBluetoothLeScanner();
//        List<ScanFilter> filters = new ArrayList<>();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopScan();
            }
        }, SCAN_MAX_TIME);

//        ScanFilter scanServiceFilter = new ScanFilter.Builder().setDeviceName(FILTER_NAME).build();
//        filters.add(scanServiceFilter);
        ScanSettings scanSettings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
        btScanner.startScan(null, scanSettings, scanCallback);
        scannerUiCallback.onScan(true);

    }

    public void stopScan() {
        btScanner.stopScan(scanCallback);
        scannerUiCallback.onScan(false);
    }

}
