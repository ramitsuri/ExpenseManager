package com.ramitsuri.expensemanager.constants.intDefs;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@StringDef({RepeatType.NONE, RepeatType.DAILY, RepeatType.WEEKLY, RepeatType.MONTHLY})
@Retention(RetentionPolicy.SOURCE)
public @interface RepeatType {
    String NONE = "NONE";
    String DAILY = "DAILY";
    String WEEKLY = "WEEKLY";
    String MONTHLY = "MONTHLY";
}
