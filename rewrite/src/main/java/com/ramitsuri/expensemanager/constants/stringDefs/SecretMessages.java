package com.ramitsuri.expensemanager.constants.stringDefs;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.StringDef;

@StringDef(value = {
        SecretMessages.ENABLE_SPLITTING,
        SecretMessages.DISABLE_SPLITTING
})
@Retention(RetentionPolicy.SOURCE)
public @interface SecretMessages {
    String ENABLE_SPLITTING = "ENABLE SPLITTING";
    String DISABLE_SPLITTING = "DISABLE SPLITTING";
}
