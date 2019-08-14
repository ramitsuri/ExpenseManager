package com.ramitsuri.expensemanager.async;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetResponse;
import com.google.api.services.sheets.v4.model.Request;
import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.constants.Others;
import com.ramitsuri.expensemanager.db.DBConstants;
import com.ramitsuri.expensemanager.entities.Expense;
import com.ramitsuri.expensemanager.entities.LoaderResponse;
import com.ramitsuri.expensemanager.helper.AppHelper;
import com.ramitsuri.expensemanager.helper.CategoryHelper;
import com.ramitsuri.expensemanager.helper.ExpenseHelper;
import com.ramitsuri.expensemanager.helper.PaymentMethodHelper;
import com.ramitsuri.expensemanager.helper.SheetsHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SheetsBackupTask extends AsyncTask<Void, Void, LoaderResponse> {

    private GoogleAccountCredential mCredential;
    private com.google.api.services.sheets.v4.Sheets mService;
    private HttpTransport mTransport;
    private JsonFactory mJsonFactory;

    public SheetsBackupTask(Context context) {
        mCredential = GoogleAccountCredential.usingOAuth2(context, Arrays.asList(Others.SCOPES)).
                setBackOff(new ExponentialBackOff());
        mCredential.setSelectedAccountName(AppHelper.getAccountName());
        mTransport = AndroidHttp.newCompatibleTransport();
        mJsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.sheets.v4.Sheets.Builder(
                mTransport, mJsonFactory, mCredential)
                .setApplicationName(context.getString(R.string.app_name))
                .build();
    }

    @Override
    protected LoaderResponse doInBackground(Void... params) {
        BatchUpdateSpreadsheetRequest content = new BatchUpdateSpreadsheetRequest();
        List<Request> requests = new ArrayList<>();
        List<Expense> expensesToBackup = ExpenseHelper.getExpensesRequiringBackup();
        Request expensesRequest = SheetsHelper.getExpenseSheetsRequest(expensesToBackup);
        requests.add(expensesRequest);
        /*if (AppHelper.isFirstBackupComplete()) {
            requests.add(SheetsHelper
                    .getDeleteRangeRequest(SheetsHelper.CATEGORIES_SHEET_ID));
            requests.add(SheetsHelper
                    .getDeleteRangeRequest(SheetsHelper.PAYMENT_METHOD_SHEET_ID));
        } else {
            requests.add(SheetsHelper.getAddNamedRangeRequest(SheetsHelper.CATEGORIES_SHEET_ID,
                    SheetsHelper.CATEGORIES_NAMED_RANGE_ID,
                    DBConstants.TABLE_CATEGORIES));
            requests.add(SheetsHelper.getAddNamedRangeRequest(SheetsHelper.PAYMENT_METHOD_SHEET_ID,
                    SheetsHelper.PAYMENT_METHODS_NAMED_RANGE_ID, DBConstants.TABLE_PAYMENT_METHOD));
        }*/
        //requests.add(SheetsHelper.getCategoriesSheetsRequest(CategoryHelper.getAllCategories()));
        /*requests.add(SheetsHelper
                .getPaymentMethodsSheetsRequest(PaymentMethodHelper.getAllPaymentMethods()));*/
        //content.setIncludeSpreadsheetInResponse(true);
        //content.setResponseIncludeGridData(true);
        content.setRequests(requests);
        Sheets.Spreadsheets.BatchUpdate batchUpdate;
        try {
            String spreadSheetId = AppHelper.getSpreadsheetId();
            if (!TextUtils.isEmpty(spreadSheetId)) {
                batchUpdate = mService.spreadsheets().batchUpdate(spreadSheetId, content);
                BatchUpdateSpreadsheetResponse response = batchUpdate.execute();
                ExpenseHelper.updateSyncStatusAfterBackup(expensesToBackup);
                AppHelper.setFirstBackupComplete(true);
                return new LoaderResponse(LoaderResponse.SUCCESS, null, null);
            } else {
                return new LoaderResponse(LoaderResponse.FAILURE, null, null);
            }
        } catch (UserRecoverableAuthIOException e) {
            return new LoaderResponse(LoaderResponse.FAILURE, e.getIntent(), null);
        } catch (IOException e) {
            return new LoaderResponse(LoaderResponse.FAILURE, null, null);
        }
    }
}
