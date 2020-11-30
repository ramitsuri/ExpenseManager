package com.ramitsuri.expensemanager;

import android.accounts.Account;
import android.app.Application;

import com.ramitsuri.expensemanager.constants.intDefs.RecordType;
import com.ramitsuri.expensemanager.data.ExpenseManagerDatabase;
import com.ramitsuri.expensemanager.data.repository.BudgetRepository;
import com.ramitsuri.expensemanager.data.repository.CategoryRepository;
import com.ramitsuri.expensemanager.data.repository.EditedSheetRepository;
import com.ramitsuri.expensemanager.data.repository.ExpenseRepository;
import com.ramitsuri.expensemanager.data.repository.LogRepository;
import com.ramitsuri.expensemanager.data.repository.PaymentMethodRepository;
import com.ramitsuri.expensemanager.data.repository.SheetRepository;
import com.ramitsuri.expensemanager.entities.Budget;
import com.ramitsuri.expensemanager.entities.Category;
import com.ramitsuri.expensemanager.entities.EditedSheet;
import com.ramitsuri.expensemanager.logging.ReleaseTree;
import com.ramitsuri.expensemanager.utils.AppHelper;
import com.ramitsuri.expensemanager.utils.PrefHelper;
import com.ramitsuri.expensemanager.utils.WorkHelper;
import com.ramitsuri.sheetscore.googleSignIn.AccountManager;
import com.ramitsuri.sheetscore.googleSignIn.SignInResponse;

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

        initSheetRepo();

        // Enqueue periodic backups
        if (!BuildConfig.DEBUG) {
            WorkHelper.cancelPeriodicLegacyBackup();
            if (!AppHelper.isPruneComplete()) {
                WorkHelper.pruneWork();
                AppHelper.setPruneComplete(true);
            }
            WorkHelper.enqueuePeriodicBackup();
        }

        if (AppHelper.isFirstRunComplete()) {
            Timber.i("Application has already been set up");
        } else {
            addDefaultData();
            AppHelper.setFirstRunComplete(true);
        }
        AppHelper.setBackupIssueFixed(false);

        if (AppHelper.isBackupIssueFixed()) {
            Timber.i("Backup issue was fixed");
        } else {
            for (int i = 2; i < 8; i++) {
                getEditedSheetRepo().insertEditedSheet(new EditedSheet(i));
            }
            AppHelper.setBackupIssueFixed(true);
        }

        // Set identifier for expenses that don't have it (Can happen for expenses that were created
        // prior to this property was added)
        if (AppHelper.isIdentifierAdded()) {
            Timber.i("Identifier was added");
        } else {
            getExpenseRepo().updateSetIdentifier();
            for (int i = 0; i < 12; i++) {
                getEditedSheetRepo().insertEditedSheet(new EditedSheet(i));
            }
            AppHelper.setIdentifierAdded(true);
        }
        removeLegacyPrefs();
    }

    private void removeLegacyPrefs() {
        PrefHelper.remove("enable_splitting");
        PrefHelper.remove("settings_auto_backup");
        PrefHelper.remove("version_info");
        PrefHelper.remove("enable_debug_options");
        PrefHelper.remove("migration_step");
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

    private void initSheetRepo() {
        Timber.i("Attempting to initialize sheet repo");
        Account account = getSignInAccount();
        if (account != null) {
            refreshSheetRepo(account);
        } else {
            Timber.i("Account is null");
        }
    }

    public void refreshSheetRepo(@Nonnull Account account) {
        Timber.i("Refreshing sheet repo");
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

    @Nullable
    public synchronized SheetRepository getSheetRepository() {
        return mSheetRepository;
    }

    @Nonnull
    public synchronized BudgetRepository getBudgetRepository() {
        return mBudgetRepository;
    }

    @Nonnull
    public synchronized EditedSheetRepository getEditedSheetRepo() {
        return mEditedSheetRepo;
    }

    @Nullable
    public Account getSignInAccount() {
        AccountManager accountManager = new AccountManager();
        SignInResponse response =
                accountManager.prepareSignIn(getInstance(), AppHelper.getScopes());
        if (response.getGoogleSignInAccount() != null) {
            return response.getGoogleSignInAccount().getAccount();
        }
        return null;
    }
}
