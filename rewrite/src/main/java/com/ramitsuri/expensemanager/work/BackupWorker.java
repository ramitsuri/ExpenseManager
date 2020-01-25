package com.ramitsuri.expensemanager.work;

import android.content.Context;

import com.ramitsuri.expensemanager.Constants;
import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.data.ExpenseManagerDatabase;
import com.ramitsuri.expensemanager.entities.Category;
import com.ramitsuri.expensemanager.entities.Expense;
import com.ramitsuri.expensemanager.entities.PaymentMethod;
import com.ramitsuri.expensemanager.utils.AppHelper;
import com.ramitsuri.sheetscore.consumerResponse.InsertConsumerResponse;

import java.util.ArrayList;
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
        int sheetId = AppHelper.getDefaultSheetId();
        String workType = getInputData().getString(Constants.Work.TYPE);

        if (MainApplication.getInstance().getSheetRepository() == null) {
            Timber.i("Sheet repo null");
            insertLog(workType,
                    Constants.LogResult.FAILURE,
                    "Sheet repo is null");
            return Result.failure();
        }

        // Expenses
        List<Expense> expensesToBackup =
                ExpenseManagerDatabase.getInstance().expenseDao().getAllUnsynced();

        // Categories
        List<Category> categoriesList = ExpenseManagerDatabase.getInstance().categoryDao().getAll();
        List<String> categories = new ArrayList<>();
        for (Category category : categoriesList) {
            categories.add(category.getName());
        }

        // Payment methods
        List<PaymentMethod> paymentMethodsList =
                ExpenseManagerDatabase.getInstance().paymentMethodDao().getAll();
        List<String> paymentMethods = new ArrayList<>();
        for (PaymentMethod paymentMethod : paymentMethodsList) {
            paymentMethods.add(paymentMethod.getName());
        }

        if (expensesToBackup == null) {
            Timber.i("Expenses to backup is null");
            insertLog(workType,
                    Constants.LogResult.FAILURE,
                    "Expenses to backup is null");
            return Result.failure();
        }

        InsertConsumerResponse response = MainApplication.getInstance().getSheetRepository()
                .getInsertRangeResponse(expensesToBackup, categories, paymentMethods, sheetId);
        if (response.isSuccessful()) {
            ExpenseManagerDatabase.getInstance().expenseDao().updateUnsynced();
            insertLog(workType,
                    Constants.LogResult.SUCCESS,
                    "Backup and deletion successful");
            return Result.success();
        }
        return Result.failure();
    }
}
