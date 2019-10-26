package com.ramitsuri.expensemanagerrewrite.utils;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

public class ToastHelper {
    public void showToast(@NonNull Context context, @StringRes int text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}
