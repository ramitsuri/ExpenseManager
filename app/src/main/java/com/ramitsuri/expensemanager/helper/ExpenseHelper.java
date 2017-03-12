package com.ramitsuri.expensemanager.helper;

import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.constants.ExpenseViewType;
import com.ramitsuri.expensemanager.db.ExpenseDB;
import com.ramitsuri.expensemanager.entities.Expense;
import com.ramitsuri.expensemanager.entities.ExpenseWrapper;

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

    public static ExpenseWrapper getExpenseWrapper(int expenseType){
        ExpenseWrapper expenseWrapper = new ExpenseWrapper();
        switch (expenseType){
            case ExpenseViewType.ALL:
                expenseWrapper = getExpenseWrapperAll();
                break;
            case ExpenseViewType.MONTH:
                expenseWrapper = getExpenseWrapperMonth();
                break;
            case ExpenseViewType.WEEK:
                expenseWrapper = getExpenseWrapperWeek();
                break;
            case ExpenseViewType.TODAY:
                expenseWrapper = getExpenseWrapperToday();
                break;
        }
        return expenseWrapper;
    }

    private static ExpenseWrapper getExpenseWrapperWeek() {
        return null;
    }

    private static ExpenseWrapper getExpenseWrapperAll() {
        return null;
    }

    private static ExpenseWrapper getExpenseWrapperMonth() {
        return null;
    }

    private static ExpenseWrapper getExpenseWrapperToday() {
        return null;
    }
}
