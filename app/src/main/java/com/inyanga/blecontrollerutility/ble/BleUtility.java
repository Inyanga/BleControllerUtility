package com.inyanga.blecontrollerutility.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;

import com.inyanga.blecontrollerutility.R;
import com.inyanga.blecontrollerutility.support.Consts;

/**
 * Created by Pavel Shakhtarin on 02.11.2018.
 */
public class BleUtility extends BluetoothGattCallback {

    private Context context;
    private BluetoothAdapter bluetoothAdapter;
    private BleCallback callback;
    private BluetoothLeScanner btScanner;

    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };

    public interface BleCallback {
        void onBluetoothEnable();

        void onBleMsg(String msg);

        void onScan(boolean start);

    }

    public BleUtility(Context context, BleCallback callback) {
        this.context = context;
        this.callback = callback;
    }

    public void initBluetooth() {
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            callback.onBleMsg(context.getResources().getString(R.string.ble_not_supported));

        } else {
            final BluetoothManager bluetoothManager =
                    (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            if (bluetoothManager != null)
                bluetoothAdapter = bluetoothManager.getAdapter();
            else {
                callback.onBleMsg(context.getResources().getString(R.string.bt_not_supported));
                return;
            }
            if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
                callback.onBluetoothEnable();
            } else {
                startScan();
            }
        }
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
        }, Consts.Ble.SCAN_MAX_TIME);

//        ScanFilter scanServiceFilter = new ScanFilter.Builder().setDeviceName(FILTER_NAME).build();
//        filters.add(scanServiceFilter);
        ScanSettings scanSettings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
        btScanner.startScan(null, scanSettings, scanCallback);
        callback.onScan(true);

    }

    public void stopScan() {
        btScanner.stopScan(scanCallback);
        callback.onScan(false);
    }
}
