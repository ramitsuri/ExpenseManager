package com.ramitsuri.expensemanager.service;

import android.app.job.JobParameters;
import android.app.job.JobService;

import com.ramitsuri.expensemanager.async.SheetsBackupTask;
import com.ramitsuri.expensemanager.entities.LoaderResponse;

public class BackupService extends JobService {
    public BackupService() {
    }

    //if you start asynchronous processing in this method, return true
    @Override
    public boolean onStartJob(final JobParameters params) {
        new SheetsBackupTask(getApplicationContext()){
            @Override
            protected void onPostExecute(LoaderResponse loaderResponse) {
                super.onPostExecute(loaderResponse);
                if(loaderResponse.getResponseCode() == LoaderResponse.SUCCESS){
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
