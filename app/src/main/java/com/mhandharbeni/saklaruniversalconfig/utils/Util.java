package com.mhandharbeni.saklaruniversalconfig.utils;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.location.LocationManager;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class Util {

    public static boolean isGpsEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static void enableGps(Activity activity) {
        if (!isGpsEnabled(activity.getApplicationContext())) {
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            builder.setAlwaysShow(true);

            Task<LocationSettingsResponse> task =
                    LocationServices.getSettingsClient(activity).checkLocationSettings(builder.build());

            task.addOnCompleteListener(task1 -> {
                try {
                    task1.getResult(ApiException.class);
                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                resolvable.startResolutionForResult(
                                        activity,
                                        Constant.REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException | ClassCastException ignored) {
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            break;
                    }
                }
            });
        }
    }

    public static byte[] generateData(List<String> lCommand) {
        List<Byte> lByte = new ArrayList<>();
        int total = 0;
        for (String s : lCommand) {
            for (char c : s.toCharArray()) total += c;
        }
        total = total & 0xFF;
        total = (~total + 1) & 0xFF;
        String hexs = Integer.toHexString(total);
        lCommand.add(hexs);
        lByte.add((byte) 0xAA);
        for (String s : lCommand) {
            for (char c : s.toCharArray()) {
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
}
