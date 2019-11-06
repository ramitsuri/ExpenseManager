package com.ramitsuri.expensemanager.work;

import android.accounts.Account;
import android.content.Context;
import android.text.TextUtils;

import com.ramitsuri.expensemanager.Constants;
import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.data.ExpenseManagerDatabase;
import com.ramitsuri.expensemanager.entities.Category;
import com.ramitsuri.expensemanager.entities.Expense;
import com.ramitsuri.expensemanager.entities.Log;
import com.ramitsuri.expensemanager.entities.PaymentMethod;
import com.ramitsuri.expensemanager.utils.AppHelper;
import com.ramitsuri.sheetscore.consumerResponse.InsertConsumerResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import timber.log.Timber;

public class BackupWorker extends Worker {

    public BackupWorker(@NonNull Context context,
            @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String spreadsheetId = AppHelper.getSpreadsheetId();
        String accountName = AppHelper.getAccountName();
        String accountType = AppHelper.getAccountType();
        String sheetId = AppHelper.getCurrentSheetId();
        String workType = getInputData().getString(Constants.Work.TYPE);

        if (TextUtils.isEmpty(spreadsheetId) || TextUtils.isEmpty(sheetId) ||
                TextUtils.isEmpty(accountName) || TextUtils.isEmpty(accountType)) {
            Timber.i(
                    "Spreadsheet Id - %s / Sheet Id - %s / Account Name - %s / Account Type - %s null or empty",
                    spreadsheetId, sheetId, accountName, accountType);
            insertLog(workType,
                    Constants.LogResult.FAILURE,
                    String.format("Spreadsheet Id - %s / Sheet Id - %s / Account Name - %s " +
                                    "/ Account Type - %s null or empty",
                            spreadsheetId, sheetId, accountName, accountType));
            return Result.failure();
        }

        Account account = new Account(accountName, accountType);

        if (MainApplication.getInstance().getSheetRepository() == null) {
            Timber.i("Sheet repo null");
            MainApplication.getInstance()
                    .initSheetRepo(account, spreadsheetId, Arrays.asList(Constants.SCOPES));
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
            MainApplication.getInstance().getExpenseRepo().deleteExpenses();
            insertLog(workType,
                    Constants.LogResult.SUCCESS,
                    "Backup and deletion successful");
            return Result.success();
        }
        return Result.failure();
    }

    private void insertLog(String type, String result, String message) {
        MainApplication.getInstance().getLogRepo().insertLog(new Log(
                System.currentTimeMillis(),
                type,
                result,
                message
        ));
    }
}
