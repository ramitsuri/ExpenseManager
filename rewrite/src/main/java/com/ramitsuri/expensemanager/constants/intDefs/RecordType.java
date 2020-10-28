package com.ramitsuri.expensemanager.constants.intDefs;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.StringDef;

@StringDef({RecordType.MONTHLY, RecordType.ANNUAL})
@Retention(RetentionPolicy.SOURCE)
public @interface RecordType {
    String MONTHLY = "MONTHLY";
    String ANNUAL = "ANNUAL";
}
