package com.ramitsuri.expensemanager.constants.intDefs;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@StringDef({AddType.MANUAL, AddType.RECUR})
@Retention(RetentionPolicy.SOURCE)
public @interface AddType {
    String MANUAL = "MANUAL";
    String RECUR = "RECUR";
}
