package com.ramitsuri.expensemanager.viewModel;

import androidx.annotation.ArrayRes;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ramitsuri.expensemanager.BuildConfig;
import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.constants.Constants;
import com.ramitsuri.expensemanager.utils.AppHelper;

import timber.log.Timber;

public class MiscellaneousViewModel extends ViewModel {

    private long mDeleteLastPressTime;
    private int mAboutPressCount;
    private boolean mEnableHidden;
    private MutableLiveData<String> mCurrentTheme;

    public MiscellaneousViewModel() {
        super();

        mDeleteLastPressTime = 0;

        mAboutPressCount = 0;

        mCurrentTheme = new MutableLiveData<>();
        mCurrentTheme.postValue(AppHelper.getCurrentTheme());
    }

    public void deleteExpenses() {
        Timber.i("Delete requested");
        long currentTime = System.currentTimeMillis();
        if (currentTime - mDeleteLastPressTime <= 2000) {
            if (BuildConfig.DEBUG) { // Extra protection, only in debug
                Timber.i("Deleting all expenses");
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

    public boolean versionInfoPressSuccess() {
        if (enableHidden()) {
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
}
