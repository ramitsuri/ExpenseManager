package com.ramitsuri.expensemanager.data.dummy;

import com.ramitsuri.expensemanager.entities.Expense;
import com.ramitsuri.expensemanager.entities.Filter;
import com.ramitsuri.expensemanager.utils.DateHelper;
import com.ramitsuri.expensemanager.utils.ObjectHelper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Expenses {
    public static long BASE_DATE_TIME = 1565818852014L; // Wed Aug 14 2019 17:40:52
    public static long ONE_DAY = 86400000;

    public static String[] descriptions() {
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

    public static String[] stores() {
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

    public static List<Expense> all() {
        List<Expense> expenses = new ArrayList<>();

        Expense expense = new Expense();
        expense.setAmount(BigDecimal.ZERO);
        expense.setCategory(Categories.getCategories()[0]);
        expense.setDateTime(BASE_DATE_TIME + 365 * ONE_DAY);
        expense.setPaymentMethod(PaymentMethods.getPaymentMethods()[0]);
        expense.setDescription(descriptions()[0]);
        expense.setStore(stores()[0]);
        expense.setIsStarred(true);
        expense.setIsSynced(false);
        expense.setSheetId(1);
        expenses.add(expense);

        expense = new Expense();
        expense.setAmount(new BigDecimal("10.00"));
        expense.setCategory(Categories.getCategories()[1]);
        expense.setDateTime(BASE_DATE_TIME + 325 * ONE_DAY);
        expense.setPaymentMethod(PaymentMethods.getPaymentMethods()[2]);
        expense.setDescription(descriptions()[4]);
        expense.setStore(stores()[5]);
        expense.setIsStarred(false);
        expense.setIsSynced(true);
        expense.setSheetId(2);
        expenses.add(expense);

        expense = new Expense();
        expense.setAmount(new BigDecimal("7.43"));
        expense.setCategory(Categories.getCategories()[4]);
        expense.setDateTime(BASE_DATE_TIME + 340 * ONE_DAY);
        expense.setPaymentMethod(PaymentMethods.getPaymentMethods()[0]);
        expense.setDescription(descriptions()[7]);
        expense.setStore(stores()[9]);
        expense.setIsStarred(true);
        expense.setIsSynced(false);
        expense.setSheetId(3);
        expenses.add(expense);

        expense = new Expense();
        expense.setAmount(new BigDecimal("17.56"));
        expense.setCategory(Categories.getCategories()[2]);
        expense.setDateTime(BASE_DATE_TIME + 200 * ONE_DAY);
        expense.setPaymentMethod(PaymentMethods.getPaymentMethods()[8]);
        expense.setDescription(descriptions()[12]);
        expense.setStore(stores()[13]);
        expense.setIsStarred(false);
        expense.setIsSynced(false);
        expense.setSheetId(1);
        expenses.add(expense);

        expense = new Expense();
        expense.setAmount(new BigDecimal("123.98"));
        expense.setCategory(Categories.getCategories()[7]);
        expense.setDateTime(BASE_DATE_TIME + 150 * ONE_DAY);
        expense.setPaymentMethod(PaymentMethods.getPaymentMethods()[3]);
        expense.setDescription(descriptions()[10]);
        expense.setStore(stores()[7]);
        expense.setIsStarred(false);
        expense.setIsSynced(false);
        expense.setSheetId(2);
        expenses.add(expense);

        expense = new Expense();
        expense.setAmount(new BigDecimal("45"));
        expense.setCategory(Categories.getCategories()[6]);
        expense.setDateTime(BASE_DATE_TIME + 300 * ONE_DAY);
        expense.setPaymentMethod(PaymentMethods.getPaymentMethods()[4]);
        expense.setDescription(descriptions()[4]);
        expense.setStore(stores()[3]);
        expense.setIsStarred(true);
        expense.setIsSynced(true);
        expense.setSheetId(3);
        expenses.add(expense);

        expense = new Expense();
        expense.setAmount(new BigDecimal("1399"));
        expense.setCategory(Categories.getCategories()[0]);
        expense.setDateTime(BASE_DATE_TIME + 223 * ONE_DAY);
        expense.setPaymentMethod(PaymentMethods.getPaymentMethods()[11]);
        expense.setDescription(descriptions()[10]);
        expense.setStore(stores()[10]);
        expense.setIsStarred(false);
        expense.setIsSynced(false);
        expense.setSheetId(1);
        expenses.add(expense);

        expense = new Expense();
        expense.setAmount(BigDecimal.ZERO);
        expense.setCategory(Categories.getCategories()[3]);
        expense.setDateTime(BASE_DATE_TIME + 311 * ONE_DAY);
        expense.setPaymentMethod(PaymentMethods.getPaymentMethods()[11]);
        expense.setDescription(descriptions()[2]);
        expense.setStore(stores()[5]);
        expense.setIsStarred(false);
        expense.setIsSynced(false);
        expense.setSheetId(1);
        expenses.add(expense);

        expense = new Expense();
        expense.setAmount(new BigDecimal("13.45"));
        expense.setCategory(Categories.getCategories()[5]);
        expense.setDateTime(BASE_DATE_TIME + 213 * ONE_DAY);
        expense.setPaymentMethod(PaymentMethods.getPaymentMethods()[5]);
        expense.setDescription(descriptions()[3]);
        expense.setStore(stores()[1]);
        expense.setIsStarred(false);
        expense.setIsSynced(true);
        expense.setSheetId(2);
        expenses.add(expense);

        expense = new Expense();
        expense.setAmount(new BigDecimal("15"));
        expense.setCategory(Categories.getCategories()[3]);
        expense.setDateTime(BASE_DATE_TIME + 198 * ONE_DAY);
        expense.setPaymentMethod(PaymentMethods.getPaymentMethods()[3]);
        expense.setDescription(descriptions()[9]);
        expense.setStore(stores()[2]);
        expense.setIsStarred(true);
        expense.setIsSynced(false);
        expense.setSheetId(3);
        expenses.add(expense);

        expense = new Expense();
        expense.setAmount(new BigDecimal("15.45"));
        expense.setCategory(Categories.getCategories()[5]);
        expense.setDateTime(BASE_DATE_TIME + 199 * ONE_DAY);
        expense.setPaymentMethod(PaymentMethods.getPaymentMethods()[10]);
        expense.setDescription(descriptions()[10]);
        expense.setStore(stores()[13]);
        expense.setIsStarred(false);
        expense.setIsSynced(false);
        expense.setSheetId(2);
        expenses.add(expense);

        expense = new Expense();
        expense.setAmount(new BigDecimal("1.40"));
        expense.setCategory(Categories.getCategories()[8]);
        expense.setDateTime(BASE_DATE_TIME + 450 * ONE_DAY);
        expense.setPaymentMethod(PaymentMethods.getPaymentMethods()[7]);
        expense.setDescription(descriptions()[7]);
        expense.setStore(stores()[4]);
        expense.setIsStarred(false);
        expense.setIsSynced(false);
        expense.setSheetId(2);
        expenses.add(expense);

        expense = new Expense();
        expense.setAmount(new BigDecimal("1.40"));
        expense.setCategory(Categories.getCategories()[2]);
        expense.setDateTime(BASE_DATE_TIME + 410 * ONE_DAY);
        expense.setPaymentMethod(PaymentMethods.getPaymentMethods()[5]);
        expense.setDescription(descriptions()[7]);
        expense.setStore(stores()[4]);
        expense.setIsStarred(false);
        expense.setIsSynced(true);
        expense.setSheetId(1);
        expenses.add(expense);

        expense = new Expense();
        expense.setAmount(new BigDecimal("31.90"));
        expense.setCategory(Categories.getCategories()[2]);
        expense.setDateTime(BASE_DATE_TIME + 178 * ONE_DAY);
        expense.setPaymentMethod(PaymentMethods.getPaymentMethods()[2]);
        expense.setDescription(descriptions()[9]);
        expense.setStore(stores()[2]);
        expense.setIsStarred(true);
        expense.setIsSynced(true);
        expense.setSheetId(-1);
        expenses.add(expense);

        expense = new Expense();
        expense.setAmount(new BigDecimal("2000"));
        expense.setCategory(Categories.getCategories()[2]);
        expense.setDateTime(BASE_DATE_TIME + 365 * ONE_DAY);
        expense.setPaymentMethod(PaymentMethods.getPaymentMethods()[2]);
        expense.setDescription(descriptions()[9]);
        expense.setStore(stores()[2]);
        expense.setIsStarred(true);
        expense.setIsSynced(true);
        expense.setSheetId(-1);
        expense.setIsIncome(true);
        expenses.add(expense);

        expense = new Expense();
        expense.setAmount(new BigDecimal("31.90"));
        expense.setCategory(Categories.getCategories()[2]);
        expense.setDateTime(BASE_DATE_TIME + 335 * ONE_DAY);
        expense.setPaymentMethod(PaymentMethods.getPaymentMethods()[2]);
        expense.setDescription(descriptions()[9]);
        expense.setStore(stores()[2]);
        expense.setIsStarred(true);
        expense.setIsSynced(true);
        expense.setSheetId(-1);
        expense.setIsIncome(true);
        expenses.add(expense);

        expense = new Expense();
        expense.setAmount(new BigDecimal("31.90"));
        expense.setCategory(Categories.getCategories()[2]);
        expense.setDateTime(BASE_DATE_TIME + 305 * ONE_DAY);
        expense.setPaymentMethod(PaymentMethods.getPaymentMethods()[2]);
        expense.setDescription(descriptions()[9]);
        expense.setStore(stores()[2]);
        expense.setIsStarred(true);
        expense.setIsSynced(true);
        expense.setSheetId(-1);
        expense.setIsIncome(true);
        expenses.add(expense);

        expense = new Expense();
        expense.setAmount(new BigDecimal("1500"));
        expense.setCategory(Categories.getCategories()[2]);
        expense.setDateTime(BASE_DATE_TIME + 275 * ONE_DAY);
        expense.setPaymentMethod(PaymentMethods.getPaymentMethods()[2]);
        expense.setDescription(descriptions()[9]);
        expense.setStore(stores()[2]);
        expense.setIsStarred(true);
        expense.setIsSynced(true);
        expense.setSheetId(-1);
        expense.setIsIncome(true);
        expenses.add(expense);

        return expenses;
    }

    public static List<Expense> getExpenses() {
        List<Expense> expenses = new ArrayList<>();
        for (Expense expense : all()) {
            if (!expense.isIncome()) {
                expenses.add(expense);
            }
        }
        return expenses;
    }

    public static List<Expense> getAllStarred() {
        List<Expense> expenses = new ArrayList<>();
        for (Expense expense : all()) {
            if (expense.isStarred()) {
                expenses.add(expense);
            }
        }
        return expenses;
    }

    public static List<Expense> getAllUnsynced() {
        List<Expense> expenses = new ArrayList<>();
        for (Expense expense : all()) {
            if (!expense.isSynced()) {
                expenses.add(expense);
            }
        }
        return expenses;
    }

    public static List<Expense> getAllsynced() {
        List<Expense> expenses = new ArrayList<>();
        for (Expense expense : all()) {
            if (expense.isSynced()) {
                expenses.add(expense);
            }
        }
        return expenses;
    }

    public static List<Expense> getAllForBackup(List<Integer> monthIndices) {
        List<Expense> expenses = new ArrayList<>();
        for (Expense expense : all()) {
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
        for (Expense expense : all()) {
            if (expense.getDateTime() <= toDateTime && expense.getDateTime() >= fromDateTime) {
                expenses.add(expense);
            }
        }
        return expenses;
    }

    public static List<Expense> getIncomes() {
        List<Expense> expenses = new ArrayList<>();
        for (Expense expense : all()) {
            if (expense.isIncome()) {
                expenses.add(expense);
            }
        }
        return expenses;
    }

    public static List<Expense> getForFilter(Filter filter) {
        // Income
        List<Expense> afterIncomeFilter = new ArrayList<>();
        for (Expense expense : all()) {
            if (filter.getIsIncome() != null) {
                if (filter.getIsIncome() && expense.isIncome()) {
                    afterIncomeFilter.add(expense);
                } else if (!filter.getIsIncome() && !expense.isIncome()) {
                    afterIncomeFilter.add(expense);
                }
            } else {
                afterIncomeFilter.add(expense);
            }
        }
        // Date range
        List<Expense> afterDateFilter = new ArrayList<>();
        for (Expense expense : afterIncomeFilter) {
            if (filter.getFromDateTime() != null && filter.getToDateTime() != null) {
                if (expense.getDateTime() <= filter.getToDateTime() &&
                        expense.getDateTime() >= filter.getFromDateTime()) {
                    afterDateFilter.add(expense);
                }
            } else {
                afterDateFilter.add(expense);
            }
        }
        // Categories
        List<Expense> afterCategoriesFilter = new ArrayList<>();
        for (Expense expense : afterDateFilter) {
            if (filter.getCategories() != null) {
                if (ObjectHelper.contains(filter.getCategories(), expense.getCategory())) {
                    afterCategoriesFilter.add(expense);
                }
            } else {
                afterCategoriesFilter.add(expense);
            }
        }
        // Payment Methods
        List<Expense> afterPaymentsFilter = new ArrayList<>();
        for (Expense expense : afterCategoriesFilter) {
            if (filter.getPaymentMethods() != null) {
                if (ObjectHelper.contains(filter.getPaymentMethods(), expense.getPaymentMethod())) {
                    afterPaymentsFilter.add(expense);
                }
            } else {
                afterPaymentsFilter.add(expense);
            }
        }
        // Synced
        List<Expense> afterSyncedFilter = new ArrayList<>();
        for (Expense expense : afterPaymentsFilter) {
            if (filter.getIsSynced() != null) {
                if (filter.getIsSynced() && expense.isSynced()) {
                    afterSyncedFilter.add(expense);
                } else if (!filter.getIsSynced() && !expense.isSynced()) {
                    afterSyncedFilter.add(expense);
                }
            } else {
                afterSyncedFilter.add(expense);
            }
        }
        // Starred
        List<Expense> afterStarredFilter = new ArrayList<>();
        for (Expense expense : afterSyncedFilter) {
            if (filter.getIsStarred() != null) {
                if (filter.getIsStarred() && expense.isStarred()) {
                    afterStarredFilter.add(expense);
                } else if (!filter.getIsStarred() && !expense.isStarred()) {
                    afterStarredFilter.add(expense);
                }
            } else {
                afterStarredFilter.add(expense);
            }
        }

        return afterStarredFilter;
    }

    public static Set<String> getStores(String startsWith) {
        Set<String> stores = new HashSet<>();
        for (Expense expense : all()) {
            if (expense.getStore().toLowerCase().startsWith(startsWith.toLowerCase())) {
                stores.add(expense.getStore());
            }
        }
        return stores;
    }

    public static Expense getForStore(String store) {
        List<Expense> expenses = all();
        Collections.sort(expenses, new Comparator<Expense>() {
            @Override
            public int compare(Expense o1, Expense o2) {
                if (o1.getDateTime() < o2.getDateTime()) {
                    return 1;
                } else if (o1.getDateTime() > o2.getDateTime()) {
                    return -1;
                }
                return 0;
            }
        });
        for (Expense expense : expenses) {
            if (expense.getStore().equalsIgnoreCase(store)) {
                return expense;
            }
        }
        return null;
    }
}
