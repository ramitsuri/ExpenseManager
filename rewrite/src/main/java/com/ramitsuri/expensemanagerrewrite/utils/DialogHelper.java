package com.ramitsuri.expensemanagerrewrite.utils;

import android.content.Context;
import android.content.DialogInterface;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

public class DialogHelper {
    public static void showAlert(@NonNull Context context,
            @StringRes int title, @StringRes int message,
            @StringRes int positiveText, DialogInterface.OnClickListener positiveListener,
            @StringRes int negativeText, DialogInterface.OnClickListener negativeListener) {
        new MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveText, positiveListener)
                .setNegativeButton(negativeText, negativeListener)
                .show();
    }
}
