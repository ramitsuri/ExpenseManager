package com.ramitsuri.expensemanager.work;

import android.accounts.Account;
import android.content.Context;
import android.text.TextUtils;

import com.ramitsuri.expensemanager.Constants;
import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.data.ExpenseManagerDatabase;
import com.ramitsuri.expensemanager.entities.Category;
import com.ramitsuri.expensemanager.entities.Expense;
import com.ramitsuri.expensemanager.entities.PaymentMethod;
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
        String appName = getInputData().getString(Constants.Work.APP_NAME);
        String spreadsheetId = getInputData().getString(Constants.Work.SPREADSHEET_ID);
        String accountName = getInputData().getString(Constants.Work.ACCOUNT_NAME);
        String accountType = getInputData().getString(Constants.Work.ACCOUNT_TYPE);
        String sheetId = getInputData().getString(Constants.Work.SHEET_ID);

        if (appName == null || TextUtils.isEmpty(spreadsheetId) || TextUtils.isEmpty(sheetId) ||
                TextUtils.isEmpty(accountName) || TextUtils.isEmpty(accountType)) {
            Timber.i(
                    "App Name - %s / Spreadsheet Id - %s / Sheet Id - %s / Account Name - %s / Account Type - %s null or empty",
                    appName, spreadsheetId, sheetId,
                    accountName, accountType);
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
            return Result.failure();
        }

        InsertConsumerResponse response = MainApplication.getInstance().getSheetRepository()
                .getInsertRangeResponse(expensesToBackup, categories, paymentMethods, sheetId);
        if (response.isSuccessful()) {
            MainApplication.getInstance().getExpenseRepo().deleteExpenses();
            return Result.success();
        }
        return Result.failure();
    }
}
