package com.ramitsuri.expensemanager.viewModel;

import android.text.TextUtils;

import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.data.repository.LogRepository;
import com.ramitsuri.expensemanager.entities.Log;
import com.ramitsuri.expensemanager.utils.AppHelper;
import com.ramitsuri.expensemanager.utils.DateHelper;
import com.ramitsuri.expensemanager.utils.SecretMessageHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

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
        final TimeZone timeZone = AppHelper.getTimeZone();

        mLogs = Transformations.map(mLogRepository.getLogs(),
                new Function<List<Log>, List<String>>() {
                    @Override
                    public List<String> apply(List<Log> input) {
                        List<String> logs = new ArrayList<>();
                        for (Log log : input) {
                            if (log != null) {
                                String sb = DateHelper.getLogDate(log.getTime(), timeZone) +
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

        SecretMessageHelper.onSecretMessageEntered(secret);
    }
}
