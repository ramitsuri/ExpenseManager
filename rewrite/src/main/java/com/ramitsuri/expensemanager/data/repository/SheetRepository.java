package com.ramitsuri.expensemanager.data.repository;

import android.accounts.Account;
import android.content.Context;

import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.Sheet;
import com.ramitsuri.expensemanager.AppExecutors;
import com.ramitsuri.expensemanager.data.ExpenseManagerDatabase;
import com.ramitsuri.expensemanager.entities.Expense;
import com.ramitsuri.expensemanager.entities.SheetInfo;
import com.ramitsuri.expensemanager.utils.SheetRequestHelper;
import com.ramitsuri.sheetscore.SheetsProcessor;
import com.ramitsuri.sheetscore.consumerResponse.EntitiesConsumerResponse;
import com.ramitsuri.sheetscore.consumerResponse.InsertConsumerResponse;
import com.ramitsuri.sheetscore.consumerResponse.RangeConsumerResponse;
import com.ramitsuri.sheetscore.consumerResponse.SheetMetadata;
import com.ramitsuri.sheetscore.consumerResponse.SheetsMetadataConsumerResponse;
import com.ramitsuri.sheetscore.intdef.Dimension;
import com.ramitsuri.sheetscore.spreadsheetResponse.BaseSpreadsheetResponse;
import com.ramitsuri.sheetscore.spreadsheetResponse.SpreadsheetSpreadsheetResponse;
import com.ramitsuri.sheetscore.spreadsheetResponse.ValueRangeSpreadsheetResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import timber.log.Timber;

public class SheetRepository {

    private SheetsProcessor mSheetsProcessor;
    private AppExecutors mExecutors;
    private ExpenseManagerDatabase mDatabase;

    public SheetRepository(@NonNull Context context, @NonNull String appName,
            @NonNull Account account, @NonNull String spreadsheetId, @NonNull List<String> scopes,
            @NonNull AppExecutors executors, @Nonnull ExpenseManagerDatabase database) {
        mSheetsProcessor = new SheetsProcessor(context, appName, account, spreadsheetId, scopes);
        mExecutors = executors;
        mDatabase = database;
    }

    /**
     * Method that runs in a background thread and prepares sheet metadata response with names and
     * ids of all the sheets present in a spreadsheet
     */
    public LiveData<List<SheetInfo>> getSheetInfos(boolean fetch) {
        final MutableLiveData<List<SheetInfo>> sheetInfosLiveData =
                new MutableLiveData<>();
        if (fetch) {
            Timber.i("Getting sheet infos from Google Sheet");
            mExecutors.networkIO().execute(new Runnable() {
                @Override
                public void run() {
                    SheetsMetadataConsumerResponse response = getSheetsMetadataResponse();
                    List<SheetInfo> sheetInfos = new ArrayList<>();
                    if (response.getSheetMetadataList() != null) {
                        for (SheetMetadata sheetMetadata : response.getSheetMetadataList()) {
                            sheetInfos.add(new SheetInfo(sheetMetadata));
                        }
                    }
                    sheetInfosLiveData.postValue(sheetInfos);
                    mDatabase.sheetDao().setAll(sheetInfos);
                }
            });
        } else {
            Timber.i("Getting sheet infos from local db");
            mExecutors.diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    List<SheetInfo> sheetInfos = new ArrayList<>();
                    List<SheetInfo> values = mDatabase.sheetDao().getAll();
                    if (values != null) {
                        sheetInfos.addAll(values);
                    }
                    sheetInfosLiveData.postValue(sheetInfos);
                }
            });
        }
        return sheetInfosLiveData;
    }

    /**
     * Method that runs in a background thread and prepares entity data from the "Entities" sheet
     * in a spreadsheet
     * <p>
     * EX: "Entities!A1:J20"
     */
    public LiveData<EntitiesConsumerResponse> getEntityData(final String range) {
        final MutableLiveData<EntitiesConsumerResponse> responseLiveData =
                new MutableLiveData<>();
        mExecutors.networkIO().execute(new Runnable() {
            @Override
            public void run() {
                EntitiesConsumerResponse response = getEntityDataResponse(range);
                responseLiveData.postValue(response);
            }
        });
        return responseLiveData;
    }

    /**
     * Method that runs in a background thread and prepares range data for a given range
     * in a spreadsheet
     * <p>
     * EX: "Aug19!A19:F"
     */
    public LiveData<RangeConsumerResponse> getRangeData(final String range) {
        final MutableLiveData<RangeConsumerResponse> responseLiveData =
                new MutableLiveData<>();
        mExecutors.networkIO().execute(new Runnable() {
            @Override
            public void run() {
                RangeConsumerResponse response = getRangeDataResponse(range);
                responseLiveData.postValue(response);
            }
        });
        return responseLiveData;
    }

    /**
     * Method that runs in a background thread and prepares range data for a given range
     * in a spreadsheet
     * <p>
     * EX: "Aug19!A19:F"
     */
    public LiveData<InsertConsumerResponse> insertRange(
            @NonNull final List<Expense> expenses,
            final int sheetId) {
        final MutableLiveData<InsertConsumerResponse> responseLiveData =
                new MutableLiveData<>();
        mExecutors.networkIO().execute(new Runnable() {
            @Override
            public void run() {
                InsertConsumerResponse response = getInsertRangeResponse(expenses, sheetId);
                responseLiveData.postValue(response);
            }
        });
        return responseLiveData;
    }

    public SheetsMetadataConsumerResponse getSheetsMetadataResponse() {
        SheetsMetadataConsumerResponse consumerResponse = new SheetsMetadataConsumerResponse();
        try {
            BaseSpreadsheetResponse response = mSheetsProcessor.getSheetsInSpreadsheet();

            List<SheetMetadata> sheetMetadataList = new ArrayList<>();
            SpreadsheetSpreadsheetResponse spreadsheetResponse =
                    (SpreadsheetSpreadsheetResponse)response;
            if (spreadsheetResponse != null) {
                for (Sheet sheet : spreadsheetResponse.getSpreadsheet().getSheets()) {
                    int sheetId = sheet.getProperties().getSheetId();
                    String sheetName = sheet.getProperties().getTitle();
                    SheetMetadata sheetMetadata = new SheetMetadata(sheetId, sheetName);
                    sheetMetadataList.add(sheetMetadata);
                }
                consumerResponse.setSheetMetadataList(sheetMetadataList);
            }
        } catch (IOException e) {
            Timber.e(e);
            consumerResponse.setException(e);
        } catch (Exception e) {
            Timber.e(e);
            consumerResponse.setException(e);
        }
        return consumerResponse;
    }

    public EntitiesConsumerResponse getEntityDataResponse(String range) {
        EntitiesConsumerResponse consumerResponse = new EntitiesConsumerResponse();
        try {
            BaseSpreadsheetResponse response =
                    mSheetsProcessor.getSheetData(range, Dimension.COLUMNS);

            List<List<Object>> objectLists =
                    ((ValueRangeSpreadsheetResponse)response).getValueRange()
                            .getValues();
            List<List<String>> entityLists = new ArrayList<>(EntitiesConsumerResponse.MAX_ENTITIES);
            if (objectLists != null) {
                for (List<Object> objectList : objectLists) {
                    if (objectList == null || objectList.size() == 0) {
                        continue;
                    }
                    List<String> entityList = new ArrayList<>(objectList.size());
                    for (Object object : objectList) {
                        if (object != null) {
                            entityList.add(object.toString());
                        }
                    }
                    entityLists.add(entityList);
                    if (entityLists.size() == EntitiesConsumerResponse.MAX_ENTITIES) {
                        break;
                    }
                }
            }
            consumerResponse.setStringLists(entityLists);
        } catch (IOException e) {
            Timber.e(e);
            consumerResponse.setException(e);
        } catch (Exception e) {
            Timber.e(e);
            consumerResponse.setException(e);
        }
        return consumerResponse;
    }

    public RangeConsumerResponse getRangeDataResponse(String range) {
        RangeConsumerResponse consumerResponse = new RangeConsumerResponse();
        try {
            BaseSpreadsheetResponse response =
                    mSheetsProcessor.getSheetData(range, Dimension.ROWS);

            List<List<Object>> objectLists =
                    ((ValueRangeSpreadsheetResponse)response).getValueRange()
                            .getValues();
            List<List<Object>> responseObjectLists = new ArrayList<>();
            if (objectLists != null) {
                for (List<Object> objectList : objectLists) {
                    if (objectList == null || objectList.size() == 0) {
                        continue;
                    }
                    List<Object> responseObjectList = new ArrayList<>(objectList.size());
                    for (Object object : objectList) {
                        if (object != null) {
                            responseObjectList.add(object);
                        }
                    }
                    responseObjectLists.add(responseObjectList);
                }
            }
            consumerResponse.setObjectLists(responseObjectLists);
        } catch (IOException e) {
            Timber.e(e);
            consumerResponse.setException(e);
        } catch (Exception e) {
            Timber.e(e);
            consumerResponse.setException(e);
        }
        return consumerResponse;
    }

    public InsertConsumerResponse getInsertRangeResponse(@NonNull List<Expense> expenses,
            int sheetId) {
        InsertConsumerResponse consumerResponse = new InsertConsumerResponse();
        try {
            BatchUpdateSpreadsheetRequest requestBody =
                    SheetRequestHelper.getUpdateRequestBody(expenses, sheetId);
            if (requestBody != null) {
                mSheetsProcessor.updateSheet(requestBody);
                consumerResponse.setSuccessful(true);
            } else {
                consumerResponse.setSuccessful(false);
            }
        } catch (IOException e) {
            Timber.e(e);
            consumerResponse.setSuccessful(false);
            consumerResponse.setException(e);
        } catch (Exception e) {
            Timber.e(e);
            consumerResponse.setSuccessful(false);
            consumerResponse.setException(e);
        }
        return consumerResponse;
    }
}
