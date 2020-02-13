package com.ramitsuri.expensemanager.work;

import android.content.Context;
import android.text.TextUtils;

import com.ramitsuri.expensemanager.Constants;
import com.ramitsuri.expensemanager.MainApplication;
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
import timber.log.Timber;

public class SyncWorker extends BaseWorker {

    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String workType = getInputData().getString(Constants.Work.TYPE);
        String message;

        if (MainApplication.getInstance().getSheetRepository() == null) {
            message = "Sheet repo null";
            Timber.e(message);
            insertLog(workType,
                    Constants.LogResult.FAILURE,
                    message);
            return Result.failure();
        }
        if (MainApplication.getInstance().getCategoryRepo() == null) {
            message = "Category repo null";
            Timber.e(message);
            insertLog(workType,
                    Constants.LogResult.FAILURE,
                    message);
            return Result.failure();
        }
        if (MainApplication.getInstance().getPaymentMethodRepo() == null) {
            message = "Payment method repo null";
            Timber.e(message);
            insertLog(workType,
                    Constants.LogResult.FAILURE,
                    message);
            return Result.failure();
        }

        // Spreadsheet Id
        String spreadsheetId = AppHelper.getSpreadsheetId();
        if (TextUtils.isEmpty(spreadsheetId)) {
            message = "Spreadsheet id is empty or null";
            Timber.e(message);
            insertLog(workType,
                    Constants.LogResult.FAILURE,
                    message);
            return Result.failure();
        }

        // Payment methods and Categories
        EntitiesConsumerResponse entities = MainApplication.getInstance().getSheetRepository()
                .getEntityDataResponse(spreadsheetId, Constants.Range.CATEGORIES_PAYMENT_METHODS);
        if (entities.getStringLists() == null || entities.getStringLists().size() != 2) {
            message = "Attempting to save categories & payment methods, list size should be 2";
            Timber.i(message);
            insertLog(workType,
                    Constants.LogResult.FAILURE,
                    message);
        } else {
            message = "Saving payment methods and categories";
            Timber.i(message);
            MainApplication.getInstance().getPaymentMethodRepo()
                    .setPaymentMethods(entities.getStringLists().get(0));
            MainApplication.getInstance().getCategoryRepo()
                    .setCategories(entities.getStringLists().get(1));
            insertLog(workType,
                    Constants.LogResult.SUCCESS,
                    message);
        }

        // Budgets
        entities = MainApplication.getInstance().getSheetRepository()
                .getEntityDataResponse(spreadsheetId, Constants.Range.BUDGETS, Dimension.ROWS);
        if (entities.getStringLists() == null || entities.getStringLists().size() == 0) {
            message = "Attempting to save budgets, list size should be greater than 0";
            Timber.i(message);
            insertLog(workType,
                    Constants.LogResult.FAILURE,
                    message);
        } else {
            List<Budget> budgets = new ArrayList<>();
            for (List<String> strings : entities.getStringLists()) {
                if (strings == null || strings.size() < 3) {
                    continue;
                }
                budgets.add(new Budget(strings));
            }
            message = "Saving budgets";
            Timber.i(message);
            MainApplication.getInstance().getBudgetRepository()
                    .setBudgets(budgets);
            insertLog(workType,
                    Constants.LogResult.SUCCESS,
                    message);
        }

        // Sheet meta data / info
        SheetsMetadataConsumerResponse response =
                MainApplication.getInstance().getSheetRepository()
                        .getSheetsMetadataResponse(spreadsheetId);
        List<SheetInfo> sheetInfos = new ArrayList<>();
        if (response.getSheetMetadataList() != null) {
            message = "Saving sheet infos";
            Timber.i(message);
            for (SheetMetadata sheetMetadata : response.getSheetMetadataList()) {
                sheetInfos.add(new SheetInfo(sheetMetadata));
            }
            ExpenseManagerDatabase.getInstance().sheetDao().setAll(sheetInfos);
            insertLog(workType,
                    Constants.LogResult.SUCCESS,
                    message);
        } else {
            message = "Sheet meta data returned is null " + response.getException().getMessage();
            Timber.i(message);
            insertLog(workType,
                    Constants.LogResult.FAILURE,
                    message);
        }

        return Result.success();
    }
}
