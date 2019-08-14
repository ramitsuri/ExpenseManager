package com.ramitsuri.expensemanagerrewrite.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class DateHelperTest {
    @Test
    public void testFriendlyDate() {
        Assert.assertEquals("Aug 14", DateHelper.getFriendlyDate(1565818852014L));
        Assert.assertEquals("Aug 14", DateHelper.getFriendlyDate(1565829852014L));
    }
}
