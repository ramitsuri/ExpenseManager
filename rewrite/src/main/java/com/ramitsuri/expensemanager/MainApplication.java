package com.ramitsuri.expensemanager;

import android.accounts.Account;
import android.app.Application;
import android.text.TextUtils;

import com.ramitsuri.expensemanager.data.ExpenseManagerDatabase;
import com.ramitsuri.expensemanager.data.repository.BudgetRepository;
import com.ramitsuri.expensemanager.data.repository.CategoryRepository;
import com.ramitsuri.expensemanager.data.repository.ExpenseRepository;
import com.ramitsuri.expensemanager.data.repository.LogRepository;
import com.ramitsuri.expensemanager.data.repository.PaymentMethodRepository;
import com.ramitsuri.expensemanager.data.repository.SheetRepository;
import com.ramitsuri.expensemanager.logging.ReleaseTree;
import com.ramitsuri.expensemanager.utils.AppHelper;
import com.ramitsuri.expensemanager.utils.WorkHelper;

import java.util.Arrays;
import java.util.List;

import timber.log.Timber;

public class MainApplication extends Application {

    private CategoryRepository mCategoryRepo;
    private PaymentMethodRepository mPaymentMethodRepo;
    private ExpenseRepository mExpenseRepo;
    private LogRepository mLogRepo;
    private BudgetRepository mBudgetRepository;

    private SheetRepository mSheetRepository;

    private static MainApplication sInstance;

    @Override
    public void onCreate() {
        Timber.i("Creating application");

        super.onCreate();

        sInstance = this;

        initTimber();

        initDataRepos();

        initSheetRepo();

        if (TextUtils.isEmpty(AppHelper.getSpreadsheetId())) {
            addDefaultData();
        } else {
            Timber.i("Application has already been set up");
        }

        // Cancel legacy work that ran at random time based on when auto backup toggle was enabled
        WorkHelper.cancelPeriodicLegacyBackup();
        // Enqueue work that runs around 2AM
        WorkHelper.enqueuePeriodicBackup();
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
        AppExecutors appExecutors = AppExecutors.getInstance();
        ExpenseManagerDatabase database = ExpenseManagerDatabase.getInstance();

        mCategoryRepo = new CategoryRepository(appExecutors, database);
        mPaymentMethodRepo = new PaymentMethodRepository(appExecutors, database);
        mExpenseRepo = new ExpenseRepository(appExecutors, database);
        mLogRepo = new LogRepository(appExecutors, database);
        mBudgetRepository = new BudgetRepository(appExecutors, database);
    }

    private void initSheetRepo() {
        String accountName = AppHelper.getAccountName();
        String accountType = AppHelper.getAccountType();

        initSheetRepo(accountName, accountType);
    }

    public void initSheetRepo(String accountName, String accountType) {
        AppExecutors appExecutors = AppExecutors.getInstance();
        ExpenseManagerDatabase database = ExpenseManagerDatabase.getInstance();
        String appName = getString(R.string.app_name);

        if (TextUtils.isEmpty(accountName) || TextUtils.isEmpty(accountType)) {
            Timber.i("Account Name - %s / Account Type - %s null or empty",
                    accountName, accountType);
            return;
        }

        Account account = new Account(accountName, accountType);

        mSheetRepository = new SheetRepository(this, appName, account,
                Arrays.asList(AppHelper.getScopes()), appExecutors, database);
    }

    public void refreshSheetRepo(String accountName, String accountType) {
        String appName = getString(R.string.app_name);

        if (TextUtils.isEmpty(accountName) || TextUtils.isEmpty(accountType)) {
            Timber.i("Account Name - %s / Account Type - %s null or empty",
                    accountName, accountType);
            return;
        }

        Account account = new Account(accountName, accountType);
        mSheetRepository
                .refreshProcessors(this, appName, account, Arrays.asList(AppHelper.getScopes()));
    }

    private void addDefaultData() {
        List<String> categories = Arrays.asList(getResources().getStringArray(R.array.categories));
        getCategoryRepo().setCategories(categories);

        List<String> paymentMethods =
                Arrays.asList(getResources().getStringArray(R.array.payment_methods));
        getPaymentMethodRepo().setPaymentMethods(paymentMethods);
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

    public BudgetRepository getBudgetRepository() {
        return mBudgetRepository;
    }
}
