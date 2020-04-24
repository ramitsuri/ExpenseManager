package com.ramitsuri.expensemanager.constants.stringDefs;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.StringDef;

@StringDef(value = {
        SecretMessages.ENABLE_SPLITTING,
        SecretMessages.DISABLE_SPLITTING,
        SecretMessages.ENABLE_EXPENSE_SYNC,
        SecretMessages.DISABLE_EXPENSE_SYNC,
        SecretMessages.ENABLE_ENTITIES_SYNC,
        SecretMessages.DISABLE_ENTITIES_SYNC,
        SecretMessages.ENABLE_INCOME,
        SecretMessages.DISABLE_INCOME
})
@Retention(RetentionPolicy.SOURCE)
public @interface SecretMessages {
    String ENABLE_SPLITTING = "ENABLE_SPLITTING";
    String DISABLE_SPLITTING = "DISABLE_SPLITTING";
    String ENABLE_EXPENSE_SYNC = "ENABLE_EXPENSE_SYNC";
    String DISABLE_EXPENSE_SYNC = "DISABLE_EXPENSE_SYNC";
    String ENABLE_ENTITIES_SYNC = "ENABLE_ENTITIES_SYNC";
    String DISABLE_ENTITIES_SYNC = "DISABLE_ENTITIES_SYNC";
    String ENABLE_INCOME = "ENABLE_INCOME";
    String DISABLE_INCOME = "DISABLE_INCOME";
    String ENABLE_WORK_LOG = "ENABLE_WORK_LOG";
    String DISABLE_WORK_LOG = "DISABLE_WORK_LOG";
}
