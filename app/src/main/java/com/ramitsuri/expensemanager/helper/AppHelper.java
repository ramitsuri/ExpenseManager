package com.ramitsuri.expensemanager.helper;

import com.ramitsuri.expensemanager.constants.PrefKeys;

public class AppHelper {

    public static boolean isFirstRunComplete(){
        return PrefHelper.get(PrefKeys.IS_FIRST_RUN_COMPLETE, false);
    }

    public static void setFirstRunComplete(){
        PrefHelper.set(PrefKeys.IS_FIRST_RUN_COMPLETE, true);
    }

    public static long getLastAddedID(){
        return PrefHelper.get(PrefKeys.LAST_STORED_ID, 20170101000L);
    }

    public static void setLastAddedID(long value){
        PrefHelper.set(PrefKeys.LAST_STORED_ID, value);
    }
}
