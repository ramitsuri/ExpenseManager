package com.ramitsuri.expensemanagerrewrite;

import android.app.Application;

import com.ramitsuri.expensemanagerrewrite.IntDefs.SourceType;
import com.ramitsuri.expensemanagerrewrite.data.DummyData;
import com.ramitsuri.expensemanagerrewrite.data.ExpenseManagerDatabase;
import com.ramitsuri.expensemanagerrewrite.data.repository.CategoryRepository;
import com.ramitsuri.expensemanagerrewrite.data.repository.ExpenseRepository;
import com.ramitsuri.expensemanagerrewrite.data.repository.PaymentMethodRepository;
import com.ramitsuri.expensemanagerrewrite.entities.Expense;
import com.ramitsuri.expensemanagerrewrite.logging.ReleaseTree;

import timber.log.Timber;

public class MainApplication extends Application {

    private CategoryRepository mCategoryRepo;
    private PaymentMethodRepository mPaymentMethodRepo;
    private ExpenseRepository mExpenseRepo;

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
        mExpenseRepo.deleteExpenses();
        for (Expense expense : DummyData.getExpenses()) {
            mExpenseRepo.insertExpense(expense);
        }

        mCategoryRepo.setCategories(DummyData.getCategories());
        mPaymentMethodRepo.setPaymentMethods(DummyData.getPaymentMethods());
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
}
