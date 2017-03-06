package com.ramitsuri.expensemanager.db;

import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.entities.Expense;

import java.util.List;

public class ExpenseHelper {

    private static ExpenseDB getDB(){
        return new ExpenseDB(MainApplication.getInstance());
    }

    public static Expense getExpense(String rowID){
        return getDB().getExpense(rowID);
    }

    public static List<Expense> getExpenses(){
        return getDB().getAllExpense();
    }

    public static void addExpense(Expense expense){
        getDB().setExpense(expense);
    }
}
