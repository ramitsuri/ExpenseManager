package com.ramitsuri.expensemanager;

import android.accounts.Account;
import android.app.Application;
import android.text.TextUtils;

import com.ramitsuri.expensemanager.IntDefs.SourceType;
import com.ramitsuri.expensemanager.data.ExpenseManagerDatabase;
import com.ramitsuri.expensemanager.data.repository.CategoryRepository;
import com.ramitsuri.expensemanager.data.repository.ExpenseRepository;
import com.ramitsuri.expensemanager.data.repository.LogRepository;
import com.ramitsuri.expensemanager.data.repository.PaymentMethodRepository;
import com.ramitsuri.expensemanager.data.repository.SheetRepository;
import com.ramitsuri.expensemanager.logging.ReleaseTree;
import com.ramitsuri.expensemanager.utils.AppHelper;

import java.util.Arrays;

import timber.log.Timber;

public class MainApplication extends Application {

    private CategoryRepository mCategoryRepo;
    private PaymentMethodRepository mPaymentMethodRepo;
    private ExpenseRepository mExpenseRepo;
    private LogRepository mLogRepo;

    private SheetRepository mSheetRepository;

    private static MainApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        sInstance = this;

        initTimber();

        initDataRepos();

        initSheetRepo();
    }

    private void initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new ReleaseTree());
        }
    }

    public static MainApplication getInstance() {
        return sInstance;
    }

    private void initDataRepos() {
        @SourceType int source = SourceType.DB;

        AppExecutors appExecutors = AppExecutors.getInstance();
        ExpenseManagerDatabase database = ExpenseManagerDatabase.getInstance();

        mCategoryRepo = new CategoryRepository(appExecutors, database, source);
        mPaymentMethodRepo = new PaymentMethodRepository(appExecutors, database, source);
        mExpenseRepo = new ExpenseRepository(appExecutors, database, source);
        mLogRepo = new LogRepository(appExecutors, database);

        // TODO DEBUG Only
        /*mExpenseRepo.deleteExpenses();
        for (Expense expense : DummyData.getExpenses()) {
            mExpenseRepo.insertExpense(expense);
        }

        mCategoryRepo.setCategories(DummyData.getCategories());
        mPaymentMethodRepo.setPaymentMethods(DummyData.getPaymentMethods());*/
    }

    private void initSheetRepo() {
        String spreadsheetId = AppHelper.getSpreadsheetId();
        String accountName = AppHelper.getAccountName();
        String accountType = AppHelper.getAccountType();

        initSheetRepo(spreadsheetId, accountName, accountType);
    }

    public void initSheetRepo(String spreadsheetId, String accountName, String accountType) {
        AppExecutors appExecutors = AppExecutors.getInstance();
        String appName = getString(R.string.app_name);

        if (TextUtils.isEmpty(spreadsheetId) ||
                TextUtils.isEmpty(accountName) || TextUtils.isEmpty(accountType)) {
            Timber.i("Spreadsheet Id - %s / Account Name - %s / Account Type - %s null or empty",
                    spreadsheetId, accountName, accountType);
            return;
        }

        Account account = new Account(accountName, accountType);

        mSheetRepository = new SheetRepository(this, appName, account, spreadsheetId,
                Arrays.asList(Constants.SCOPES), appExecutors);
    }

    public CategoryRepository getCategoryRepo() {
        return mCategoryRepo;
    }

    public PaymentMethodRepository getPaymentMethodRepo() {
        return mPaymentMethodRepo;
    }

    public ExpenseRepository getExpenseRepo() {
        return mExpenseRepo;
    }

    public LogRepository getLogRepo() {
        return mLogRepo;
    }

    public SheetRepository getSheetRepository() {
        return mSheetRepository;
    }
}
