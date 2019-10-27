package com.ramitsuri.expensemanagerrewrite.data.repository;

import android.accounts.Account;
import android.content.Context;

import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.Sheet;
import com.ramitsuri.expensemanagerrewrite.AppExecutors;
import com.ramitsuri.expensemanagerrewrite.entities.Expense;
import com.ramitsuri.expensemanagerrewrite.utils.SheetRequestHelper;
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

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import timber.log.Timber;

public class SheetRepository {

    private SheetsProcessor mSheetsProcessor;
    private AppExecutors mExecutors;

    public SheetRepository(@NonNull Context context, @NonNull String appName,
            @NonNull Account account, @NonNull String spreadsheetId, @NonNull List<String> scopes,
            @NonNull AppExecutors executors) {
        mSheetsProcessor = new SheetsProcessor(context, appName, account, spreadsheetId, scopes);
        mExecutors = executors;
    }

    /**
     * Method that runs in a background thread and prepares sheet metadata response with names and
     * ids of all the sheets present in a spreadsheet
     */
    public LiveData<SheetsMetadataConsumerResponse> getSheetsMetadata() {
        final MutableLiveData<SheetsMetadataConsumerResponse> responseLiveData =
                new MutableLiveData<>();
        mExecutors.networkIO().execute(new Runnable() {
            @Override
            public void run() {
                SheetsMetadataConsumerResponse response = getSheetsMetadataResponse();
                responseLiveData.postValue(response);
            }
        });
        return responseLiveData;
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
            @NonNull final List<String> categories,
            @NonNull final List<String> paymentMethods,
            @NonNull final String sheetId) {
        final MutableLiveData<InsertConsumerResponse> responseLiveData =
                new MutableLiveData<>();
        mExecutors.networkIO().execute(new Runnable() {
            @Override
            public void run() {
                InsertConsumerResponse response =
                        getInsertRangeResponse(expenses, categories, paymentMethods, sheetId);
                responseLiveData.postValue(response);
            }
        });
        return responseLiveData;
    }

    private SheetsMetadataConsumerResponse getSheetsMetadataResponse() {
        SheetsMetadataConsumerResponse consumerResponse = new SheetsMetadataConsumerResponse();
        try {
            BaseSpreadsheetResponse response = mSheetsProcessor.getSheetsInSpreadsheet();

            List<SheetMetadata> sheetMetadataList = new ArrayList<>();
            for (Sheet sheet : ((SpreadsheetSpreadsheetResponse)response).getSpreadsheet()
                    .getSheets()) {
                int sheetId = sheet.getProperties().getSheetId();
                String sheetName = sheet.getProperties().getTitle();
                SheetMetadata sheetMetadata = new SheetMetadata(sheetId, sheetName);
                sheetMetadataList.add(sheetMetadata);
            }
            consumerResponse.setSheetMetadataList(sheetMetadataList);
        } catch (IOException e) {
            Timber.e(e);
            consumerResponse.setException(e);
        } catch (Exception e) {
            Timber.e(e);
            consumerResponse.setException(e);
        }
        return consumerResponse;
    }

    private EntitiesConsumerResponse getEntityDataResponse(String range) {
        EntitiesConsumerResponse consumerResponse = new EntitiesConsumerResponse();
        try {
            BaseSpreadsheetResponse response =
                    mSheetsProcessor.getSheetData(range, Dimension.COLUMNS);

            List<List<Object>> objectLists =
                    ((ValueRangeSpreadsheetResponse)response).getValueRange()
                            .getValues();
            List<List<String>> entityLists = new ArrayList<>(EntitiesConsumerResponse.MAX_ENTITIES);
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

    private RangeConsumerResponse getRangeDataResponse(String range) {
        RangeConsumerResponse consumerResponse = new RangeConsumerResponse();
        try {
            BaseSpreadsheetResponse response =
                    mSheetsProcessor.getSheetData(range, Dimension.ROWS);

            List<List<Object>> objectLists =
                    ((ValueRangeSpreadsheetResponse)response).getValueRange()
                            .getValues();
            List<List<Object>> responseObjectLists = new ArrayList<>();
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
            @NonNull List<String> categories,
            @NonNull List<String> paymentMethods,
            @NonNull String sheetId) {
        InsertConsumerResponse consumerResponse = new InsertConsumerResponse();
        try {
            BatchUpdateSpreadsheetRequest requestBody =
                    SheetRequestHelper
                            .getUpdateRequestBody(expenses, categories, paymentMethods, sheetId);
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
