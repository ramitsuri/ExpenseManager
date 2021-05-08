package com.ramitsuri.expensemanager.utils;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.ramitsuri.expensemanager.data.dummy.Expenses;
import com.ramitsuri.expensemanager.entities.Expense;
import com.ramitsuri.expensemanager.ui.adapter.ExpenseWrapper;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.TimeZone;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4ClassRunner.class)
public class TransformationHelperTest {

    @Test
    public void testToExpenseWrapperList() {
        List<Expense> input = Expenses.getExpenses();
        List<ExpenseWrapper> output =
                TransformationHelper.toExpenseWrapperList(input, TimeZone.getDefault());
        assertNotNull(output);
        assertTrue(input.size() < output.size());
        assertNull(output.get(0).getExpense());
        assertNotNull(output.get(output.size() - 1).getExpense());
        System.out.println(output);
    }
}