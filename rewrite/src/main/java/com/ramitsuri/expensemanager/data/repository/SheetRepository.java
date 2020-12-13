package com.ramitsuri.expensemanager.data.repository;

import android.accounts.Account;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.services.sheets.v4.model.AddSheetResponse;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.SheetProperties;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.ramitsuri.expensemanager.AppExecutors;
import com.ramitsuri.expensemanager.constants.stringDefs.BackupInfoStatus;
import com.ramitsuri.expensemanager.entities.Budget;
import com.ramitsuri.expensemanager.entities.Category;
import com.ramitsuri.expensemanager.entities.Expense;
import com.ramitsuri.expensemanager.entities.PaymentMethod;
import com.ramitsuri.expensemanager.entities.RecurringExpenseInfo;
import com.ramitsuri.expensemanager.entities.SheetInfo;
import com.ramitsuri.expensemanager.utils.AppHelper;
import com.ramitsuri.expensemanager.utils.SheetRequestHelper;
import com.ramitsuri.sheetscore.SheetsProcessor;
import com.ramitsuri.sheetscore.consumerResponse.AddSheetConsumerResponse;
import com.ramitsuri.sheetscore.consumerResponse.CreateSpreadsheetConsumerResponse;
import com.ramitsuri.sheetscore.consumerResponse.EntitiesConsumerResponse;
import com.ramitsuri.sheetscore.consumerResponse.InsertConsumerResponse;
import com.ramitsuri.sheetscore.consumerResponse.RangeConsumerResponse;
import com.ramitsuri.sheetscore.consumerResponse.RangesConsumerResponse;
import com.ramitsuri.sheetscore.consumerResponse.SheetMetadata;
import com.ramitsuri.sheetscore.consumerResponse.SheetsMetadataConsumerResponse;
import com.ramitsuri.sheetscore.intdef.Dimension;
import com.ramitsuri.sheetscore.spreadsheetResponse.BaseResponse;
import com.ramitsuri.sheetscore.spreadsheetResponse.CreateSpreadsheetResponse;
import com.ramitsuri.sheetscore.spreadsheetResponse.SpreadsheetSpreadsheetResponse;
import com.ramitsuri.sheetscore.spreadsheetResponse.UpdateSpreadsheetResponse;
import com.ramitsuri.sheetscore.spreadsheetResponse.ValueRangeSpreadsheetResponse;
import com.ramitsuri.sheetscore.spreadsheetResponse.ValueRangesSpreadsheetResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import timber.log.Timber;

public class SheetRepository {

    private SheetsProcessor mSheetsProcessor;
    private final AppExecutors mExecutors;

    public SheetRepository(@NonNull Context context, @NonNull String appName,
            @NonNull Account account, @NonNull List<String> scopes,
            @NonNull AppExecutors executors) {
        mSheetsProcessor = new SheetsProcessor(context, appName, account, scopes);
        mExecutors = executors;
    }

    public LiveData<CreateSpreadsheetConsumerResponse> createSpreadsheet(
            @Nonnull final String spreadsheetTitle,
            @Nonnull final String entitiesSheetTitle,
            @Nonnull final List<PaymentMethod> paymentMethods,
            @Nonnull final List<Category> categories,
            @Nonnull final List<String> months,
            @Nonnull final List<Budget> budgets) {
        final MutableLiveData<CreateSpreadsheetConsumerResponse> responseLiveData =
                new MutableLiveData<>();
        mExecutors.networkIO().execute(new Runnable() {
            @Override
            public void run() {
                CreateSpreadsheetConsumerResponse response = getCreateSpreadsheetResponse(
                        spreadsheetTitle,
                        entitiesSheetTitle,
                        paymentMethods,
                        categories,
                        months,
                        budgets);
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
                onOperationSuccess();
            }
        } catch (IOException e) {
            Timber.e(e);
            consumerResponse.setException(e);
            onOperationException(e);
        }
        return consumerResponse;
    }

    public LiveData<EntitiesConsumerResponse> getEntity(@Nonnull final String spreadsheetId,
            @Nonnull final String range) {
        final MutableLiveData<EntitiesConsumerResponse> responseLiveData =
                new MutableLiveData<>();
        mExecutors.networkIO().execute(new Runnable() {
            @Override
            public void run() {
                EntitiesConsumerResponse response = getEntityDataResponse(spreadsheetId,
                        range);
                responseLiveData.postValue(response);
            }
        });
        return responseLiveData;
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
                onOperationSuccess();
            }
            consumerResponse.setStringLists(entityLists);
        } catch (IOException e) {
            Timber.e(e);
            consumerResponse.setException(e);
            onOperationException(e);
        }
        return consumerResponse;
    }

    public RangesConsumerResponse getRangesDataResponse(@Nonnull String spreadsheetId,
            @Nonnull List<String> range) {
        RangesConsumerResponse consumerResponse = new RangesConsumerResponse();
        try {
            BaseResponse response =
                    mSheetsProcessor.getSheetData(spreadsheetId, range, Dimension.ROWS);

            List<RangeConsumerResponse> values = new ArrayList<>();
            if (response != null) {
                List<ValueRange> valueRangeList =
                        ((ValueRangesSpreadsheetResponse)response).getValueRanges();
                if (valueRangeList != null) {
                    for (ValueRange valueRange : valueRangeList) {
                        RangeConsumerResponse value = new RangeConsumerResponse();
                        value.setRange(valueRange.getRange());
                        List<List<Object>> objectLists = valueRange.getValues();
                        List<List<Object>> responseObjectLists = new ArrayList<>();
                        if (objectLists != null) {
                            for (List<Object> objectList : objectLists) {
                                if (objectList == null || objectList.size() == 0) {
                                    continue;
                                }
                                List<Object> responseObjectList =
                                        new ArrayList<>(objectList.size());
                                for (Object object : objectList) {
                                    if (object != null) {
                                        responseObjectList.add(object);
                                    }
                                }
                                responseObjectLists.add(responseObjectList);
                            }
                        }
                        value.setObjectLists(responseObjectLists);
                        values.add(value);
                    }
                    onOperationSuccess();
                }
            }
            consumerResponse.setValues(values);
        } catch (IOException e) {
            Timber.e(e);
            consumerResponse.setException(e);
            onOperationException(e);
        }
        return consumerResponse;
    }

    public InsertConsumerResponse getInsertRangeResponse(@Nonnull String spreadsheetId,
            @NonNull List<Expense> expenses,
            @Nullable List<Integer> editedMonths,
            @Nonnull List<SheetInfo> sheetInfos,
            @Nullable List<RecurringExpenseInfo> recurringExpenses,
            @Nullable SheetInfo recurringSheetInfo) {
        InsertConsumerResponse consumerResponse = new InsertConsumerResponse();
        try {
            BatchUpdateSpreadsheetRequest requestBody = SheetRequestHelper
                    .getUpdateRequestBody(expenses, editedMonths, sheetInfos, recurringExpenses,
                            recurringSheetInfo);
            mSheetsProcessor.updateSheet(spreadsheetId, requestBody);
            consumerResponse.setSuccessful(true);
            onOperationSuccess();
        } catch (IOException e) {
            Timber.e(e);
            consumerResponse.setSuccessful(false);
            consumerResponse.setException(e);
            onOperationException(e);
        }
        return consumerResponse;
    }

    public InsertConsumerResponse getInsertEntitiesResponse(@Nonnull String spreadsheetId,
            @Nullable List<Category> categories,
            @Nullable List<PaymentMethod> paymentMethods,
            @Nullable List<Budget> budgets,
            @Nonnull List<String> months,
            int entitiesSheetId) {
        InsertConsumerResponse consumerResponse = new InsertConsumerResponse();
        try {
            BatchUpdateSpreadsheetRequest requestBody = SheetRequestHelper
                    .getUpdateEntitiesRequestBody(categories, paymentMethods, budgets, months,
                            entitiesSheetId);
            if (requestBody != null) {
                mSheetsProcessor.updateSheet(spreadsheetId, requestBody);
                consumerResponse.setSuccessful(true);
                onOperationSuccess();
            } else {
                consumerResponse.setSuccessful(false);
            }
        } catch (IOException e) {
            Timber.e(e);
            consumerResponse.setSuccessful(false);
            consumerResponse.setException(e);
            onOperationException(e);
        }
        return consumerResponse;
    }

    public CreateSpreadsheetConsumerResponse getCreateSpreadsheetResponse(
            @Nonnull String spreadsheetTitle,
            @Nonnull String entitiesSheetTitle,
            @Nonnull List<PaymentMethod> paymentMethods,
            @Nonnull List<Category> categories,
            @Nonnull List<String> months,
            @Nonnull List<Budget> budgets) {
        CreateSpreadsheetConsumerResponse consumerResponse =
                new CreateSpreadsheetConsumerResponse();
        try {
            Spreadsheet requestBody = SheetRequestHelper
                    .getCreateRequestBody(spreadsheetTitle, entitiesSheetTitle,
                            paymentMethods, categories, months, budgets);
            CreateSpreadsheetResponse response =
                    (CreateSpreadsheetResponse)mSheetsProcessor.createSheet(requestBody);
            String spreadsheetId = response.getResponse().getSpreadsheetId();
            List<SheetMetadata> sheets = new ArrayList<>();
            for (Sheet sheet : response.getResponse().getSheets()) {
                SheetMetadata sheetMetadata =
                        new SheetMetadata(sheet.getProperties().getSheetId(),
                                sheet.getProperties().getTitle());
                sheets.add(sheetMetadata);
            }
            consumerResponse.setSpreadsheetId(spreadsheetId);
            consumerResponse.setSheetMetadataList(sheets);
            onOperationSuccess();
        } catch (IOException e) {
            Timber.e(e);
            consumerResponse.setException(e);
            onOperationException(e);
        }
        return consumerResponse;
    }

    @NonNull
    public AddSheetConsumerResponse getAddSheetResponse(@Nonnull String spreadsheetId,
            @Nonnull String sheetName,
            int sheetIndex) {
        AddSheetConsumerResponse consumerResponse = new AddSheetConsumerResponse();
        try {
            BatchUpdateSpreadsheetRequest request = SheetRequestHelper
                    .getAddSheetRequest(sheetName, sheetIndex);
            UpdateSpreadsheetResponse response = (UpdateSpreadsheetResponse)mSheetsProcessor
                    .updateSheet(spreadsheetId, request);
            if (response.getBatchUpdateSpreadsheetResponse() != null &&
                    response.getBatchUpdateSpreadsheetResponse().getReplies() != null &&
                    !response.getBatchUpdateSpreadsheetResponse().getReplies().isEmpty() &&
                    response.getBatchUpdateSpreadsheetResponse().getReplies().get(0) != null) {
                AddSheetResponse addSheetResponse =
                        response.getBatchUpdateSpreadsheetResponse().getReplies().get(0)
                                .getAddSheet();
                if (addSheetResponse != null && addSheetResponse.getProperties() != null) {
                    SheetProperties properties = addSheetResponse.getProperties();
                    consumerResponse.setSheetMetadata(
                            new SheetMetadata(properties.getSheetId(), properties.getTitle()));
                }
            }
        } catch (IOException e) {
            Timber.e(e);
            consumerResponse.setException(e);
            onOperationException(e);
        }
        return consumerResponse;
    }

    public void refreshProcessors(@NonNull Context context, @NonNull String appName,
            @NonNull Account account, @NonNull List<String> scopes) {
        mSheetsProcessor = new SheetsProcessor(context, appName, account, scopes);
        //mDriveProcessor = new DriveProcessor(context, appName, account, scopes);
    }

    private void onOperationSuccess() {
        AppHelper.setBackupInfoStatus(BackupInfoStatus.OK);
    }

    private void onOperationException(Exception e) {
        if (e instanceof UserRecoverableAuthIOException) {
            AppHelper.setBackupInfoStatus(BackupInfoStatus.ERROR);
        }
    }
}
