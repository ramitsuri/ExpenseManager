package com.ramitsuri.expensemanager;

import android.app.Application;

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
