package com.ramitsuri.expensemanager.utils;

import android.util.SparseArray;

import com.google.api.services.sheets.v4.model.AppendCellsRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.BooleanCondition;
import com.google.api.services.sheets.v4.model.CellData;
import com.google.api.services.sheets.v4.model.CellFormat;
import com.google.api.services.sheets.v4.model.ConditionValue;
import com.google.api.services.sheets.v4.model.DataValidationRule;
import com.google.api.services.sheets.v4.model.ExtendedValue;
import com.google.api.services.sheets.v4.model.NumberFormat;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.RowData;
import com.ramitsuri.expensemanager.Constants;
import com.ramitsuri.expensemanager.entities.Expense;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import static com.ramitsuri.expensemanager.Constants.Sheets.DATE;
import static com.ramitsuri.expensemanager.Constants.Sheets.DATE_IS_VALID;
import static com.ramitsuri.expensemanager.Constants.Sheets.DATE_PATTERN;
import static com.ramitsuri.expensemanager.Constants.Sheets.FLAG;
import static com.ramitsuri.expensemanager.Constants.Sheets.ONE_OF_LIST;

public class SheetRequestHelper {

    public static BatchUpdateSpreadsheetRequest getUpdateRequestBody(
            List<Expense> expensesToBackup,
            List<String> categories,
            List<String> paymentMethods,
            int defaultSheetId) {
        BatchUpdateSpreadsheetRequest requestBody = new BatchUpdateSpreadsheetRequest();
        List<Request> requests = new ArrayList<>();

        SparseArray<List<Expense>> map = getSheetIdExpenseMap(expensesToBackup, defaultSheetId);

        for (int i = 0; i < map.size(); i++) {
            int sheetId = map.keyAt(i);
            List<Expense> expenses = map.get(map.keyAt(i));
            if (expenses == null) {
                continue;
            }

            Request request = new Request();
            AppendCellsRequest appendCellsRequest = new AppendCellsRequest();
            appendCellsRequest.setFields("*");
            appendCellsRequest.setSheetId(sheetId);

            List<RowData> rowDataList = new ArrayList<>();
            for (Expense expense : expenses) {
                RowData rowData = new RowData();
                List<CellData> cellDataList = new ArrayList<>();
                CellData cellData;

                // Date
                cellData = new CellData();
                cellData.setUserEnteredValue(new ExtendedValue()
                        .setNumberValue((double)DateHelper.toSheetsDate(expense.getDateTime())));
                cellData.setUserEnteredFormat(
                        new CellFormat().setNumberFormat(
                                new NumberFormat().setType(DATE).setPattern(DATE_PATTERN)));
                cellData.setDataValidation(new DataValidationRule()
                        .setCondition(new BooleanCondition().setType(DATE_IS_VALID)));
                cellDataList.add(cellData);

                // Description
                cellData = new CellData();
                cellData.setUserEnteredValue(
                        new ExtendedValue()
                                .setStringValue(String.valueOf(expense.getDescription())));
                cellDataList.add(cellData);

                // Store
                cellData = new CellData();
                cellData.setUserEnteredValue(
                        new ExtendedValue().setStringValue(String.valueOf(expense.getStore())));
                cellDataList.add(cellData);

                // Amount
                cellData = new CellData();
                cellData.setUserEnteredValue(
                        new ExtendedValue().setNumberValue(expense.getAmount().doubleValue()));
                cellDataList.add(cellData);

                // Payment Method
                cellData = new CellData();
                cellData.setUserEnteredValue(new ExtendedValue()
                        .setStringValue(String.valueOf(expense.getPaymentMethod())));
                cellData.setDataValidation(new DataValidationRule()
                        .setCondition(new BooleanCondition().setType(ONE_OF_LIST)
                                .setValues(getPaymentMethodConditionValues(paymentMethods)))
                        .setStrict(true)
                        .setShowCustomUi(true));
                cellDataList.add(cellData);

                // Category
                cellData = new CellData();
                cellData.setUserEnteredValue(new ExtendedValue()
                        .setStringValue(String.valueOf(expense.getCategory())));
                cellData.setDataValidation(new DataValidationRule()
                        .setCondition(new BooleanCondition().setType(ONE_OF_LIST)
                                .setValues(getCategoriesConditionValues(categories)))
                        .setStrict(true)
                        .setShowCustomUi(true));
                cellDataList.add(cellData);

                // Flag
                if (expense.isStarred()) {
                    cellData = new CellData();
                    cellData.setUserEnteredValue(new ExtendedValue()
                            .setStringValue(FLAG));
                    cellData.setDataValidation(new DataValidationRule()
                            .setCondition(new BooleanCondition().setType(ONE_OF_LIST)
                                    .setValues(getFlagConditionValues()))
                            .setStrict(true)
                            .setShowCustomUi(true));
                    cellDataList.add(cellData);
                }

                rowData.setValues(cellDataList);
                rowDataList.add(rowData);
            }
            appendCellsRequest.setRows(rowDataList);
            request.setAppendCells(appendCellsRequest);
            requests.add(request);
        }
        requestBody.setRequests(requests);
        return requestBody;
    }

    private static ArrayList<ConditionValue> getPaymentMethodConditionValues(
            List<String> paymentMethods) {
        ArrayList<ConditionValue> conditionValues = new ArrayList<>();

        for (String paymentMethod : paymentMethods) {
            ConditionValue value = new ConditionValue();
            value.setUserEnteredValue(paymentMethod);
            conditionValues.add(value);
        }

        return conditionValues;
    }

    private static ArrayList<ConditionValue> getCategoriesConditionValues(
            List<String> categories) {
        ArrayList<ConditionValue> conditionValues = new ArrayList<>();

        for (String category : categories) {
            ConditionValue value = new ConditionValue();
            value.setUserEnteredValue(category);
            conditionValues.add(value);
        }

        return conditionValues;
    }

    private static ArrayList<ConditionValue> getFlagConditionValues() {
        ArrayList<ConditionValue> conditionValues = new ArrayList<>();

        ConditionValue value = new ConditionValue();
        value.setUserEnteredValue(FLAG);
        conditionValues.add(value);

        return conditionValues;
    }

    private static SparseArray<List<Expense>> getSheetIdExpenseMap(@Nonnull List<Expense> expenses,
            int defaultSheetId) {
        SparseArray<List<Expense>> map = new SparseArray<>();
        for (Expense expense : expenses) {
            int sheetId;
            if (expense.getSheetId() == Constants.UNDEFINED) { // Expense doesn't have a sheet id
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
}
