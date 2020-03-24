package com.ramitsuri.expensemanager.viewModel;

import android.text.TextUtils;

import com.ramitsuri.expensemanager.BuildConfig;
import com.ramitsuri.expensemanager.Constants;
import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.data.repository.SheetRepository;
import com.ramitsuri.expensemanager.utils.AppHelper;
import com.ramitsuri.expensemanager.utils.WorkHelper;
import com.ramitsuri.sheetscore.consumerResponse.EntitiesConsumerResponse;

import javax.annotation.Nullable;

import androidx.annotation.ArrayRes;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import timber.log.Timber;

public class MiscellaneousViewModel extends ViewModel {

    private long mDeleteLastPressTime;
    private int mAboutPressCount;
    private boolean mEnableHidden;
    private MutableLiveData<String> mSpreadsheetId;
    private MutableLiveData<String> mCurrentTheme;
    private SheetRepository mRepository;

    public MiscellaneousViewModel() {
        super();

        mRepository = MainApplication.getInstance().getSheetRepository();

        mDeleteLastPressTime = 0;

        mAboutPressCount = 0;

        mSpreadsheetId = new MutableLiveData<>();
        mSpreadsheetId.postValue(AppHelper.getSpreadsheetId());

        mCurrentTheme = new MutableLiveData<>();
        mCurrentTheme.postValue(AppHelper.getCurrentTheme());
    }

    public void initiateBackup() {
        Timber.i("Initiating backup");
        WorkHelper.enqueueOneTimeBackup();
    }

    public void syncDataFromSheet() {
        Timber.i("Initiating sync");
        WorkHelper.enqueueOneTimeSync();
    }

    public void syncExpensesFromSheet() {
        Timber.i("Initiating sync expenses");
        WorkHelper.enqueueOneTimeExpenseSync();
    }

    public void deleteExpenses() {
        Timber.i("Deleting expenses");
        long currentTime = System.currentTimeMillis();
        if (currentTime - mDeleteLastPressTime <= 2000) {
            if (BuildConfig.DEBUG) { // Extra protection, only in debug
                MainApplication.getInstance().getExpenseRepo().delete();
            }
            mDeleteLastPressTime = 0;
        } else {
            mDeleteLastPressTime = currentTime;
        }
    }

    public boolean enableHidden() {
        return mEnableHidden || BuildConfig.DEBUG;
    }

    public boolean enableDeleteAll() {
        return BuildConfig.DEBUG;
    }

    public LiveData<String> getSpreadsheetIdLive() {
        return mSpreadsheetId;
    }

    public String getSpreadsheetId() {
        return AppHelper.getSpreadsheetId();
    }

    public void setSpreadsheetId(String spreadsheetId) {
        AppHelper.setSpreadsheetId(spreadsheetId);
        mSpreadsheetId.postValue(spreadsheetId);
    }

    public boolean versionInfoPressSuccess() {
        if (mEnableHidden || BuildConfig.DEBUG) {
            return false;
        }
        mAboutPressCount = mAboutPressCount + 1;
        if (mAboutPressCount >= 7) {
            mEnableHidden = true;
            return true;
        }
        return false;
    }

    public String getVersionInfo() {
        return AppHelper.getVersionInfo();
    }

    @ArrayRes
    public int getThemeTitles() {
        return R.array.theme_titles;
    }

    public int getSelectedTheme() {
        String currentTheme = AppHelper.getCurrentTheme();
        if (currentTheme != null) {
            if (currentTheme.equals(Constants.SystemTheme.LIGHT)) {
                return 0;
            } else if (currentTheme.equals(Constants.SystemTheme.DARK)) {
                return 1;
            } else {
                return 2;
            }
        }
        return 2; // Default to system or battery saver
    }

    public LiveData<String> getSelectedThemeLive() {
        return mCurrentTheme;
    }

    public void setTheme(int themeIndex) {
        String theme;
        if (themeIndex == 0) {
            theme = Constants.SystemTheme.LIGHT;
        } else if (themeIndex == 1) {
            theme = Constants.SystemTheme.DARK;
        } else {
            theme = Constants.SystemTheme.SYSTEM_DEFAULT;
        }
        AppHelper.setCurrentTheme(theme);
        mCurrentTheme.postValue(theme);
    }

    // Used only to get the exception to restore access
    @Nullable
    public LiveData<EntitiesConsumerResponse> restoreAccess() {
        String spreadsheetId = AppHelper.getSpreadsheetId();
        if (TextUtils.isEmpty(spreadsheetId)) {
            Timber.i("SpreadsheetId is null or empty");
            return null;
        }
        MainApplication.getInstance()
                .refreshSheetRepo(AppHelper.getAccountName(), AppHelper.getAccountType());
        return mRepository.getEntityData(spreadsheetId, Constants.Range.CATEGORIES_PAYMENT_METHODS);
    }
}
