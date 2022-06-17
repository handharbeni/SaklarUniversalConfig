package com.mhandharbeni.saklaruniversalconfig.utils;

import android.content.Context;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class UtilDialogs {
    public static void showDialog(Context context, String title, String message) {
        new MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialogInterface, i) -> {

                })
                .show();
    }
}
