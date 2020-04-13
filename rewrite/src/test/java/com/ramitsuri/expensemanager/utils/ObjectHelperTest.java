package com.ramitsuri.expensemanager.utils;

import com.ramitsuri.expensemanager.data.dummy.Categories;
import com.ramitsuri.expensemanager.data.dummy.SheetInfos;
import com.ramitsuri.expensemanager.entities.SheetInfo;
import com.ramitsuri.sheetscore.consumerResponse.SheetMetadata;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

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

    @Test
    public void testIsSheetInfosValid() {
        List<String> months = SheetInfos.getMonths();
        List<SheetInfo> sheetInfos = SheetInfos.getSheetInfos();

        assertTrue(ObjectHelper.isSheetInfosValid(sheetInfos, months));

        sheetInfos.add(new SheetInfo(new SheetMetadata(23323, "Template")));
        assertTrue(ObjectHelper.isSheetInfosValid(sheetInfos, months));

        sheetInfos.remove(0);
        sheetInfos.remove(0);
        assertFalse(ObjectHelper.isSheetInfosValid(sheetInfos, months));
    }
}
