    package com.ramitsuri.expensemanager.db;

import android.provider.BaseColumns;

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
        public static final String COLUMN_NOTES = "notes";
        public static final String COLUMN_SYNC_STATUS = "syncStatus";
        public static final String COLUMN_FLAGGED = "flagged";
    }
}
