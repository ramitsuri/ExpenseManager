package com.ramitsuri.expensemanagerlegacy.helper;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class ActivityHelper {

    public static void showSoftKeyboard(Context context, View view) {
        InputMethodManager manager = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    public static void hideSoftKeyboard(Context context, View view) {
        InputMethodManager manager = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
