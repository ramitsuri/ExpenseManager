package com.ramitsuri.expensemanager.entities

import android.os.Parcel
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
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
                isSynced = true,
                isStarred = true,
                sheetId = 1,
                isIncome = true,
                recordType = RecordType.MONTHLY,
                identifier = "1"
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
        assertEquals(expense.isSynced, fromParcel.isSynced)
        assertEquals(expense.sheetId, fromParcel.sheetId)
        assertEquals(expense.isIncome, fromParcel.isIncome)
        assertEquals(expense.recordType, fromParcel.recordType)
        assertEquals(expense.identifier, fromParcel.identifier)
    }
}