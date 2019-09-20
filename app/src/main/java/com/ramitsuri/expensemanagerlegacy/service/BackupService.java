package com.ramitsuri.expensemanagerlegacy.service;

import android.app.job.JobParameters;
import android.app.job.JobService;

import com.ramitsuri.expensemanagerlegacy.async.SheetsBackupTask;
import com.ramitsuri.expensemanagerlegacy.entities.LoaderResponse;
import com.ramitsuri.expensemanagerlegacy.helper.AppHelper;

public class BackupService extends JobService {
    public BackupService() {
    }

    //if you start asynchronous processing in this method, return true
    @Override
    public boolean onStartJob(final JobParameters params) {
        new SheetsBackupTask(getApplicationContext()) {
            @Override
            protected void onPostExecute(LoaderResponse loaderResponse) {
                super.onPostExecute(loaderResponse);
                if (loaderResponse.getResponseCode() == LoaderResponse.SUCCESS) {
                    AppHelper.setLastBackupTime(System.currentTimeMillis());
                    jobFinished(params, false);
                } else {
                    jobFinished(params, true);
                }
            }
        }.execute();
        return true;
    }

    //If the job fails for some reason, return true
    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
