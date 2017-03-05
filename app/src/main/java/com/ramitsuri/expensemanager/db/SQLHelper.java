package com.ramitsuri.expensemanager.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLHelper extends SQLiteOpenHelper {

    private static SQLHelper instance = null;
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "expensemanager.db";
    private static final String SQL_CREATE_TABLE_EXPENSE =
            "CREATE TABLE " + ExpenseContract.ExpenseEntry.TABLE_NAME + " (" +
                    ExpenseContract.ExpenseEntry._ID + " INTEGER PRIMARY KEY," +
                    ExpenseContract.ExpenseEntry.COLUMN_ROW_ID + " TEXT," +
                    ExpenseContract.ExpenseEntry.COLUMN_DATE_TIME + " INTEGER,"+
                    ExpenseContract.ExpenseEntry.COLUMN_AMOUNT + " REAL,"+
                    ExpenseContract.ExpenseEntry.COLUMN_PAYMENT_MODE + " TEXT,"+
                    ExpenseContract.ExpenseEntry.COLUMN_CATEGORY_ID + " INTEGER,"+
                    ExpenseContract.ExpenseEntry.COLUMN_CATEGORY_NAME + " TEXT,"+
                    ExpenseContract.ExpenseEntry.COLUMN_NOTES + " TEXT,"+
                    ExpenseContract.ExpenseEntry.COLUMN_SYNC_STATUS + " TEXT)," +
                    ExpenseContract.ExpenseEntry.COLUMN_FLAGGED+ " INTEGER,"
    ;

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ExpenseContract.ExpenseEntry.TABLE_NAME;

    public static SQLHelper getInstance(Context context) {
        if (instance == null) {
            instance = new SQLHelper(context.getApplicationContext());
        }
        return instance;
    }

    public SQLHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public SQLHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_EXPENSE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }
}
