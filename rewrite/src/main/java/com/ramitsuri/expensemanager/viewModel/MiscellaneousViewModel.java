package com.ramitsuri.expensemanager.viewModel;

import android.accounts.Account;
import android.content.Intent;
import android.text.TextUtils;

import com.ramitsuri.expensemanager.BuildConfig;
import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.constants.Constants;
import com.ramitsuri.expensemanager.constants.stringDefs.BackupInfoStatus;
import com.ramitsuri.expensemanager.utils.AppHelper;
import com.ramitsuri.expensemanager.utils.WorkHelper;
import com.ramitsuri.sheetscore.googleSignIn.AccountManager;
import com.ramitsuri.sheetscore.googleSignIn.SignInResponse;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import androidx.annotation.ArrayRes;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.work.WorkInfo;
import timber.log.Timber;

public class MiscellaneousViewModel extends ViewModel {

    private long mDeleteLastPressTime;
    private int mAboutPressCount;
    private boolean mEnableHidden;
    private MutableLiveData<String> mCurrentTheme;
    private MutableLiveData<String> mBackupInfoStatus;

    public MiscellaneousViewModel() {
        super();

        mDeleteLastPressTime = 0;

        mAboutPressCount = 0;

        mCurrentTheme = new MutableLiveData<>();
        mCurrentTheme.postValue(AppHelper.getCurrentTheme());

        mBackupInfoStatus = new MutableLiveData<>();
        postBackupInfoStatus();
    }

    public void initiateBackup() {
        Timber.i("Initiating backup");
        WorkHelper.enqueueOneTimeBackup();
        WorkHelper.enqueueOneTimeEntitiesBackup();
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

    public String getSpreadsheetId() {
        return AppHelper.getSpreadsheetId();
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

    public boolean enableExpenseSync() {
        return enableHidden() && AppHelper.isExpenseSyncEnabled();
    }

    public boolean enableEntitiesSync() {
        return enableHidden() && AppHelper.isEntitiesSyncEnabled();
    }

    @Nonnull
    public LiveData<String> getBackupInfoStatus() {
        return mBackupInfoStatus;
    }

    public LiveData<Boolean> onBackupInfoClicked() {
        if (TextUtils.isEmpty(getSpreadsheetId())) { // Create spreadsheet
            Timber.i("Spreadsheet id is empty, initiating create spreadsheet");
            mBackupInfoStatus.postValue(BackupInfoStatus.CREATING);
            WorkHelper.enqueueOneTimeCreateSpreadsheet();
            // Transform work info to post status for backup info. if work has completed,
            // a new status needs to be posted so progress can be hidden
            return Transformations
                    .map(WorkHelper.getWorkStatus(WorkHelper.getOneTimeCreateSpreadsheetTag()),
                            new Function<List<WorkInfo>, Boolean>() {
                                @Override
                                public Boolean apply(List<WorkInfo> workInfoList) {
                                    if (workInfoList != null && workInfoList.size() > 0) {
                                        WorkInfo info = workInfoList.get(0);
                                        if (info.getState().isFinished()) {
                                            postBackupInfoStatus();
                                            return true;
                                        }
                                    }
                                    return null;
                                }
                            });
        }
        return null;
    }

    @Nullable
    public Account getSignInAccount() {
        AccountManager accountManager = new AccountManager();
        SignInResponse response =
                accountManager.prepareSignIn(MainApplication.getInstance(), AppHelper.getScopes());
        if (response.getGoogleSignInAccount() != null) {
            Account account = response.getGoogleSignInAccount().getAccount();
            if (account != null) {
                onAccountInfoReceived(account);
                return account;
            }
        }
        return null;
    }

    @Nullable
    public Intent getSignInIntent() {
        AccountManager accountManager = new AccountManager();
        SignInResponse response =
                accountManager.prepareSignIn(MainApplication.getInstance(), AppHelper.getScopes());
        return response.getGoogleSignInIntent();
    }

    private void onAccountInfoReceived(@Nonnull Account account) {
        Timber.i("On account received %s", account.name);
        MainApplication.getInstance().refreshSheetRepo(account);
    }

    private void postBackupInfoStatus() {
        String savedBackupInfoStatus = AppHelper.getBackupInfoStatus();
        if (TextUtils.isEmpty(getSpreadsheetId())) { // No spreadsheet id, no connection
            mBackupInfoStatus.postValue(BackupInfoStatus.NO);
        } else if (BackupInfoStatus.ERROR.equals(savedBackupInfoStatus)) { // Error
            mBackupInfoStatus.postValue(BackupInfoStatus.ERROR);
        } else if (BackupInfoStatus.OK.equals(savedBackupInfoStatus)) { // All good
            mBackupInfoStatus.postValue(BackupInfoStatus.OK);
            // TODO move to MainApp onCreate once transition complete
            // Enqueue periodic backups
            WorkHelper.enqueuePeriodicBackup();
            WorkHelper.enqueuePeriodicEntitiesBackup();
        } else { // Maybe connected
            mBackupInfoStatus.postValue(BackupInfoStatus.MAYBE);
        }
    }
}
