package com.ramitsuri.expensemanager.utils;

import com.ramitsuri.expensemanager.entities.SheetInfo;

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

    public static boolean isSheetInfosValid(@Nonnull List<SheetInfo> sheetInfos,
            @Nonnull List<String> months) {
        if (sheetInfos.size() == 0 || sheetInfos.size() < months.size()) {
            return false;
        }

        boolean valid = false;
        for (String month : months) {
            for (SheetInfo sheetInfo : sheetInfos) {
                if (sheetInfo.getSheetName().equalsIgnoreCase(month)) {
                    valid = true;
                    break;
                }
            }
            if (!valid) {
                return false;
            }
        }
        return valid;
    }
}

