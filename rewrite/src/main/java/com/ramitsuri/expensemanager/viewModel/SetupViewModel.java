package com.ramitsuri.expensemanager.viewModel;

import com.ramitsuri.expensemanager.Constants;
import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.data.repository.CategoryRepository;
import com.ramitsuri.expensemanager.data.repository.PaymentMethodRepository;
import com.ramitsuri.expensemanager.data.repository.SheetRepository;
import com.ramitsuri.sheetscore.consumerResponse.EntitiesConsumerResponse;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import timber.log.Timber;

public class SetupViewModel extends ViewModel {

    private CategoryRepository mCategoryRepository;
    private PaymentMethodRepository mPaymentMethodRepository;
    private SheetRepository mSheetRepository;

    public SetupViewModel() {
        super();

        mCategoryRepository = MainApplication.getInstance().getCategoryRepo();
        mPaymentMethodRepository = MainApplication.getInstance().getPaymentMethodRepo();
    }

    public void initSheetRepository(String accountName, String accountType, String spreadsheetId) {
        if (MainApplication.getInstance().getSheetRepository() == null) {
            Timber.e("Sheet repo is null");
            MainApplication.getInstance().initSheetRepo(spreadsheetId, accountName, accountType);
        }
        mSheetRepository = MainApplication.getInstance().getSheetRepository();
    }

    @Nullable
    public LiveData<EntitiesConsumerResponse> getEntitiesFromSheets() {
        if (mSheetRepository != null) {
            return mSheetRepository.getEntityData(Constants.Range.CATEGORIES_PAYMENT_METHODS);
        }
        return null;
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
