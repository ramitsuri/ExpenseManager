package com.ramitsuri.expensemanager.work;

import android.content.Context;
import android.text.TextUtils;

import com.ramitsuri.expensemanager.Constants;
import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.data.ExpenseManagerDatabase;
import com.ramitsuri.expensemanager.entities.Expense;
import com.ramitsuri.expensemanager.entities.SheetInfo;
import com.ramitsuri.expensemanager.utils.AppHelper;
import com.ramitsuri.expensemanager.utils.TransformationHelper;
import com.ramitsuri.sheetscore.consumerResponse.RangeConsumerResponse;
import com.ramitsuri.sheetscore.consumerResponse.RangesConsumerResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.work.WorkerParameters;
import timber.log.Timber;

public class ExpenseSyncWorker extends BaseWorker {

    public ExpenseSyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String workType = getInputData().getString(Constants.Work.TYPE);
        String message;

        if (ExpenseManagerDatabase.getInstance().sheetDao() == null) {
            message = "Sheet dao null";
            Timber.e(message);
            insertLog(workType,
                    Constants.LogResult.FAILURE,
                    message);
            return Result.failure();
        }

        if (ExpenseManagerDatabase.getInstance().expenseDao() == null) {
            message = "Expense dao null";
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

        List<SheetInfo> sheetInfos = ExpenseManagerDatabase.getInstance().sheetDao().getAll();
        if (sheetInfos == null || sheetInfos.size() == 0) {
            message = "Sheet infos null or size 0";
            Timber.e(message);
            insertLog(workType,
                    Constants.LogResult.FAILURE,
                    message);
            return Result.failure();
        }

        Map<String, Integer> expensesRangeMap = TransformationHelper
                .getExpenseRanges(TransformationHelper.filterSheetInfos(sheetInfos));
        List<String> expenseRanges = new ArrayList<>(expensesRangeMap.keySet());

        // Expenses
        RangesConsumerResponse ranges = MainApplication.getInstance().getSheetRepository()
                .getRangesDataResponse(spreadsheetId, expenseRanges);
        if (ranges == null || ranges.getValues().size() == 0) {
            message = "Attempting to save expenses, list size should be > 2";
            Timber.i(message);
            insertLog(workType,
                    Constants.LogResult.FAILURE,
                    message);
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("Saving expenses;");
            for (RangeConsumerResponse value : ranges.getValues()) {
                Integer sheetId = null;
                List<Expense> expenses = new ArrayList<>();
                String rangeName = value.getRange();
                if (!TextUtils.isEmpty(rangeName) && rangeName.contains("!") &&
                        !TextUtils.isEmpty(rangeName.split("!")[0])) {
                    sheetId = expensesRangeMap
                            .get(rangeName.split("!")[0] + Constants.Sheets.EXPENSE_RANGE);
                    if (sheetId != null && value.getObjectLists() != null) {
                        for (List<Object> objects : value.getObjectLists()) {
                            if (objects == null || objects.size() < 5) {
                                sb.append("Objects in object lists null or size less than 5;");
                                continue;
                            }
                            Expense expense = new Expense(objects, sheetId);
                            expenses.add(expense);
                        }
                    } else {
                        sb.append("SheetId or object lists in range null;");
                    }
                } else {
                    sb.append("Invalid rangeName;");
                }
                if (sheetId != null) {
                    sb.append("Range: ")
                            .append(rangeName)
                            .append(" count: ")
                            .append(expenses.size())
                            .append(";");
                    ExpenseManagerDatabase.getInstance().expenseDao().insert(expenses, sheetId);
                }
            }
            Timber.i(sb.toString());
            insertLog(workType,
                    Constants.LogResult.SUCCESS,
                    sb.toString());
        }

        return Result.success();
    }
}
