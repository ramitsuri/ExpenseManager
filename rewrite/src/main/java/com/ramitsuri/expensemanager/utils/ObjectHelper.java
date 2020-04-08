package com.ramitsuri.expensemanager.utils;

import java.util.List;

import javax.annotation.Nonnull;

public class ObjectHelper {
    public static boolean contains(@Nonnull List<String> values, @Nonnull String value) {
        for (String string : values) {
            if (string.equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }
}

