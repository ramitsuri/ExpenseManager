package com.ramitsuri.expensemanager.work;

import android.content.Context;

import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.entities.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public abstract class BaseWorker extends Worker {

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
}
