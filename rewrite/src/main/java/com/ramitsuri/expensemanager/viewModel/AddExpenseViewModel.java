package com.ramitsuri.expensemanager.viewModel;

import com.ramitsuri.expensemanager.Constants;
import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.data.repository.CategoryRepository;
import com.ramitsuri.expensemanager.data.repository.ExpenseRepository;
import com.ramitsuri.expensemanager.data.repository.PaymentMethodRepository;
import com.ramitsuri.expensemanager.data.repository.SheetRepository;
import com.ramitsuri.expensemanager.entities.Category;
import com.ramitsuri.expensemanager.entities.Expense;
import com.ramitsuri.expensemanager.entities.PaymentMethod;
import com.ramitsuri.expensemanager.entities.SheetInfo;
import com.ramitsuri.expensemanager.utils.AppHelper;

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

public class AddExpenseViewModel extends ViewModel {

    private ExpenseRepository mExpenseRepo;
    private CategoryRepository mCategoryRepo;
    private PaymentMethodRepository mPaymentMethodRepo;
    private SheetRepository mSheetRepo;

    private Expense mExpense;
    private LiveData<List<String>> mCategories;
    private LiveData<List<String>> mPaymentMethods;
    private LiveData<List<SheetInfo>> mSheetInfos;
    private int mAddMode;

    private boolean mChangesMade;

    public AddExpenseViewModel(Expense expense) {
        super();

        mExpenseRepo = MainApplication.getInstance().getExpenseRepo();
        mCategoryRepo = MainApplication.getInstance().getCategoryRepo();
        mPaymentMethodRepo = MainApplication.getInstance().getPaymentMethodRepo();
        mSheetRepo = MainApplication.getInstance().getSheetRepository();

        mCategories = Transformations.map(mCategoryRepo.getCategories(),
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
        mPaymentMethods = Transformations.map(mPaymentMethodRepo.getPaymentMethods(),
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

        mSheetInfos = Transformations.map(mSheetRepo.getSheetInfos(false),
                new Function<List<SheetInfo>, List<SheetInfo>>() {
                    @Override
                    public List<SheetInfo> apply(List<SheetInfo> input) {
                        List<SheetInfo> sheetInfos = new ArrayList<>();
                        if (input != null) {
                            for (SheetInfo sheetInfo : input) {
                                if (sheetInfo.getSheetName().equals("Entities") ||
                                        sheetInfo.getSheetName().equals("Template") ||
                                        sheetInfo.getSheetName().equals("Calculator")) {
                                    continue;
                                }
                                sheetInfos.add(sheetInfo);
                            }
                        }
                        return sheetInfos;
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

    public LiveData<List<SheetInfo>> getSheetInfos() {
        return mSheetInfos;
    }

    public void addExpense() {
        Expense expense = mExpense;
        mExpenseRepo.insertExpense(expense);
        AppHelper.setDefaultSheetId(expense.getSheetId());
        reset(null);
    }

    public void editExpense() {
        Expense expense = mExpense;
        mExpenseRepo.editExpense(expense);
        reset(null);
    }

    public long getExpenseDate() {
        return mExpense.getDateTime();
    }

    public void setExpenseDate(long date) {
        mExpense.setDateTime(date);
        setChangesMade();
    }

    public void setExpenseCategory(@NonNull String category) {
        boolean changesMade = !category.equals(mExpense.getCategory());
        if (changesMade) {
            //setChangesMade();
            mExpense.setCategory(category);
        }
    }

    public void setExpensePaymentMethod(@NonNull String paymentMethod) {
        boolean changesMade = !paymentMethod.equals(mExpense.getPaymentMethod());
        if (changesMade) {
            //setChangesMade();
            mExpense.setPaymentMethod(paymentMethod);
        }
    }

    public void setExpenseAmount(@NonNull String amount) {
        BigDecimal bdAmount = new BigDecimal(amount);
        boolean changesMade = !(bdAmount.compareTo(mExpense.getAmount()) == 0);
        if (changesMade) {
            setChangesMade();
            mExpense.setAmount(bdAmount);
        }
    }

    public void setExpenseStore(@NonNull String store) {
        boolean changesMade = !store.equals(mExpense.getStore());
        if (changesMade) {
            setChangesMade();
            mExpense.setStore(store);
        }
    }

    public void setExpenseDescription(@NonNull String description) {
        mExpense.setDescription(description);
        setChangesMade();
    }

    public void setExpenseSheet(@Nonnull SheetInfo sheetInfo) {
        boolean changesMade = sheetInfo.getSheetId() != mExpense.getSheetId();
        if (changesMade) {
            mExpense.setSheetId(sheetInfo.getSheetId());
        }
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

    private void reset(@Nullable Expense expense) {
        if (expense != null) {
            mExpense = expense;
            mAddMode = Constants.AddExpenseMode.EDIT;
        } else {
            mExpense = new Expense();
            mExpense.setDateTime(new Date().getTime());
            mExpense.setAmount(BigDecimal.ZERO);
            mExpense.setSheetId(AppHelper.getDefaultSheetId());
            mAddMode = Constants.AddExpenseMode.ADD;
        }
    }
}
