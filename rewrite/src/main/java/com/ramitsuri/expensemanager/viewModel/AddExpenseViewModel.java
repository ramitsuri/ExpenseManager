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
import com.ramitsuri.expensemanager.entities.SheetInfo;
import com.ramitsuri.expensemanager.utils.AppHelper;
import com.ramitsuri.expensemanager.utils.CurrencyHelper;

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
    private Integer mOldSheetId;
    private LiveData<List<String>> mCategories;
    private LiveData<List<String>> mPaymentMethods;
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

        reset(expense);
    }

    public LiveData<List<String>> getCategories() {
        return mCategories;
    }

    public LiveData<List<String>> getPaymentMethods() {
        return mPaymentMethods;
    }

    public void add() {
        Expense expense = new Expense(mExpense);
        if (mIsSplit) {
            expense.setAmount(CurrencyHelper.divide(expense.getAmount(), new BigDecimal("2")));
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
        boolean wasSynced = mExpense.isSynced();
        mExpenseRepo.edit(expense);
        // Backed up expense was edited, update Edited Sheets table to add this expense's sheet id
        if (wasSynced) { // Backed up expense was edited
            int editedSheetId;
            // Expense was moved from old sheet to new sheet
            // Old sheet would need to be rewritten as an expense from that was deleted
            if (mOldSheetId != null) {
                editedSheetId = mOldSheetId;
            } else { // Sheet wasn't changed so current sheet would need to be rewritten
                editedSheetId = expense.getSheetId();
            }
            MainApplication.getInstance().getEditedSheetRepo()
                    .insertEditedSheet(new EditedSheet(editedSheetId));
        }
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

    public void setSheet(@Nonnull SheetInfo sheetInfo) {
        boolean changesMade = sheetInfo.getSheetId() != mExpense.getSheetId();
        if (changesMade) {
            // A backed up expense is basically being deleted from the old sheet. Save it so that
            // it can be updated.
            if (mExpense.isSynced()) {
                mOldSheetId = mExpense.getSheetId();
            }
            mExpense.setSheetId(sheetInfo.getSheetId());
        }
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

    private void reset(@Nullable Expense expense) {
        mIsSplit = false;
        mOldSheetId = null;
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
}
