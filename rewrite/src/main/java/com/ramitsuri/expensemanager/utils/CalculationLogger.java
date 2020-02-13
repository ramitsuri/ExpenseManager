package com.ramitsuri.expensemanager.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;

public class CalculationLogger {
    private static final String DIVIDER = " | ";

    @NonNull
    private final StringBuilder mStringBuilder;
    @NonNull
    private final Formatter mFormatter;

    @NonNull
    private List<ColumnSpec> mColumns;
    private String mFormat;

    public CalculationLogger() {
        mStringBuilder = new StringBuilder();
        mFormatter = new Formatter(mStringBuilder, Locale.US);
        mColumns = new ArrayList<>();
        applyColumnSpec(mColumns);
    }

    public void setColumns(@NonNull List<ColumnSpec> columns) {
        if (columns != mColumns) {
            mColumns = columns;
            applyColumnSpec(mColumns);
        }
    }

    private void applyColumnSpec(List<ColumnSpec> columns) {
        StringBuilder newFormat = new StringBuilder();
        for (int index = 0; index < columns.size(); index++) {
            if (index > 0) {
                newFormat.append(DIVIDER);
            }
            ColumnSpec column = columns.get(index);
            newFormat.append("%");
            if ("s".equals(column.formatSpecifier)) {
                // Example: %15.15s = Pad width to 15 and truncate cell value if needed.
                newFormat.append(column.width);
                // Formatter "precision" will truncate the right-end of the string!
                // (this will enforce the max length of a column cell value)
                newFormat.append(".").append(column.width);
            } else {
                newFormat.append(column.width);
            }
            newFormat.append(column.formatSpecifier);
        }
        mFormat = newFormat.toString();
    }

    public void addHeader() {
        List<ColumnSpec> columns = mColumns;
        String[] columnNames = new String[mColumns.size()];
        for (int index = 0; index < columnNames.length; index++) {
            columnNames[index] = mColumns.get(index).name;
        }

        int horizontalWidth = 0;
        StringBuilder headerFormat = new StringBuilder();
        for (int index = 0; index < columns.size(); index++) {
            if (index > 0) {
                headerFormat.append(DIVIDER);
                horizontalWidth += DIVIDER.length();
            }
            ColumnSpec column = columns.get(index);
            headerFormat.append("%").append(column.width).append("s");
            horizontalWidth += column.width;
        }

        char[] horizontalRule = new char[horizontalWidth];
        Arrays.fill(horizontalRule, '-');
        mStringBuilder.append("\n").append(horizontalRule).append("\n");
        mFormatter.format(headerFormat.toString(), (Object[])columnNames);
        mStringBuilder.append("\n").append(horizontalRule);
    }

    public void addRow(Object... args) {
        mStringBuilder.append("\n");
        mFormatter.format(mFormat, args);
    }

    public void addNewline() {
        mStringBuilder.append("\n");
    }

    public void addTitle(String title) {
        mStringBuilder.append("\n").append(title);
    }

    public String getOutput() {
        return mStringBuilder.toString();
    }

    public void reset() {
        mStringBuilder.setLength(0);
    }

    public static class ColumnSpec {
        /**
         * Example: "Unit Price"
         */
        String name;
        /**
         * Example: "s" or ".2f"
         *
         * @see Formatter
         */
        @NonNull
        String formatSpecifier;
        /**
         * Example: 16
         */
        int width;

        public ColumnSpec(String name, @NonNull String formatSpecifier, int width) {
            this.name = name;
            this.formatSpecifier = Objects.requireNonNull(formatSpecifier);
            this.width = width;
        }
    }
}
