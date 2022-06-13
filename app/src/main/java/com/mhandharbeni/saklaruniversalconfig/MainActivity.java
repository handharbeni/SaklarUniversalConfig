package com.mhandharbeni.saklaruniversalconfig;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
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
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mhandharbeni.saklaruniversalconfig.databinding.ActivityMainBinding;
import com.mhandharbeni.saklaruniversalconfig.utils.Constant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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

    private final ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                boolean readStoragePermission = Boolean.TRUE.equals(result.get(Manifest.permission.READ_EXTERNAL_STORAGE));
                boolean writeStoragePermission = Boolean.TRUE.equals(result.get(Manifest.permission.WRITE_EXTERNAL_STORAGE));
                boolean fineLocationPermission = Boolean.TRUE.equals(result.get(Manifest.permission.ACCESS_FINE_LOCATION));
                boolean coarseLocationPermission = Boolean.TRUE.equals(result.get(Manifest.permission.ACCESS_COARSE_LOCATION));
                if (!readStoragePermission) {
                    // read storage denied
                } else if (!writeStoragePermission) {
                    // write storage denied
                } else if (!fineLocationPermission) {
                    // fine location denied
                } else if (!coarseLocationPermission) {
                    // coarse location denied
                }

                if (readStoragePermission) {
                    // read storage accepted
                } else if (writeStoragePermission) {
                    // write storage accepted
                } else if (fineLocationPermission || coarseLocationPermission) {
                    // coarse location accepted
                    enableGps();
                //                    Log.d(TAG, "locationTask: "+fineLocationPermission+" "+coarseLocationPermission);
//                    LocationSettingsRequest builder = new LocationSettingsRequest.Builder()
//                            .addLocationRequest(locationRequestHighAccuracy)
//                            .setNeedBle(true)
//                            .build();
//                    Task<LocationSettingsResponse> resultLocation =
//                            LocationServices.getSettingsClient(this).checkLocationSettings(builder);
//                    resultLocation.addOnCompleteListener(task -> {
//                        Log.d(TAG, "locationTask : "+task.getResult().toString());
//                    });
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
                            .getLiveData(Constant.BLUETOOTH_SCAN_REQUEST).observe(this, o -> {
                                checkBluetoothDevice();
                            });
                    Objects.requireNonNull(controller.getCurrentBackStackEntry())
                            .getSavedStateHandle()
                            .getLiveData(Constant.BLUETOOTH_CONNECT_REQUEST).observe(this, o -> {
                                try {
                                    service.connect((BluetoothDevice) o);
                                } catch (Exception ignored) {}
                            });
                    Objects.requireNonNull(controller.getCurrentBackStackEntry())
                            .getSavedStateHandle()
                            .getLiveData(Constant.BLUETOOTH_SEND_COMMAND).observe(this, o -> {
                                sendCommand((String) o);
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

//    @AfterPermissionGranted(Constant.RC_PERMISSION)
    void requestPermission() {
        enableGps();
        String[] listPermission = new String[Constant.LIST_PERMISSION.size()];
        for (int i = 0; i < Constant.LIST_PERMISSION.size(); i++) {
            listPermission[i] = Constant.LIST_PERMISSION.get(i);
//            requestPermissionLauncher.launch(Constant.LIST_PERMISSION.get(i));
        }

        requestPermissionLauncher.launch(listPermission);
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

        service.setOnEventCallback(this);
        service.setOnScanCallback(this);

        try {
            service.disconnect();
        } catch (Exception ignored) {

        } finally {
            service.startScan();
        }
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
        }
        searchBondedDevices();
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
        } else if (status == BluetoothStatus.CONNECTING){
            bluetoothConnected = false;
        } else if (status == BluetoothStatus.NONE) {
            bluetoothConnected = false;
        }
        Objects.requireNonNull(navController.getCurrentBackStackEntry()).getSavedStateHandle().set(Constant.BLUETOOTH_CONNECTED, bluetoothConnected);
        Objects.requireNonNull(navController.getCurrentBackStackEntry()).getSavedStateHandle().set(Constant.BLUETOOTH_CONNECTED_STRING, status);
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

    void sendCommand(String command) {
        try {
            if (command.equalsIgnoreCase("off")) {
                service.write(generateDataOff());
            } else if (command.equalsIgnoreCase("on")){
                service.write(generateDataOn());
            }
        } catch (Exception ignored) {}
    }


    byte[] generateDataOn(){
        List<Byte> lByte = new ArrayList<>();
        List<String> lCommand = new ArrayList<>(
                Arrays.asList(
                        "01",
                        "01",
                        "01",
                        "DD"
                )
        );
        int total = 0;
        for (String s : lCommand) {
            for (char c : s.toCharArray()) {
                total += c;
            }
        }
        total = total & 0xFF;
        total = (~total + 1) & 0xFF;
        String hexs = Integer.toHexString(total);
        lCommand.add(hexs);
        lByte.add((byte) 0xAA);
        for (String s: lCommand) {
            for (char c : s.toCharArray()) {
                String hex = String.format("%04x", (int) c);
                lByte.add((byte) c);
            }
        }
        lByte.add((byte) 0x0D);
        lByte.add((byte) 0x0A);

        byte[] rByte = new byte[lByte.size()];
        for (int i = 0; i < lByte.size(); i++) {
            rByte[i] = lByte.get(i);
        }
        return rByte;
    }

    byte[] generateDataOff() {
        List<Byte> lByte = new ArrayList<>();
        List<String> lCommand = new ArrayList<>(
                Arrays.asList(
                        "03",
                        "01",
                        "01",
                        "DB"
                )
        );
        int total = 0;
        for (String s : lCommand) {
            for (char c : s.toCharArray()) {
                total += c;
            }
        }
        total = total & 0xFF;
        total = (~total + 1) & 0xFF;
        String hexs = Integer.toHexString(total);
        lCommand.add(hexs);
        lByte.add((byte) 0xAA);
        for (String s: lCommand) {
            for (char c : s.toCharArray()) {
                String hex = String.format("%04x", (int) c);
                lByte.add((byte) c);
            }
        }
        lByte.add((byte) 0x0D);
        lByte.add((byte) 0x0A);

        byte[] rByte = new byte[lByte.size()];
        for (int i = 0; i < lByte.size(); i++) {
            rByte[i] = lByte.get(i);
        }
        return rByte;
    }

    public boolean isGpsEnabled(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public void enableGps() {
        if(!isGpsEnabled()){
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            builder.setAlwaysShow(true); //this displays dialog box like Google Maps with two buttons - OK and NO,THANKS

            Task<LocationSettingsResponse> task =
                    LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());

            task.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
                @Override
                public void onComplete(Task<LocationSettingsResponse> task) {
                    try {
                        LocationSettingsResponse response = task.getResult(ApiException.class);
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                    } catch (ApiException exception) {
                        switch (exception.getStatusCode()) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                // Location settings are not satisfied. But could be fixed by showing the
                                // user a dialog.
                                try {
                                    // Cast to a resolvable exception.
                                    ResolvableApiException resolvable = (ResolvableApiException) exception;
                                    // Show the dialog by calling startResolutionForResult(),
                                    // and check the result in onActivityResult().
                                    resolvable.startResolutionForResult(
                                            MainActivity.this,
                                            Constant.REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException e) {
                                    // Ignore the error.
                                } catch (ClassCastException e) {
                                    // Ignore, should be an impossible error.
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                // Location settings are not satisfied. However, we have no way to fix the
                                // settings so we won't show the dialog.
                                break;
                        }
                    }
                }
            });
        }
    }
}