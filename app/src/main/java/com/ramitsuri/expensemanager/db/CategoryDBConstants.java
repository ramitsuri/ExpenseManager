package com.ramitsuri.expensemanager.db;

public class CategoryDBConstants {

    public static final String TABLE_CATEGORIES = "categories";
    public static final String COLUMN_ID = "categoryId";
    public static final String COLUMN_NAME = "categoryName";

    public static final String CREATE_TABLE_CATEGORIES =
            "CREATE TABLE "
                    + TABLE_CATEGORIES
                    + "("
                    + COLUMN_ID + " INTEGER, "
                    + COLUMN_NAME + " TEXT"
                    + ");"
            ;
}
