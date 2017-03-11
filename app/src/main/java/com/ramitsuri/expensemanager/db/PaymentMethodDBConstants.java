package com.ramitsuri.expensemanager.db;

public class PaymentMethodDBConstants {
    public static final String TABLE_PAYMENT_METHOD = "paymentMethods";
    public static final String COLUMN_ID = "paymentMethodId";
    public static final String COLUMN_NAME = "paymentMethodName";

    public static final String CREATE_TABLE_CATEGORIES =
            "CREATE TABLE "
                    + TABLE_PAYMENT_METHOD
                    + "("
                    + COLUMN_ID + " INTEGER, "
                    + COLUMN_NAME + " TEXT"
                    + ");"
            ;
}
