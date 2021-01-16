package com.ramitsuri.expensemanager.utils;

import com.ramitsuri.expensemanager.data.dummy.Categories;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ObjectHelperTest {
    @Test
    public void testContains() {
        assertTrue(ObjectHelper.contains(
                Arrays.asList(Categories.getCategories()), Categories.getCategories()[0]));

        assertTrue(ObjectHelper.contains(
                Arrays.asList(Categories.getCategories()),
                Categories.getCategories()[0].toLowerCase()));

        assertFalse(ObjectHelper.contains(
                Arrays.asList(Categories.getCategories()),
                ""));

        assertFalse(ObjectHelper.contains(
                Arrays.asList(Categories.getCategories()),
                "haram"));
    }
}
