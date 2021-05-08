package com.ramitsuri.expensemanager.entities

import android.os.Parcel
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.ramitsuri.expensemanager.constants.intDefs.AddType
import com.ramitsuri.expensemanager.constants.intDefs.RecordType
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.math.BigDecimal

@RunWith(AndroidJUnit4ClassRunner::class)
class ExpenseTest {
    @Test
    fun testParcelable() {
        val expense = Expense(
                dateTime = 1L,
                amount = BigDecimal.TEN,
                paymentMethod = "Visa",
                category = "Shopping",
                description = "Grocery",
                store = "Publix",
                isStarred = true,
                recordType = RecordType.MONTHLY,
                identifier = "1",
                addType = AddType.MANUAL
        )

        val parcel = Parcel.obtain()
        expense.writeToParcel(parcel, 0)
        parcel.setDataPosition(0)

        val fromParcel = Expense.CREATOR.createFromParcel(parcel)
        assertEquals(expense.dateTime, fromParcel.dateTime)
        assertEquals(expense.amount.compareTo(fromParcel.amount), 0)
        assertEquals(expense.paymentMethod, fromParcel.paymentMethod)
        assertEquals(expense.category, fromParcel.category)
        assertEquals(expense.description, fromParcel.description)
        assertEquals(expense.store, fromParcel.store)
        assertEquals(expense.isStarred, fromParcel.isStarred)
        assertEquals(expense.recordType, fromParcel.recordType)
        assertEquals(expense.identifier, fromParcel.identifier)
        assertEquals(expense.addType, fromParcel.addType)
    }
}