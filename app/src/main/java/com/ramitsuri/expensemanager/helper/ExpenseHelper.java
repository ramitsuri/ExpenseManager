package com.ramitsuri.expensemanager.helper;

import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.constants.ExpenseViewType;
import com.ramitsuri.expensemanager.db.ExpenseDB;
import com.ramitsuri.expensemanager.entities.Expense;
import com.ramitsuri.expensemanager.entities.ExpenseWrapper;

import java.math.BigDecimal;
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

    public static boolean addExpense(Expense expense){
        return getDB().setExpense(expense);
    }

    public static boolean editDateTime(String id, long dateTime){
        return getDB().editExpenseDateTime(id, dateTime);
    }

    public static boolean editAmount(String id, BigDecimal amount){
        return getDB().editExpenseAmount(id, amount);
    }

    public static boolean editStore(String id, String store){
        return getDB().editExpenseStore(id, store);
    }

    public static boolean editDescription(String id, String description){
        return getDB().editExpenseDescription(id, description);
    }

    public static boolean editSyncStatus(String id, boolean syncStatus){
        return getDB().editExpenseSyncStatus(id, syncStatus);
    }

    public static boolean editFlagged(String id, boolean flag){
        return getDB().editExpenseFlag(id, flag);
    }

    public static boolean editCategory(String id, int categoryId){
        return getDB().editExpenseCategoryId(id, categoryId);
    }

    public static boolean editPaymentMethodId(String id, int paymentMethodId){
        return getDB().editExpensePaymentMethodId(id, paymentMethodId);
    }

    public static boolean deleteExpense(String id){
        return getDB().deleteExpense(id);
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
