package com.ramitsuri.expensemanager.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLHelper extends SQLiteOpenHelper {


    public static final int DATABASE_VERSION = 1;
    private static SQLHelper sInstance;
    public static final String DATABASE_NAME = "expensemanager.db";

    public static synchronized SQLHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new SQLHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    public SQLHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public SQLHelper(Context context, String name,
                     SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(ExpenseDBConstants.CREATE_TABLE_EXPENSES);
        sqLiteDatabase.execSQL(CategoryDBConstants.CREATE_TABLE_CATEGORIES);
        sqLiteDatabase.execSQL(PaymentMethodDBConstants.CREATE_TABLE_CATEGORIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String drop = "DROP TABLE IF EXISTS ";
        sqLiteDatabase.execSQL(drop + ExpenseDBConstants.TABLE_EEPENSES);
        sqLiteDatabase.execSQL(drop + CategoryDBConstants.CREATE_TABLE_CATEGORIES);
        sqLiteDatabase.execSQL(drop + PaymentMethodDBConstants.CREATE_TABLE_CATEGORIES);
        onCreate(sqLiteDatabase);
    }
}
