package com.inyanga.blecontrollerutility.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.pm.PackageManager;

import com.inyanga.blecontrollerutility.R;
import com.inyanga.blecontrollerutility.ble.callbacks.BleUtilityCallback;

import java.util.UUID;

/**
 * Created by Pavel Shakhtarin on 02.11.2018.
 */
public class BleUtility  {

    private Context context;
    private BluetoothAdapter bluetoothAdapter;
    private BleUtilityCallback callback;

    private static final UUID DATA_SERVICE = UUID.fromString("F000C0E0-0451-4000-B000-000000000000");
    private static final UUID DATA_CHAR = UUID.fromString("F000C0E1-0451-4000-B000-000000000000");
    private static final UUID CCC_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    private BluetoothGatt mGatt;
    private BluetoothGattService dataService;
    private BluetoothGattCharacteristic dataChar;
    private BluetoothGattDescriptor cccDesc;

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

    public void connect(BluetoothDevice device) {
        mGatt = device.connectGatt(context, false, gattCallback);
    }

    /***********************************************************************************************
     BluetoothGattServerCallback implementation
     **********************************************************************************************/

    private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if(newState == BluetoothGatt.STATE_CONNECTED &&
                    status==BluetoothGatt.GATT_SUCCESS) {
                if(!gatt.discoverServices())
                    return;
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_FAILURE) {
//                connectionFailure();
                return;
            }

            BluetoothGattService dataService = gatt.getService(DATA_SERVICE);
            if (dataService != null) {
                dataChar = dataService.getCharacteristic(DATA_CHAR);
                if (dataChar != null) {
                    cccDesc = dataChar.getDescriptor(CCC_DESCRIPTOR);
                    if (cccDesc != null) {
                        cccDesc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        gatt.writeDescriptor(cccDesc);
                        gatt.setCharacteristicNotification(dataChar, true);
                    }
                }
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            dataChar.setValue(new byte[]  {1,23,4,4});
            gatt.writeCharacteristic(dataChar);
        }
    };

}
