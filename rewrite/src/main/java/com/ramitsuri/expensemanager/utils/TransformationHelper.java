package com.ramitsuri.expensemanager.utils;

import android.util.LongSparseArray;

import com.ramitsuri.expensemanager.constants.Constants;
import com.ramitsuri.expensemanager.constants.intDefs.ListItemType;
import com.ramitsuri.expensemanager.entities.Expense;
import com.ramitsuri.expensemanager.entities.SheetInfo;
import com.ramitsuri.expensemanager.ui.adapter.ExpenseWrapper;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import static com.ramitsuri.expensemanager.constants.Constants.Sheets.EXPENSE_RANGE;

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

    public static List<SheetInfo> filterSheetInfos(List<SheetInfo> input) {
        List<SheetInfo> sheetInfos = new ArrayList<>();
        if (input != null) {
            for (SheetInfo sheetInfo : input) {
                if (sheetInfo.getSheetName().equals(Constants.SheetNames.ENTITIES) ||
                        sheetInfo.getSheetName().equals(Constants.SheetNames.TEMPLATE) ||
                        sheetInfo.getSheetName().equals(Constants.SheetNames.CALCULATOR)) {
                    continue;
                }
                sheetInfos.add(sheetInfo);
            }
        }
        return sheetInfos;
    }

    public static List<String> getExpenseRanges(@Nonnull List<SheetInfo> sheetInfos) {
        List<String> ranges = new ArrayList<>();
        for (SheetInfo info : sheetInfos) {
            ranges.add(info.getSheetName() + EXPENSE_RANGE);
        }
        return ranges;
    }
}
