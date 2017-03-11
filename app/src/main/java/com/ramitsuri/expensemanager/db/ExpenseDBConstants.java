package com.ramitsuri.expensemanager.db;

public class ExpenseDBConstants {

    public static final String TABLE_EEPENSES = "expenses";
    public static final String COLUMN_ROW_ID = "rowIdentifier";
    public static final String COLUMN_DATE_TIME = "dateTime";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_PAYMENT_MODE_ID = "paymentModeId";
    public static final String COLUMN_CATEGORY_ID = "categoryId";
    public static final String COLUMN_NOTES = "notes";
    public static final String COLUMN_STORE = "store";
    public static final String COLUMN_SYNC_STATUS = "syncStatus";
    public static final String COLUMN_FLAGGED = "flagged";


    public static final String CREATE_TABLE_EXPENSES =
            "CREATE TABLE "
                    + TABLE_EEPENSES
                    + "("
                    + COLUMN_ROW_ID + " TEXT, "
                    + COLUMN_DATE_TIME + " INTEGER, "
                    + COLUMN_AMOUNT + " REAL, "
                    + COLUMN_PAYMENT_MODE_ID + " INTEGER, "
                    + COLUMN_CATEGORY_ID + " INTEGER, "
                    + COLUMN_NOTES + " TEXT, "
                    + COLUMN_STORE + " TEXT, "
                    + COLUMN_SYNC_STATUS + " TEXT, "
                    + COLUMN_FLAGGED+ " INTEGER "
                    + ");"
            ;
}
