package com.ramitsuri.expensemanager.constants.stringDefs;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@StringDef(value = {
        SecretMessages.ENABLE_WORK_LOG,
        SecretMessages.DISABLE_WORK_LOG,
        SecretMessages.CANCEL_ONE_TIME,
        SecretMessages.CANCEL_PERIODIC,
        SecretMessages.ENQUEUE_ONE_TIME,
        SecretMessages.ENQUEUE_PERIODIC
})
@Retention(RetentionPolicy.SOURCE)
public @interface SecretMessages {
    String ENABLE_WORK_LOG = "ENABLE_WORK_LOG";
    String DISABLE_WORK_LOG = "DISABLE_WORK_LOG";
    String CANCEL_ONE_TIME = "CANCEL_ONE_TIME";
    String CANCEL_PERIODIC = "CANCEL_PERIODIC";
    String ENQUEUE_ONE_TIME = "ENQUEUE_ONE_TIME";
    String ENQUEUE_PERIODIC = "ENQUEUE_PERIODIC";
}
