package com.inyanga.blecontrollerutility;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.BinderThread;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.inyanga.blecontrollerutility.ble.BleScanner;
import com.inyanga.blecontrollerutility.ble.BleUtility;
import com.inyanga.blecontrollerutility.ble.callbacks.BleScannerCallback;
import com.inyanga.blecontrollerutility.ble.callbacks.BleUtilityCallback;
import com.inyanga.blecontrollerutility.support.ScanListAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentScanList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentScanList extends Fragment implements BleScannerCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private BluetoothAdapter bluetoothAdapter;
    private BleUtilityCallback bleUtilityCallback;
    private BleUtility bleUtility;
    private List<ScanResult> scanResults;
    private ScanListAdapter adapter;


    @Bind(R.id.device_recycler)
    RecyclerView deviceRecycler;

    public FragmentScanList() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentScanList.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentScanList newInstance(String param1, String param2) {
        FragmentScanList fragment = new FragmentScanList();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        scanResults = new ArrayList<>();



        final BluetoothManager bluetoothManager = (BluetoothManager) getContext().getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager != null) {
            bluetoothAdapter = bluetoothManager.getAdapter();
        } else {
            bleUtilityCallback.onBleMsg("BLE не поддерживается устройством");
        }
        try {
            initBleUtility();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scan_list, container, false);
        ButterKnife.bind(this, view);
        adapter = new ScanListAdapter(getContext(), scanResults);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        deviceRecycler.setLayoutManager(layoutManager);
        deviceRecycler.setAdapter(adapter);
        return view;
    }


    @Override
    public void onScan(boolean start) {

    }

    @Override
    public void onDeviceFound(ScanResult result) {
        if(!scanResults.contains(result))
            scanResults.add(result);
        adapter.notifyDataSetChanged();
    }

    private void initBleUtility() throws Exception {
        if (bleUtilityCallback == null)
            throw new Exception("Set BleUtilityCallback first");
        if (bleUtility == null)
            bleUtility = new BleUtility(getContext(), bluetoothAdapter, bleUtilityCallback);
        if (bleUtility.initBluetooth())
            initScanner();
    }

    public void setBleCallback(BleUtilityCallback bleCallback) {
        this.bleUtilityCallback = bleCallback;
    }

    public void initScanner() {
        BleScanner bleScanner = new BleScanner(this, bluetoothAdapter);
        bleScanner.startScan();
    }
}
