package com.ramitsuri.expensemanager.entities;

import android.os.Parcel;

import com.ramitsuri.expensemanager.constants.intDefs.RecordType;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.util.Arrays;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4ClassRunner.class)
public class BudgetParcelableTest {

    @Test
    public void testParcelable() {
        Budget budget = new Budget();
        budget.setId(0);
        budget.setRecordType(RecordType.MONTHLY);
        budget.setName("Test");
        budget.setAmount(BigDecimal.TEN);
        budget.setCategories(Arrays.asList("Utilities", "Rent", "Home"));

        Parcel parcel = Parcel.obtain();

        budget.writeToParcel(parcel, 0);

        parcel.setDataPosition(0);

        // Reconstruct object from parcel and asserts:
        Budget createdFromParcel = Budget.CREATOR.createFromParcel(parcel);
        assertEquals(budget.getId(), createdFromParcel.getId());
        assertEquals(budget.getName(), createdFromParcel.getName());
        assertEquals(budget.getAmount(), createdFromParcel.getAmount());
        assertEquals(budget.getCategories().size(), createdFromParcel.getCategories().size());
        assertEquals(budget.getRecordType(), createdFromParcel.getRecordType());
    }
}
