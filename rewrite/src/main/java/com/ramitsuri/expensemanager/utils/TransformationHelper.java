package com.ramitsuri.expensemanager.utils;

import android.util.LongSparseArray;

import com.ramitsuri.expensemanager.constants.intDefs.ListItemType;
import com.ramitsuri.expensemanager.entities.Expense;
import com.ramitsuri.expensemanager.ui.adapter.ExpenseWrapper;

import java.util.ArrayList;
import java.util.List;

public class TransformationHelper {

    public static List<ExpenseWrapper> toExpenseWrapperList(List<Expense> input) {
        LongSparseArray<List<Expense>> map = new LongSparseArray<>();
        for (Expense expense : input) {
            long commonDate = DateHelper.toSheetsDate(expense.getDateTime());
            List<Expense> expenses = map.get(commonDate);
            if (expenses == null) {
                expenses = new ArrayList<>();
            }
            expenses.add(expense);
            map.put(commonDate, expenses);
        }
        List<ExpenseWrapper> expenseWrappers = new ArrayList<>();
        for (int i = map.size() - 1; i >= 0; i--) {
            long date = map.keyAt(i);
            List<Expense> expenses = map.get(date);
            if (expenses == null || expenses.size() == 0) {
                continue;
            }

            ExpenseWrapper wrapper = new ExpenseWrapper();
            wrapper.setItemType(ListItemType.HEADER);
            wrapper.setDate(DateHelper.getFriendlyDate(expenses.get(0).getDateTime()));
            wrapper.setExpense(null);
            expenseWrappers.add(wrapper);

            for (Expense expense : expenses) {
                wrapper = new ExpenseWrapper();
                wrapper.setItemType(ListItemType.ITEM);
                wrapper.setExpense(expense);
                wrapper.setDate(null);
                expenseWrappers.add(wrapper);
            }
        }
        return expenseWrappers;
    }
}
