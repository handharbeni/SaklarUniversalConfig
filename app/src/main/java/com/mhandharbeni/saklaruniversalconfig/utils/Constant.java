package com.mhandharbeni.saklaruniversalconfig.utils;

import android.Manifest;

import java.util.ArrayList;
import java.util.Arrays;

public class Constant {
    public static final int RC_PERMISSION = 123;
    public static final int REQUEST_CHECK_SETTINGS = 111;
    public static final String BLUETOOTH_SCAN_REQUEST = "BLUETOOTH_SCAN_REQUEST";
    public static final String BLUETOOTH_CONNECT_REQUEST = "BLUETOOTH_CONNECT_REQUEST";
    public static final String BLUETOOTH_SEND_COMMAND = "BLUETOOTH_SEND_COMMAND";
    public static final String BLUETOOTH_DEVICE = "BLUETOOTH_DEVICE";
    public static final String BLUETOOTH_CONNECTED = "BLUETOOTH_CONNECTED";
    public static final String BLUETOOTH_CONNECTED_STRING = "BLUETOOTH_CONNECTED_STRING";
    public static final ArrayList<String> LIST_PERMISSION = new ArrayList<>(
            Arrays.asList(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_PRIVILEGED,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
    );
    public static final int REQUEST_CODE_ENABLE_BLUETOOTH = 200;
    public static final int REQUEST_CODE_PERMISSION = 300;
    public static int REQUEST_CODE = 0;

}
