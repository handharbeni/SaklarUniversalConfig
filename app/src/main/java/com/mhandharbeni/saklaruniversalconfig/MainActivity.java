package com.mhandharbeni.saklaruniversalconfig;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothClassicService;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothConfiguration;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothService;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothStatus;
import com.mhandharbeni.saklaruniversalconfig.database.AppDb;
import com.mhandharbeni.saklaruniversalconfig.database.models.Buttons;
import com.mhandharbeni.saklaruniversalconfig.databinding.ActivityMainBinding;
import com.mhandharbeni.saklaruniversalconfig.utils.Constant;
import com.mhandharbeni.saklaruniversalconfig.utils.Util;
import com.mhandharbeni.saklaruniversalconfig.utils.UtilList;
import com.mhandharbeni.saklaruniversalconfig.utils.UtilNav;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class MainActivity extends AppCompatActivity
        implements BluetoothService.OnBluetoothEventCallback,
        BluetoothService.OnBluetoothScanCallback {
    public static boolean bluetoothConnected = false;
    private final String TAG = MainActivity.class.getSimpleName();
    private final ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestMultiplePermissions(),
                    this::responsePermissions
            );
    NavController navController;
    List<BluetoothDevice> listDevice = new ArrayList<>();
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothConfiguration config;
    private BluetoothService service;
    private AppDb appDb;

    ActivityResultLauncher<Intent> mainActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            this::resultActivity);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        requestPermission();

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);

        navController.addOnDestinationChangedListener(this::observeChild);

        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this,
                R.id.nav_host_fragment_content_main
        );
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    void requestPermission() {
        Util.enableGps(this);
        String[] listPermission = new String[Constant.LIST_PERMISSION.size()];
        for (int i = 0; i < Constant.LIST_PERMISSION.size(); i++) {
            listPermission[i] = Constant.LIST_PERMISSION.get(i);
        }
        requestPermissionLauncher.launch(listPermission);
    }

    void initBluetooth() {
        config = new BluetoothConfiguration();
        config.context = getApplicationContext();
        config.bluetoothServiceClass = BluetoothClassicService.class;
        config.bufferSize = 1024;
        config.characterDelimiter = '\n';
        config.deviceName = getResources().getString(R.string.app_name);
        config.callListenersInMainThread = true;

        config.uuid = UUID.fromString(Constant.UUID);

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

    void checkBluetoothDevice() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            turnOnBT();
        }
    }

    void turnOnBT() {
        if (!bluetoothAdapter.isEnabled()) {
            Constant.REQUEST_CODE = Constant.REQUEST_CODE_ENABLE_BLUETOOTH;
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mainActivityLauncher.launch(enableBtIntent);
        }
        initBluetooth();
    }

    @Override
    public void onDataRead(byte[] buffer, int length) {

    }

    @Override
    public void onStatusChange(BluetoothStatus status) {
        if (status == BluetoothStatus.CONNECTED) {
            bluetoothConnected = true;
            navController.navigateUp();
        } else if (status == BluetoothStatus.CONNECTING) {
            bluetoothConnected = false;
        } else if (status == BluetoothStatus.NONE) {
            bluetoothConnected = false;
        }
        new UtilNav<Boolean>().setStateHandle(navController, Constant.BLUETOOTH_CONNECTED, bluetoothConnected);
        new UtilNav<BluetoothStatus>().setStateHandle(navController, Constant.BLUETOOTH_CONNECTED_STRING, status);
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

        if (device.getName() != null) {
            if (!listDevice.contains(device)) {
                listDevice.add(device);
            }
        }

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
        AtomicReference<BluetoothFragment> bluetoothFragment = new AtomicReference<>();

        try {
            if (navHostFragment != null) {
                bluetoothFragment.set((BluetoothFragment) navHostFragment.getChildFragmentManager().getFragments().get(0));
            }
            if (bluetoothFragment.get() != null) {
                bluetoothFragment.get().updateDevice(listDevice);
            }
        } catch (Exception ignored) {
        }

    }

    @Override
    public void onStartScan() {

    }

    @Override
    public void onStopScan() {

    }

    void resultActivity(ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_OK) {
            if (Constant.REQUEST_CODE == Constant.REQUEST_CODE_ENABLE_BLUETOOTH) {
                initBluetooth();
            }
        }
    }

    void responsePermissions(Map<String, Boolean> result) {
        boolean readStoragePermission = Boolean.TRUE.equals(result.get(Manifest.permission.READ_EXTERNAL_STORAGE));
        boolean writeStoragePermission = Boolean.TRUE.equals(result.get(Manifest.permission.WRITE_EXTERNAL_STORAGE));
        boolean fineLocationPermission = Boolean.TRUE.equals(result.get(Manifest.permission.ACCESS_FINE_LOCATION));
        boolean coarseLocationPermission = Boolean.TRUE.equals(result.get(Manifest.permission.ACCESS_COARSE_LOCATION));
        if (fineLocationPermission || coarseLocationPermission) {
            // coarse location accepted
            Util.enableGps(this);
        }

        if (writeStoragePermission) {
            initDb();
        }
    }

    void observeChild(NavController navController, NavDestination navDestination, Bundle arguments) {
        new UtilNav<>()
                .observeValue(
                        navController,
                        this,
                        Constant.REQUEST_PERMISSION,
                        o -> requestPermission());

        new UtilNav<>()
                .observeValue(
                        navController,
                        this,
                        Constant.BLUETOOTH_SCAN_REQUEST,
                        o -> checkBluetoothDevice());

        new UtilNav<BluetoothDevice>()
                .observeValue(
                        navController,
                        this,
                        Constant.BLUETOOTH_CONNECT_REQUEST,
                        o -> {
                            try {
                                service.connect(o);
                            } catch (Exception ignored) {
                            }
                        });

        new UtilNav<>()
                .observeValue(
                        navController,
                        this,
                        Constant.BLUETOOTH_SEND_COMMAND,
                        this::sendCommand);
    }

    void sendCommand(Object command) {
        try {
            List<String> listCommand = new UtilList<String>().convertObjectToList(command);
            service.write(Util.generateData(listCommand));
        } catch (Exception ignored) {
        }
    }

    void initDb() {
        appDb = AppDb.getInstance(getApplicationContext());
        initData();
    }

    void initData() {
        if (appDb.buttons().getList().size() < 1) {
            // default data
            List<Buttons> listButtons = new ArrayList<>();
            for (int i = 0; i < 32; i++) {
                Buttons buttons = new Buttons();
                if (i<=8) {
                    buttons.setPosition(String.valueOf(i));
                    buttons.setMode("1");
                    buttons.setRelay("1");
                    buttons.setType(1);
                    buttons.setLabel("Button "+i);
                } else if (i>8 && i<=16) {
                    buttons.setPosition(String.valueOf(i));
                    buttons.setMode("2");
                    buttons.setRelay("1");
                    buttons.setType(1);
                    buttons.setLabel("Button "+i);
                } else if (i>16 && i<=24) {
                    buttons.setPosition(String.valueOf(i));
                    buttons.setMode("3");
                    buttons.setRelay("1");
                    buttons.setType(1);
                    buttons.setLabel("Button "+i);
                } else if (i>24) {
                    buttons.setPosition(String.valueOf(i));
                    buttons.setMode("4");
                    buttons.setRelay("1");
                    buttons.setType(1);
                    buttons.setLabel("Button "+i);
                }
                listButtons.add(i, buttons);
            }

            appDb.buttons().insert(listButtons);
        }
    }
}