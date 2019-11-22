package com.ramitsuri.expensemanager.viewModel;

import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.data.repository.LogRepository;
import com.ramitsuri.expensemanager.data.repository.SheetRepository;
import com.ramitsuri.expensemanager.entities.Log;
import com.ramitsuri.sheetscore.consumerResponse.SheetMetadata;
import com.ramitsuri.sheetscore.consumerResponse.SheetsMetadataConsumerResponse;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    public LiveData<List<String>> getSheets() {
        return Transformations.map(mSheetRepository.getSheetsMetadata(),
                new Function<SheetsMetadataConsumerResponse, List<String>>() {
                    @Override
                    public List<String> apply(SheetsMetadataConsumerResponse input) {
                        Timber.i("Transforming sheets");
                        List<String> sheets = new ArrayList<>();
                        for (SheetMetadata sheetMetadata : input.getSheetMetadataList()) {
                            if (sheetMetadata != null) {
                                String sb = sheetMetadata.getSheetName() +
                                        " | " +
                                        sheetMetadata.getSheetId();
                                sheets.add(sb);
                            }
                        }
                        return sheets;
                    }
                });
    }
}
