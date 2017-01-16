    package com.ramitsuri.expensemanager.db;

import android.provider.BaseColumns;

/**
 * Created by ramitsuri on 1/15/2017.
 */

public final class ExpenseContract {

    private ExpenseContract(){}

    public static class ExpenseEntry implements BaseColumns{
        public static final String TABLE_NAME = "expense";
        public static final String COLUMN_ROW_ID = "rowID";
        public static final String COLUMN_DATE_TIME = "dateTime";
        public static final String COLUMN_AMOUNT = "amount";
        public static final String COLUMN_PAYMENT_MODE = "PaymentMode";
        public static final String COLUMN_CATEGORY_ID = "categoryID";
        public static final String COLUMN_CATEGORY_NAME = "categoryName";
        public static final String COLUMN_CATEGORY_PARENT_ID = "categoryParentID";
        public static final String COLUMN_NOTES = "notes";
        public static final String COLUMN_SYNC_STATUS = "syncStatus";

        public static final int ID_COLUMN_ROW_ID = 1;
        public static final int ID_COLUMN_DATE_TIME = 2;
        public static final int ID_COLUMN_AMOUNT = 3;
        public static final int ID_COLUMN_PAYMENT_MODE = 4;
        public static final int ID_COLUMN_CATEGORY_ID = 5;
        public static final int ID_COLUMN_CATEGORY_NAME = 6;
        public static final int ID_COLUMN_CATEGORY_PARENT_ID = 7;
        public static final int ID_COLUMN_NOTES = 8;
        public static final int ID_COLUMN_SYNC_STATUS = 8;
    }
}
