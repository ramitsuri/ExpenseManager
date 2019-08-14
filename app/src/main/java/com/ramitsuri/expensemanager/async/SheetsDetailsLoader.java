package com.ramitsuri.expensemanager.async;

import android.content.Context;
import android.text.TextUtils;

import androidx.loader.content.AsyncTaskLoader;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.constants.Others;
import com.ramitsuri.expensemanager.entities.LoaderResponse;
import com.ramitsuri.expensemanager.entities.SheetDto;
import com.ramitsuri.expensemanager.helper.AppHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SheetsDetailsLoader extends AsyncTaskLoader<List<SheetDto>> {

    private com.google.api.services.sheets.v4.Sheets mService = null;

    public SheetsDetailsLoader(Context context, String accountName) {
        super(context);
        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(context,
                Arrays.asList(Others.SCOPES)).setBackOff(new ExponentialBackOff());
        credential.setSelectedAccountName(AppHelper.getAccountName());
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.sheets.v4.Sheets.Builder(
                transport, jsonFactory, credential)
                .setApplicationName(context.getString(R.string.app_name))
                .build();
    }

    @Override
    public List<SheetDto> loadInBackground() {
        try {
            String spreadsheetId = AppHelper.getSpreadsheetId();
            if (!TextUtils.isEmpty(spreadsheetId)) {
                Sheets.Spreadsheets.Get request = mService.spreadsheets().get(spreadsheetId);
                request.setIncludeGridData(false);

                Spreadsheet response = request.execute();
                List<SheetDto> sheets = new ArrayList<>();
                for (Sheet sheet : response.getSheets()) {
                    int sheetId = sheet.getProperties().getSheetId();
                    String sheetName = sheet.getProperties().getTitle();
                    SheetDto sheetDto = new SheetDto(sheetId, sheetName);
                    sheets.add(sheetDto);
                }
                return sheets;
            } else {
                return null;
            }
        } catch (UserRecoverableAuthIOException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }
}
