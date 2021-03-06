package com.ramitsuri.expensemanager.viewModel;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.constants.Constants;
import com.ramitsuri.expensemanager.constants.intDefs.RecordType;
import com.ramitsuri.expensemanager.constants.intDefs.RecurType;
import com.ramitsuri.expensemanager.data.repository.CategoryRepository;
import com.ramitsuri.expensemanager.data.repository.ExpenseRepository;
import com.ramitsuri.expensemanager.data.repository.PaymentMethodRepository;
import com.ramitsuri.expensemanager.data.repository.RecurringExpenseRepository;
import com.ramitsuri.expensemanager.entities.Category;
import com.ramitsuri.expensemanager.entities.Expense;
import com.ramitsuri.expensemanager.entities.PaymentMethod;
import com.ramitsuri.expensemanager.entities.RecurringExpenseInfo;
import com.ramitsuri.expensemanager.utils.ObjectHelper;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.Nonnull;

import timber.log.Timber;

public class AddExpenseViewModel extends ViewModel {

    private Expense mExpense;
    private int mAddMode;
    private boolean mChangesMade;

    private final ExpenseRepository mExpenseRepo;
    private final RecurringExpenseRepository mRecurringRepo;
    private final LiveData<List<Category>> mCategories;
    private final LiveData<List<PaymentMethod>> mPaymentMethods;
    private final LiveData<List<String>> mStores;
    private MediatorLiveData<String> mRecurTypeLive;

    public AddExpenseViewModel(Expense expense) {
        super();

        mExpenseRepo = MainApplication.getInstance().getExpenseRepo();
        mRecurringRepo = MainApplication.getInstance().getRecurringRepo();

        categoryRepo().getForRecordType(RecordType.MONTHLY);
        mCategories = categoryRepo().getCategories();
        mPaymentMethods = paymentRepo().getPaymentMethods();

        mStores = mExpenseRepo.getStores();

        reset(expense);
    }

    public LiveData<List<Category>> getCategories() {
        return mCategories;
    }

    public LiveData<List<PaymentMethod>> getPaymentMethods() {
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
        mExpenseRepo.insert(expense);
        saveRecurringExpenseInfo(expense);
        reset(null);
    }

    public void edit() {
        Expense expense = new Expense(mExpense);
        expense.setId(mExpense.getId());
        mExpenseRepo.edit(expense);
        saveRecurringExpenseInfo(expense);
        reset(null);
    }

    public long getDate() {
        return mExpense.getDateTime();
    }

    public void setDate(long date) {
        mExpense.setDateTime(date);
        setChangesMade();
    }

    @Nullable
    public Category getCategory() {
        Category category = new Category();
        category.setName(mExpense.getCategory());
        category.setRecordType(mExpense.getRecordType());
        return category;
    }

    public void setCategory(@NonNull Category category) {
        if (mCategories.getValue() != null &&
                ObjectHelper.indexOf(mCategories.getValue(), category.getName()) != -1) {
            boolean changesMade = !category.getName().equals(mExpense.getCategory());
            if (changesMade) {
                //setChangesMade();
                mExpense.setCategory(category.getName());
                setRecordType(category.getRecordType());
            }
        }
    }

    public void setRecordType(@Nonnull @RecordType String recordType) {
        mExpense.setRecordType(recordType);
    }

    @Nullable
    public PaymentMethod getPaymentMethod() {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setName(mExpense.getPaymentMethod());
        return paymentMethod;
    }

    public void setPaymentMethod(@NonNull PaymentMethod paymentMethod) {
        if (mPaymentMethods.getValue() != null &&
                ObjectHelper.indexOf(mPaymentMethods.getValue(), paymentMethod.getName()) != -1) {
            boolean changesMade = !paymentMethod.getName().equals(mExpense.getPaymentMethod());
            if (changesMade) {
                //setChangesMade();
                mExpense.setPaymentMethod(paymentMethod.getName());
            }
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
        mExpense.setStarred(flagged);
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

    public void setRecurType(@NonNull @RecurType String recurType) {
        if (mRecurTypeLive != null) {
            mRecurTypeLive.postValue(recurType);
        }
    }

    @Nonnull
    public LiveData<String> getRecurType() {
        if (mRecurTypeLive == null) {
            mRecurTypeLive = new MediatorLiveData<>();
            mRecurTypeLive.addSource(mRecurringRepo.get(mExpense.getIdentifier()),
                    recurringExpenseInfo -> {
                        String recurType;
                        if (recurringExpenseInfo == null) {
                            recurType = RecurType.NONE;
                        } else {
                            recurType = recurringExpenseInfo.getRecurType();
                        }
                        mRecurTypeLive.setValue(recurType);
                    });
        }
        return mRecurTypeLive;
    }

    public Bundle getRecurTypeArgs() {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BundleKeys.RECUR_TYPE, mRecurTypeLive.getValue());
        return bundle;
    }

    public void onAnnualTabSelected() {
        categoryRepo().getForRecordType(RecordType.ANNUAL);
    }

    public void onMonthlyTabSelected() {
        categoryRepo().getForRecordType(RecordType.MONTHLY);
    }

    private void reset(@Nullable Expense expense) {
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

    private void saveRecurringExpenseInfo(@Nonnull Expense expense) {
        RecurringExpenseInfo info = new RecurringExpenseInfo();
        info.setIdentifier(expense.getIdentifier());
        info.setLastOccur(expense.getDateTime());
        info.setRecurType(mRecurTypeLive.getValue());
        mRecurringRepo.insertUpdateOrDelete(info);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Timber.i("View model cleared");
        mRecurTypeLive = null;
    }

    public boolean enableEntitiesAutoComplete() {
        return mAddMode == Constants.AddExpenseMode.ADD;
    }

    private CategoryRepository categoryRepo() {
        return MainApplication.getInstance().getCategoryRepo();
    }

    private PaymentMethodRepository paymentRepo() {
        return MainApplication.getInstance().getPaymentMethodRepo();
    }
}
