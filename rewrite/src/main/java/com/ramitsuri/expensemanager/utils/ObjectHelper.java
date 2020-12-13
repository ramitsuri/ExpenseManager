package com.ramitsuri.expensemanager.utils;

import android.util.SparseArray;

import com.ramitsuri.expensemanager.entities.SheetInfo;
import com.ramitsuri.expensemanager.ui.adapter.ListEqualizer;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ObjectHelper {
    public static boolean contains(@Nonnull List<String> values, @Nonnull String value) {
        for (String string : values) {
            if (string.equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a String value is contained in a list of ListEqualizer.
     * Returns the index of matching item, -1 otherwise.
     */
    public static <T extends ListEqualizer> int indexOf(@Nonnull List<T> items,
            @Nonnull String value) {
        int index = -1;
        for (int i = 0; i < items.size(); i++) {
            ListEqualizer item = items.get(i);
            if (value.equalsIgnoreCase(item.getValue())) {
                index = i;
                break;
            }
        }
        return index;
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

    @Nullable
    public static SheetInfo getSheetInfo(@Nonnull List<SheetInfo> sheetInfos,
            @Nonnull String sheetName) {
        if (sheetInfos.size() == 0) {
            return null;
        }

        for (SheetInfo sheetInfo : sheetInfos) {
            if (sheetInfo.getSheetName().equalsIgnoreCase(sheetName)) {
                return sheetInfo;
            }
        }
        return null;
    }

    /**
     * Method to convert a generic {@link SparseArray} to a {@link List}
     *
     * @param sparseArray SparseArray to convert
     * @return List of items from sparseArray
     */
    public static <T> List<T> sparseArrayToList(SparseArray<T> sparseArray) {
        int listSize = 0;
        if (sparseArray != null) {
            listSize = sparseArray.size();
        }
        List<T> arrayList = new ArrayList<>(listSize);
        for (int i = 0; i < listSize; i++) {
            arrayList.add(sparseArray.valueAt(i));
        }
        return arrayList;
    }
}

