package com.ramitsuri.expensemanager.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.ramitsuri.expensemanager.entities.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryDB extends BaseDB {

    public CategoryDB(Context context) {
        super(context);
    }

    public String[] getAllColumns() {
        return new String[] {
                DBConstants.COLUMN_CATEGORIES_ID,
                DBConstants.COLUMN_CATEGORIES_NAME
        };
    }

    public ContentValues getCategoryContentValues(Category category) {
        int id = category.getId();
        String name = category.getName();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstants.COLUMN_CATEGORIES_ID, id);
        contentValues.put(DBConstants.COLUMN_CATEGORIES_NAME, name);

        return contentValues;
    }

    public Category getCategoryFromCursor(Cursor cursor) {
        Category category = new Category();
        for (String column : cursor.getColumnNames()) {
            if (column.equals(DBConstants.COLUMN_CATEGORIES_ID)) {
                int value = cursor.getInt(cursor.getColumnIndex(DBConstants.COLUMN_CATEGORIES_ID));
                category.setId(value);
            } else if (column.equals(DBConstants.COLUMN_CATEGORIES_NAME)) {
                String value =
                        cursor.getString(cursor.getColumnIndex(DBConstants.COLUMN_CATEGORIES_NAME));
                category.setName(value);
            }
        }
        return category;
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

    public List<Category> getAllCategories() {
        open();

        String[] columns = getAllColumns();

        Cursor cursor = getCursor(DBConstants.TABLE_CATEGORIES, columns, null, null, null,
                null, null, null);

        List<Category> categories = new ArrayList<>();
        try {
            if (cursor.moveToFirst()) {
                do {
                    Category category = getCategoryFromCursor(cursor);
                    categories.add(category);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {

        }

        cursor.close();
        close();

        return categories;
    }

    public Category getFirstCategory() {
        open();

        String[] columns = getAllColumns();

        Cursor cursor = getCursor(DBConstants.TABLE_CATEGORIES, columns, null, null, null, null,
                null, null);
        Category category = null;
        if (cursor.moveToFirst()) {
            category = getCategoryFromCursor(cursor);
        }
        cursor.close();
        close();
        return category;
    }

    //TODO don't delete if category has corresponding rows in expense table
    public synchronized boolean deleteCategory(int categoryId) {
        if (categoryId <= 0) {
            throw new IllegalArgumentException();
        }
        open();
        String selection1 = DBConstants.COLUMN_CATEGORIES_ID + " = ?";
        String[] selectionArgs1 = new String[] {
                String.valueOf(categoryId)
        };

        int result1 = mDatabase.delete(
                DBConstants.TABLE_CATEGORIES,
                selection1,
                selectionArgs1
        );

        close();

        return result1 > 0;
    }

    public synchronized boolean setCategoryName(int categoryId, String newName) {
        if (categoryId <= 0) {
            throw new IllegalArgumentException();
        }
        open();

        String selection = DBConstants.COLUMN_CATEGORIES_ID + " = ?";
        String[] selectionArgs = new String[] {
                String.valueOf(categoryId)
        };

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstants.COLUMN_CATEGORIES_NAME, newName);

        int result = mDatabase.update(
                DBConstants.TABLE_CATEGORIES,
                contentValues,
                selection,
                selectionArgs
        );
        close();

        return result > 0;
    }
}
