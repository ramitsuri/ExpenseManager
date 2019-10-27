package com.ramitsuri.expensemanager.viewModel;

import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.data.repository.CategoryRepository;
import com.ramitsuri.expensemanager.data.repository.ExpenseRepository;
import com.ramitsuri.expensemanager.data.repository.PaymentMethodRepository;
import com.ramitsuri.expensemanager.entities.Category;
import com.ramitsuri.expensemanager.entities.Expense;
import com.ramitsuri.expensemanager.entities.PaymentMethod;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class AddExpenseViewModel extends ViewModel {

    private ExpenseRepository mExpenseRepo;
    private CategoryRepository mCategoryRepo;
    private PaymentMethodRepository mPaymentMethodRepo;

    private Expense mExpense;
    private LiveData<List<String>> mCategories;
    private LiveData<List<String>> mPaymentMethods;

    private boolean mChangesMade;

    public AddExpenseViewModel() {
        super();

        MainApplication.getInstance().initRepos();
        mExpenseRepo = MainApplication.getInstance().getExpenseRepo();
        mCategoryRepo = MainApplication.getInstance().getCategoryRepo();
        mPaymentMethodRepo = MainApplication.getInstance().getPaymentMethodRepo();

        mCategories = Transformations.map(mCategoryRepo.getCategories(),
                new Function<List<Category>, List<String>>() {
                    @Override
                    public List<String> apply(List<Category> categories) {
                        List<String> categoryStrings = new ArrayList<>();
                        for (Category category : categories) {
                            categoryStrings.add(category.getName());
                        }
                        return categoryStrings;
                    }
                });
        mPaymentMethods = Transformations.map(mPaymentMethodRepo.getPaymentMethods(),
                new Function<List<PaymentMethod>, List<String>>() {
                    @Override
                    public List<String> apply(List<PaymentMethod> paymentMethods) {
                        List<String> paymentMethodStrings = new ArrayList<>();
                        for (PaymentMethod paymentMethod : paymentMethods) {
                            paymentMethodStrings.add(paymentMethod.getName());
                        }
                        return paymentMethodStrings;
                    }
                });

        reset();
    }

    public LiveData<List<String>> getCategories() {
        return mCategories;
    }

    public LiveData<List<String>> getPaymentMethods() {
        return mPaymentMethods;
    }

    public void addExpense() {
        Expense expense = mExpense;
        mExpenseRepo.insertExpense(expense);
        reset();
    }

    public long getExpenseDate() {
        return mExpense.getDateTime();
    }

    public void setExpenseDate(long date) {
        mExpense.setDateTime(date);
        setChangesMade();
    }

    public void setExpenseCategory(@NonNull String category) {
        mExpense.setCategory(category);
        setChangesMade();
    }

    public void setExpensePaymentMethod(@NonNull String paymentMethod) {
        mExpense.setPaymentMethod(paymentMethod);
        setChangesMade();
    }

    public void setExpenseAmount(@NonNull String amount) {
        mExpense.setAmount(new BigDecimal(amount));
        setChangesMade();
    }

    public void setExpenseStore(@NonNull String store) {
        mExpense.setStore(store);
        setChangesMade();
    }

    public void setExpenseDescription(@NonNull String description) {
        mExpense.setDescription(description);
        setChangesMade();
    }

    public boolean isChangesMade() {
        return mChangesMade;
    }

    private void setChangesMade() {
        mChangesMade = true;
    }

    private void reset() {
        mExpense = new Expense();
        mExpense.setDateTime(new Date().getTime());
    }
}
