package com.ramitsuri.expensemanager.viewModel;

import android.text.TextUtils;

import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.constants.stringDefs.SecretMessages;
import com.ramitsuri.expensemanager.data.repository.LogRepository;
import com.ramitsuri.expensemanager.entities.Log;
import com.ramitsuri.expensemanager.utils.AppHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import timber.log.Timber;

public class MetadataViewModel extends ViewModel {
    private LogRepository mLogRepository;

    private LiveData<List<String>> mLogs;

    public MetadataViewModel() {
        mLogRepository = MainApplication.getInstance().getLogRepo();

        mLogs = Transformations.map(mLogRepository.getLogs(),
                new Function<List<Log>, List<String>>() {
                    @Override
                    public List<String> apply(List<Log> input) {
                        List<String> logs = new ArrayList<>();
                        for (Log log : input) {
                            if (log != null) {
                                String sb = new Date(log.getTime()) +
                                        " | " +
                                        log.getType() +
                                        " | " +
                                        log.getResult() +
                                        " | " +
                                        log.getMessage();
                                logs.add(sb);
                            }
                        }
                        return logs;
                    }
                });
    }

    public void refreshLogs() {
        mLogRepository.getAllLogs();
    }

    public LiveData<List<String>> getLogs() {
        return mLogs;
    }

    private void deleteLogs() {
        mLogRepository.deleteAll();
    }

    public void onDeleteClicked(String secret) {
        if (TextUtils.isEmpty(secret)) {
            Timber.i("Secret is empty, deleting logs");
            deleteLogs();
            refreshLogs();
            return;
        } else {
            Timber.i("Secret is not empty, not deleting logs");
        }

        switch (secret.trim().toUpperCase()) {
            case SecretMessages.ENABLE_SPLITTING:
                Timber.i("Enabling splitting");
                AppHelper.setSplittingEnabled(true);
                break;

            case SecretMessages.DISABLE_SPLITTING:
                Timber.i("Disabling splitting");
                AppHelper.setSplittingEnabled(false);
                break;

            case SecretMessages.ENABLE_EXPENSE_SYNC:
                Timber.i("Enabling expense sync");
                AppHelper.setExpenseSyncEnabled(true);
                break;

            case SecretMessages.DISABLE_EXPENSE_SYNC:
                Timber.i("Disabling expense sync");
                AppHelper.setExpenseSyncEnabled(false);
                break;

            case SecretMessages.ENABLE_ENTITIES_SYNC:
                Timber.i("Enabling entities sync");
                AppHelper.setEntitiesSyncEnabled(true);
                break;

            case SecretMessages.DISABLE_ENTITIES_SYNC:
                Timber.i("Disabling entities sync");
                AppHelper.setEntitiesSyncEnabled(false);
                break;

            default:
                Timber.i("Secret message means nothing");
        }
    }
}
