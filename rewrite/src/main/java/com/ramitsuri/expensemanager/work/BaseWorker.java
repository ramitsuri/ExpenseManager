package com.ramitsuri.expensemanager.work;

import android.content.Context;

import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.constants.Constants;
import com.ramitsuri.expensemanager.entities.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import timber.log.Timber;

abstract class BaseWorker extends Worker {

    BaseWorker(@NonNull Context context,
            @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    void insertLog(String type, String result, String message) {
        MainApplication.getInstance().getLogRepo().insertLog(new Log(
                System.currentTimeMillis(),
                type,
                result,
                message
        ));
    }

    void onFailure(String type, String message) {
        Timber.i(message);
        insertLog(type, Constants.LogResult.FAILURE, message);
    }

    void onSuccess(String type, String message) {
        Timber.i(message);
        insertLog(type, Constants.LogResult.SUCCESS, message);
    }
}
