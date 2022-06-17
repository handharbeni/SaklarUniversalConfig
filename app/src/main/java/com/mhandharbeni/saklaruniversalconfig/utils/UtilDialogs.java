package com.mhandharbeni.saklaruniversalconfig.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

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

    public static void showDialog(
            Context context,
            View view,
            String positiveButton,
            DialogCallbacks dialogCallbacks
    ) {
        new MaterialAlertDialogBuilder(context)
                .setView(view)
                .setPositiveButton(positiveButton, (dialogInterface, i) -> dialogCallbacks.onPositiveClick())
                .setOnCancelListener(dialogInterface -> dialogCallbacks.onCancel())
                .show();
    }


    public static void showDialog(
            Context context,
            View view,
            String positiveButton,
            String negativeButton,
            DialogCallbacks dialogCallbacks
    ) {
        new MaterialAlertDialogBuilder(context)
                .setView(view)
                .setPositiveButton(positiveButton, (dialogInterface, i) -> dialogCallbacks.onPositiveClick())
                .setNegativeButton(negativeButton, (dialogInterface, i) -> dialogCallbacks.onNegativeClick())
                .setOnCancelListener(dialogInterface -> dialogCallbacks.onCancel())
                .show();
    }

    public interface DialogCallbacks{
        void onPositiveClick();
        void onNegativeClick();
        void onCancel();
    }
}
