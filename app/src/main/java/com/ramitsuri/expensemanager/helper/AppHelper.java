package com.ramitsuri.expensemanager.helper;

import com.ramitsuri.expensemanager.constants.PrefKeys;

public class AppHelper {

    public static boolean isFirstRunComplete(){
        return PrefHelper.get(PrefKeys.IS_FIRST_RUN_COMPLETE, false);
    }

    public static void setFirstRunComplete(){
        PrefHelper.set(PrefKeys.IS_FIRST_RUN_COMPLETE, true);
    }
}
