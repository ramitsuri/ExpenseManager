package com.ramitsuri.expensemanager.constants.intDefs;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@StringDef({RecurType.NONE, RecurType.DAILY, RecurType.WEEKLY, RecurType.MONTHLY})
@Retention(RetentionPolicy.SOURCE)
public @interface RecurType {
    String NONE = "NONE";
    String DAILY = "DAILY";
    String WEEKLY = "WEEKLY";
    String MONTHLY = "MONTHLY";
}
