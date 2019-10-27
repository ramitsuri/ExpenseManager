package com.ramitsuri.expensemanager.utils;

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
import com.ramitsuri.expensemanager.entities.Expense;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class SheetRequestHelper {

    public static BatchUpdateSpreadsheetRequest getUpdateRequestBody(
            List<Expense> expenses,
            List<String> categories,
            List<String> paymentMethods,
            String sheetId) {
        BatchUpdateSpreadsheetRequest requestBody = new BatchUpdateSpreadsheetRequest();
        List<Request> requests = new ArrayList<>();
        Request request = new Request();
        AppendCellsRequest appendCellsRequest = new AppendCellsRequest();
        appendCellsRequest.setFields("*");
        int sheetIdInt;
        try {
            sheetIdInt = Integer.parseInt(sheetId);
        } catch (NumberFormatException ex) {
            Timber.e("Failed to convert sheet id to string");
            return null;
        }
        appendCellsRequest.setSheetId(sheetIdInt);

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
                            new NumberFormat().setType("DATE").setPattern("M/d/yyyy")));
            cellData.setDataValidation(new DataValidationRule()
                    .setCondition(new BooleanCondition().setType("DATE_IS_VALID")));
            cellDataList.add(cellData);

            // Description
            cellData = new CellData();
            cellData.setUserEnteredValue(
                    new ExtendedValue().setStringValue(String.valueOf(expense.getDescription())));
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
                    .setCondition(new BooleanCondition().setType("ONE_OF_LIST")
                            .setValues(getPaymentMethodConditionValues(paymentMethods)))
                    .setStrict(true)
                    .setShowCustomUi(true));
            cellDataList.add(cellData);

            // Category
            cellData = new CellData();
            cellData.setUserEnteredValue(new ExtendedValue()
                    .setStringValue(String.valueOf(expense.getCategory())));
            cellData.setDataValidation(new DataValidationRule()
                    .setCondition(new BooleanCondition().setType("ONE_OF_LIST")
                            .setValues(getCategoriesConditionValues(categories)))
                    .setStrict(true)
                    .setShowCustomUi(true));
            cellDataList.add(cellData);

            rowData.setValues(cellDataList);
            rowDataList.add(rowData);
        }
        appendCellsRequest.setRows(rowDataList);
        request.setAppendCells(appendCellsRequest);
        requests.add(request);
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
}
