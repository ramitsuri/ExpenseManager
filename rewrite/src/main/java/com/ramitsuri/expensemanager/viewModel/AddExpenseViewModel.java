package com.ramitsuri.expensemanager.viewModel;

import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.constants.Constants;
import com.ramitsuri.expensemanager.data.repository.CategoryRepository;
import com.ramitsuri.expensemanager.data.repository.ExpenseRepository;
import com.ramitsuri.expensemanager.data.repository.PaymentMethodRepository;
import com.ramitsuri.expensemanager.entities.Category;
import com.ramitsuri.expensemanager.entities.EditedSheet;
import com.ramitsuri.expensemanager.entities.Expense;
import com.ramitsuri.expensemanager.entities.PaymentMethod;
import com.ramitsuri.expensemanager.utils.AppHelper;
import com.ramitsuri.expensemanager.utils.CurrencyHelper;
import com.ramitsuri.expensemanager.utils.DateHelper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Nonnull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import timber.log.Timber;

public class AddExpenseViewModel extends ViewModel {

    private ExpenseRepository mExpenseRepo;

    private Expense mExpense;
    private Integer mOldMonthIndex;
    private LiveData<List<String>> mCategories;
    private LiveData<List<String>> mPaymentMethods;
    private LiveData<List<String>> mStores;
    private int mAddMode;

    private boolean mChangesMade, mIsSplit;

    public AddExpenseViewModel(Expense expense) {
        super();

        mExpenseRepo = MainApplication.getInstance().getExpenseRepo();
        CategoryRepository categoryRepo = MainApplication.getInstance().getCategoryRepo();
        PaymentMethodRepository paymentMethodRepo =
                MainApplication.getInstance().getPaymentMethodRepo();

        mCategories = Transformations.map(categoryRepo.getCategories(),
                new Function<List<Category>, List<String>>() {
                    @Override
                    public List<String> apply(List<Category> categories) {
                        List<String> categoryStrings = new ArrayList<>();
                        if (categories != null) {
                            for (Category category : categories) {
                                categoryStrings.add(category.getName());
                            }
                        }
                        return categoryStrings;
                    }
                });
        mPaymentMethods = Transformations.map(paymentMethodRepo.getPaymentMethods(),
                new Function<List<PaymentMethod>, List<String>>() {
                    @Override
                    public List<String> apply(List<PaymentMethod> paymentMethods) {
                        List<String> paymentMethodStrings = new ArrayList<>();
                        if (paymentMethods != null) {
                            for (PaymentMethod paymentMethod : paymentMethods) {
                                paymentMethodStrings.add(paymentMethod.getName());
                            }
                        }
                        return paymentMethodStrings;
                    }
                });

        mStores = mExpenseRepo.getStores();

        reset(expense);
    }

    public LiveData<List<String>> getCategories() {
        return mCategories;
    }

    public LiveData<List<String>> getPaymentMethods() {
        return mPaymentMethods;
    }

    @Nullable
    public LiveData<List<String>> getStores() {
        return mStores;
    }

    public void onStoreValueChanged(@Nonnull String startsWith) {
        mExpenseRepo.refreshStores(startsWith);
    }

    public LiveData<Expense> getForStore(@Nonnull String store) {
        return mExpenseRepo.getForStore(store);
    }

    public void add() {
        Expense expense = new Expense(mExpense);
        if (mIsSplit) {
            expense.setAmount(CurrencyHelper.divide(expense.getAmount(), new BigDecimal("2")));
        }
        if (expense.isIncome()) { // Set category to "INCOME" always
            expense.setCategory(Constants.Basic.INCOME);
        }
        mExpenseRepo.insert(expense);
        reset(null);
    }

    public void edit() {
        Expense expense = new Expense(mExpense);
        expense.setId(mExpense.getId());
        expense.setIsSynced(false);
        if (mIsSplit) {
            expense.setAmount(CurrencyHelper.divide(expense.getAmount(), new BigDecimal("2")));
        }
        if (expense.isIncome()) { // Set category to "INCOME" always
            expense.setCategory(Constants.Basic.INCOME);
        }
        boolean wasSynced = mExpense.isSynced();
        mExpenseRepo.edit(expense);
        // Backed up expense was edited, update EditedSheets table to add this expense's month index
        if (wasSynced) { // Backed up expense was edited
            int editedMonthIndex;
            if (mOldMonthIndex != null) {
                // Expense date was changed to fall in a different sheet
                // Old sheet would need to be rewritten as an expense from that was deleted
                editedMonthIndex = mOldMonthIndex;
            } else {
                // Date's month wasn't changed so sheet corresponding to expense's month would
                // need to be rewritten
                editedMonthIndex = DateHelper.getMonthIndexFromDate(expense.getDateTime());
            }
            MainApplication.getInstance().getEditedSheetRepo()
                    .insertEditedSheet(new EditedSheet(editedMonthIndex));
        }
        reset(null);
    }

    public long getDate() {
        return mExpense.getDateTime();
    }

    public void setDate(long date) {
        int oldMonthIndex = DateHelper.getMonthIndexFromDate(mExpense.getDateTime());
        int newMonthIndex = DateHelper.getMonthIndexFromDate(date);
        boolean monthChanged = oldMonthIndex != newMonthIndex;
        if (monthChanged) {
            // A backed up expense is basically being deleted from the sheet corresponding to
            // old month. Save the old month so that sheet holding expenses for that month
            // can be updated.
            if (mExpense.isSynced()) {
                mOldMonthIndex = oldMonthIndex;
            }
        }
        mExpense.setDateTime(date);
        setChangesMade();
    }

    @Nullable
    public String getCategory() {
        return mExpense.getCategory();
    }

    public void setCategory(@NonNull String category) {
        boolean changesMade = !category.equals(mExpense.getCategory());
        if (changesMade) {
            //setChangesMade();
            mExpense.setCategory(category);
        }
    }

    @Nullable
    public String getPaymentMethod() {
        return mExpense.getPaymentMethod();
    }

    public void setPaymentMethod(@NonNull String paymentMethod) {
        boolean changesMade = !paymentMethod.equals(mExpense.getPaymentMethod());
        if (changesMade) {
            //setChangesMade();
            mExpense.setPaymentMethod(paymentMethod);
        }
    }

    public void setAmount(@NonNull String amount) {
        BigDecimal bdAmount = new BigDecimal(amount);
        boolean changesMade = !(bdAmount.compareTo(mExpense.getAmount()) == 0);
        if (changesMade) {
            setChangesMade();
            mExpense.setAmount(bdAmount);
        }
    }

    @Nullable
    public BigDecimal getAmount() {
        return mExpense.getAmount();
    }

    public void setStore(@NonNull String store) {
        boolean changesMade = !store.equals(mExpense.getStore());
        if (changesMade) {
            setChangesMade();
            mExpense.setStore(store);
        }
    }

    @Nullable
    public String getStore() {
        return mExpense.getStore();
    }

    public void setDescription(@NonNull String description) {
        mExpense.setDescription(description);
        setChangesMade();
    }

    @Nullable
    public String getDescription() {
        return mExpense.getDescription();
    }

    public boolean isFlagged() {
        return mExpense.isStarred();
    }

    public void setFlag(boolean flagged) {
        mExpense.setIsStarred(flagged);
        setChangesMade();
    }

    public boolean isChangesMade() {
        return mChangesMade;
    }

    private void setChangesMade() {
        mChangesMade = true;
    }

    public int getAddMode() {
        return mAddMode;
    }

    public boolean isSplitAvailable() {
        return AppHelper.isSplittingEnabled();
    }

    public boolean isSplit() {
        return mIsSplit;
    }

    public void setSplit() {
        mIsSplit = !mIsSplit;
    }

    public boolean isIncomeAvailable() {
        return AppHelper.isIncomeEnabled();
    }

    public boolean isIncome() {
        return mExpense.isIncome();
    }

    public void setIncome(boolean isIncome) {
        mExpense.setIsIncome(isIncome);
    }

    private void reset(@Nullable Expense expense) {
        mIsSplit = false;
        mOldMonthIndex = null;
        if (expense != null) {
            mExpense = expense;
            mAddMode = Constants.AddExpenseMode.EDIT;
        } else {
            mExpense = new Expense();
            mExpense.setDateTime(new Date().getTime());
            mExpense.setAmount(BigDecimal.ZERO);
            mAddMode = Constants.AddExpenseMode.ADD;
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Timber.i("View model cleared");
    }

    public boolean enableEntitiesAutoComplete() {
        return mAddMode == Constants.AddExpenseMode.ADD;
    }
}
