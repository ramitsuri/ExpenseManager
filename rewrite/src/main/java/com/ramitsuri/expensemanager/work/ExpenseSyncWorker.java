package com.ramitsuri.expensemanager.work;

import android.content.Context;
import android.text.TextUtils;

import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.constants.Constants;
import com.ramitsuri.expensemanager.data.ExpenseManagerDatabase;
import com.ramitsuri.expensemanager.entities.Expense;
import com.ramitsuri.expensemanager.entities.SheetInfo;
import com.ramitsuri.expensemanager.utils.AppHelper;
import com.ramitsuri.expensemanager.utils.TransformationHelper;
import com.ramitsuri.sheetscore.consumerResponse.RangeConsumerResponse;
import com.ramitsuri.sheetscore.consumerResponse.RangesConsumerResponse;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.work.WorkerParameters;

public class ExpenseSyncWorker extends BaseWorker {

    public ExpenseSyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
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

        if (ExpenseManagerDatabase.getInstance().sheetDao() == null) {
            onFailure(workType, "Sheet dao null");
            return Result.failure();
        }

        if (ExpenseManagerDatabase.getInstance().expenseDao() == null) {
            onFailure(workType, "Expense dao null");
            return Result.failure();
        }

        // Spreadsheet Id
        String spreadsheetId = AppHelper.getSpreadsheetId();
        if (TextUtils.isEmpty(spreadsheetId)) {
            onFailure(workType, "Spreadsheet id is empty or null");
            return Result.failure();
        }

        List<SheetInfo> sheetInfos = ExpenseManagerDatabase.getInstance().sheetDao().getAll();
        if (sheetInfos == null || sheetInfos.size() == 0) {
            onFailure(workType, "Sheet infos null or size 0");
            return Result.failure();
        }

        List<String> expenseRanges = TransformationHelper
                .getExpenseRanges(TransformationHelper.filterSheetInfos(sheetInfos));

        // Expenses
        RangesConsumerResponse ranges = MainApplication.getInstance().getSheetRepository()
                .getRangesDataResponse(spreadsheetId, expenseRanges);
        if (ranges == null || ranges.getValues().size() == 0) {
            onFailure(workType, "Attempting to save expenses, list size should be > 2");
            return Result.failure();
        } else {
            // Delete existing synced expenses
            ExpenseManagerDatabase.getInstance().expenseDao().deleteSynced();
            StringBuilder sb = new StringBuilder();
            sb.append("Saving expenses;");
            for (RangeConsumerResponse value : ranges.getValues()) {
                List<Expense> expenses = new ArrayList<>();
                String rangeName = value.getRange();
                if (!TextUtils.isEmpty(rangeName) && rangeName.contains("!") &&
                        !TextUtils.isEmpty(rangeName.split("!")[0])) {
                    if (value.getObjectLists() != null) {
                        for (List<Object> objects : value.getObjectLists()) {
                            if (objects == null || objects.size() < 5) {
                                sb.append("Objects in object lists null or size less than 5;");
                                continue;
                            }
                            Expense expense = new Expense(objects);
                            expenses.add(expense);
                        }
                    } else {
                        sb.append("Object lists in range null;");
                    }
                } else {
                    sb.append("Invalid rangeName;");
                }
                sb.append("Range: ")
                        .append(rangeName)
                        .append(" count: ")
                        .append(expenses.size())
                        .append(";");
                ExpenseManagerDatabase.getInstance().expenseDao().insert(expenses);
            }
            onSuccess(workType, sb.toString());
            return Result.success();
        }
    }
}
