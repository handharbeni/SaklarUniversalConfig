package com.mhandharbeni.saklaruniversalconfig;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothStatus;
import com.mhandharbeni.saklaruniversalconfig.adapters.BluetoothDevicesAdapter;
import com.mhandharbeni.saklaruniversalconfig.databinding.FragmentBluetoothBinding;
import com.mhandharbeni.saklaruniversalconfig.utils.Constant;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BluetoothFragment extends Fragment implements BluetoothDevicesAdapter.DeviceCallback {
    private final String TAG = BluetoothFragment.class.getSimpleName();
    private FragmentBluetoothBinding binding;

    BluetoothDevicesAdapter bluetoothDevicesAdapter;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentBluetoothBinding.inflate(inflater, container, false);

        initAdapter();

        NavHostFragment.findNavController(BluetoothFragment.this)
                        .getCurrentBackStackEntry().getSavedStateHandle().getLiveData(Constant.BLUETOOTH_CONNECTED_STRING)
                        .observe(getViewLifecycleOwner(), o -> {
                            if (o == BluetoothStatus.CONNECTED) {

                            } else if (o == BluetoothStatus.CONNECTING) {

                            } else if (o == BluetoothStatus.NONE) {
                                bluetoothDevicesAdapter.notifyDataSetChanged();
                            }
                        });


        binding.refreshDevice.setOnRefreshListener(() ->
                NavHostFragment.findNavController(BluetoothFragment.this)
                        .getCurrentBackStackEntry()
                        .getSavedStateHandle()
                        .set(Constant.BLUETOOTH_SCAN_REQUEST, "BluetoothFragment"));
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.refreshDevice.setRefreshing(true);
                NavHostFragment.findNavController(BluetoothFragment.this)
                        .getCurrentBackStackEntry()
                        .getSavedStateHandle()
                        .set(Constant.BLUETOOTH_SCAN_REQUEST, "BluetoothFragment");
            }
        });


        return binding.getRoot();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void updateDevice(List<BluetoothDevice> listDevice) {
        try {
            bluetoothDevicesAdapter.updateData(listDevice);
            binding.refreshDevice.setRefreshing(false);
        } catch (Exception ignored) {
        }
    }

    void initAdapter() {
        bluetoothDevicesAdapter = new BluetoothDevicesAdapter(requireContext(), new ArrayList<>(), this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        binding.rvDevice.setLayoutManager(linearLayoutManager);
        binding.rvDevice.setAdapter(bluetoothDevicesAdapter);
    }

    @Override
    public void onDeviceClick(BluetoothDevice device) {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

        }
        Log.d(TAG, "onDeviceClick: " + device.getName());
        Objects.requireNonNull(NavHostFragment.findNavController(BluetoothFragment.this)
                        .getCurrentBackStackEntry())
                        .getSavedStateHandle()
                        .set(Constant.BLUETOOTH_CONNECT_REQUEST, device);
    }
}