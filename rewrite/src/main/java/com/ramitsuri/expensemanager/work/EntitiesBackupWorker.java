package com.ramitsuri.expensemanager.work;

import android.content.Context;
import android.text.TextUtils;

import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.constants.Constants;
import com.ramitsuri.expensemanager.data.ExpenseManagerDatabase;
import com.ramitsuri.expensemanager.entities.Budget;
import com.ramitsuri.expensemanager.entities.Category;
import com.ramitsuri.expensemanager.entities.PaymentMethod;
import com.ramitsuri.expensemanager.entities.SheetInfo;
import com.ramitsuri.expensemanager.utils.AppHelper;
import com.ramitsuri.sheetscore.consumerResponse.InsertConsumerResponse;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.work.WorkerParameters;

public class EntitiesBackupWorker extends BaseWorker {

    public EntitiesBackupWorker(@NonNull Context context,
            @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String workType = getInputData().getString(Constants.Work.TYPE);

        boolean isEntitiesEdited = AppHelper.isEntitiesEdited();
        if (!isEntitiesEdited) {
            onFailure(workType, "Entities weren't edited");
            return Result.failure();
        }

        if (MainApplication.getInstance().getSheetRepository() == null) {
            onFailure(workType, "Sheet repo is null");
            return Result.failure();
        }

        // Spreadsheet Id
        String spreadsheetId = AppHelper.getSpreadsheetId();
        if (TextUtils.isEmpty(spreadsheetId)) {
            onFailure(workType, "Spreadsheet id is empty or null");
            return Result.failure();
        }

        // Entities Sheet Id
        int entitiesSheetId = Constants.Basic.UNDEFINED;
        List<SheetInfo> sheetInfos = ExpenseManagerDatabase.getInstance().sheetDao().getAll();
        for (SheetInfo info : sheetInfos) {
            if (info.getSheetName().equalsIgnoreCase(Constants.SheetNames.ENTITIES)) {
                entitiesSheetId = info.getSheetId();
                break;
            }
        }
        if (entitiesSheetId == Constants.Basic.UNDEFINED) {
            onFailure(workType, "Cannot find record of entities sheet id");
            return Result.failure();
        }

        // Entities
        List<Category> categories =
                ExpenseManagerDatabase.getInstance().categoryDao().getAll();
        List<PaymentMethod> paymentMethods =
                ExpenseManagerDatabase.getInstance().paymentMethodDao().getAll();
        List<Budget> budgets =
                ExpenseManagerDatabase.getInstance().budgetDao().getAll();
        if (categories == null || categories.size() == 0 ||
                paymentMethods == null || paymentMethods.size() == 0) {
            onFailure(workType, "Categories, or payment methods are empty. This shouldn't happen");
            return Result.failure();
        }

        InsertConsumerResponse response = MainApplication.getInstance().getSheetRepository()
                .getInsertEntitiesResponse(spreadsheetId, categories, paymentMethods, budgets,
                        AppHelper.getMonths(), entitiesSheetId);
        if (response.isSuccessful()) {
            // Entities have been backed up, now they're in unedited state
            AppHelper.setEntitiesEdited(false);
            onSuccess(workType, "Entities backup successful");
            return Result.success();
        } else if (response.getException() != null) {
            onFailure(workType, response.getException().getMessage());
            return Result.failure();
        }

        onFailure(workType, "Unknown reason");
        return Result.failure();
    }
}
