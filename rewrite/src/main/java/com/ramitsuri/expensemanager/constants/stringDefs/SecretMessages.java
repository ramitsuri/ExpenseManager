package com.ramitsuri.expensemanager.constants.stringDefs;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.StringDef;

@StringDef(value = {
        SecretMessages.ENABLE_SPLITTING,
        SecretMessages.DISABLE_SPLITTING,
        SecretMessages.ENABLE_EXPENSE_SYNC,
        SecretMessages.DISABLE_EXPENSE_SYNC
})
@Retention(RetentionPolicy.SOURCE)
public @interface SecretMessages {
    String ENABLE_SPLITTING = "ENABLE_SPLITTING";
    String DISABLE_SPLITTING = "DISABLE_SPLITTING";
    String ENABLE_EXPENSE_SYNC = "ENABLE_EXPENSE_SYNC";
    String DISABLE_EXPENSE_SYNC = "DISABLE_EXPENSE_SYNC";
}
