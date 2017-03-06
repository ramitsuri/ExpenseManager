package com.ramitsuri.expensemanager.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ramitsuri.expensemanager.entities.Category;
import com.ramitsuri.expensemanager.entities.Expense;

import java.util.ArrayList;
import java.util.List;

public class ExpenseDB {

    private SQLHelper mSQLHelper;
    private SQLiteDatabase mDatabase;
    private static final String ADAPTER_ROWID = "ROWID as _id";

    public ExpenseDB(Context context) {
        mSQLHelper = SQLHelper.getInstance(context.getApplicationContext());
    }

    private void open() {
        mDatabase = mSQLHelper.getWritableDatabase();
    }

    private void close(){

    }

    public Cursor getCursor(String table, String[] columns, String selection, String[] selectionArgs
            , String groupBy, String having, String orderBy, String limit) {
        open();
        return mDatabase.query(table, columns, selection, selectionArgs, groupBy, having, orderBy,
                limit);
    }

    public Cursor getCursor(String sql, String[] selectionArgs) {
        open();
        return mDatabase.rawQuery(sql, selectionArgs);
    }

    public String[] getAllColumns() {
        return new String[]{
                ADAPTER_ROWID,
                Constants.COLUMN_ROW_ID,
                Constants.COLUMN_DATE_TIME,
                Constants.COLUMN_AMOUNT,
                Constants.COLUMN_PAYMENT_MODE,
                Constants.COLUMN_CATEGORY_ID,
                Constants.COLUMN_CATEGORY_NAME,
                Constants.COLUMN_NOTES,
                Constants.COLUMN_STORE,
                Constants.COLUMN_SYNC_STATUS,
                Constants.COLUMN_FLAGGED
        };
    }

    public ContentValues getExpensesContentValues(Expense expense) {
        String rowId = expense.getRowIdentifier();
        long dateTime = expense.getDateTime();
        String amount = expense.getAmount();
        String paymentMode = expense.getPaymentMode();
        int categoryID = expense.getCategory().getId();
        String categoryName = expense.getCategory().getName();
        String notes = expense.getDescription();
        String store = expense.getStore();
        boolean syncStatus = expense.isSynced();
        boolean flagged = expense.isFlagged();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.COLUMN_ROW_ID, rowId);
        contentValues.put(Constants.COLUMN_DATE_TIME, dateTime);
        contentValues.put(Constants.COLUMN_AMOUNT, amount);
        contentValues.put(Constants.COLUMN_PAYMENT_MODE, paymentMode);
        contentValues.put(Constants.COLUMN_CATEGORY_ID, categoryID);
        contentValues.put(Constants.COLUMN_CATEGORY_NAME, categoryName);
        contentValues.put(Constants.COLUMN_NOTES, notes);
        contentValues.put(Constants.COLUMN_STORE, store);
        contentValues.put(Constants.COLUMN_SYNC_STATUS, syncStatus);
        contentValues.put(Constants.COLUMN_FLAGGED, flagged);

        return contentValues;
    }

    private Expense getExpenseFromCursor(Cursor cursor) {
        Expense expense = new Expense();
        Category category = new Category();
        for(String column: cursor.getColumnNames()){
            if(column.equals(Constants.COLUMN_ROW_ID)){
                String value = cursor.getString(cursor.getColumnIndex(Constants.COLUMN_ROW_ID));
                expense.setRowIdentifier(value);
            } else if(column.equals(Constants.COLUMN_DATE_TIME)){
                long value = cursor.getLong(cursor.getColumnIndex(Constants.COLUMN_DATE_TIME));
                expense.setDateTime(value);
            } else if(column.equals(Constants.COLUMN_AMOUNT)){
                String value = cursor.getString(cursor.getColumnIndex(Constants.COLUMN_AMOUNT));
                expense.setAmount(value);
            } else if(column.equals(Constants.COLUMN_PAYMENT_MODE)){
                String value = cursor.getString(cursor.getColumnIndex(Constants.COLUMN_PAYMENT_MODE));
                expense.setPaymentMode(value);
            } else if(column.equals(Constants.COLUMN_CATEGORY_ID)){
                int value = cursor.getInt(cursor.getColumnIndex(Constants.COLUMN_CATEGORY_ID));
                category.setId(value);
            } else if(column.equals(Constants.COLUMN_CATEGORY_NAME)){
                String value = cursor.getString(cursor.getColumnIndex(Constants.COLUMN_CATEGORY_NAME));
                category.setName(value);
            } else if(column.equals(Constants.COLUMN_NOTES)){
                String value = cursor.getString(cursor.getColumnIndex(Constants.COLUMN_NOTES));
                expense.setDescription(value);
            } else if(column.equals(Constants.COLUMN_STORE)){
                String value = cursor.getString(cursor.getColumnIndex(Constants.COLUMN_STORE));
                expense.setStore(value);
            } else if(column.equals(Constants.COLUMN_SYNC_STATUS)){
                int value = cursor.getInt(cursor.getColumnIndex(Constants.COLUMN_SYNC_STATUS));
                expense.setIsSynced(getBooleanForInt(value));
            } else if(column.equals(Constants.COLUMN_FLAGGED)){
                int value = cursor.getInt(cursor.getColumnIndex(Constants.COLUMN_FLAGGED));
                expense.setIsFlagged(getBooleanForInt(value));
            }
        }
        expense.setCategory(category);
        return expense;
    }

    public synchronized boolean setExpense(Expense expense){
        open();

        boolean insertSuccess = true;
        ContentValues contentValues = getExpensesContentValues(expense);
        long result = mDatabase.insertOrThrow(Constants.TABLE_EEPENSES, null, contentValues);
        if(result <= 0){
            insertSuccess = false;
        }
        close();
        return insertSuccess;
    }

    public Expense getExpense(String id) {
        open();
        String[] columns = getAllColumns();
        String selection = Constants.COLUMN_ROW_ID + " = ? ";
        String[] selectionArgs = new String[]{
                id
        };

        Cursor cursor = getCursor(Constants.TABLE_EEPENSES, columns, selection, selectionArgs,
                null, null, null, null);

        Expense expense = null;
        try {
            if(cursor.moveToFirst()){
                expense = getExpenseFromCursor(cursor);
            }
        }
        catch (Exception e){

        }

        cursor.close();
        close();
        return expense;
    }


    public List<Expense> getAllExpense() {
        open();

        String[] columns = getAllColumns();

        Cursor cursor = getCursor(Constants.TABLE_EEPENSES, columns, null, null, null, null, null,
                null);

        List<Expense> expenses = new ArrayList<>();
        try {
            if(cursor.moveToFirst()){
                do {
                    Expense expense = getExpenseFromCursor(cursor);
                    expenses.add(expense);
                } while (cursor.moveToNext());
            }
        } catch (Exception e){

        }

        cursor.close();
        close();

        return expenses;
    }

    public void editExpense(Expense expense) {

    }

    public void deleteExpense(int id) {

    }

    public void deleteAllExpense() {

    }

    private boolean getBooleanForInt(int anInt) {
        return anInt == 0 ? false : true;
    }
}
