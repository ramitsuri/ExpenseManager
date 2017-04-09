package com.ramitsuri.expensemanager.async;


import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.AsyncTaskLoader;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.constants.Others;
import com.ramitsuri.expensemanager.helper.AppHelper;
import com.ramitsuri.expensemanager.helper.SheetsHelper;

import java.io.IOException;
import java.util.Arrays;

public class SheetsCreateLoader extends AsyncTaskLoader<Spreadsheet> {

    private GoogleAccountCredential mCredential;
    private com.google.api.services.sheets.v4.Sheets mService;
    private HttpTransport mTransport;
    private JsonFactory mJsonFactory;
    private FragmentActivity mActivity;

    public SheetsCreateLoader(Context context) {
        super(context);
        mActivity = (FragmentActivity)context;
        mCredential = GoogleAccountCredential.usingOAuth2(context, Arrays.asList(Others.SCOPES)).
                setBackOff(new ExponentialBackOff());
        mCredential.setSelectedAccountName(AppHelper.getAccountName());
        mTransport = AndroidHttp.newCompatibleTransport();
        mJsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.sheets.v4.Sheets.Builder(
                mTransport, mJsonFactory, mCredential)
                .setApplicationName(getContext().getString(R.string.app_name))
                .build();
    }

    @Override
    public Spreadsheet loadInBackground() {
        Spreadsheet spreadsheet = SheetsHelper.getNewSpreadsheet();
        try {
            spreadsheet = mService.spreadsheets().create(spreadsheet).execute();
        } catch (UserRecoverableAuthIOException e) {
            mActivity.startActivityForResult(e.getIntent(), Others.REQUEST_AUTHORIZATION);
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return spreadsheet;
    }
}
