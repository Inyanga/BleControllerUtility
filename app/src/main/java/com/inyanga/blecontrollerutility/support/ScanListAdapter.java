package com.inyanga.blecontrollerutility.support;

import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.inyanga.blecontrollerutility.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ScanListAdapter extends RecyclerView.Adapter<ScanListAdapter.ScanViewHolder> {

    private Context context;
    private List<ScanResult> scanResults;
    private OnItemClickListener itemClickListener;

    public ScanListAdapter(Context context, List<ScanResult> scanResults, OnItemClickListener itemClickListener) {
        this.context = context;
        this.scanResults = scanResults;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ScanViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.device_item_holder, viewGroup, false);
        return new ScanViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull final ScanViewHolder scanViewHolder, int i) {
        ScanResult result = scanResults.get(i);
        scanViewHolder.deviceName.setText(result.getDevice().getName());
        scanViewHolder.deviceMac.setText(result.getDevice().getAddress());
        scanViewHolder.deviceRssi.setText(String.valueOf(result.getRssi()));
        scanViewHolder.holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClick(scanViewHolder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return scanResults.size();
    }

    class ScanViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.device_name)
        TextView deviceName;
        @Bind(R.id.device_mac)
        TextView deviceMac;
        @Bind(R.id.device_rssi)
        TextView deviceRssi;
        @Bind(R.id.holder)
        ConstraintLayout holder;

        ScanViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
