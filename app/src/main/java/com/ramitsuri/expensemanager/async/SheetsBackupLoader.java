package com.ramitsuri.expensemanager.async;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetResponse;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.constants.Others;
import com.ramitsuri.expensemanager.entities.Expense;
import com.ramitsuri.expensemanager.helper.AppHelper;
import com.ramitsuri.expensemanager.helper.ExpenseHelper;
import com.ramitsuri.expensemanager.helper.SheetsHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SheetsBackupLoader extends AsyncTaskLoader<Boolean> {

    private GoogleAccountCredential mCredential;
    private com.google.api.services.sheets.v4.Sheets mService;
    private HttpTransport mTransport;
    private JsonFactory mJsonFactory;

    public SheetsBackupLoader(Context context) {
        super(context);
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
    public Boolean loadInBackground() {
        BatchUpdateSpreadsheetRequest content = new BatchUpdateSpreadsheetRequest();
        List<Request> requests = new ArrayList<>();
        List<Expense> expensesToBackup = ExpenseHelper.getExpensesRequiringBackup();
        requests.add(SheetsHelper.getExpenseSheetsRequest(expensesToBackup));
        content.setIncludeSpreadsheetInResponse(true);
        content.setResponseIncludeGridData(true);
        content.setRequests(requests);
        Sheets.Spreadsheets.BatchUpdate b;
        Sheets.Spreadsheets.Get s;
        BatchUpdateSpreadsheetResponse r;

        try {
            String a = content.toPrettyString();
            b = mService.spreadsheets().batchUpdate(AppHelper.getSheetsId(), content);
            r = b.execute();
             //s = mService.spreadsheets().get(AppHelper.getSheetsId());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
