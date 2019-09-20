package com.ramitsuri.expensemanagerlegacy.entities;

import android.content.Intent;

public class LoaderResponse {
    public static final int SUCCESS = 1;
    public static final int REQUEST_ACCESS = 2;
    public static final int FAILURE = 3;
    private int mResponseCode;
    private Intent mIntent;
    private String mSheetId;

    public LoaderResponse(int responseCode, Intent intent, String sheetId) {
        mResponseCode = responseCode;
        mIntent = intent;
        mSheetId = sheetId;
    }

    public int getResponseCode() {
        return mResponseCode;
    }

    public void setResponseCode(int responseCode) {
        mResponseCode = responseCode;
    }

    public Intent getIntent() {
        return mIntent;
    }

    public void setIntent(Intent intent) {
        mIntent = intent;
    }

    public String getSheetId() {
        return mSheetId;
    }

    public LoaderResponse setSheetId(String sheetId) {
        mSheetId = sheetId;
        return this;
    }
}
