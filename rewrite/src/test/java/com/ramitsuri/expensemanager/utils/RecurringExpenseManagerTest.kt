package com.ramitsuri.expensemanager.utils

import com.ramitsuri.expensemanager.constants.intDefs.RecurType
import com.ramitsuri.expensemanager.entities.RecurringExpenseInfo
import org.junit.Assert
import org.junit.Test
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class RecurringExpenseManagerTest {
    @Test
    fun testCanRecur_monthly_shouldReturnTrue() {
        val currentTime = Instant.now()
        val zoneId = ZoneId.of("America/New_York")
        val zoneDatetime = ZonedDateTime.ofInstant(currentTime, zoneId)
        val recurringExpenseInfo = RecurringExpenseInfo(
                "1000",
                zoneDatetime.minusMonths(1).toInstant().toEpochMilli(),
                RecurType.MONTHLY)

        Assert.assertTrue(RecurringExpenseManager().canRecur(recurringExpenseInfo,
                currentTime.toEpochMilli(), zoneId))
    }
    @Test
    fun testCanRecur_moreThanOneMonthDifference_shouldReturnTrue() {
        val currentTime = Instant.now()
        val zoneId = ZoneId.of("America/New_York")
        val zoneDatetime = ZonedDateTime.ofInstant(currentTime, zoneId)
        val recurringExpenseInfo = RecurringExpenseInfo(
                "1000",
                zoneDatetime.minusMonths(1).minusSeconds(1).toInstant().toEpochMilli(),
                RecurType.MONTHLY)

        Assert.assertTrue(RecurringExpenseManager().canRecur(recurringExpenseInfo,
                currentTime.toEpochMilli(), zoneId))
    }
    @Test
    fun testCanRecur_lessThanOneMonthDifference_shouldReturnFalse() {
        val currentTime = Instant.now()
        val zoneId = ZoneId.of("America/New_York")
        val zoneDatetime = ZonedDateTime.ofInstant(currentTime, zoneId)
        val recurringExpenseInfo = RecurringExpenseInfo(
                "1000",
                zoneDatetime.minusMonths(1).plusSeconds(1).toInstant().toEpochMilli(),
                RecurType.MONTHLY)

        Assert.assertFalse(RecurringExpenseManager().canRecur(recurringExpenseInfo,
                currentTime.toEpochMilli(), zoneId))
    }
}