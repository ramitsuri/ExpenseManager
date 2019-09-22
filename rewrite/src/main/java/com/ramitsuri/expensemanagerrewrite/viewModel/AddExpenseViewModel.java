package com.ramitsuri.expensemanagerrewrite.viewModel;

import com.ramitsuri.expensemanagerrewrite.MainApplication;
import com.ramitsuri.expensemanagerrewrite.data.repository.CategoryRepository;
import com.ramitsuri.expensemanagerrewrite.data.repository.ExpenseRepository;
import com.ramitsuri.expensemanagerrewrite.data.repository.PaymentMethodRepository;
import com.ramitsuri.expensemanagerrewrite.entities.Category;
import com.ramitsuri.expensemanagerrewrite.entities.Expense;
import com.ramitsuri.expensemanagerrewrite.entities.PaymentMethod;

import java.util.ArrayList;
import java.util.List;

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

    public AddExpenseViewModel() {
        super();

        MainApplication.getInstance().initRepos();
        mExpenseRepo = MainApplication.getInstance().getExpenseRepo();
        mCategoryRepo = MainApplication.getInstance().getCategoryRepo();
        mPaymentMethodRepo = MainApplication.getInstance().getPaymentMethodRepo();

        mExpense = new Expense();
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
    }

    public LiveData<List<String>> getCategories() {
        return mCategories;
    }

    public LiveData<List<String>> getPaymentMethods() {
        return mPaymentMethods;
    }

    public void addExpense() {
        mExpenseRepo.insertExpense(mExpense);
        mExpense = new Expense();
    }
}
