package com.ramitsuri.expensemanagerrewrite.viewModel;

import com.ramitsuri.expensemanagerrewrite.Constants;
import com.ramitsuri.expensemanagerrewrite.MainApplication;
import com.ramitsuri.expensemanagerrewrite.data.repository.CategoryRepository;
import com.ramitsuri.expensemanagerrewrite.data.repository.PaymentMethodRepository;
import com.ramitsuri.expensemanagerrewrite.data.repository.SheetRepository;
import com.ramitsuri.sheetscore.consumerResponse.EntitiesConsumerResponse;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import timber.log.Timber;

public class SetupViewModel extends ViewModel {

    private CategoryRepository mCategoryRepository;
    private PaymentMethodRepository mPaymentMethodRepository;
    private SheetRepository mSheetRepository;

    public SetupViewModel() {
        super();

        MainApplication.getInstance().initRepos();
        mCategoryRepository = MainApplication.getInstance().getCategoryRepo();
        mPaymentMethodRepository = MainApplication.getInstance().getPaymentMethodRepo();
        mSheetRepository = MainApplication.getInstance().getSheetRepository();
    }

    public LiveData<EntitiesConsumerResponse> getEntitiesFromSheets() {
        return mSheetRepository.getEntityData(Constants.Range.CATEGORIES_PAYMENT_METHODS);
    }

    public void saveEntities(List<List<String>> stringsList) {
        if (stringsList.size() != 2) {
            Timber.w("Attempting to save entities, list size should be 2. Exiting.");
            return;
        }
        if (mPaymentMethodRepository != null) {
            mPaymentMethodRepository.setPaymentMethods(stringsList.get(0));
        }
        if (mCategoryRepository != null) {
            mCategoryRepository.setCategories(stringsList.get(1));
        }
    }
}