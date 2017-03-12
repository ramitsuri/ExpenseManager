package com.ramitsuri.expensemanager;

import android.app.Application;

import com.ramitsuri.expensemanager.helper.PrefHelper;

public class MainApplication extends Application {

    private static MainApplication sSingleton;

    @Override
    public void onCreate(){
        super.onCreate();
        sSingleton = this;
    }

    public static MainApplication getInstance(){
        return sSingleton;
    }
}
