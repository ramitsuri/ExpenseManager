package com.ramitsuri.expensemanager.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.ramitsuri.expensemanager.entities.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryDB extends BaseDB{

    public CategoryDB(Context context){
        super(context);
    }

    public String[] getAllColumns(){
        return new String[]{
                ADAPTER_ROWID,
                CategoryDBConstants.COLUMN_ID,
                CategoryDBConstants.COLUMN_NAME
        };
    }

    public ContentValues getCategoryContentValues(Category category){
        int id = category.getId();
        String name = category.getName();

        ContentValues contentValues = new ContentValues();
        contentValues.put(CategoryDBConstants.COLUMN_ID, id);
        contentValues.put(CategoryDBConstants.COLUMN_NAME, name);

        return contentValues;
    }

    public Category getCategoryFromCursor(Cursor cursor){
        Category category = new Category();
        for(String column: cursor.getColumnNames()){
            if(column.equals(CategoryDBConstants.COLUMN_ID)){
                int value = cursor.getInt(cursor.getColumnIndex(CategoryDBConstants.COLUMN_ID));
                category.setId(value);
            } else if(column.equals(CategoryDBConstants.COLUMN_NAME)){
                String value =
                        cursor.getString(cursor.getColumnIndex(CategoryDBConstants.COLUMN_NAME));
                category.setName(value);
            }
        }
        return category;
    }

    public synchronized boolean setCategory(Category category){
        open();

        boolean insertSuccess = true;
        ContentValues contentValues = getCategoryContentValues(category);
        long result = mDatabase.insertOrThrow(CategoryDBConstants.TABLE_CATEGORIES, null,
                contentValues);
        if(result <= 0){
            insertSuccess = false;
        }
        close();
        return insertSuccess;
    }

    public List<Category> getAllCategories() {
        open();

        String[] columns = getAllColumns();

        Cursor cursor = getCursor(CategoryDBConstants.TABLE_CATEGORIES, columns, null, null, null,
                null, null, null);

        List<Category> categories = new ArrayList<>();
        try {
            if(cursor.moveToFirst()){
                do {
                    Category category = getCategoryFromCursor(cursor);
                    categories.add(category);
                } while (cursor.moveToNext());
            }
        } catch (Exception e){

        }

        cursor.close();
        close();

        return categories;
    }

    //TODO don't delete if category has corresponding rows in expense table
    public synchronized boolean deleteCategory(int categoryId){
        if(categoryId <= 0){
            throw new IllegalArgumentException();
        }
        open();
        String selection1 = CategoryDBConstants.COLUMN_ID + " = ?";
        String[] selectionArgs1 = new String[]{
                String.valueOf(categoryId)
        };

        int result1 = mDatabase.delete(
                CategoryDBConstants.TABLE_CATEGORIES,
                selection1,
                selectionArgs1
        );

        close();

        return result1 > 0;
    }

    public synchronized boolean setCategoryName(int categoryId, String newName){
        if(categoryId <= 0){
            throw new IllegalArgumentException();
        }
        open();

        String selection = CategoryDBConstants.COLUMN_ID + " = ?";
        String[] selectionArgs = new String[]{
                String.valueOf(categoryId)
        };

        ContentValues contentValues = new ContentValues();
        contentValues.put(CategoryDBConstants.COLUMN_NAME, newName);

        int result = mDatabase.update(
                CategoryDBConstants.TABLE_CATEGORIES,
                contentValues,
                selection,
                selectionArgs
        );
        close();

        return result > 0;
    }
}
