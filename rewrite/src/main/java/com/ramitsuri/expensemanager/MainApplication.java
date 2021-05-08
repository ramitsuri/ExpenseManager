package com.ramitsuri.expensemanager;

import android.app.Application;

import com.ramitsuri.expensemanager.constants.intDefs.RecordType;
import com.ramitsuri.expensemanager.data.ExpenseManagerDatabase;
import com.ramitsuri.expensemanager.data.repository.BudgetRepository;
import com.ramitsuri.expensemanager.data.repository.CategoryRepository;
import com.ramitsuri.expensemanager.data.repository.ExpenseRepository;
import com.ramitsuri.expensemanager.data.repository.LogRepository;
import com.ramitsuri.expensemanager.data.repository.PaymentMethodRepository;
import com.ramitsuri.expensemanager.data.repository.RecurringExpenseRepository;
import com.ramitsuri.expensemanager.dependency.Injector;
import com.ramitsuri.expensemanager.entities.Budget;
import com.ramitsuri.expensemanager.entities.Category;
import com.ramitsuri.expensemanager.logging.ReleaseTree;
import com.ramitsuri.expensemanager.utils.AppHelper;
import com.ramitsuri.expensemanager.utils.CrashReportingHelper;
import com.ramitsuri.expensemanager.utils.PrefHelper;
import com.ramitsuri.expensemanager.utils.WorkHelper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import timber.log.Timber;

public class MainApplication extends Application {

    private Injector mInjector;
    private CategoryRepository mCategoryRepo;
    private PaymentMethodRepository mPaymentMethodRepo;
    private ExpenseRepository mExpenseRepo;
    private LogRepository mLogRepo;
    private BudgetRepository mBudgetRepository;
    private RecurringExpenseRepository mRecurringRepo;

    private static MainApplication sInstance;

    private boolean showEOLWarning = true;

    @Override
    public void onCreate() {
        Timber.i("Creating application");

        super.onCreate();

        sInstance = this;

        initCrashReportingHelper();

        initTimber();

        initInjector();

        initDataRepos();

        // Enqueue periodic works
        if (!BuildConfig.DEBUG) {
            WorkHelper.cancelByTag(
                    "one_time_backup",
                    "one_time_expenses_backup",
                    "one_time_entities_backup",
                    "scheduled_backup",
                    "periodic_backup",
                    "periodic_expenses_backup",
                    "periodic_entities_backup",
                    "one_time_sync",
                    "one_time_expense_sync",
                    "one_time_create_spreadsheet");
            if (!AppHelper.isPruneComplete()) {
                WorkHelper.pruneWork();
                AppHelper.setPruneComplete(true);
            }
            WorkHelper.enqueueRecurringExpensesRunner();
        }

        if (AppHelper.isFirstRunComplete()) {
            Timber.i("Application has already been set up");
        } else {
            addDefaultData();
            AppHelper.setFirstRunComplete(true);
        }

        // Set identifier for expenses that don't have it (Can happen for expenses that were created
        // prior to this property was added)
        if (AppHelper.isIdentifierAdded()) {
            Timber.i("Identifier was added");
        } else {
            getExpenseRepo().updateSetIdentifier();
            AppHelper.setIdentifierAdded(true);
        }
        removeLegacyPrefs();
    }

    private void initCrashReportingHelper() {
        if (BuildConfig.DEBUG) {
            CrashReportingHelper.getInstance().disable();
        }
    }

    private void removeLegacyPrefs() {
        PrefHelper.remove("enable_splitting");
        PrefHelper.remove("settings_auto_backup");
        PrefHelper.remove("version_info");
        PrefHelper.remove("enable_debug_options");
        PrefHelper.remove("migration_step");
        PrefHelper.remove("settings_account_name");
        PrefHelper.remove("settings_account_type");
        PrefHelper.remove("settings_spreadsheet_id");
        PrefHelper.remove("settings_sheet_id");
        PrefHelper.remove("default_sheet_id");
        PrefHelper.remove("enable_expense_sync");
        PrefHelper.remove("enable_entities_sync");
        PrefHelper.remove("is_entities_edited");
        PrefHelper.remove("backup_info_status");
        PrefHelper.remove("enable_income");
        PrefHelper.remove("enable_backup_now");
        PrefHelper.remove("surprise_message");
        PrefHelper.remove("shared_collection_name");
        PrefHelper.remove("shared_this_source");
        PrefHelper.remove("shared_other_source");
        PrefHelper.remove("backup_issue_fixed");
        PrefHelper.remove("is_prune_complete");
        PrefHelper.remove("replace_work");
    }

    private void initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new ReleaseTree());
        }
    }

    private void initInjector() {
        mInjector = new Injector();
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
        mRecurringRepo = new RecurringExpenseRepository(appExecutors, database);
    }

    private void addDefaultData() {
        List<String> categoryStrings =
                Arrays.asList(getResources().getStringArray(R.array.categories));
        List<Category> categories = new ArrayList<>();
        for (String categoryString : categoryStrings) {
            Category category = new Category();
            category.setName(categoryString);
            category.setRecordType(RecordType.MONTHLY);
            categories.add(category);
        }
        getCategoryRepo().setCategories(categories);

        List<String> paymentMethods =
                Arrays.asList(getResources().getStringArray(R.array.payment_methods));
        getPaymentMethodRepo().setPaymentMethods(paymentMethods);

        // Add categories to budgets such that each budget has max 3 categories
        int maxSize = 3;
        List<Budget> budgets = new ArrayList<>();
        int budgetSize =
                categoryStrings.size() / maxSize + (categoryStrings.size() % maxSize == 0 ? 0 : 1);
        for (int i = 0; i < budgetSize; i++) {
            Budget budget = new Budget();
            budget.setName(getResources().getString(R.string.default_budget_format, i + 1));
            budget.setAmount(new BigDecimal("100"));
            int categoryIndex = i * maxSize;
            while (categoryStrings.size() > categoryIndex &&
                    budget.getCategories().size() < maxSize) {
                List<String> budgetCategories = budget.getCategories();
                budgetCategories.add(categoryStrings.get(categoryIndex));
                budget.setCategories(budgetCategories);
                categoryIndex = categoryIndex + 1;
            }
            budgets.add(budget);
        }

        getBudgetRepository().setBudgets(budgets);
    }

    @Nonnull
    public synchronized CategoryRepository getCategoryRepo() {
        return mCategoryRepo;
    }

    @Nonnull
    public synchronized PaymentMethodRepository getPaymentMethodRepo() {
        return mPaymentMethodRepo;
    }

    @Nonnull
    public synchronized ExpenseRepository getExpenseRepo() {
        return mExpenseRepo;
    }

    @Nonnull
    public synchronized LogRepository getLogRepo() {
        return mLogRepo;
    }

    @Nonnull
    public synchronized BudgetRepository getBudgetRepository() {
        return mBudgetRepository;
    }

    @Nonnull
    public synchronized RecurringExpenseRepository getRecurringRepo() {
        return mRecurringRepo;
    }

    @Nonnull
    public Injector getInjector() {
        return mInjector;
    }

    public void eolWarningDone() {
        showEOLWarning = false;
    }

    public boolean showEOLWarning() {
        return showEOLWarning;
    }
}
