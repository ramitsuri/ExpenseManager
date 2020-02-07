package com.ramitsuri.expensemanager.viewModel;

import android.text.TextUtils;

import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.data.repository.LogRepository;
import com.ramitsuri.expensemanager.data.repository.SheetRepository;
import com.ramitsuri.expensemanager.entities.Log;
import com.ramitsuri.expensemanager.entities.SheetInfo;
import com.ramitsuri.expensemanager.utils.AppHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import timber.log.Timber;

public class MetadataViewModel extends ViewModel {
    private SheetRepository mSheetRepository;
    private LogRepository mLogRepository;

    private LiveData<List<String>> mLogs;

    public MetadataViewModel() {
        if (MainApplication.getInstance().getSheetRepository() == null) {
            Timber.e("Sheet repo is null");
        }
        mSheetRepository = MainApplication.getInstance().getSheetRepository();
        mLogRepository = MainApplication.getInstance().getLogRepo();

        mLogs = Transformations.map(mLogRepository.getAllLogs(),
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

    public LiveData<List<String>> getLogs() {
        return mLogs;
    }

    public void deleteLogs() {
        mLogRepository.deleteAll();
    }

    @Nullable
    public LiveData<List<String>> getSheets() {
        String spreadsheetId = AppHelper.getSpreadsheetId();
        if (TextUtils.isEmpty(spreadsheetId)) {
            Timber.i("SpreadsheetId is null or empty");
            return null;
        } else {
            return Transformations.map(mSheetRepository.getSheetInfos(spreadsheetId, true),
                    new Function<List<SheetInfo>, List<String>>() {
                        @Override
                        public List<String> apply(List<SheetInfo> input) {
                            Timber.i("Transforming sheets");
                            List<String> sheets = new ArrayList<>();
                            for (SheetInfo sheetInfo : input) {
                                if (sheetInfo != null) {
                                    String sb = sheetInfo.getSheetName() +
                                            " | " +
                                            sheetInfo.getSheetId();
                                    sheets.add(sb);
                                }
                            }
                            return sheets;
                        }
                    });
        }
    }
}
