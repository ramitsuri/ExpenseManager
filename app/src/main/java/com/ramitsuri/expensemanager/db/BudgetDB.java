package com.ramitsuri.expensemanager.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.ramitsuri.expensemanager.entities.Budget;
import com.ramitsuri.expensemanager.entities.Category;
import com.ramitsuri.expensemanager.helper.CategoryHelper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class BudgetDB extends BaseDB {

    public BudgetDB(Context context) {
        super(context);
    }

    public String[] getAllColumns() {
        return new String[] {
                DBConstants.COLUMN_BUDGET_ID,
                DBConstants.COLUMN_BUDGET_CATEGORY_IDS,
                DBConstants.COLUMN_BUDGET_AMOUNT
        };
    }

    public ContentValues getBudgetContentValues(Budget budget) {
        String amount = String.valueOf(budget.getTotalBudget());
        StringBuilder sb = new StringBuilder();
        for (Category category : budget.getCategories()) {
            sb.append(category.getId());
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        String ids = sb.toString();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstants.COLUMN_BUDGET_CATEGORY_IDS, ids);
        contentValues.put(DBConstants.COLUMN_BUDGET_AMOUNT, amount);
        return contentValues;
    }

    public Budget getBudgetFromCursor(Cursor cursor, List<Category> categories) {
        Budget budget = new Budget();
        for (String column : cursor.getColumnNames()) {
            if (column.equals(DBConstants.COLUMN_BUDGET_ID)) {
                int value = cursor.getInt(cursor.getColumnIndex(DBConstants.COLUMN_BUDGET_ID));
                budget.setId(value);
            } else if (column.equals(DBConstants.COLUMN_BUDGET_AMOUNT)) {
                String value =
                        cursor.getString(cursor.getColumnIndex(DBConstants.COLUMN_BUDGET_AMOUNT));
                budget.setTotalBudget(new BigDecimal(value));
            } else if (column.equals(DBConstants.COLUMN_BUDGET_CATEGORY_IDS)) {
                String value =
                        cursor.getString(
                                cursor.getColumnIndex(DBConstants.COLUMN_BUDGET_CATEGORY_IDS));
                budget.setCategories(getCategoriesFromIds(categories));
            }
        }
        return budget;
    }

    public synchronized boolean setCategory(String name) {
        open();

        boolean insertSuccess = true;
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstants.COLUMN_CATEGORIES_NAME, name);
        long result = mDatabase.insertOrThrow(DBConstants.TABLE_CATEGORIES, null,
                contentValues);
        if (result <= 0) {
            insertSuccess = false;
        }
        close();
        return insertSuccess;
    }

    public List<Budget> getAllBudgets() {
        open();

        String[] columns = getAllColumns();

        Cursor cursor = getCursor(DBConstants.TABLE_BUDGET, columns, null, null, null,
                null, null, null);

        List<Budget> budgets = new ArrayList<>();
        try {
            if (cursor.moveToFirst()) {
                List<Category> categories = CategoryHelper.getAllCategories();
                do {
                    Budget budget = getBudgetFromCursor(cursor, categories);
                    budgets.add(budget);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {

        }

        cursor.close();
        close();

        return budgets;
    }

    public synchronized boolean deleteBudget(int budgetId) {
        if (budgetId <= 0) {
            throw new IllegalArgumentException();
        }
        open();
        String selection1 = DBConstants.COLUMN_BUDGET_ID + " = ?";
        String[] selectionArgs1 = new String[] {
                String.valueOf(budgetId)
        };

        int result1 = mDatabase.delete(
                DBConstants.TABLE_BUDGET,
                selection1,
                selectionArgs1
        );

        close();

        return result1 > 0;
    }

    public synchronized boolean setBudgetAmount(int budgetId, BigDecimal newBudget) {
        if (budgetId <= 0) {
            throw new IllegalArgumentException();
        }
        open();

        String selection = DBConstants.COLUMN_BUDGET_ID + " = ?";
        String[] selectionArgs = new String[] {
                String.valueOf(budgetId)
        };

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstants.COLUMN_BUDGET_AMOUNT, String.valueOf(newBudget));

        int result = mDatabase.update(
                DBConstants.TABLE_BUDGET,
                contentValues,
                selection,
                selectionArgs
        );
        close();

        return result > 0;
    }

    //TODO
    public List<Category> getCategoriesFromIds(List<Category> categories) {

        return null;
    }
}
