package com.ramitsuri.expensemanager.constants.intDefs;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;

@IntDef({PickedItem.CATEGORY, PickedItem.PAYMENT_METHOD})
@Retention(RetentionPolicy.SOURCE)
public @interface PickedItem {
    int CATEGORY = 0;
    int PAYMENT_METHOD = 1;
}
