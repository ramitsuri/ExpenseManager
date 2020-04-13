package com.ramitsuri.expensemanager.data.dummy;

import com.ramitsuri.expensemanager.entities.Budget;

import java.util.ArrayList;
import java.util.List;

public class Budgets {
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
