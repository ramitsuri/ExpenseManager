package com.ramitsuri.expensemanager.utils;

import com.ramitsuri.expensemanager.data.DummyData;
import com.ramitsuri.expensemanager.entities.Expense;
import com.ramitsuri.expensemanager.entities.ExpenseWrapper;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class TransformationHelperTest {
    @Test
    public void testToExpenseWrapperList() {
        List<Expense> input = DummyData.getExpenses();
        List<ExpenseWrapper> output = TransformationHelper.toExpenseWrapperList(input);
        assertNotNull(output);
        assertTrue(input.size() < output.size());
        assertNull(output.get(0).getExpense());
        assertNotNull(output.get(output.size() - 1).getExpense());
        System.out.println(output);
    }
}
