package com.ramitsuri.expensemanagerrewrite;

import com.ramitsuri.expensemanagerrewrite.entities.Category;
import com.ramitsuri.expensemanagerrewrite.entities.Expense;
import com.ramitsuri.expensemanagerrewrite.entities.PaymentMethod;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TestUtils {
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

        Expense expense = new Expense();
        expense.setAmount(BigDecimal.ZERO);
        expense.setCategory(getCategories()[0]);
        expense.setDateTime(new Date().getTime());
        expense.setPaymentMethod(getPaymentMethods()[0]);
        expense.setDescription(getDescriptions()[0]);
        expense.setStore(getStores()[0]);
        expense.setIsStarred(true);
        expense.setIsSynced(false);
        expenses.add(expense);

        expense = new Expense();
        expense.setAmount(BigDecimal.ZERO);
        expense.setCategory(getCategories()[1]);
        expense.setDateTime(new Date().getTime());
        expense.setPaymentMethod(getPaymentMethods()[2]);
        expense.setDescription(getDescriptions()[4]);
        expense.setStore(getStores()[5]);
        expense.setIsStarred(false);
        expense.setIsSynced(true);
        expenses.add(expense);

        expense = new Expense();
        expense.setAmount(BigDecimal.ZERO);
        expense.setCategory(getCategories()[4]);
        expense.setDateTime(new Date().getTime());
        expense.setPaymentMethod(getPaymentMethods()[0]);
        expense.setDescription(getDescriptions()[7]);
        expense.setStore(getStores()[9]);
        expense.setIsStarred(true);
        expense.setIsSynced(false);
        expenses.add(expense);

        expense = new Expense();
        expense.setAmount(BigDecimal.ZERO);
        expense.setCategory(getCategories()[2]);
        expense.setDateTime(new Date().getTime());
        expense.setPaymentMethod(getPaymentMethods()[8]);
        expense.setDescription(getDescriptions()[12]);
        expense.setStore(getStores()[13]);
        expense.setIsStarred(false);
        expense.setIsSynced(false);
        expenses.add(expense);

        expense = new Expense();
        expense.setAmount(BigDecimal.ZERO);
        expense.setCategory(getCategories()[7]);
        expense.setDateTime(new Date().getTime());
        expense.setPaymentMethod(getPaymentMethods()[3]);
        expense.setDescription(getDescriptions()[10]);
        expense.setStore(getStores()[7]);
        expense.setIsStarred(false);
        expense.setIsSynced(false);
        expenses.add(expense);

        expense = new Expense();
        expense.setAmount(BigDecimal.ZERO);
        expense.setCategory(getCategories()[6]);
        expense.setDateTime(new Date().getTime());
        expense.setPaymentMethod(getPaymentMethods()[4]);
        expense.setDescription(getDescriptions()[4]);
        expense.setStore(getStores()[3]);
        expense.setIsStarred(true);
        expense.setIsSynced(true);
        expenses.add(expense);

        expense = new Expense();
        expense.setAmount(BigDecimal.ZERO);
        expense.setCategory(getCategories()[0]);
        expense.setDateTime(new Date().getTime());
        expense.setPaymentMethod(getPaymentMethods()[11]);
        expense.setDescription(getDescriptions()[10]);
        expense.setStore(getStores()[10]);
        expense.setIsStarred(false);
        expense.setIsSynced(false);
        expenses.add(expense);

        expense = new Expense();
        expense.setAmount(BigDecimal.ZERO);
        expense.setCategory(getCategories()[3]);
        expense.setDateTime(new Date().getTime());
        expense.setPaymentMethod(getPaymentMethods()[11]);
        expense.setDescription(getDescriptions()[2]);
        expense.setStore(getStores()[5]);
        expense.setIsStarred(false);
        expense.setIsSynced(false);
        expenses.add(expense);

        expense = new Expense();
        expense.setAmount(BigDecimal.ZERO);
        expense.setCategory(getCategories()[5]);
        expense.setDateTime(new Date().getTime());
        expense.setPaymentMethod(getPaymentMethods()[5]);
        expense.setDescription(getDescriptions()[3]);
        expense.setStore(getStores()[1]);
        expense.setIsStarred(false);
        expense.setIsSynced(true);
        expenses.add(expense);

        expense = new Expense();
        expense.setAmount(BigDecimal.ZERO);
        expense.setCategory(getCategories()[3]);
        expense.setDateTime(new Date().getTime());
        expense.setPaymentMethod(getPaymentMethods()[3]);
        expense.setDescription(getDescriptions()[9]);
        expense.setStore(getStores()[2]);
        expense.setIsStarred(true);
        expense.setIsSynced(false);
        expenses.add(expense);

        expense = new Expense();
        expense.setAmount(BigDecimal.ZERO);
        expense.setCategory(getCategories()[5]);
        expense.setDateTime(new Date().getTime());
        expense.setPaymentMethod(getPaymentMethods()[10]);
        expense.setDescription(getDescriptions()[10]);
        expense.setStore(getStores()[13]);
        expense.setIsStarred(false);
        expense.setIsSynced(false);
        expenses.add(expense);

        expense = new Expense();
        expense.setAmount(BigDecimal.ZERO);
        expense.setCategory(getCategories()[8]);
        expense.setDateTime(new Date().getTime());
        expense.setPaymentMethod(getPaymentMethods()[5]);
        expense.setDescription(getDescriptions()[7]);
        expense.setStore(getStores()[10]);
        expense.setIsStarred(false);
        expense.setIsSynced(false);
        expenses.add(expense);

        expense = new Expense();
        expense.setAmount(BigDecimal.ZERO);
        expense.setCategory(getCategories()[2]);
        expense.setDateTime(new Date().getTime());
        expense.setPaymentMethod(getPaymentMethods()[2]);
        expense.setDescription(getDescriptions()[9]);
        expense.setStore(getStores()[2]);
        expense.setIsStarred(true);
        expense.setIsSynced(true);
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
}
