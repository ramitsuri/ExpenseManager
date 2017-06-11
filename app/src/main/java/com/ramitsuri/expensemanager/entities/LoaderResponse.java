package com.ramitsuri.expensemanager.entities;

import android.content.Intent;

public class LoaderResponse {
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
