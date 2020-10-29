package com.ramitsuri.expensemanager.work;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.work.WorkerParameters;

import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.constants.Constants;
import com.ramitsuri.expensemanager.constants.intDefs.RecordType;
import com.ramitsuri.expensemanager.data.ExpenseManagerDatabase;
import com.ramitsuri.expensemanager.entities.Budget;
import com.ramitsuri.expensemanager.entities.Category;
import com.ramitsuri.expensemanager.entities.SheetInfo;
import com.ramitsuri.expensemanager.utils.AppHelper;
import com.ramitsuri.sheetscore.consumerResponse.EntitiesConsumerResponse;
import com.ramitsuri.sheetscore.consumerResponse.SheetMetadata;
import com.ramitsuri.sheetscore.consumerResponse.SheetsMetadataConsumerResponse;
import com.ramitsuri.sheetscore.intdef.Dimension;

import java.util.ArrayList;
import java.util.List;

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
        if (entities.getStringLists() == null || entities.getStringLists().size() != 3) {
            onFailure(workType,
                    "Attempting to save categories & payment methods, list size should be 3");
        } else {
            onSuccess(workType, "Saving payment methods");
            MainApplication.getInstance().getPaymentMethodRepo()
                    .setPaymentMethods(entities.getStringLists().get(0));

            onSuccess(workType, "Saving categories");
            List<Category> categories = new ArrayList<>();
            for (int i = 0; i < entities.getStringLists().get(1).size(); i++) {
                String name = entities.getStringLists().get(1).get(i);
                String recordType = entities.getStringLists().get(2).get(i);
                if (TextUtils.isEmpty(recordType)) {
                    recordType = RecordType.MONTHLY;
                }
                categories.add(new Category(name, recordType));
            }
            MainApplication.getInstance().getCategoryRepo()
                    .setCategories(categories);
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
            ExpenseManagerDatabase.getInstance().budgetDao().setAll(budgets);
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
