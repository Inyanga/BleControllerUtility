package com.inyanga.blecontrollerutility;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.inyanga.blecontrollerutility.ble.BleUtility;
import com.inyanga.blecontrollerutility.support.Consts;

public class MainActivity extends AppCompatActivity implements BleUtility.BleCallback {

    private BleUtility bleUtility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentScanList()).commit();
        bleUtility = new BleUtility(getApplicationContext(), this);
        bleUtility.initBluetooth();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBleMsg(String msg) {

    }

    @Override
    public void onBluetoothEnable() {
        Intent btEnableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(btEnableIntent, Consts.Ble.BT_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == Consts.Ble.BT_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                bleUtility.startScan();
                onScan(true);
            }
        }
    }

    @Override
    public void onScan(boolean start) {

    }


}
