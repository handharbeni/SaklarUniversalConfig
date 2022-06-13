package com.mhandharbeni.saklaruniversalconfig.adapters;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.mhandharbeni.saklaruniversalconfig.databinding.BluetoothItemBinding;

import java.util.ArrayList;
import java.util.List;

public class BluetoothDevicesAdapter extends RecyclerView.Adapter<BluetoothDevicesAdapter.ViewHolder> {
    Context context;
    List<BluetoothDevice> listDevice = new ArrayList<>();
    DeviceCallback deviceCallback;

    public BluetoothDevicesAdapter(Context context, List<BluetoothDevice> listDevice, DeviceCallback deviceCallback) {
        this.context = context;
        this.listDevice = listDevice;
        this.deviceCallback = deviceCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        BluetoothItemBinding binding = BluetoothItemBinding.inflate(layoutInflater);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        binding.getRoot().setLayoutParams(lp);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BluetoothDevice bluetoothDevice = listDevice.get(position);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

        }
        holder.binding.btName.setText(bluetoothDevice.getName());
        holder.binding.btAddress.setText(bluetoothDevice.getAddress());
        holder.itemView.setOnClickListener(view -> {
            holder.binding.btAddress.setText("CONNECTING");
            deviceCallback.onDeviceClick(bluetoothDevice);
        });
    }

    @Override
    public int getItemCount() {
        return listDevice.size();
    }

    public void updateData(List<BluetoothDevice> listDevice) {
        this.listDevice = listDevice;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        BluetoothItemBinding binding;
        public ViewHolder(@NonNull BluetoothItemBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }

    public interface DeviceCallback {
        void onDeviceClick(BluetoothDevice device);
    }
}
