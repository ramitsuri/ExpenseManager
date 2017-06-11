package com.ramitsuri.expensemanager.async;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.entities.LoaderResponse;
import com.ramitsuri.expensemanager.helper.SheetsHelper;

import java.io.IOException;
import java.util.Arrays;

public class SheetsCreateLoader extends AsyncTaskLoader<LoaderResponse> {

    private GoogleAccountCredential mCredential;
    private com.google.api.services.sheets.v4.Sheets mService = null;
    private HttpTransport mTransport;
    private JsonFactory mJsonFactory;

    private static final String[] SCOPES = { SheetsScopes.SPREADSHEETS, SheetsScopes.DRIVE};

    public SheetsCreateLoader(Context context, String accountName) {
        super(context);
        mCredential = GoogleAccountCredential.usingOAuth2(context, Arrays.asList(SCOPES)).
                setBackOff(new ExponentialBackOff());
        if(accountName != null){
            mCredential.setSelectedAccountName(accountName);
        }
        mTransport = AndroidHttp.newCompatibleTransport();
        mJsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.sheets.v4.Sheets.Builder(
                mTransport, mJsonFactory, mCredential)
                .setApplicationName(getContext().getString(R.string.app_name))
                .build();
    }

    @Override
    public LoaderResponse loadInBackground() {
        try {
            Spreadsheet sheet = mService.spreadsheets().create(SheetsHelper.getNewSpreadsheet()).execute();
            return new LoaderResponse(1, null, sheet.getSpreadsheetId());
        } catch (UserRecoverableAuthIOException e) {
            return new LoaderResponse(2, e.getIntent(), null);
        } catch (IOException e) {
            return new LoaderResponse(3, null, null);
        }
    }
}
