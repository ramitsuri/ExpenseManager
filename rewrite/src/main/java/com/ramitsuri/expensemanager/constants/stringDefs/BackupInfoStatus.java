package com.ramitsuri.expensemanager.constants.stringDefs;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.StringDef;

@StringDef(value = {
        BackupInfoStatus.NO,
        BackupInfoStatus.MAYBE,
        BackupInfoStatus.CREATING,
        BackupInfoStatus.ERROR,
        BackupInfoStatus.OK
})
@Retention(RetentionPolicy.SOURCE)
public @interface BackupInfoStatus {
    String NO = "not_enabled"; // Backup not enabled (spreadsheet id is empty)
    String MAYBE = "likely_enabled"; // Backup is likely enabled, might be some issues
    String CREATING = "creating_spreadsheet"; // Creating spreadsheet
    String ERROR = "user_recoverable_error"; // When more access is needed
    String OK = "everything_good"; // Everything should be good
}
