package com.ramitsuri.expensemanager;

import android.accounts.Account;
import android.app.Application;
import android.text.TextUtils;

import com.ramitsuri.expensemanager.IntDefs.SourceType;
import com.ramitsuri.expensemanager.data.ExpenseManagerDatabase;
import com.ramitsuri.expensemanager.data.repository.CategoryRepository;
import com.ramitsuri.expensemanager.data.repository.ExpenseRepository;
import com.ramitsuri.expensemanager.data.repository.ExpenseSheetsRepository;
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
    private ExpenseSheetsRepository mExpenseSheetsRepo;

    private SheetRepository mSheetRepository;

    private static MainApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        sInstance = this;

        initTimber();

        initDataRepos();

        initSheetRepo();

        initExpenseSheetRepo();
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
        mExpenseRepo = new ExpenseRepository(appExecutors, database);
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
                Arrays.asList(Constants.SCOPES), appExecutors, database);
    }

    private void initExpenseSheetRepo() {
        AppExecutors appExecutors = AppExecutors.getInstance();
        ExpenseManagerDatabase database = ExpenseManagerDatabase.getInstance();

        mExpenseSheetsRepo = new ExpenseSheetsRepository(appExecutors, database, mSheetRepository);
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

    public ExpenseSheetsRepository getExpenseSheetsRepo() {
        return mExpenseSheetsRepo;
    }
}
