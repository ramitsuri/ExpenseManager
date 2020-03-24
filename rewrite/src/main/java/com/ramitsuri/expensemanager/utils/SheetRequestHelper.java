package com.ramitsuri.expensemanager.utils;

import android.util.SparseArray;

import com.google.api.services.drive.model.File;
import com.google.api.services.sheets.v4.model.AppendCellsRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.BooleanCondition;
import com.google.api.services.sheets.v4.model.CellData;
import com.google.api.services.sheets.v4.model.CellFormat;
import com.google.api.services.sheets.v4.model.Color;
import com.google.api.services.sheets.v4.model.ConditionValue;
import com.google.api.services.sheets.v4.model.DataValidationRule;
import com.google.api.services.sheets.v4.model.DuplicateSheetRequest;
import com.google.api.services.sheets.v4.model.ExtendedValue;
import com.google.api.services.sheets.v4.model.GridData;
import com.google.api.services.sheets.v4.model.GridProperties;
import com.google.api.services.sheets.v4.model.GridRange;
import com.google.api.services.sheets.v4.model.NumberFormat;
import com.google.api.services.sheets.v4.model.Padding;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.RowData;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.SheetProperties;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.SpreadsheetProperties;
import com.google.api.services.sheets.v4.model.TextFormat;
import com.google.api.services.sheets.v4.model.UpdateCellsRequest;
import com.ramitsuri.expensemanager.Constants;
import com.ramitsuri.expensemanager.entities.Budget;
import com.ramitsuri.expensemanager.entities.Expense;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.ramitsuri.expensemanager.Constants.Range.USER_ENTERED_CATEGORIES;
import static com.ramitsuri.expensemanager.Constants.Range.USER_ENTERED_PAYMENT_METHODS;
import static com.ramitsuri.expensemanager.Constants.Sheets.DATE;
import static com.ramitsuri.expensemanager.Constants.Sheets.DATE_IS_VALID;
import static com.ramitsuri.expensemanager.Constants.Sheets.DATE_PATTERN;
import static com.ramitsuri.expensemanager.Constants.Sheets.FLAG;
import static com.ramitsuri.expensemanager.Constants.Sheets.ONE_OF_LIST;
import static com.ramitsuri.expensemanager.Constants.Sheets.ONE_OF_RANGE;

public class SheetRequestHelper {

    public static BatchUpdateSpreadsheetRequest getUpdateRequestBody(
            @Nonnull List<Expense> expensesToBackup,
            @Nullable List<Integer> editedSheetIds,
            int defaultSheetId) {
        BatchUpdateSpreadsheetRequest requestBody = new BatchUpdateSpreadsheetRequest();
        List<Request> requests = new ArrayList<>();

        // Update whole sheets that had deleted or edited expenses
        SparseArray<List<Expense>> updateMap = getUpdateMap(expensesToBackup, editedSheetIds);
        for (int i = 0; i < updateMap.size(); i++) {
            int sheetId = updateMap.keyAt(i);
            List<Expense> expenses = updateMap.get(updateMap.keyAt(i));
            if (expenses == null) {
                continue;
            }

            Request request = new Request();
            UpdateCellsRequest updateCellsRequest = new UpdateCellsRequest();
            updateCellsRequest.setFields("*");
            updateCellsRequest.setRange(new GridRange()
                    .setSheetId(sheetId)
                    .setStartColumnIndex(0)
                    .setStartColumnIndex(0));

            List<RowData> rowDataList = new ArrayList<>();
            for (Expense expense : expenses) {
                RowData rowData = getExpenseRowData(expense);
                rowDataList.add(rowData);
            }
            updateCellsRequest.setRows(rowDataList);
            request.setUpdateCells(updateCellsRequest);
            requests.add(request);
        }

        // Append new expenses to their sheets
        SparseArray<List<Expense>> appendMap = getAppendMap(expensesToBackup, defaultSheetId);
        for (int i = 0; i < appendMap.size(); i++) {
            int sheetId = appendMap.keyAt(i);
            List<Expense> expenses = appendMap.get(appendMap.keyAt(i));
            if (expenses == null) {
                continue;
            }

            Request request = new Request();
            AppendCellsRequest appendCellsRequest = new AppendCellsRequest();
            appendCellsRequest.setFields("*");
            appendCellsRequest.setSheetId(sheetId);

            List<RowData> rowDataList = new ArrayList<>();
            for (Expense expense : expenses) {
                RowData rowData = getExpenseRowData(expense);
                rowDataList.add(rowData);
            }
            appendCellsRequest.setRows(rowDataList);
            request.setAppendCells(appendCellsRequest);
            requests.add(request);
        }

        requestBody.setRequests(requests);
        return requestBody;
    }

    public static Spreadsheet getCreateRequestBody(
            @Nonnull String spreadsheetTitle,
            @Nonnull String entitiesSheetTitle,
            int entitiesSheetIndex,
            @Nonnull String templateSheetTitle,
            int templateSheetIndex,
            @Nonnull List<String> paymentMethods,
            @Nonnull List<String> categories,
            @Nonnull List<Budget> budgets) {
        Spreadsheet request = new Spreadsheet();

        SpreadsheetProperties properties = new SpreadsheetProperties();
        properties.setTitle(spreadsheetTitle)
                .setLocale(Locale.forLanguageTag(Locale.getDefault().toLanguageTag()).toString())
                .setAutoRecalc("ON_CHANGE")
                .setTimeZone(TimeZone.getDefault().getID())
                .setDefaultFormat(new CellFormat()
                        .setBackgroundColor(new Color()
                                .setBlue(1f)
                                .setGreen(1f)
                                .setRed(1f))
                        .setPadding(new Padding()
                                .setTop(2)
                                .setRight(3)
                                .setBottom(2)
                                .setLeft(3))
                        .setVerticalAlignment("BOTTOM")
                        .setWrapStrategy("OVERFLOW_CELL")
                        .setTextFormat(new TextFormat()
                                .setForegroundColor(new Color())
                                .setFontFamily("arial,sans,sans-serif")
                                .setFontSize(10)
                                .setBold(false)
                                .setItalic(false)
                                .setStrikethrough(false)
                                .setUnderline(false)));
        request.setProperties(properties);

        List<Sheet> sheets = new ArrayList<>();
        sheets.add(getEntitiesSheet(entitiesSheetTitle, entitiesSheetIndex,
                paymentMethods, categories, budgets));
        sheets.add(getExpenseSheet(templateSheetTitle, templateSheetIndex));

        request.setSheets(sheets);
        return request;
    }

    public static File getCopyFileRequestBody(String fileName) {
        File request = new File();
        request.setName(fileName);
        return request;
    }

    public static BatchUpdateSpreadsheetRequest getDuplicateSheetsRequest(
            int sourceSheetId,
            int startIndex,
            @Nonnull List<String> sheetNames) {
        BatchUpdateSpreadsheetRequest requestBody = new BatchUpdateSpreadsheetRequest();
        List<Request> requestList = new ArrayList<>();

        for (String month : sheetNames) {
            Request request = new Request();

            DuplicateSheetRequest duplicateSheetRequest = new DuplicateSheetRequest()
                    .setSourceSheetId(sourceSheetId)
                    .setInsertSheetIndex(startIndex)
                    .setNewSheetName(month);

            request.setDuplicateSheet(duplicateSheetRequest);
            requestList.add(request);
            startIndex++;
        }
        requestBody.setRequests(requestList);
        return requestBody;
    }

    private static Sheet getExpenseSheet(String title, int index) {
        int rows = 300;
        int columns = 7;
        Sheet sheet = new Sheet();
        sheet.setProperties(getSheetProperties(title, index, rows, columns));

        List<GridData> dataList = new ArrayList<>();
        GridData data = new GridData();
        List<RowData> rowDataList = new ArrayList<>();
        for (int rowIndex = 0; rowIndex < rows; rowIndex++) {
            RowData rowData = new RowData();
            List<CellData> valuesList = new ArrayList<>();
            CellData value;

            // Date
            value = getEmptyCell();
            value.setUserEnteredFormat(
                    new CellFormat().setNumberFormat(
                            new NumberFormat().setType(DATE).setPattern(DATE_PATTERN)));
            value.setDataValidation(new DataValidationRule()
                    .setCondition(new BooleanCondition().setType(DATE_IS_VALID)));
            valuesList.add(value);

            // Description
            value = getEmptyCell();
            valuesList.add(value);

            // Store
            value = getEmptyCell();
            valuesList.add(value);

            // Amount
            value = getEmptyCell();
            valuesList.add(value);

            // Payment Method
            value = getEmptyCell();
            value.setDataValidation(new DataValidationRule()
                    .setCondition(new BooleanCondition().setType(ONE_OF_RANGE)
                            .setValues(getPaymentMethodConditionValues()))
                    .setStrict(true)
                    .setShowCustomUi(true));
            valuesList.add(value);

            // Category
            value = getEmptyCell();
            value.setDataValidation(new DataValidationRule()
                    .setCondition(new BooleanCondition().setType(ONE_OF_RANGE)
                            .setValues(getCategoriesConditionValues()))
                    .setStrict(true)
                    .setShowCustomUi(true));
            valuesList.add(value);

            // Flag
            value = getEmptyCell();
            value.setDataValidation(new DataValidationRule()
                    .setCondition(new BooleanCondition().setType(ONE_OF_LIST)
                            .setValues(getFlagConditionValues()))
                    .setStrict(true)
                    .setShowCustomUi(true));
            valuesList.add(value);

            rowData.setValues(valuesList);
            rowDataList.add(rowData);
        }
        data.setRowData(rowDataList);
        dataList.add(data);

        sheet.setData(dataList);

        return sheet;
    }

    private static Sheet getEntitiesSheet(@Nonnull String title,
            int index,
            @Nonnull List<String> paymentMethods,
            @Nonnull List<String> categories,
            @Nonnull List<Budget> budgets) {
        int rows = 22;
        int columns = 13;
        Sheet sheet = new Sheet();
        sheet.setProperties(getSheetProperties(title, index, rows, columns));

        List<GridData> dataList = new ArrayList<>();
        GridData data = new GridData();
        List<RowData> rowDataList = new ArrayList<>();
        for (int rowIndex = 0; rowIndex < 22; rowIndex++) {
            RowData rowData = new RowData();
            List<CellData> valuesList = new ArrayList<>();
            CellData value;

            // Payment Method
            if (rowIndex < paymentMethods.size()) {
                value = getValueCell(paymentMethods.get(rowIndex));
            } else {
                value = getEmptyCell();
            }
            valuesList.add(value);

            // Empty cell
            value = getEmptyCell();
            valuesList.add(value);

            // Category
            if (rowIndex < categories.size()) {
                value = getValueCell(categories.get(rowIndex));
            } else {
                value = getEmptyCell();
            }
            valuesList.add(value);

            // Empty cell
            value = getEmptyCell();
            valuesList.add(value);

            // Budget
            if (rowIndex < budgets.size()) {
                Budget budget = budgets.get(rowIndex);

                value = getValueCell(budget.getName());
                valuesList.add(value);

                value = getValueCell(budget.getAmount());
                valuesList.add(value);

                for (String category : budget.getCategories()) {
                    value = getValueCell(category);
                    valuesList.add(value);
                }
            } else {
                value = getEmptyCell();
                valuesList.add(value);
            }

            rowData.setValues(valuesList);
            rowDataList.add(rowData);
        }
        data.setRowData(rowDataList);
        dataList.add(data);

        sheet.setData(dataList);
        return sheet;
    }

    private static SheetProperties getSheetProperties(@Nonnull String title, int index,
            int rows, int columns) {
        return new SheetProperties()
                .setTitle(title)
                .setIndex(index)
                .setSheetType("GRID")
                .setGridProperties(
                        new GridProperties()
                                .setRowCount(rows)
                                .setColumnCount(columns));
    }

    private static CellData getEmptyCell() {
        return new CellData()
                .setUserEnteredFormat(getUserEnteredFormat())
                .setEffectiveFormat(getEffectiveFormat());
    }

    private static CellData getValueCell(String value) {
        return new CellData()
                .setUserEnteredValue(new ExtendedValue().setStringValue(value))
                .setEffectiveValue(new ExtendedValue().setStringValue(value))
                .setFormattedValue(value)
                .setUserEnteredFormat(getUserEnteredFormat())
                .setEffectiveFormat(getEffectiveFormat());
    }

    private static CellData getValueCell(BigDecimal value) {
        return new CellData()
                .setUserEnteredValue(new ExtendedValue().setNumberValue(value.doubleValue()))
                .setEffectiveValue(new ExtendedValue().setNumberValue(value.doubleValue()))
                .setUserEnteredFormat(getUserEnteredFormat())
                .setEffectiveFormat(getEffectiveFormat());
    }

    private static CellFormat getUserEnteredFormat() {
        return new CellFormat()
                .setTextFormat(new TextFormat().setFontFamily("Roboto"));
    }

    private static CellFormat getEffectiveFormat() {
        return new CellFormat()
                .setTextFormat(new TextFormat().setFontFamily("Roboto").setFontSize(10));
    }

    private static ArrayList<ConditionValue> getPaymentMethodConditionValues() {
        ArrayList<ConditionValue> conditionValues = new ArrayList<>();

        ConditionValue value = new ConditionValue();
        value.setUserEnteredValue(USER_ENTERED_PAYMENT_METHODS);
        conditionValues.add(value);

        return conditionValues;
    }

    private static ArrayList<ConditionValue> getCategoriesConditionValues() {
        ArrayList<ConditionValue> conditionValues = new ArrayList<>();

        ConditionValue value = new ConditionValue();
        value.setUserEnteredValue(USER_ENTERED_CATEGORIES);
        conditionValues.add(value);

        return conditionValues;
    }

    private static ArrayList<ConditionValue> getFlagConditionValues() {
        ArrayList<ConditionValue> conditionValues = new ArrayList<>();

        ConditionValue value = new ConditionValue();
        value.setUserEnteredValue(FLAG);
        conditionValues.add(value);

        return conditionValues;
    }

    private static SparseArray<List<Expense>> getAppendMap(@Nonnull List<Expense> expenses,
            int defaultSheetId) {
        SparseArray<List<Expense>> map = new SparseArray<>();
        for (Expense expense : expenses) {
            int sheetId;
            // Expense doesn't have a sheet id
            if (expense.getSheetId() == Constants.Basic.UNDEFINED) {
                sheetId = defaultSheetId;
            } else { // Expense has a sheet id
                sheetId = expense.getSheetId();
            }
            // Add expense to map
            List<Expense> expenseList = map.get(sheetId);
            if (expenseList == null) {
                expenseList = new ArrayList<>();
                expenseList.add(expense);
            } else {
                expenseList.add(expense);
            }
            map.put(sheetId, expenseList);
        }
        return map;
    }

    /**
     * Generates a map of sheet id and list of expenses that were edited or deleted after they were
     * backed up. The expense list should contain all expenses for the edited sheet ids.
     * The ones that were edited or deleted along with ones that weren't.
     * This is because there isn't a way right now to just update the sheet rows for expenses that
     * were edited. The whole sheet would be emptied, and these expenses will be added to it.
     */
    private static SparseArray<List<Expense>> getUpdateMap(@Nonnull List<Expense> expenses,
            @Nullable List<Integer> editedSheetIds) {
        SparseArray<List<Expense>> map = new SparseArray<>();
        if (editedSheetIds == null || editedSheetIds.size() == 0) {
            return map;
        }
        if (expenses.size() == 0) {
            for (Integer editedSheetId : editedSheetIds) {
                map.put(editedSheetId, new ArrayList<Expense>());
            }
        }
        for (Iterator<Expense> iterator = expenses.iterator(); iterator.hasNext(); ) {
            Expense expense = iterator.next();
            if (!editedSheetIds.contains(expense.getSheetId())) {
                // Sheet id for the expense is not one of those which has a backed up expense
                // that was edited. This expense will be taken care of in append request
                continue;
            } else {
                // This is an expense that was edited after it was backed up or an expense from the
                // same sheet id. Add it to update map and remove it from original list so that
                // it isn't read again when generating Append map.
                iterator.remove();
            }
            int sheetId = expense.getSheetId();

            // Add expense to map
            List<Expense> expenseList = map.get(sheetId);
            if (expenseList == null) {
                expenseList = new ArrayList<>();
                expenseList.add(expense);
            } else {
                expenseList.add(expense);
            }
            map.put(sheetId, expenseList);
        }
        return map;
    }

    private static RowData getExpenseRowData(Expense expense) {
        RowData rowData = new RowData();
        List<CellData> valuesList = new ArrayList<>();
        CellData values;

        // Date
        values = new CellData();
        values.setUserEnteredValue(new ExtendedValue()
                .setNumberValue((double)DateHelper.toSheetsDate(expense.getDateTime())));
        values.setUserEnteredFormat(
                new CellFormat().setNumberFormat(
                        new NumberFormat().setType(DATE).setPattern(DATE_PATTERN)));
        values.setDataValidation(new DataValidationRule()
                .setCondition(new BooleanCondition().setType(DATE_IS_VALID)));
        valuesList.add(values);

        // Description
        values = new CellData();
        values.setUserEnteredValue(
                new ExtendedValue()
                        .setStringValue(String.valueOf(expense.getDescription())));
        valuesList.add(values);

        // Store
        values = new CellData();
        values.setUserEnteredValue(
                new ExtendedValue().setStringValue(String.valueOf(expense.getStore())));
        valuesList.add(values);

        // Amount
        values = new CellData();
        values.setUserEnteredValue(
                new ExtendedValue().setNumberValue(expense.getAmount().doubleValue()));
        valuesList.add(values);

        // Payment Method
        values = new CellData();
        values.setUserEnteredValue(new ExtendedValue()
                .setStringValue(String.valueOf(expense.getPaymentMethod())));
        values.setDataValidation(new DataValidationRule()
                .setCondition(new BooleanCondition().setType(ONE_OF_RANGE)
                        .setValues(getPaymentMethodConditionValues()))
                .setStrict(true)
                .setShowCustomUi(true));
        valuesList.add(values);

        // Category
        values = new CellData();
        values.setUserEnteredValue(new ExtendedValue()
                .setStringValue(String.valueOf(expense.getCategory())));
        values.setDataValidation(new DataValidationRule()
                .setCondition(new BooleanCondition().setType(ONE_OF_RANGE)
                        .setValues(getCategoriesConditionValues()))
                .setStrict(true)
                .setShowCustomUi(true));
        valuesList.add(values);

        // Flag
        if (expense.isStarred()) {
            values = new CellData();
            values.setUserEnteredValue(new ExtendedValue()
                    .setStringValue(FLAG));
            values.setDataValidation(new DataValidationRule()
                    .setCondition(new BooleanCondition().setType(ONE_OF_LIST)
                            .setValues(getFlagConditionValues()))
                    .setStrict(true)
                    .setShowCustomUi(true));
            valuesList.add(values);
        }

        rowData.setValues(valuesList);
        return rowData;
    }
}
