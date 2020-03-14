package com.ramitsuri.expensemanager.viewModel;

import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.data.repository.CategoryRepository;
import com.ramitsuri.expensemanager.data.repository.PaymentMethodRepository;
import com.ramitsuri.expensemanager.entities.Budget;
import com.ramitsuri.expensemanager.ui.fragment.SetupCurrentStep;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SetupViewModel extends ViewModel {

    @SetupCurrentStep
    private int mCurrentStep;
    private MutableLiveData<Integer> mCurrentStepLiveData;
    private MutableLiveData<List<String>> mCategoriesLive;
    private MutableLiveData<List<String>> mPaymentMethodsLive;
    private MutableLiveData<List<Budget>> mBudgets;

    public SetupViewModel() {
        super();

        mCurrentStep = SetupCurrentStep.CATEGORIES;
        mCurrentStepLiveData = new MutableLiveData<>();
        mCurrentStepLiveData.postValue(mCurrentStep);

        mCategoriesLive = categoryRepo().getCategoryStrings();
        mPaymentMethodsLive = paymentMethodRepo().getPaymentMethodStrings();
        mBudgets = new MutableLiveData<>();
    }

    public LiveData<Integer> getCurrentStepLive() {
        return mCurrentStepLiveData;
    }

    @SetupCurrentStep
    public int getCurrentStep() {
        return mCurrentStep;
    }

    public boolean canGoPrevious() {
        return mCurrentStep != SetupCurrentStep.CATEGORIES;
    }

    public void goPrevious() {
        if (canGoPrevious()) {
            mCurrentStep = mCurrentStep - 1;
            mCurrentStepLiveData.postValue(mCurrentStep);
        }
    }

    public boolean canGoNext() {
        return mCurrentStep != SetupCurrentStep.BUDGETS;
    }

    public void goNext() {
        if (canGoNext()) {
            mCurrentStep = mCurrentStep + 1;
            mCurrentStepLiveData.postValue(mCurrentStep);
        }
    }

    public LiveData<List<String>> getCategoriesLive() {
        return mCategoriesLive;
    }

    public void setCategories(List<String> categories) {
        mCategoriesLive.postValue(categories);
    }

    public LiveData<List<String>> getPaymentMethodsLive() {
        return mPaymentMethodsLive;
    }

    public void setPaymentMethods(List<String> paymentMethods) {
        mPaymentMethodsLive.postValue(paymentMethods);
    }

    public LiveData<List<Budget>> getBudgets() {
        return mBudgets;
    }

    public void setBudgets(List<Budget> budgets) {
        mBudgets.postValue(budgets);
    }

    public void saveCategories(List<String> categories) {
        if (categoryRepo() != null) {
            categoryRepo().setCategories(categories);
        }
    }

    public void savePaymentMethods(List<String> paymentMethods) {
        if (paymentMethodRepo() != null) {
            paymentMethodRepo().setPaymentMethods(paymentMethods);
        }
    }

    public void saveBudgets(List<Budget> budgets) {
        if (MainApplication.getInstance().getBudgetRepository() != null) {
            MainApplication.getInstance().getBudgetRepository().setBudgets(budgets);
        }
    }

    private CategoryRepository categoryRepo() {
        return MainApplication.getInstance().getCategoryRepo();
    }

    private PaymentMethodRepository paymentMethodRepo() {
        return MainApplication.getInstance().getPaymentMethodRepo();
    }
}
