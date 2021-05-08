package com.ramitsuri.expensemanager.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.R;

import javax.annotation.Nonnull;

import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import timber.log.Timber;

public class DialogHelper {
    public static void showAlert(@NonNull Context context,
            @StringRes int message,
            @StringRes int positiveText, DialogInterface.OnClickListener positiveListener,
            @StringRes int negativeText, DialogInterface.OnClickListener negativeListener) {
        new MaterialAlertDialogBuilder(context)
                .setMessage(message)
                .setPositiveButton(positiveText, positiveListener)
                .setNegativeButton(negativeText, negativeListener)
                .show();
    }

    public static void showAlertWithInput(@NonNull Context context,
            @Nonnull EditText input,
            @StringRes int title,
            @StringRes int positiveText, DialogInterface.OnClickListener positiveListener,
            @StringRes int negativeText, DialogInterface.OnClickListener negativeListener) {
        new MaterialAlertDialogBuilder(context)
                .setView(input)
                .setTitle(title)
                .setPositiveButton(positiveText, positiveListener)
                .setNegativeButton(negativeText, negativeListener)
                .show();
    }

    public static void showAlertList(@NonNull Context context,
            @ArrayRes int arrayList, int checkedItem,
            DialogInterface.OnClickListener itemClickListener,
            @StringRes int title,
            @StringRes int negativeText, DialogInterface.OnClickListener negativeListener) {
        new MaterialAlertDialogBuilder(context)
                .setSingleChoiceItems(arrayList, checkedItem, itemClickListener)
                .setTitle(title)
                .setNegativeButton(negativeText, negativeListener)
                .show();
    }

    public static void showEOLDialog(@NonNull Context context,
            DialogInterface.OnClickListener positiveListener) {
        new MaterialAlertDialogBuilder(context)
                .setTitle(R.string.eol_dialog_title)
                .setMessage(R.string.eol_dialog_message)
                .setPositiveButton(R.string.eol_dialog_positive, positiveListener)
                .show();
    }

    private static void log(int which) {
        Timber.i(MainApplication.getInstance().getResources().getStringArray(R.array.theme_titles)
                .toString());
        Timber.i("Clicked " + which);
    }
}
