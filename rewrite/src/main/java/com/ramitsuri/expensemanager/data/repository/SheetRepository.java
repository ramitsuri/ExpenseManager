package com.ramitsuri.expensemanager.data.repository;

import android.accounts.Account;
import android.content.Context;

import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.ramitsuri.expensemanager.AppExecutors;
import com.ramitsuri.expensemanager.entities.Budget;
import com.ramitsuri.expensemanager.entities.Category;
import com.ramitsuri.expensemanager.entities.Expense;
import com.ramitsuri.expensemanager.entities.PaymentMethod;
import com.ramitsuri.expensemanager.entities.SheetInfo;
import com.ramitsuri.expensemanager.utils.SheetRequestHelper;
import com.ramitsuri.sheetscore.SheetsProcessor;
import com.ramitsuri.sheetscore.consumerResponse.EntitiesConsumerResponse;
import com.ramitsuri.sheetscore.consumerResponse.InsertConsumerResponse;
import com.ramitsuri.sheetscore.consumerResponse.RangeConsumerResponse;
import com.ramitsuri.sheetscore.consumerResponse.RangesConsumerResponse;
import com.ramitsuri.sheetscore.consumerResponse.SheetMetadata;
import com.ramitsuri.sheetscore.consumerResponse.SheetsMetadataConsumerResponse;
import com.ramitsuri.sheetscore.intdef.Dimension;
import com.ramitsuri.sheetscore.spreadsheetResponse.BaseResponse;
import com.ramitsuri.sheetscore.spreadsheetResponse.SpreadsheetSpreadsheetResponse;
import com.ramitsuri.sheetscore.spreadsheetResponse.ValueRangeSpreadsheetResponse;
import com.ramitsuri.sheetscore.spreadsheetResponse.ValueRangesSpreadsheetResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import timber.log.Timber;

public class SheetRepository {

    private SheetsProcessor mSheetsProcessor;

    public SheetRepository(@NonNull Context context, @NonNull String appName,
            @NonNull Account account, @NonNull List<String> scopes,
            @NonNull AppExecutors executors) {
        mSheetsProcessor = new SheetsProcessor(context, appName, account, scopes);
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
                }
            }
            consumerResponse.setValues(values);
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
            @Nullable List<Integer> editedMonths,
            @Nonnull List<SheetInfo> sheetInfos) {
        InsertConsumerResponse consumerResponse = new InsertConsumerResponse();
        try {
            BatchUpdateSpreadsheetRequest requestBody = SheetRequestHelper
                    .getUpdateRequestBody(expenses, editedMonths, sheetInfos);
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

    public InsertConsumerResponse getInsertEntitiesResponse(@Nonnull String spreadsheetId,
            @Nullable List<Category> categories,
            @Nullable List<PaymentMethod> paymentMethods,
            @Nullable List<Budget> budgets,
            int entitiesSheetId) {
        InsertConsumerResponse consumerResponse = new InsertConsumerResponse();
        try {
            BatchUpdateSpreadsheetRequest requestBody = SheetRequestHelper
                    .getUpdateEntitiesRequestBody(categories, paymentMethods, budgets,
                            entitiesSheetId);
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

    public void refreshProcessors(@NonNull Context context, @NonNull String appName,
            @NonNull Account account, @NonNull List<String> scopes) {
        mSheetsProcessor = new SheetsProcessor(context, appName, account, scopes);
        //mDriveProcessor = new DriveProcessor(context, appName, account, scopes);
    }
}
