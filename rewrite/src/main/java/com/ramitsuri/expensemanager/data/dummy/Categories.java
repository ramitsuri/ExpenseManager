package com.ramitsuri.expensemanager.data.dummy;

import com.ramitsuri.expensemanager.entities.Category;

import java.util.ArrayList;
import java.util.List;

public class Categories {
    public static String[] getCategories() {
        return new String[] {
                "Food",
                "Travel",
                "Entertainment",
                "Utilities",
                "Rent",
                "Home",
                "Groceries",
                "Tech",
                "Miscellaneous",
                "Fun",
                "Personal",
                "Shopping",
                "Car",
        };
    }

    public static List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        for (String categoryName : getCategories()) {
            Category category = new Category();
            category.setName(categoryName);
            categories.add(category);
        }
        return categories;
    }
}
