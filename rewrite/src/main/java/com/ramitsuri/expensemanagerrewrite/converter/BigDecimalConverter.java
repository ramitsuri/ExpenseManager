package com.ramitsuri.expensemanagerrewrite.converter;

import android.text.TextUtils;

import java.math.BigDecimal;

import androidx.room.TypeConverter;

public class BigDecimalConverter {
    @TypeConverter
    public static BigDecimal fromString(String value) {
        return TextUtils.isEmpty(value) ? BigDecimal.ZERO : new BigDecimal(value);
    }

    @TypeConverter
    public static String toString(BigDecimal value) {
        return value == null ? "" : value.toString();
    }
}
