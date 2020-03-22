package com.ramitsuri.expensemanager.data;

import com.ramitsuri.expensemanager.Constants;
import com.ramitsuri.expensemanager.entities.Budget;
import com.ramitsuri.expensemanager.entities.Category;
import com.ramitsuri.expensemanager.entities.Expense;
import com.ramitsuri.expensemanager.entities.Log;
import com.ramitsuri.expensemanager.entities.PaymentMethod;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DummyData {
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

    public static String[] getPaymentMethods() {
        return new String[] {
                "Discover",
                "Cash",
                "Chase",
                "Amazon",
                "Chase CH",
                "Master 53",
                "Disney",
                "AMEX",
                "Card1",
                "Card2",
                "Card3",
                "Card4",
                "Card5",
        };
    }

    public static List<PaymentMethod> getAllPaymentMethods() {
        List<PaymentMethod> paymentMethods = new ArrayList<>();
        for (String paymentMethodName : getPaymentMethods()) {
            PaymentMethod paymentMethod = new PaymentMethod();
            paymentMethod.setName(paymentMethodName);
            paymentMethods.add(paymentMethod);
        }
        return paymentMethods;
    }

    public static String[] getDescriptions() {
        return new String[] {
                "Car EMI",
                "internet",
                "Roti, amritsari kulcha, chhole, toor daal, Namkeen, onion, tomato",
                "Drive",
                "Apples, banana",
                "Tag renew",
                "Beer usha",
                "Pizza Jess",
                "Japanese takeout",
                "Wash",
                "Apples, oranges, lemon, cucumber, onion, tomato, watermelon",
                "Bread, eggs, cheese, yogurt",
                "Tampa AirBnb",
                "Gas"
        };
    }

    public static String[] getStores() {
        return new String[] {
                "PNC",
                "Comcast",
                "Patel brothers",
                "Google",
                "Publix",
                "FL DL & TAG GO-RENEW DMV",
                "Root down",
                "Moon River",
                "Sake house",
                "Herschel St",
                "Jacksonville Farmer's market",
                "Publix",
                "Jess",
                "BP"
        };
    }

    public static List<Expense> getExpenses() {
        List<Expense> expenses = new ArrayList<>();
        long base = 1565818852014L;
        long oneDay = 86400000;

        Expense expense = new Expense();
        expense.setAmount(BigDecimal.ZERO);
        expense.setCategory(getCategories()[0]);
        expense.setDateTime(base);
        expense.setPaymentMethod(getPaymentMethods()[0]);
        expense.setDescription(getDescriptions()[0]);
        expense.setStore(getStores()[0]);
        expense.setIsStarred(true);
        expense.setIsSynced(false);
        expense.setSheetId(1);
        expenses.add(expense);

        expense = new Expense();
        expense.setAmount(new BigDecimal("10.00"));
        expense.setCategory(getCategories()[1]);
        expense.setDateTime(base);
        expense.setPaymentMethod(getPaymentMethods()[2]);
        expense.setDescription(getDescriptions()[4]);
        expense.setStore(getStores()[5]);
        expense.setIsStarred(false);
        expense.setIsSynced(true);
        expense.setSheetId(2);
        expenses.add(expense);

        expense = new Expense();
        expense.setAmount(new BigDecimal("7.43"));
        expense.setCategory(getCategories()[4]);
        expense.setDateTime(base + oneDay);
        expense.setPaymentMethod(getPaymentMethods()[0]);
        expense.setDescription(getDescriptions()[7]);
        expense.setStore(getStores()[9]);
        expense.setIsStarred(true);
        expense.setIsSynced(false);
        expense.setSheetId(3);
        expenses.add(expense);

        expense = new Expense();
        expense.setAmount(new BigDecimal("17.56"));
        expense.setCategory(getCategories()[2]);
        expense.setDateTime(base + oneDay);
        expense.setPaymentMethod(getPaymentMethods()[8]);
        expense.setDescription(getDescriptions()[12]);
        expense.setStore(getStores()[13]);
        expense.setIsStarred(false);
        expense.setIsSynced(false);
        expense.setSheetId(1);
        expenses.add(expense);

        expense = new Expense();
        expense.setAmount(new BigDecimal("123.98"));
        expense.setCategory(getCategories()[7]);
        expense.setDateTime(base - oneDay);
        expense.setPaymentMethod(getPaymentMethods()[3]);
        expense.setDescription(getDescriptions()[10]);
        expense.setStore(getStores()[7]);
        expense.setIsStarred(false);
        expense.setIsSynced(false);
        expense.setSheetId(2);
        expenses.add(expense);

        expense = new Expense();
        expense.setAmount(new BigDecimal("45"));
        expense.setCategory(getCategories()[6]);
        expense.setDateTime(base + 3 * oneDay);
        expense.setPaymentMethod(getPaymentMethods()[4]);
        expense.setDescription(getDescriptions()[4]);
        expense.setStore(getStores()[3]);
        expense.setIsStarred(true);
        expense.setIsSynced(true);
        expense.setSheetId(3);
        expenses.add(expense);

        expense = new Expense();
        expense.setAmount(new BigDecimal("1399"));
        expense.setCategory(getCategories()[0]);
        expense.setDateTime(base - 2 * oneDay);
        expense.setPaymentMethod(getPaymentMethods()[11]);
        expense.setDescription(getDescriptions()[10]);
        expense.setStore(getStores()[10]);
        expense.setIsStarred(false);
        expense.setIsSynced(false);
        expense.setSheetId(1);
        expenses.add(expense);

        expense = new Expense();
        expense.setAmount(BigDecimal.ZERO);
        expense.setCategory(getCategories()[3]);
        expense.setDateTime(base + 3 * oneDay);
        expense.setPaymentMethod(getPaymentMethods()[11]);
        expense.setDescription(getDescriptions()[2]);
        expense.setStore(getStores()[5]);
        expense.setIsStarred(false);
        expense.setIsSynced(false);
        expense.setSheetId(1);
        expenses.add(expense);

        expense = new Expense();
        expense.setAmount(new BigDecimal("13.45"));
        expense.setCategory(getCategories()[5]);
        expense.setDateTime(base - oneDay);
        expense.setPaymentMethod(getPaymentMethods()[5]);
        expense.setDescription(getDescriptions()[3]);
        expense.setStore(getStores()[1]);
        expense.setIsStarred(false);
        expense.setIsSynced(true);
        expense.setSheetId(2);
        expenses.add(expense);

        expense = new Expense();
        expense.setAmount(new BigDecimal("15"));
        expense.setCategory(getCategories()[3]);
        expense.setDateTime(base + 2 * oneDay);
        expense.setPaymentMethod(getPaymentMethods()[3]);
        expense.setDescription(getDescriptions()[9]);
        expense.setStore(getStores()[2]);
        expense.setIsStarred(true);
        expense.setIsSynced(false);
        expense.setSheetId(3);
        expenses.add(expense);

        expense = new Expense();
        expense.setAmount(new BigDecimal("15.45"));
        expense.setCategory(getCategories()[5]);
        expense.setDateTime(base - 2 * oneDay);
        expense.setPaymentMethod(getPaymentMethods()[10]);
        expense.setDescription(getDescriptions()[10]);
        expense.setStore(getStores()[13]);
        expense.setIsStarred(false);
        expense.setIsSynced(false);
        expense.setSheetId(2);
        expenses.add(expense);

        expense = new Expense();
        expense.setAmount(new BigDecimal("1.40"));
        expense.setCategory(getCategories()[8]);
        expense.setDateTime(base - 2 * oneDay);
        expense.setPaymentMethod(getPaymentMethods()[5]);
        expense.setDescription(getDescriptions()[7]);
        expense.setStore(getStores()[10]);
        expense.setIsStarred(false);
        expense.setIsSynced(false);
        expense.setSheetId(2);
        expenses.add(expense);

        expense = new Expense();
        expense.setAmount(new BigDecimal("31.90"));
        expense.setCategory(getCategories()[2]);
        expense.setDateTime(base + 2 * oneDay);
        expense.setPaymentMethod(getPaymentMethods()[2]);
        expense.setDescription(getDescriptions()[9]);
        expense.setStore(getStores()[2]);
        expense.setIsStarred(true);
        expense.setIsSynced(true);
        expense.setSheetId(-1);
        expenses.add(expense);

        return expenses;
    }

    public static List<Expense> getAllStarred() {
        List<Expense> expenses = new ArrayList<>();
        for (Expense expense : getExpenses()) {
            if (expense.isStarred()) {
                expenses.add(expense);
            }
        }
        return expenses;
    }

    public static List<Expense> getAllUnsynced() {
        List<Expense> expenses = new ArrayList<>();
        for (Expense expense : getExpenses()) {
            if (!expense.isSynced()) {
                expenses.add(expense);
            }
        }
        return expenses;
    }

    public static List<Expense> getAllsynced() {
        List<Expense> expenses = new ArrayList<>();
        for (Expense expense : getExpenses()) {
            if (expense.isSynced()) {
                expenses.add(expense);
            }
        }
        return expenses;
    }

    public static List<Log> getLogs() {
        List<Log> logs = new ArrayList<>();

        long base = 1565818852014L;
        long oneDay = 86400000;

        Log log = new Log();
        log.setTime(base - 10 * oneDay);
        log.setType(Constants.Tag.ONE_TIME_BACKUP);
        log.setResult(Constants.LogResult.SUCCESS);
        log.setMessage(null);
        log.setIsAcknowledged(false);

        log = new Log();
        log.setTime(base - 5 * oneDay);
        log.setType(Constants.Tag.ONE_TIME_BACKUP);
        log.setResult(Constants.LogResult.SUCCESS);
        log.setMessage(null);
        log.setIsAcknowledged(true);

        log = new Log();
        log.setTime(base - 6 * oneDay);
        log.setType(Constants.Tag.SCHEDULED_BACKUP_LEGACY);
        log.setResult(Constants.LogResult.SUCCESS);
        log.setMessage(null);
        log.setIsAcknowledged(false);

        log = new Log();
        log.setTime(base + 10 * oneDay);
        log.setType(Constants.Tag.SCHEDULED_BACKUP_LEGACY);
        log.setResult(Constants.LogResult.FAILURE);
        log.setMessage(null);
        log.setIsAcknowledged(false);

        return logs;
    }

    public static List<Log> getUnacknowledgedLogs() {
        List<Log> logs = new ArrayList<>();
        for (Log log : getLogs()) {
            if (!log.isAcknowledged()) {
                logs.add(log);
            }
        }
        return logs;
    }

    public static List<Budget> getBudgets() {
        List<Budget> budgets = new ArrayList<>();

        List<String> list = new ArrayList<String>() {{
            add("Travel");
            add("100");
            add("Travel");
        }};
        Budget budget = new Budget(list);
        budgets.add(budget);

        list = new ArrayList<String>() {{
            add("Ent., Shopping");
            add("200");
            add("Entertainment");
            add("Tech");
            add("Fun");
            add("Personal");
            add("Shopping");
        }};
        budget = new Budget(list);
        budgets.add(budget);

        list = new ArrayList<String>() {{
            add("Rent & Utilities");
            add("400");
            add("Utilities");
            add("Rent");
        }};
        budget = new Budget(list);
        budgets.add(budget);

        list = new ArrayList<String>() {{
            add("Groceries, Food");
            add("400");
            add("Home");
            add("Food");
            add("Groceries");
        }};
        budget = new Budget(list);
        budgets.add(budget);

        list = new ArrayList<String>() {{
            add("Miscellaneous");
            add("200");
            add("Miscellaneous");
        }};
        budget = new Budget(list);
        budgets.add(budget);

        list = new ArrayList<String>() {{
            add("Car");
            add("100");
            add("Car");
        }};
        budget = new Budget(list);
        budgets.add(budget);

        return budgets;
    }
}
