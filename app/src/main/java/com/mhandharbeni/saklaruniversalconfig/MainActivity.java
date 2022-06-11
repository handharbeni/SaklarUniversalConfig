package com.mhandharbeni.saklaruniversalconfig;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothClassicService;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothConfiguration;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothService;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothStatus;
import com.mhandharbeni.saklaruniversalconfig.databinding.ActivityMainBinding;
import com.mhandharbeni.saklaruniversalconfig.utils.Constant;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class MainActivity extends AppCompatActivity implements BluetoothService.OnBluetoothEventCallback, BluetoothService.OnBluetoothScanCallback {
    private final String TAG = MainActivity.class.getSimpleName();
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    NavController navController;
    public static boolean bluetoothConnected = false;

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                requestPermission();
            }
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                Log.d(TAG, "onReceive: " + deviceName + " " + deviceHardwareAddress);
            }
        }
    };


    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            });
    ActivityResultLauncher<Intent> mainActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    if (Constant.REQUEST_CODE == Constant.REQUEST_CODE_ENABLE_BLUETOOTH) {
                        searchBondedDevices();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        requestPermission();

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                    Objects.requireNonNull(controller.getCurrentBackStackEntry())
                            .getSavedStateHandle()
                            .getLiveData(Constant.BLUETOOTH_SCAN_REQUEST).observeForever(o -> {
                                checkBluetoothDevice();
                            });
                    Objects.requireNonNull(controller.getCurrentBackStackEntry())
                            .getSavedStateHandle()
                            .getLiveData(Constant.BLUETOOTH_CONNECT_REQUEST).observeForever(o -> {
                                try {
                                    service.connect((BluetoothDevice) o);
                                } catch (Exception ignored) {}
                            });
                }
        );


        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
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
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    void requestPermission() {
        String[] listPermission = new String[Constant.LIST_PERMISSION.size()];
        for (int i = 0; i < Constant.LIST_PERMISSION.size(); i++) {
            listPermission[i] = Constant.LIST_PERMISSION.get(i);
            requestPermissionLauncher.launch(Constant.LIST_PERMISSION.get(i));
        }
        ActivityCompat.requestPermissions(this, listPermission, 200);
    }

    BluetoothAdapter bluetoothAdapter;
    BluetoothConfiguration config;
    BluetoothService service;

    List<BluetoothDevice> listDevice = new ArrayList<>();

    void initBluetooth() {
        config = new BluetoothConfiguration();
        config.context = getApplicationContext();
        config.bluetoothServiceClass = BluetoothClassicService.class;
        config.bufferSize = 1024;
        config.characterDelimiter = '\n';
        config.deviceName = getResources().getString(R.string.app_name);
        config.callListenersInMainThread = true;

        config.uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); // Required

        BluetoothService.init(config);
        service = BluetoothService.getDefaultInstance();

        service.disconnect();
        service.startScan();

        service.setOnEventCallback(this);
        service.setOnScanCallback(this);
    }

    boolean checkBluetoothDevice() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            turnOnBT();
            return true;
        } else {
            // doesn't support bluetooth
            return false;
        }
    }

    void turnOnBT() {
        if (!bluetoothAdapter.isEnabled()) {
            Constant.REQUEST_CODE = Constant.REQUEST_CODE_ENABLE_BLUETOOTH;
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mainActivityLauncher.launch(enableBtIntent);
        } else {
            searchBondedDevices();
        }
    }

    void searchBondedDevices() {
        initBluetooth();
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//            requestPermission();
//            return;
//        }
//        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
//
//        if (pairedDevices.size() > 0) {
//            // There are paired devices. Get the name and address of each paired device.
//            for (BluetoothDevice device : pairedDevices) {
//                String deviceName = device.getName();
//                String deviceHardwareAddress = device.getAddress(); // MAC address
//                Log.d(TAG, "searchBondedDevices: "+deviceName+" "+deviceHardwareAddress);
//            }
//        }
//
//        // Register for broadcasts when a device is discovered.
//        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//        registerReceiver(receiver, filter);
//
//        bluetoothAdapter.startDiscovery();

    }

    @Override
    public void onDataRead(byte[] buffer, int length) {

    }

    @Override
    public void onStatusChange(BluetoothStatus status) {
        Log.d(TAG, "onStatusChange: "+status.toString());
        if (status == BluetoothStatus.CONNECTED) {
            bluetoothConnected = true;
            navController.navigateUp();
        }
    }

    @Override
    public void onDeviceName(String deviceName) {

    }

    @Override
    public void onToast(String message) {

    }

    @Override
    public void onDataWrite(byte[] buffer) {

    }

    @Override
    public void onDeviceDiscovered(BluetoothDevice device, int rssi) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
        }
        if (!listDevice.contains(device)) {
            if (device.getName() != null) {
                listDevice.add(device);
            }
        }

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
        AtomicReference<BluetoothFragment> bluetoothFragment = new AtomicReference<>();

        Log.d(TAG, "onDeviceDiscovered: " + device.getName());
        try {
            if (navHostFragment != null) {
                bluetoothFragment.set((BluetoothFragment) navHostFragment.getChildFragmentManager().getFragments().get(0));
            }
            if (bluetoothFragment.get() != null) {
                bluetoothFragment.get().updateDevice(listDevice);
            }
        } catch (Exception ignored) {}

    }

    @Override
    public void onStartScan() {

    }

    @Override
    public void onStopScan() {

    }
}