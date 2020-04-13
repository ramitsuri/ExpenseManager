package com.ramitsuri.expensemanager.data.dummy;

import com.ramitsuri.expensemanager.entities.Expense;
import com.ramitsuri.expensemanager.utils.DateHelper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Expenses {
    public static long BASE_DATE_TIME = 1565818852014L; // Wed Aug 14 2019 17:40:52
    public static long ONE_DAY = 86400000;

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

        Expense expense = new Expense();
        expense.setAmount(BigDecimal.ZERO);
        expense.setCategory(Categories.getCategories()[0]);
        expense.setDateTime(BASE_DATE_TIME + 365 * ONE_DAY);
        expense.setPaymentMethod(PaymentMethods.getPaymentMethods()[0]);
        expense.setDescription(getDescriptions()[0]);
        expense.setStore(getStores()[0]);
        expense.setIsStarred(true);
        expense.setIsSynced(false);
        expense.setSheetId(1);
        expenses.add(expense);

        expense = new Expense();
        expense.setAmount(new BigDecimal("10.00"));
        expense.setCategory(Categories.getCategories()[1]);
        expense.setDateTime(BASE_DATE_TIME + 325 * ONE_DAY);
        expense.setPaymentMethod(PaymentMethods.getPaymentMethods()[2]);
        expense.setDescription(getDescriptions()[4]);
        expense.setStore(getStores()[5]);
        expense.setIsStarred(false);
        expense.setIsSynced(true);
        expense.setSheetId(2);
        expenses.add(expense);

        expense = new Expense();
        expense.setAmount(new BigDecimal("7.43"));
        expense.setCategory(Categories.getCategories()[4]);
        expense.setDateTime(BASE_DATE_TIME + 340 * ONE_DAY);
        expense.setPaymentMethod(PaymentMethods.getPaymentMethods()[0]);
        expense.setDescription(getDescriptions()[7]);
        expense.setStore(getStores()[9]);
        expense.setIsStarred(true);
        expense.setIsSynced(false);
        expense.setSheetId(3);
        expenses.add(expense);

        expense = new Expense();
        expense.setAmount(new BigDecimal("17.56"));
        expense.setCategory(Categories.getCategories()[2]);
        expense.setDateTime(BASE_DATE_TIME + 200 * ONE_DAY);
        expense.setPaymentMethod(PaymentMethods.getPaymentMethods()[8]);
        expense.setDescription(getDescriptions()[12]);
        expense.setStore(getStores()[13]);
        expense.setIsStarred(false);
        expense.setIsSynced(false);
        expense.setSheetId(1);
        expenses.add(expense);

        expense = new Expense();
        expense.setAmount(new BigDecimal("123.98"));
        expense.setCategory(Categories.getCategories()[7]);
        expense.setDateTime(BASE_DATE_TIME + 150 * ONE_DAY);
        expense.setPaymentMethod(PaymentMethods.getPaymentMethods()[3]);
        expense.setDescription(getDescriptions()[10]);
        expense.setStore(getStores()[7]);
        expense.setIsStarred(false);
        expense.setIsSynced(false);
        expense.setSheetId(2);
        expenses.add(expense);

        expense = new Expense();
        expense.setAmount(new BigDecimal("45"));
        expense.setCategory(Categories.getCategories()[6]);
        expense.setDateTime(BASE_DATE_TIME + 300 * ONE_DAY);
        expense.setPaymentMethod(PaymentMethods.getPaymentMethods()[4]);
        expense.setDescription(getDescriptions()[4]);
        expense.setStore(getStores()[3]);
        expense.setIsStarred(true);
        expense.setIsSynced(true);
        expense.setSheetId(3);
        expenses.add(expense);

        expense = new Expense();
        expense.setAmount(new BigDecimal("1399"));
        expense.setCategory(Categories.getCategories()[0]);
        expense.setDateTime(BASE_DATE_TIME + 223 * ONE_DAY);
        expense.setPaymentMethod(PaymentMethods.getPaymentMethods()[11]);
        expense.setDescription(getDescriptions()[10]);
        expense.setStore(getStores()[10]);
        expense.setIsStarred(false);
        expense.setIsSynced(false);
        expense.setSheetId(1);
        expenses.add(expense);

        expense = new Expense();
        expense.setAmount(BigDecimal.ZERO);
        expense.setCategory(Categories.getCategories()[3]);
        expense.setDateTime(BASE_DATE_TIME + 311 * ONE_DAY);
        expense.setPaymentMethod(PaymentMethods.getPaymentMethods()[11]);
        expense.setDescription(getDescriptions()[2]);
        expense.setStore(getStores()[5]);
        expense.setIsStarred(false);
        expense.setIsSynced(false);
        expense.setSheetId(1);
        expenses.add(expense);

        expense = new Expense();
        expense.setAmount(new BigDecimal("13.45"));
        expense.setCategory(Categories.getCategories()[5]);
        expense.setDateTime(BASE_DATE_TIME + 213 * ONE_DAY);
        expense.setPaymentMethod(PaymentMethods.getPaymentMethods()[5]);
        expense.setDescription(getDescriptions()[3]);
        expense.setStore(getStores()[1]);
        expense.setIsStarred(false);
        expense.setIsSynced(true);
        expense.setSheetId(2);
        expenses.add(expense);

        expense = new Expense();
        expense.setAmount(new BigDecimal("15"));
        expense.setCategory(Categories.getCategories()[3]);
        expense.setDateTime(BASE_DATE_TIME + 198 * ONE_DAY);
        expense.setPaymentMethod(PaymentMethods.getPaymentMethods()[3]);
        expense.setDescription(getDescriptions()[9]);
        expense.setStore(getStores()[2]);
        expense.setIsStarred(true);
        expense.setIsSynced(false);
        expense.setSheetId(3);
        expenses.add(expense);

        expense = new Expense();
        expense.setAmount(new BigDecimal("15.45"));
        expense.setCategory(Categories.getCategories()[5]);
        expense.setDateTime(BASE_DATE_TIME + 199 * ONE_DAY);
        expense.setPaymentMethod(PaymentMethods.getPaymentMethods()[10]);
        expense.setDescription(getDescriptions()[10]);
        expense.setStore(getStores()[13]);
        expense.setIsStarred(false);
        expense.setIsSynced(false);
        expense.setSheetId(2);
        expenses.add(expense);

        expense = new Expense();
        expense.setAmount(new BigDecimal("1.40"));
        expense.setCategory(Categories.getCategories()[8]);
        expense.setDateTime(BASE_DATE_TIME + 450 * ONE_DAY);
        expense.setPaymentMethod(PaymentMethods.getPaymentMethods()[5]);
        expense.setDescription(getDescriptions()[7]);
        expense.setStore(getStores()[10]);
        expense.setIsStarred(false);
        expense.setIsSynced(false);
        expense.setSheetId(2);
        expenses.add(expense);

        expense = new Expense();
        expense.setAmount(new BigDecimal("1.40"));
        expense.setCategory(Categories.getCategories()[8]);
        expense.setDateTime(BASE_DATE_TIME + 410 * ONE_DAY);
        expense.setPaymentMethod(PaymentMethods.getPaymentMethods()[5]);
        expense.setDescription(getDescriptions()[7]);
        expense.setStore(getStores()[10]);
        expense.setIsStarred(false);
        expense.setIsSynced(true);
        expense.setSheetId(1);
        expenses.add(expense);

        expense = new Expense();
        expense.setAmount(new BigDecimal("31.90"));
        expense.setCategory(Categories.getCategories()[2]);
        expense.setDateTime(BASE_DATE_TIME + 178 * ONE_DAY);
        expense.setPaymentMethod(PaymentMethods.getPaymentMethods()[2]);
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

    public static List<Expense> getAllForBackup(List<Integer> monthIndices) {
        List<Expense> expenses = new ArrayList<>();
        for (Expense expense : getExpenses()) {
            if (!expense.isSynced() ||
                    monthIndices
                            .contains(DateHelper.getMonthIndexFromDate(expense.getDateTime()))) {
                // Expense is not synced or expense's month is contained in supplied month indices
                expenses.add(expense);
            }
        }
        return expenses;
    }

    public static List<Expense> getAllForDateRange(long fromDateTime, long toDateTime) {
        List<Expense> expenses = new ArrayList<>();
        for (Expense expense : getExpenses()) {
            if (expense.getDateTime() <= toDateTime && expense.getDateTime() >= fromDateTime) {
                expenses.add(expense);
            }
        }
        return expenses;
    }
}
