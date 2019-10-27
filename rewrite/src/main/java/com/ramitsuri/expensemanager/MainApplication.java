package com.ramitsuri.expensemanager;

import android.accounts.Account;
import android.app.Application;

import com.ramitsuri.expensemanager.IntDefs.SourceType;
import com.ramitsuri.expensemanager.data.ExpenseManagerDatabase;
import com.ramitsuri.expensemanager.data.repository.CategoryRepository;
import com.ramitsuri.expensemanager.data.repository.ExpenseRepository;
import com.ramitsuri.expensemanager.data.repository.PaymentMethodRepository;
import com.ramitsuri.expensemanager.data.repository.SheetRepository;
import com.ramitsuri.expensemanager.logging.ReleaseTree;

import java.util.List;

import androidx.annotation.NonNull;
import timber.log.Timber;

public class MainApplication extends Application {

    private CategoryRepository mCategoryRepo;
    private PaymentMethodRepository mPaymentMethodRepo;
    private ExpenseRepository mExpenseRepo;

    private SheetRepository mSheetRepository;

    private static MainApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        sInstance = this;

        initTimber();
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

    public void initRepos() {
        @SourceType int source = SourceType.DB;

        AppExecutors appExecutors = AppExecutors.getInstance();
        ExpenseManagerDatabase database = ExpenseManagerDatabase.getInstance();

        mCategoryRepo = new CategoryRepository(appExecutors, database, source);
        mPaymentMethodRepo = new PaymentMethodRepository(appExecutors, database, source);
        mExpenseRepo = new ExpenseRepository(appExecutors, database, source);

        // TODO DEBUG Only
        /*mExpenseRepo.deleteExpenses();
        for (Expense expense : DummyData.getExpenses()) {
            mExpenseRepo.insertExpense(expense);
        }

        mCategoryRepo.setCategories(DummyData.getCategories());
        mPaymentMethodRepo.setPaymentMethods(DummyData.getPaymentMethods());*/
    }

    public void initSheetRepo(@NonNull Account account,
            @NonNull String spreadsheetId,
            @NonNull List<String> scopes) {
        AppExecutors appExecutors = AppExecutors.getInstance();
        String appName = getString(R.string.app_name);

        mSheetRepository =
                new SheetRepository(this, appName, account, spreadsheetId, scopes, appExecutors);
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

    public SheetRepository getSheetRepository() {
        return mSheetRepository;
    }
}
