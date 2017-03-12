package com.ramitsuri.expensemanager.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class BaseDB {

    private SQLHelper mSQLHelper;
    protected SQLiteDatabase mDatabase;

    public BaseDB(Context context){
        mSQLHelper = SQLHelper.getInstance(context.getApplicationContext());
    }

    protected void open(){
        mDatabase = mSQLHelper.getWritableDatabase();
    }

    protected void close(){

    }

    public Cursor getCursor(String table, String[] columns, String selection,
                            String[] selectionArgs, String groupBy, String having, String orderBy,
                            String limit){
        open();
        return mDatabase.query(table, columns, selection, selectionArgs, groupBy, having, orderBy,
                limit);
    }

    public Cursor getCursor(String sql, String[] selectionArgs){
        open();
        return mDatabase.rawQuery(sql, selectionArgs);
    }

    protected boolean isTrue(int dbBoolean){
        return dbBoolean == 1;
    }

    protected String getCol(String table, String column){
        return table + "." + column;
    }

    protected String getCol(String table, String column, String as){
        return table + "." + column + " As " + as;
    }

    protected String getJoinTable(String table1, String table1Column, String table2,
                                  String table2Column){
        return new StringBuilder()
                .append(table1)
                .append(" JOIN ")
                .append(table2)
                .append(" ON ")
                .append(getCol(table1, table1Column))
                .append("=")
                .append(getCol(table2, table2Column))
                .toString();
    }

    protected String getJoinTable(String table1, String table1Column, String table2,
                                  String table2Column, String table3, String table3Column){
        return new StringBuilder()
                .append(getJoinTable(table1, table1Column, table2, table2Column))
                .append(" JOIN ")
                .append(table3)
                .append(" ON ")
                .append(getCol(table1, table1Column))
                .append(" = ")
                .append(getCol(table3, table3Column))
                .toString();
    }
}
