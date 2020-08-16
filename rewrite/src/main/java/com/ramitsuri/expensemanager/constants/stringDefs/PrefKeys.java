package com.ramitsuri.expensemanager.constants.stringDefs;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.StringDef;

@StringDef(value = {
        PrefKeys.ACCOUNT_NAME,
        PrefKeys.ACCOUNT_TYPE,
        PrefKeys.SPREADSHEET_ID,
        PrefKeys.SHEET_ID,
        PrefKeys.THEME,
        PrefKeys.DEFAULT_SHEET_ID,
        PrefKeys.ENABLE_EXPENSE_SYNC,
        PrefKeys.ENABLE_ENTITIES_SYNC,
        PrefKeys.IS_ENTITIES_EDITED,
        PrefKeys.TIME_ZONE_ID,
        PrefKeys.IS_FIRST_RUN_COMPLETE,
        PrefKeys.BACKUP_INFO_STATUS,
        PrefKeys.ENABLE_INCOME,
        PrefKeys.ENABLE_WORK_LOG,
        PrefKeys.ENABLE_BACKUP_NOW,
        PrefKeys.IS_PRUNE_COMPLETE,
        PrefKeys.SURPRISE_MESSAGE,
        PrefKeys.SHARED_COLLECTION_NAME,
        PrefKeys.SHARED_THIS_SOURCE,
        PrefKeys.SHARED_OTHER_SOURCE,
        PrefKeys.BACKUP_ISSUE_FIXED
})
@Retention(RetentionPolicy.SOURCE)
public @interface PrefKeys {
    String ACCOUNT_NAME = "settings_account_name";
    String ACCOUNT_TYPE = "settings_account_type";
    String SPREADSHEET_ID = "settings_spreadsheet_id";
    String SHEET_ID = "settings_sheet_id";
    String THEME = "settings_theme";
    String DEFAULT_SHEET_ID = "default_sheet_id";
    String ENABLE_EXPENSE_SYNC = "enable_expense_sync";
    String ENABLE_ENTITIES_SYNC = "enable_entities_sync";
    String IS_ENTITIES_EDITED = "is_entities_edited";
    String TIME_ZONE_ID = "settings_time_zone_id";
    String IS_FIRST_RUN_COMPLETE = "is_first_run_complete";
    String BACKUP_INFO_STATUS = "backup_info_status";
    String ENABLE_INCOME = "enable_income";
    String ENABLE_WORK_LOG = "enable_work_log";
    String ENABLE_BACKUP_NOW = "enable_backup_now";
    String IS_PRUNE_COMPLETE = "is_prune_complete";
    String SURPRISE_MESSAGE = "surprise_message";
    String SHARED_COLLECTION_NAME = "shared_collection_name";
    String SHARED_THIS_SOURCE = "shared_this_source";
    String SHARED_OTHER_SOURCE = "shared_other_source";
    String BACKUP_ISSUE_FIXED = "backup_issue_fixed";
}
