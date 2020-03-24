package com.ramitsuri.expensemanager.constants.intDefs;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;

@IntDef({SourceType.LOCAL, SourceType.DB})
@Retention(RetentionPolicy.SOURCE)
public @interface SourceType {
    int LOCAL = 0;
    int DB = 1;
}
