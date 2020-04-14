package com.ramitsuri.expensemanager;

import android.accounts.Account;
import android.app.Application;
import android.text.TextUtils;

import com.ramitsuri.expensemanager.data.ExpenseManagerDatabase;
import com.ramitsuri.expensemanager.data.repository.BudgetRepository;
import com.ramitsuri.expensemanager.data.repository.CategoryRepository;
import com.ramitsuri.expensemanager.data.repository.EditedSheetRepository;
import com.ramitsuri.expensemanager.data.repository.ExpenseRepository;
import com.ramitsuri.expensemanager.data.repository.LogRepository;
import com.ramitsuri.expensemanager.data.repository.PaymentMethodRepository;
import com.ramitsuri.expensemanager.data.repository.SheetRepository;
import com.ramitsuri.expensemanager.entities.Budget;
import com.ramitsuri.expensemanager.logging.ReleaseTree;
import com.ramitsuri.expensemanager.utils.AppHelper;
import com.ramitsuri.expensemanager.utils.WorkHelper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import timber.log.Timber;

public class MainApplication extends Application {

    private CategoryRepository mCategoryRepo;
    private PaymentMethodRepository mPaymentMethodRepo;
    private ExpenseRepository mExpenseRepo;
    private LogRepository mLogRepo;
    private BudgetRepository mBudgetRepository;
    private EditedSheetRepository mEditedSheetRepo;

    private SheetRepository mSheetRepository;

    private static MainApplication sInstance;

    @Override
    public void onCreate() {
        Timber.i("Creating application");

        super.onCreate();

        sInstance = this;

        initTimber();

        initDataRepos();

        // Enqueue periodic backups
        if (!BuildConfig.DEBUG) {
            WorkHelper.enqueuePeriodicBackup();
            WorkHelper.enqueuePeriodicEntitiesBackup();
        }

        if (AppHelper.isFirstRunComplete()) {
            Timber.i("Application has already been set up");
        } else {
            addDefaultData();
            AppHelper.setFirstRunComplete(true);
        }
    }

    private void transitionExistingUsers() {
        if (BuildConfig.DEBUG) {
            return;
        }
        if (!TextUtils.isEmpty(AppHelper.getSpreadsheetId())) { // Already existing users
            // Cancel existing works
            WorkHelper.cancelPeriodicLegacyBackup();
            WorkHelper.cancelPeriodicBackup();
            WorkHelper.cancelPeriodicEntitiesBackup();

            // Update existing expenses to unsynced state so that expense backup will pick them up
            getExpenseRepo().updateSetAllUnsynced();

            // Set first run complete so that default data doesn't overwrite real data
            AppHelper.setFirstRunComplete(true);
            AppHelper.setSpreadsheetId(null);
        }
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
        mEditedSheetRepo = new EditedSheetRepository(appExecutors, database);
    }

    public void refreshSheetRepo(@Nonnull Account account) {
        String appName = getString(R.string.app_name);
        List<String> scopes = Arrays.asList(AppHelper.getScopes());
        if (mSheetRepository == null) {
            mSheetRepository =
                    new SheetRepository(this, appName, account, scopes, AppExecutors.getInstance());
        } else {
            mSheetRepository.refreshProcessors(this, appName, account, scopes);
        }
    }

    private void addDefaultData() {
        List<String> categories = Arrays.asList(getResources().getStringArray(R.array.categories));
        getCategoryRepo().setCategories(categories);

        List<String> paymentMethods =
                Arrays.asList(getResources().getStringArray(R.array.payment_methods));
        getPaymentMethodRepo().setPaymentMethods(paymentMethods);

        // Add categories to budgets such that each budget has max 3 categories
        int maxSize = 3;
        List<Budget> budgets = new ArrayList<>();
        int budgetSize = categories.size() / maxSize + (categories.size() % maxSize == 0 ? 0 : 1);
        for (int i = 0; i < budgetSize; i++) {
            Budget budget = new Budget();
            budget.setName(getResources().getString(R.string.default_budget_format, i + 1));
            budget.setAmount(new BigDecimal("100"));
            int categoryIndex = i * maxSize;
            while (categories.size() > categoryIndex && budget.getCategories().size() < maxSize) {
                List<String> budgetCategories = budget.getCategories();
                budgetCategories.add(categories.get(categoryIndex));
                budget.setCategories(budgetCategories);
                categoryIndex = categoryIndex + 1;
            }
            budgets.add(budget);
        }

        getBudgetRepository().setBudgets(budgets);
    }

    @Nonnull
    public CategoryRepository getCategoryRepo() {
        return mCategoryRepo;
    }

    @Nonnull
    public PaymentMethodRepository getPaymentMethodRepo() {
        return mPaymentMethodRepo;
    }

    @Nonnull
    public ExpenseRepository getExpenseRepo() {
        return mExpenseRepo;
    }

    @Nonnull
    public LogRepository getLogRepo() {
        return mLogRepo;
    }

    @Nullable
    public SheetRepository getSheetRepository() {
        return mSheetRepository;
    }

    @Nonnull
    public BudgetRepository getBudgetRepository() {
        return mBudgetRepository;
    }

    @Nonnull
    public EditedSheetRepository getEditedSheetRepo() {
        return mEditedSheetRepo;
    }
}
