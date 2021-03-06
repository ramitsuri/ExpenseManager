package com.ramitsuri.expensemanager.utils;

import android.util.SparseArray;

import com.ramitsuri.expensemanager.ui.adapter.ListEqualizer;

import java.util.ArrayList;
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

