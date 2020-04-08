package com.ramitsuri.expensemanager.work;

import android.content.Context;
import android.text.TextUtils;

import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.constants.Constants;
import com.ramitsuri.expensemanager.data.ExpenseManagerDatabase;
import com.ramitsuri.expensemanager.entities.Expense;
import com.ramitsuri.expensemanager.utils.AppHelper;
import com.ramitsuri.sheetscore.consumerResponse.InsertConsumerResponse;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.work.WorkerParameters;
import timber.log.Timber;

public class BackupWorker extends BaseWorker {

    public BackupWorker(@NonNull Context context,
            @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        int defaultSheetId = AppHelper.getDefaultSheetId();
        String workType = getInputData().getString(Constants.Work.TYPE);

        if (MainApplication.getInstance().getSheetRepository() == null) {
            Timber.i("Sheet repo null");
            insertLog(workType,
                    Constants.LogResult.FAILURE,
                    "Sheet repo is null");
            return Result.failure();
        }

        // Spreadsheet Id
        String spreadsheetId = AppHelper.getSpreadsheetId();
        if (TextUtils.isEmpty(spreadsheetId)) {
            Timber.i("Spreadsheet id is empty or null");
            insertLog(workType,
                    Constants.LogResult.FAILURE,
                    "Spreadsheet id is empty or null");
            return Result.failure();
        }

        List<Integer> editedMonths = ExpenseManagerDatabase.getInstance().editedSheetDao().getAll();

        // Expenses
        List<Expense> expensesToBackup;
        if (editedMonths == null || editedMonths.size() == 0) {
            expensesToBackup = ExpenseManagerDatabase.getInstance().expenseDao().getAllUnsynced();
        } else {
            expensesToBackup = ExpenseManagerDatabase.getInstance().expenseDao()
                    .getAllForBackup(editedMonths);
        }

        if (expensesToBackup == null) {
            Timber.i("Expenses to backup is null");
            insertLog(workType,
                    Constants.LogResult.FAILURE,
                    "Expenses to backup is null");
            return Result.failure();
        }

        if (expensesToBackup.size() == 0) {
            Timber.i("Expenses to backup size is zero");
            insertLog(workType,
                    Constants.LogResult.FAILURE,
                    "Expenses to backup size is zero");
            return Result.failure();
        }

        InsertConsumerResponse response = MainApplication.getInstance().getSheetRepository()
                .getInsertRangeResponse(spreadsheetId, expensesToBackup, editedMonths,
                        defaultSheetId);
        if (response.isSuccessful()) {
            ExpenseManagerDatabase.getInstance().expenseDao().updateUnsynced();
            // Delete all records of edited sheets as all expenses in them are backed up now
            ExpenseManagerDatabase.getInstance().editedSheetDao().deleteAll();
            insertLog(workType,
                    Constants.LogResult.SUCCESS,
                    "Backup and deletion successful");
            return Result.success();
        } else if (response.getException() != null) {
            insertLog(workType,
                    Constants.LogResult.FAILURE,
                    response.getException().getMessage());
        }

        insertLog(workType,
                Constants.LogResult.FAILURE,
                "Unknown reason");
        return Result.failure();
    }
}
