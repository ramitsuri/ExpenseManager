package com.ramitsuri.expensemanager.work;

import android.content.Context;
import android.text.TextUtils;

import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.constants.Constants;
import com.ramitsuri.expensemanager.data.ExpenseManagerDatabase;
import com.ramitsuri.expensemanager.entities.Budget;
import com.ramitsuri.expensemanager.entities.SheetInfo;
import com.ramitsuri.expensemanager.utils.AppHelper;
import com.ramitsuri.sheetscore.consumerResponse.EntitiesConsumerResponse;
import com.ramitsuri.sheetscore.consumerResponse.SheetMetadata;
import com.ramitsuri.sheetscore.consumerResponse.SheetsMetadataConsumerResponse;
import com.ramitsuri.sheetscore.intdef.Dimension;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.work.WorkerParameters;

public class SyncWorker extends BaseWorker {

    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String workType = getInputData().getString(Constants.Work.TYPE);

        if (MainApplication.getInstance().getSheetRepository() == null) {
            onFailure(workType, "Sheet repo null");
            return Result.failure();
        }

        // Spreadsheet Id
        String spreadsheetId = AppHelper.getSpreadsheetId();
        if (TextUtils.isEmpty(spreadsheetId)) {
            onFailure(workType, "Spreadsheet id is empty or null");
            return Result.failure();
        }

        // Payment methods and Categories
        EntitiesConsumerResponse entities = MainApplication.getInstance().getSheetRepository()
                .getEntityDataResponse(spreadsheetId, Constants.Range.CATEGORIES_PAYMENT_METHODS);
        if (entities.getStringLists() == null || entities.getStringLists().size() != 2) {
            onFailure(workType,
                    "Attempting to save categories & payment methods, list size should be 2");
        } else {
            onSuccess(workType, "Saving payment methods and categories");
            MainApplication.getInstance().getPaymentMethodRepo()
                    .setPaymentMethods(entities.getStringLists().get(0));
            MainApplication.getInstance().getCategoryRepo()
                    .setCategories(entities.getStringLists().get(1));
        }

        // Budgets
        entities = MainApplication.getInstance().getSheetRepository()
                .getEntityDataResponse(spreadsheetId, Constants.Range.BUDGETS, Dimension.ROWS);
        if (entities.getStringLists() == null || entities.getStringLists().size() == 0) {
            onFailure(workType, "Attempting to save budgets, list size should be greater than 0");
        } else {
            List<Budget> budgets = new ArrayList<>();
            for (List<String> strings : entities.getStringLists()) {
                if (strings == null || strings.size() < 3) {
                    continue;
                }
                budgets.add(new Budget(strings));
            }
            onSuccess(workType, "Saving budgets");
        }

        // Sheet meta data / info
        SheetsMetadataConsumerResponse response =
                MainApplication.getInstance().getSheetRepository()
                        .getSheetsMetadataResponse(spreadsheetId);
        List<SheetInfo> sheetInfos = new ArrayList<>();
        if (response.getSheetMetadataList() != null) {
            for (SheetMetadata sheetMetadata : response.getSheetMetadataList()) {
                sheetInfos.add(new SheetInfo(sheetMetadata));
            }
            ExpenseManagerDatabase.getInstance().sheetDao().setAll(sheetInfos);
            onSuccess(workType, "Saving sheet infos");
            return Result.success();
        } else if (response.getException() != null) {
            onFailure(workType, response.getException().getMessage());
            return Result.failure();
        }

        onFailure(workType, "Unknown reason");
        return Result.failure();
    }
}
