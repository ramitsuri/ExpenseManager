package com.ramitsuri.expensemanager.db;

public class DBConstants {

    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_CATEGORIES = "categories";
    public static final String COLUMN_CATEGORIES_ID = "categoryId";
    public static final String COLUMN_CATEGORIES_NAME = "categoryName";

    public static final String CREATE_TABLE_CATEGORIES =
            "CREATE TABLE "
                    + TABLE_CATEGORIES
                    + "("
                    + COLUMN_CATEGORIES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                    + COLUMN_CATEGORIES_NAME + " TEXT"
                    + ");"
            ;


    public static final String TABLE_EXPENSES = "expenses";
    public static final String COLUMN_EXPENSE_ROW_ID = "rowIdentifier";
    public static final String COLUMN_EXPENSE_DATE_TIME = "dateTime";
    public static final String COLUMN_EXPENSE_AMOUNT = "amount";
    public static final String COLUMN_EXPENSE_PAYMENT_METHOD_ID = "paymentMethodId";
    public static final String COLUMN_EXPENSE_CATEGORY_ID = "categoryId";
    public static final String COLUMN_EXPENSE_NOTES = "notes";
    public static final String COLUMN_EXPENSE_STORE = "store";
    public static final String COLUMN_EXPENSE_SYNC_STATUS = "syncStatus";
    public static final String COLUMN_EXPENSE_FLAGGED = "flagged";


    public static final String CREATE_TABLE_EXPENSES =
            "CREATE TABLE "
                    + TABLE_EXPENSES
                    + "("
                    + COLUMN_EXPENSE_ROW_ID + " TEXT, "
                    + COLUMN_EXPENSE_DATE_TIME + " INTEGER, "
                    + COLUMN_EXPENSE_AMOUNT + " TEXT, "
                    + COLUMN_EXPENSE_PAYMENT_METHOD_ID + " INTEGER, "
                    + COLUMN_EXPENSE_CATEGORY_ID + " INTEGER, "
                    + COLUMN_EXPENSE_NOTES + " TEXT, "
                    + COLUMN_EXPENSE_STORE + " TEXT, "
                    + COLUMN_EXPENSE_SYNC_STATUS + " INTEGER, "
                    + COLUMN_EXPENSE_FLAGGED + " INTEGER "
                    + ");"
            ;

    public static final String TABLE_PAYMENT_METHOD = "paymentMethods";
    public static final String COLUMN_PAYMENT_METHOD_ID = "paymentMethodId";
    public static final String COLUMN_PAYMENT_METHOD_NAME = "paymentMethodName";

    public static final String CREATE_TABLE_PAYMENT_METHODS =
            "CREATE TABLE "
                    + TABLE_PAYMENT_METHOD
                    + "("
                    + COLUMN_PAYMENT_METHOD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                    + COLUMN_PAYMENT_METHOD_NAME + " TEXT"
                    + ");"
            ;
}
