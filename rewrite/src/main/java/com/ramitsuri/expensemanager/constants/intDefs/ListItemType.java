package com.ramitsuri.expensemanager.constants.intDefs;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;

@IntDef(flag = true, value = {
        ListItemType.HEADER,
        ListItemType.ITEM
})
@Retention(RetentionPolicy.SOURCE)
public @interface ListItemType {
    int HEADER = 0; // Header
    int ITEM = 1; // Item
}
