package com.ramitsuri.expensemanagerrewrite.utils;

import com.ramitsuri.expensemanagerrewrite.data.DummyData;
import com.ramitsuri.expensemanagerrewrite.entities.Expense;
import com.ramitsuri.expensemanagerrewrite.entities.ExpenseWrapper;

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
