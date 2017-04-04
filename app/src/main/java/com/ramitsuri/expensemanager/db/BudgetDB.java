package com.ramitsuri.expensemanager.db;

import android.content.ContentValues;
import android.content.Context;

import com.ramitsuri.expensemanager.entities.Budget;

public class BudgetDB extends BaseDB {

    public BudgetDB(Context context) {
        super(context);
    }

    public String[] getAllColumns(){
        return new String[]{
                DBConstants.COLUMN_CATEGORIES_ID,
                DBConstants.COLUMN_CATEGORIES_NAME
        };
    }

    public String[] getAllJoinColumns() {
        
    }
}
