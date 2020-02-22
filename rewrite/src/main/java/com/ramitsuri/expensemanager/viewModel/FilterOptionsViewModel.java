package com.ramitsuri.expensemanager.viewModel;

import android.text.TextUtils;

import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.data.repository.SheetRepository;
import com.ramitsuri.expensemanager.entities.SheetInfo;
import com.ramitsuri.expensemanager.utils.AppHelper;
import com.ramitsuri.expensemanager.utils.TransformationHelper;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import timber.log.Timber;

public class FilterOptionsViewModel extends ViewModel {
    private LiveData<List<SheetInfo>> mSheetInfos;

    public FilterOptionsViewModel() {
        super();
        SheetRepository sheetRepo = MainApplication.getInstance().getSheetRepository();

        String spreadsheetId = AppHelper.getSpreadsheetId();
        if (TextUtils.isEmpty(spreadsheetId)) {
            Timber.i("SpreadsheetId is null or empty");
        } else {
            mSheetInfos = Transformations.map(sheetRepo.getSheetInfos(spreadsheetId, false),
                    new Function<List<SheetInfo>, List<SheetInfo>>() {
                        @Override
                        public List<SheetInfo> apply(List<SheetInfo> input) {
                            return TransformationHelper.filterSheetInfos(input);
                        }
                    });
        }
    }

    @Nullable
    public LiveData<List<SheetInfo>> getSheetInfos() {
        return mSheetInfos;
    }
}
