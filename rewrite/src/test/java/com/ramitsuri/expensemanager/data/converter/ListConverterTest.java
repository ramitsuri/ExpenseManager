package com.ramitsuri.expensemanager.data.converter;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ListConverterTest {
    @Test
    public void testFromString() {
        List<String> list = new ArrayList<>(Arrays.asList("Utilities", "Rent", "Home"));
        String string = "[\"Utilities\",\"Rent\",\"Home\"]";
        System.out.println(string);
        assertEquals(list.size(), ListConverter.fromString(string).size());
    }

    @Test
    public void testToString() {
        String string = "[\"Utilities\",\"Rent\",\"Home\"]";
        List<String> list = new ArrayList<>(Arrays.asList("Utilities", "Rent", "Home"));
        assertEquals(string, ListConverter.toString(list));
    }
}
