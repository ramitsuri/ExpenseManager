package com.ramitsuri.expensemanager.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.ramitsuri.expensemanager.entities.Category;
import com.ramitsuri.expensemanager.entities.Expense;
import com.ramitsuri.expensemanager.entities.PaymentMethod;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ExpenseDB extends BaseDB{


    public ExpenseDB(Context context) {
        super(context);
    }

    public String[] getAllColumns() {
        return new String[]{
                DBConstants.COLUMN_EXPENSE_ROW_ID,
                DBConstants.COLUMN_EXPENSE_DATE_TIME,
                DBConstants.COLUMN_EXPENSE_AMOUNT,
                DBConstants.COLUMN_EXPENSE_PAYMENT_METHOD_ID,
                DBConstants.COLUMN_EXPENSE_CATEGORY_ID,
                DBConstants.COLUMN_EXPENSE_NOTES,
                DBConstants.COLUMN_EXPENSE_STORE,
                DBConstants.COLUMN_EXPENSE_SYNC_STATUS,
                DBConstants.COLUMN_EXPENSE_FLAGGED
        };
    }

    public String[] getAllJoinColumns(){
        String rowIdColumn =
                getCol(DBConstants.TABLE_EXPENSES, DBConstants.COLUMN_EXPENSE_ROW_ID);
        String dateTimeColumn =
                getCol(DBConstants.TABLE_EXPENSES, DBConstants.COLUMN_EXPENSE_DATE_TIME);
        String amountColumn =
                getCol(DBConstants.TABLE_EXPENSES, DBConstants.COLUMN_EXPENSE_AMOUNT);
        String notesColumn =
                getCol(DBConstants.TABLE_EXPENSES, DBConstants.COLUMN_EXPENSE_NOTES);
        String storeColumn =
                getCol(DBConstants.TABLE_EXPENSES, DBConstants.COLUMN_EXPENSE_STORE);
        String syncStatusColumn =
                getCol(DBConstants.TABLE_EXPENSES, DBConstants.COLUMN_EXPENSE_SYNC_STATUS);
        String flagColumn =
                getCol(DBConstants.TABLE_EXPENSES, DBConstants.COLUMN_EXPENSE_FLAGGED);
        String paymentMethodIdColumn =
                getCol(DBConstants.TABLE_PAYMENT_METHOD, DBConstants.COLUMN_PAYMENT_METHOD_ID);
        String paymentMethodNameColumn =
                getCol(DBConstants.TABLE_PAYMENT_METHOD, DBConstants.COLUMN_PAYMENT_METHOD_NAME);
        String categoryIdColumn =
                getCol(DBConstants.TABLE_CATEGORIES, DBConstants.COLUMN_CATEGORIES_ID);
        String categoryNameColumn =
                getCol(DBConstants.TABLE_CATEGORIES, DBConstants.COLUMN_CATEGORIES_NAME);


        String[] columns = {
                rowIdColumn,
                dateTimeColumn,
                amountColumn,
                notesColumn,
                storeColumn,
                syncStatusColumn,
                flagColumn,
                paymentMethodIdColumn,
                paymentMethodNameColumn,
                categoryIdColumn,
                categoryNameColumn
        };
        return columns;
    }

    public ContentValues getExpensesContentValues(Expense expense) {
        String rowId = expense.getRowIdentifier();
        long dateTime = expense.getDateTime();
        String amount = String.valueOf(expense.getAmount());
        int paymentMethodId = expense.getPaymentMethod().getId();
        int categoryID = expense.getCategory().getId();
        String notes = expense.getDescription();
        String store = expense.getStore();
        boolean syncStatus = expense.isSynced();
        boolean flagged = expense.isFlagged();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstants.COLUMN_EXPENSE_ROW_ID, rowId);
        contentValues.put(DBConstants.COLUMN_EXPENSE_DATE_TIME, dateTime);
        contentValues.put(DBConstants.COLUMN_EXPENSE_AMOUNT, amount);
        contentValues.put(DBConstants.COLUMN_EXPENSE_PAYMENT_METHOD_ID, paymentMethodId);
        contentValues.put(DBConstants.COLUMN_EXPENSE_CATEGORY_ID, categoryID);
        contentValues.put(DBConstants.COLUMN_EXPENSE_NOTES, notes);
        contentValues.put(DBConstants.COLUMN_EXPENSE_STORE, store);
        contentValues.put(DBConstants.COLUMN_EXPENSE_SYNC_STATUS, syncStatus);
        contentValues.put(DBConstants.COLUMN_EXPENSE_FLAGGED, flagged);

        return contentValues;
    }

    private Expense getExpenseFromCursor(Cursor cursor) {
        Expense expense = new Expense();
        Category category = new Category();
        PaymentMethod paymentMethod = new PaymentMethod();
        for(String column: cursor.getColumnNames()){
            if(column.equals(DBConstants.COLUMN_EXPENSE_ROW_ID)){
                String value = cursor.getString(
                        cursor.getColumnIndex(DBConstants.COLUMN_EXPENSE_ROW_ID));
                expense.setRowIdentifier(value);
            } else if(column.equals(DBConstants.COLUMN_EXPENSE_DATE_TIME)){
                long value = cursor.getLong(
                        cursor.getColumnIndex(DBConstants.COLUMN_EXPENSE_DATE_TIME));
                expense.setDateTime(value);
            } else if(column.equals(DBConstants.COLUMN_EXPENSE_AMOUNT)){
                String value = cursor.getString(
                        cursor.getColumnIndex(DBConstants.COLUMN_EXPENSE_AMOUNT));
                expense.setAmount(new BigDecimal(value));
            } else if(column.equals(DBConstants.COLUMN_EXPENSE_NOTES)){
                String value = cursor.getString(
                        cursor.getColumnIndex(DBConstants.COLUMN_EXPENSE_NOTES));
                expense.setDescription(value);
            } else if(column.equals(DBConstants.COLUMN_EXPENSE_STORE)){
                String value = cursor.getString(
                        cursor.getColumnIndex(DBConstants.COLUMN_EXPENSE_STORE));
                expense.setStore(value);
            } else if(column.equals(DBConstants.COLUMN_EXPENSE_SYNC_STATUS)){
                int value = cursor.getInt(
                        cursor.getColumnIndex(DBConstants.COLUMN_EXPENSE_SYNC_STATUS));
                expense.setIsSynced(isTrue(value));
            } else if(column.equals(DBConstants.COLUMN_EXPENSE_FLAGGED)){
                int value = cursor.getInt(
                        cursor.getColumnIndex(DBConstants.COLUMN_EXPENSE_FLAGGED));
                expense.setIsFlagged(isTrue(value));
            } else if(column.equals(DBConstants.COLUMN_PAYMENT_METHOD_ID)){
                int value = cursor.getInt(
                        cursor.getColumnIndex(DBConstants.COLUMN_PAYMENT_METHOD_ID));
                paymentMethod.setId(value);
            } else if(column.equals(DBConstants.COLUMN_PAYMENT_METHOD_NAME)){
                String value = cursor.getString(
                        cursor.getColumnIndex(DBConstants.COLUMN_PAYMENT_METHOD_NAME));
                paymentMethod.setName(value);
            } else if(column.equals(DBConstants.COLUMN_CATEGORIES_ID)){
                int value = cursor.getInt(
                        cursor.getColumnIndex(DBConstants.COLUMN_CATEGORIES_ID));
                category.setId(value);
            } else if(column.equals(DBConstants.COLUMN_CATEGORIES_NAME)){
                String value = cursor.getString(
                        cursor.getColumnIndex(DBConstants.COLUMN_CATEGORIES_NAME));
                category.setName(value);
            }
        }
        expense.setCategory(category);
        expense.setPaymentMethod(paymentMethod);
        return expense;
    }

    public synchronized boolean setExpense(Expense expense){
        open();

        boolean insertSuccess = true;
        ContentValues contentValues = getExpensesContentValues(expense);
        long result = mDatabase.insertOrThrow(DBConstants.TABLE_EXPENSES, null,
                contentValues);
        if(result <= 0){
            insertSuccess = false;
        }
        close();
        return insertSuccess;
    }

    public Expense getExpense(String id) {
        open();
        String table = getJoinTable(
                DBConstants.TABLE_EXPENSES, DBConstants.COLUMN_EXPENSE_PAYMENT_METHOD_ID,
                DBConstants.TABLE_PAYMENT_METHOD, DBConstants.COLUMN_PAYMENT_METHOD_ID,
                DBConstants.TABLE_CATEGORIES, DBConstants.COLUMN_CATEGORIES_ID);

        String[] columns = getAllJoinColumns();

        String selection = DBConstants.COLUMN_EXPENSE_ROW_ID + " = ? ";
        String[] selectionArgs = new String[]{
                id
        };

        Cursor cursor = getCursor(table, columns, selection,
                selectionArgs, null, null, null, null);

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

        String table = getJoinTable(
                DBConstants.TABLE_EXPENSES, DBConstants.COLUMN_EXPENSE_PAYMENT_METHOD_ID,
                DBConstants.TABLE_PAYMENT_METHOD, DBConstants.COLUMN_PAYMENT_METHOD_ID,
                DBConstants.TABLE_CATEGORIES, DBConstants.COLUMN_CATEGORIES_ID);

        String[] columns = getAllJoinColumns();

        String selection = null;
        String[] selectionArgs = null;

        Cursor cursor = getCursor(table, columns, selection, selectionArgs, null, null, null, null);

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

    public boolean editExpenseDateTime(String rowId, long dateTime) {
        open();

        String selection = DBConstants.COLUMN_EXPENSE_ROW_ID + " = ?";
        String[] selectionArgs = new String[]{
                rowId
        };

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstants.COLUMN_EXPENSE_DATE_TIME, dateTime);

        int result = mDatabase.update(
                DBConstants.TABLE_EXPENSES,
                contentValues,
                selection,
                selectionArgs
        );
        close();

        return result > 0;
    }

    public boolean editExpenseAmount(String rowId, BigDecimal amount) {
        open();

        String selection = DBConstants.COLUMN_EXPENSE_ROW_ID + " = ?";
        String[] selectionArgs = new String[]{
                rowId
        };

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstants.COLUMN_EXPENSE_AMOUNT, String.valueOf(amount));

        int result = mDatabase.update(
                DBConstants.TABLE_EXPENSES,
                contentValues,
                selection,
                selectionArgs
        );
        close();

        return result > 0;
    }

    public boolean editExpenseDescription(String rowId, String description) {
        open();

        String selection = DBConstants.COLUMN_EXPENSE_ROW_ID + " = ?";
        String[] selectionArgs = new String[]{
                rowId
        };

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstants.COLUMN_EXPENSE_NOTES, description);

        int result = mDatabase.update(
                DBConstants.TABLE_EXPENSES,
                contentValues,
                selection,
                selectionArgs
        );
        close();

        return result > 0;
    }

    public boolean editExpenseStore(String rowId, String store) {
        open();

        String selection = DBConstants.COLUMN_EXPENSE_ROW_ID + " = ?";
        String[] selectionArgs = new String[]{
                rowId
        };

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstants.COLUMN_EXPENSE_STORE, store);

        int result = mDatabase.update(
                DBConstants.TABLE_EXPENSES,
                contentValues,
                selection,
                selectionArgs
        );
        close();

        return result > 0;
    }

    public boolean editExpenseSyncStatus(String rowId, boolean syncStatus) {
        open();

        String selection = DBConstants.COLUMN_EXPENSE_ROW_ID + " = ?";
        String[] selectionArgs = new String[]{
                rowId
        };

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstants.COLUMN_EXPENSE_SYNC_STATUS, syncStatus);

        int result = mDatabase.update(
                DBConstants.TABLE_EXPENSES,
                contentValues,
                selection,
                selectionArgs
        );
        close();

        return result > 0;
    }

    public boolean editExpenseFlag(String rowId, boolean isFlagged) {
        open();

        String selection = DBConstants.COLUMN_EXPENSE_ROW_ID + " = ?";
        String[] selectionArgs = new String[]{
                rowId
        };

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstants.COLUMN_EXPENSE_FLAGGED, isFlagged);

        int result = mDatabase.update(
                DBConstants.TABLE_EXPENSES,
                contentValues,
                selection,
                selectionArgs
        );
        close();

        return result > 0;
    }
    public boolean editExpensePaymentMethodId(String rowId, int id) {
        open();

        String selection = DBConstants.COLUMN_EXPENSE_ROW_ID + " = ?";
        String[] selectionArgs = new String[]{
                rowId
        };

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstants.COLUMN_EXPENSE_PAYMENT_METHOD_ID, id);

        int result = mDatabase.update(
                DBConstants.TABLE_EXPENSES,
                contentValues,
                selection,
                selectionArgs
        );
        close();

        return result > 0;
    }

    public boolean editExpenseCategoryId(String rowId, int id) {
        open();

        String selection = DBConstants.COLUMN_EXPENSE_ROW_ID + " = ?";
        String[] selectionArgs = new String[]{
                rowId
        };

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstants.COLUMN_EXPENSE_CATEGORY_ID, id);

        int result = mDatabase.update(
                DBConstants.TABLE_EXPENSES,
                contentValues,
                selection,
                selectionArgs
        );
        close();

        return result > 0;
    }

    public synchronized boolean deleteExpense(String id){
        open();
        String selection = DBConstants.COLUMN_EXPENSE_ROW_ID + " = ?";
        String[] selectionArgs = new String[]{
                id
        };

        int result = mDatabase.delete(
                DBConstants.TABLE_EXPENSES,
                selection,
                selectionArgs
        );

        close();

        return result > 0;
    }

    public void deleteAllExpense() {

    }
}
