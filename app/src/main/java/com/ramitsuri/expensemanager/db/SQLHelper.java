package com.ramitsuri.expensemanager.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ramitsuri.expensemanager.constants.DB;

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
        super(context, DATABASE_NAME, null, DB.DATABASE_VERSION);
    }

    public SQLHelper(Context context, String name,
                     SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DB.CREATE_TABLE_EXPENSES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DB.TABLE_EEPENSES);
        onCreate(sqLiteDatabase);
    }
}
