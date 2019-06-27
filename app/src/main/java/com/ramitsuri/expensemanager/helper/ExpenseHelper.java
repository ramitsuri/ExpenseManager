package com.ramitsuri.expensemanager.helper;

import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.constants.ExpenseViewType;
import com.ramitsuri.expensemanager.db.ExpenseDB;
import com.ramitsuri.expensemanager.entities.Expense;
import com.ramitsuri.expensemanager.entities.ExpenseWrapper;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ExpenseHelper {

    private static ExpenseDB getDB() {
        return new ExpenseDB(MainApplication.getInstance());
    }

    public static Expense getExpense(String rowID) {
        return getDB().getExpense(rowID);
    }

    public static List<Expense> getExpenses() {
        return getDB().getAllExpense(null, null);
    }

    public static List<Expense> getExpensesRequiringBackup() {
        return getDB().getAllExpensesRequiringBackup();
    }

    public static boolean updateSyncStatusAfterBackup(List<Expense> expenses) {
        return getDB().updateExpensesSyncStatus(expenses);
    }

    public static boolean addExpense(Expense expense) {
        return getDB().setExpense(expense);
    }

    public static boolean editDateTime(String id, long dateTime) {
        return getDB().editExpenseDateTime(id, dateTime);
    }

    public static boolean editAmount(String id, BigDecimal amount) {
        return getDB().editExpenseAmount(id, amount);
    }

    public static boolean editStore(String id, String store) {
        return getDB().editExpenseStore(id, store);
    }

    public static boolean editDescription(String id, String description) {
        return getDB().editExpenseDescription(id, description);
    }

    public static boolean editSyncStatus(String id, boolean syncStatus) {
        return getDB().editExpenseSyncStatus(id, syncStatus);
    }

    public static boolean editFlagged(String id, boolean flag) {
        return getDB().editExpenseFlag(id, flag);
    }

    public static boolean editCategory(String id, int categoryId) {
        return getDB().editExpenseCategoryId(id, categoryId);
    }

    public static boolean editPaymentMethodId(String id, int paymentMethodId) {
        return getDB().editExpensePaymentMethodId(id, paymentMethodId);
    }

    public static boolean deleteExpense(String id) {
        return getDB().deleteExpense(id);
    }

    public static void deleteAll() {
        getDB().deleteAllExpense();
    }

    public static void deleteBackedUpExpenses() {
        getDB().deleteBackedUpExpense();
    }

    public static ExpenseWrapper getExpenseWrapper(int expenseType) {
        ExpenseWrapper expenseWrapper = new ExpenseWrapper();
        switch (expenseType) {
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
        ExpenseWrapper expenseWrapper = new ExpenseWrapper();
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        long startDate = DateHelper.getLongDateForDB(DateHelper.getFirstDayOfWeek(date));
        long endDate = DateHelper.getLongDateForDB(DateHelper.getLastDayOfWeek(date));
        expenseWrapper.setExpenses(getDB().getAllExpenseInDateRange(startDate, endDate));

        /*Expense topExpense = getDB().getTopExpenseInPeriod(startDate, endDate);
        expenseWrapper.setTopExpense(topExpense.getDescription() + ", " +
                AppHelper.getCurrency().split("-")[1]+ topExpense.getAmount());*/
        expenseWrapper.setTotal(String.valueOf(getTotal(expenseWrapper.getExpenses())));

        expenseWrapper.setDate(DateHelper.getPrettyDate(startDate, endDate));
        return expenseWrapper;
    }

    private static ExpenseWrapper getExpenseWrapperAll() {
        ExpenseWrapper expenseWrapper = new ExpenseWrapper();
        expenseWrapper.setExpenses(getDB().getAllExpense(null, null));

        expenseWrapper.setTotal(String.valueOf(getTotal(expenseWrapper.getExpenses())));

        expenseWrapper.setDate("All Expenses");
        return expenseWrapper;
    }

    private static ExpenseWrapper getExpenseWrapperMonth() {
        ExpenseWrapper expenseWrapper = new ExpenseWrapper();

        //expenses
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        long startDate = DateHelper.getLongDateForDB(year, month,
                calendar.getActualMinimum(calendar.DAY_OF_MONTH));
        long endDate = DateHelper.getLongDateForDB(year, month,
                calendar.getActualMaximum(calendar.DAY_OF_MONTH));
        expenseWrapper.setExpenses(getDB().getAllExpenseInDateRange(startDate, endDate));

        /*Expense topExpense = getDB().getTopExpenseInPeriod(startDate, endDate);
        expenseWrapper.setTopExpense(topExpense.getDescription() + ", " +
                AppHelper.getCurrency().split("-")[1]+ topExpense.getAmount());*/
        expenseWrapper.setTotal(String.valueOf(getTotal(expenseWrapper.getExpenses())));

        expenseWrapper.setDate(DateHelper.getPrettyMonthDate(year, month, calendar.DAY_OF_MONTH));
        return expenseWrapper;
    }

    private static ExpenseWrapper getExpenseWrapperToday() {
        ExpenseWrapper expenseWrapper = new ExpenseWrapper();

        //expenses
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        long date = DateHelper.getLongDateForDB(year, month, day);
        expenseWrapper.setExpenses(getDB().getAllExpenseForDay(date));

        //Expense topExpense = getDB().getTopExpenseOnDay(date);
        //expenseWrapper.setTopExpense(topExpense.getDescription() + ", " +
        //AppHelper.getCurrency().split("-")[1]+ topExpense.getAmount());
        expenseWrapper.setTotal(String.valueOf(getTotal(expenseWrapper.getExpenses())));

        expenseWrapper.setDate(DateHelper.getPrettyDate(year, month, day));

        return expenseWrapper;
    }

    private static String getTotal(List<Expense> expenses) {
        BigDecimal total = new BigDecimal("0");
        for (Expense expense : expenses) {
            total = total.add(expense.getAmount());
        }
        return String.valueOf(total);
    }
}
