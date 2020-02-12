package com.ramitsuri.expensemanager.data.repository;

import android.accounts.Account;
import android.content.Context;

import com.google.api.services.drive.model.File;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.Sheet;
import com.ramitsuri.expensemanager.AppExecutors;
import com.ramitsuri.expensemanager.data.ExpenseManagerDatabase;
import com.ramitsuri.expensemanager.entities.Expense;
import com.ramitsuri.expensemanager.entities.SheetInfo;
import com.ramitsuri.expensemanager.utils.SheetRequestHelper;
import com.ramitsuri.sheetscore.DriveProcessor;
import com.ramitsuri.sheetscore.SheetsProcessor;
import com.ramitsuri.sheetscore.consumerResponse.EntitiesConsumerResponse;
import com.ramitsuri.sheetscore.consumerResponse.FileConsumerResponse;
import com.ramitsuri.sheetscore.consumerResponse.InsertConsumerResponse;
import com.ramitsuri.sheetscore.consumerResponse.RangeConsumerResponse;
import com.ramitsuri.sheetscore.consumerResponse.SheetMetadata;
import com.ramitsuri.sheetscore.consumerResponse.SheetsMetadataConsumerResponse;
import com.ramitsuri.sheetscore.driveResponse.FileCopyResponse;
import com.ramitsuri.sheetscore.intdef.Dimension;
import com.ramitsuri.sheetscore.spreadsheetResponse.BaseResponse;
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
    private DriveProcessor mDriveProcessor;
    private AppExecutors mExecutors;
    private ExpenseManagerDatabase mDatabase;

    public SheetRepository(@NonNull Context context, @NonNull String appName,
            @NonNull Account account, @NonNull List<String> scopes,
            @NonNull AppExecutors executors, @Nonnull ExpenseManagerDatabase database) {
        mSheetsProcessor = new SheetsProcessor(context, appName, account, scopes);
        mDriveProcessor = new DriveProcessor(context, appName, account, scopes);
        mExecutors = executors;
        mDatabase = database;
    }

    /**
     * Method that runs in a background thread and prepares sheet metadata response with names and
     * ids of all the sheets present in a spreadsheet
     */
    public LiveData<List<SheetInfo>> getSheetInfos(@Nonnull final String spreadsheetId,
            boolean fetch) {
        final MutableLiveData<List<SheetInfo>> sheetInfosLiveData =
                new MutableLiveData<>();
        if (fetch) {
            Timber.i("Getting sheet infos from Google Sheet");
            mExecutors.networkIO().execute(new Runnable() {
                @Override
                public void run() {
                    SheetsMetadataConsumerResponse response =
                            getSheetsMetadataResponse(spreadsheetId);
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
    public LiveData<EntitiesConsumerResponse> getEntityData(@Nonnull final String spreadsheetId,
            @Nonnull final String range) {
        final MutableLiveData<EntitiesConsumerResponse> responseLiveData =
                new MutableLiveData<>();
        mExecutors.networkIO().execute(new Runnable() {
            @Override
            public void run() {
                EntitiesConsumerResponse response = getEntityDataResponse(spreadsheetId, range);
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
    public LiveData<RangeConsumerResponse> getRangeData(@Nonnull final String spreadsheetId,
            @Nonnull final String range) {
        final MutableLiveData<RangeConsumerResponse> responseLiveData =
                new MutableLiveData<>();
        mExecutors.networkIO().execute(new Runnable() {
            @Override
            public void run() {
                RangeConsumerResponse response = getRangeDataResponse(spreadsheetId, range);
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
    public LiveData<InsertConsumerResponse> insertRange(@Nonnull final String spreadsheetId,
            @NonNull final List<Expense> expenses,
            final int sheetId) {
        final MutableLiveData<InsertConsumerResponse> responseLiveData =
                new MutableLiveData<>();
        mExecutors.networkIO().execute(new Runnable() {
            @Override
            public void run() {
                InsertConsumerResponse response =
                        getInsertRangeResponse(spreadsheetId, expenses, sheetId);
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
    public LiveData<FileConsumerResponse> copySpreadsheet(@Nonnull final String spreadsheetId,
            @NonNull final String fileName) {
        final MutableLiveData<FileConsumerResponse> responseLiveData = new MutableLiveData<>();
        mExecutors.networkIO().execute(new Runnable() {
            @Override
            public void run() {
                FileConsumerResponse response = getCopySpreadsheetResponse(spreadsheetId, fileName);
                responseLiveData.postValue(response);
            }
        });
        return responseLiveData;
    }

    public SheetsMetadataConsumerResponse getSheetsMetadataResponse(@Nonnull String spreadsheetId) {
        SheetsMetadataConsumerResponse consumerResponse = new SheetsMetadataConsumerResponse();
        try {
            BaseResponse response = mSheetsProcessor.getSheetsInSpreadsheet(spreadsheetId);

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

    public EntitiesConsumerResponse getEntityDataResponse(@Nonnull String spreadsheetId,
            @Nonnull String range) {
        return getEntityDataResponse(spreadsheetId, range, Dimension.COLUMNS);
    }

    public EntitiesConsumerResponse getEntityDataResponse(@Nonnull String spreadsheetId,
            @Nonnull String range, @Dimension String dimension) {
        EntitiesConsumerResponse consumerResponse = new EntitiesConsumerResponse();
        try {
            BaseResponse response =
                    mSheetsProcessor.getSheetData(spreadsheetId, range, dimension);

            List<List<Object>> objectLists =
                    ((ValueRangeSpreadsheetResponse)response).getValueRange()
                            .getValues();
            List<List<String>> entityLists = new ArrayList<>();
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

    public RangeConsumerResponse getRangeDataResponse(@Nonnull String spreadsheetId,
            @Nonnull String range) {
        RangeConsumerResponse consumerResponse = new RangeConsumerResponse();
        try {
            BaseResponse response =
                    mSheetsProcessor.getSheetData(spreadsheetId, range, Dimension.ROWS);

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

    public InsertConsumerResponse getInsertRangeResponse(@Nonnull String spreadsheetId,
            @NonNull List<Expense> expenses,
            int sheetId) {
        InsertConsumerResponse consumerResponse = new InsertConsumerResponse();
        try {
            BatchUpdateSpreadsheetRequest requestBody =
                    SheetRequestHelper.getUpdateRequestBody(expenses, sheetId);
            if (requestBody != null) {
                mSheetsProcessor.updateSheet(spreadsheetId, requestBody);
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

    public FileConsumerResponse getCopySpreadsheetResponse(@Nonnull String spreadsheetId,
            @NonNull String fileName) {
        FileConsumerResponse consumerResponse = new FileConsumerResponse();
        try {
            File requestBody =
                    SheetRequestHelper.getCopyFileRequestBody(fileName);
            BaseResponse response = mDriveProcessor.copyFile(spreadsheetId, requestBody);
            String newFileId = ((FileCopyResponse)response).getResponse().getId();
            consumerResponse.setFileId(newFileId);
        } catch (IOException e) {
            Timber.e(e);
            consumerResponse.setException(e);
        } catch (Exception e) {
            Timber.e(e);
            consumerResponse.setException(e);
        }
        return consumerResponse;
    }

    public void refreshProcessors(@NonNull Context context, @NonNull String appName,
            @NonNull Account account, @NonNull List<String> scopes) {
        mSheetsProcessor = new SheetsProcessor(context, appName, account, scopes);
        mDriveProcessor = new DriveProcessor(context, appName, account, scopes);
    }
}
