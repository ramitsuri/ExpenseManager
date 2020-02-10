package com.ramitsuri.expensemanager.IntDefs;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;

@IntDef(flag = true, value = {
        MigrationStep.COPY,
        MigrationStep.COPY_IN_PROGRESS,
        MigrationStep.RESTORE_ACCESS,
        MigrationStep.RESTORE_ACCESS_IN_PROGRESS,
        MigrationStep.COMPLETE
})
@Retention(RetentionPolicy.SOURCE)
public @interface MigrationStep {
    int COPY = 0; // Copy current Spreadsheet
    int COPY_IN_PROGRESS = 1; // Copy operation is in progress
    int RESTORE_ACCESS = 2; // Restore access to Google Account
    int RESTORE_ACCESS_IN_PROGRESS = 3; // Restore operation is in process
    int COMPLETE = 4; // Completed
}
