package com.ramitsuri.expensemanager.utils;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class CurrencyHelperTest {
    @Test
    public void testFormatForDisplay() {
        assertEquals("$1.34", CurrencyHelper.formatForDisplay(true, new BigDecimal("1.34"), true));
        assertEquals("$1.34", CurrencyHelper.formatForDisplay(true, new BigDecimal("1.34"), false));
        assertEquals("1.34", CurrencyHelper.formatForDisplay(false, new BigDecimal("1.34"), true));
        assertEquals("1.34", CurrencyHelper.formatForDisplay(false, new BigDecimal("1.34"), false));
        
        assertEquals("$1.30", CurrencyHelper.formatForDisplay(true, new BigDecimal("1.30"), true));
        assertEquals("$1.30", CurrencyHelper.formatForDisplay(true, new BigDecimal("1.30"), false));
        assertEquals("1.30", CurrencyHelper.formatForDisplay(false, new BigDecimal("1.30"), true));
        assertEquals("1.30", CurrencyHelper.formatForDisplay(false, new BigDecimal("1.30"), false));

        assertEquals("$1", CurrencyHelper.formatForDisplay(true, new BigDecimal("1.00"), true));
        assertEquals("$1.00", CurrencyHelper.formatForDisplay(true, new BigDecimal("1.00"), false));
        assertEquals("1", CurrencyHelper.formatForDisplay(false, new BigDecimal("1.00"), true));
        assertEquals("1.00", CurrencyHelper.formatForDisplay(false, new BigDecimal("1.00"), false));

        assertEquals("$1", CurrencyHelper.formatForDisplay(true, new BigDecimal("1"), true));
        assertEquals("$1.00", CurrencyHelper.formatForDisplay(true, new BigDecimal("1"), false));
        assertEquals("1", CurrencyHelper.formatForDisplay(false, new BigDecimal("1"), true));
        assertEquals("1.00", CurrencyHelper.formatForDisplay(false, new BigDecimal("1"), false));
    }
}
