package com.ramitsuri.expensemanagerlegacy.helper;

import com.ramitsuri.expensemanagerlegacy.MainApplication;
import com.ramitsuri.expensemanagerlegacy.db.CategoryDB;
import com.ramitsuri.expensemanagerlegacy.entities.Category;

import java.util.List;

public class CategoryHelper {

    private static CategoryDB getDB() {
        return new CategoryDB(MainApplication.getInstance());
    }

    public static List<Category> getAllCategories() {
        return getDB().getAllCategories();
    }

    public static boolean addCategory(String categoryName) {
        return getDB().setCategory(categoryName);
    }

    public static boolean updateCategoryName(int id, String name) {
        return getDB().setCategoryName(id, name);
    }

    public static Category getFirstCategory() {
        return getDB().getFirstCategory();
    }
}
