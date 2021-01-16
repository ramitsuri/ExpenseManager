package com.ramitsuri.expensemanager.constants.stringDefs;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface PrefKeys {
    String THEME = "settings_theme";
    String TIME_ZONE_ID = "settings_time_zone_id";
    String IS_FIRST_RUN_COMPLETE = "is_first_run_complete";
    String ENABLE_WORK_LOG = "enable_work_log";
    String IS_PRUNE_COMPLETE = "is_prune_complete_2";
    String IDENTIFIER_ADDED = "identifier_added";
}
