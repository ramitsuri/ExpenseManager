package com.ramitsuri.expensemanager.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLHelper extends SQLiteOpenHelper {

    private static SQLHelper sInstance;
    public static final String DATABASE_NAME = "expensemanager.db";

    public static synchronized SQLHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new SQLHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    public SQLHelper(Context context) {
        super(context, DATABASE_NAME, null, DBConstants.DATABASE_VERSION);
    }

    public SQLHelper(Context context, String name,
            SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DBConstants.CREATE_TABLE_EXPENSES);
        sqLiteDatabase.execSQL(DBConstants.CREATE_TABLE_CATEGORIES);
        sqLiteDatabase.execSQL(DBConstants.CREATE_TABLE_PAYMENT_METHODS);
        sqLiteDatabase.execSQL(DBConstants.CREATE_TABLE_BUDGET);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String drop = "DROP TABLE IF EXISTS ";
        /*sqLiteDatabase.execSQL(drop + DBConstants.TABLE_EXPENSES);
        sqLiteDatabase.execSQL(drop + DBConstants.TABLE_CATEGORIES);
        sqLiteDatabase.execSQL(drop + DBConstants.TABLE_PAYMENT_METHOD);*/
        sqLiteDatabase.execSQL(DBConstants.CREATE_TABLE_BUDGET);
        onCreate(sqLiteDatabase);
    }
}
