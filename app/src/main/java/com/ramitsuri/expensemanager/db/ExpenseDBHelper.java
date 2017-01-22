package com.ramitsuri.expensemanager.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ramitsuri.expensemanager.entities.Category;
import com.ramitsuri.expensemanager.entities.Expense;

import java.util.List;

/**
 * Created by ramitsuri on 1/15/2017.
 */

public class ExpenseDBHelper {

    SQLHelper sqlHelper;
    Context context;
    public ExpenseDBHelper(Context context){
        this.context = context;
        sqlHelper = SQLHelper.getInstance(this.context);
    }

    public Expense getExpense(int id){
        SQLiteDatabase db = sqlHelper.getReadableDatabase();
        String[] projection = {
                ExpenseContract.ExpenseEntry._ID,
                ExpenseContract.ExpenseEntry.COLUMN_ROW_ID,
                ExpenseContract.ExpenseEntry.COLUMN_DATE_TIME,
                ExpenseContract.ExpenseEntry.COLUMN_AMOUNT,
                ExpenseContract.ExpenseEntry.COLUMN_PAYMENT_MODE,
                ExpenseContract.ExpenseEntry.COLUMN_CATEGORY_ID,
                ExpenseContract.ExpenseEntry.COLUMN_CATEGORY_NAME,
                ExpenseContract.ExpenseEntry.COLUMN_CATEGORY_PARENT_ID,
                ExpenseContract.ExpenseEntry.COLUMN_NOTES,
                ExpenseContract.ExpenseEntry.COLUMN_SYNC_STATUS
        };
        String selection = ExpenseContract.ExpenseEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };
        Cursor cursor = db.query(
                ExpenseContract.ExpenseEntry.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null,                                 // The sort order
                null
        );
        if(cursor!=null)
            cursor.moveToFirst();

        return cursorToExpense(cursor);
    }


    public List<Expense> getAllExpense(){

        return null;
    }

    public void addExpense(Expense expense){

    }

    public void editExpense(Expense expense){

    }

    public void deleteExpense(int id){

    }

    public void deleteAllExpense(){

    }

    private boolean getBooleanForInt(int anInt) {
        return anInt == 0 ? false : true;
    }

    private Expense cursorToExpense(Cursor cursor) {
        Expense expense = new Expense();
        expense.setRowIdentifier(cursor.getString(ExpenseContract.ExpenseEntry.ID_COLUMN_ROW_ID));
        expense.setAmount(cursor.getInt(ExpenseContract.ExpenseEntry.ID_COLUMN_AMOUNT));
        expense.setDateTime(cursor.getLong(ExpenseContract.ExpenseEntry.ID_COLUMN_DATE_TIME));
        expense.setNotes(cursor.getString(ExpenseContract.ExpenseEntry.ID_COLUMN_NOTES));
        expense.setSyncStatus(getBooleanForInt(cursor.getInt(ExpenseContract.ExpenseEntry.ID_COLUMN_SYNC_STATUS)));
        expense.setPaymentMode(cursor.getString(ExpenseContract.ExpenseEntry.ID_COLUMN_PAYMENT_MODE));
        Category category = new Category();
        category.setId(cursor.getInt(ExpenseContract.ExpenseEntry.ID_COLUMN_CATEGORY_ID));
        category.setName(cursor.getString(ExpenseContract.ExpenseEntry.ID_COLUMN_CATEGORY_NAME));
        category.setParentID(cursor.getInt(ExpenseContract.ExpenseEntry.ID_COLUMN_CATEGORY_PARENT_ID));
        expense.setCategory(category);

        return expense;
    }
}
