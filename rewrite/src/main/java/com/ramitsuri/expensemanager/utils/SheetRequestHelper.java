package com.ramitsuri.expensemanager.utils;

import com.google.api.services.sheets.v4.model.AppendCellsRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.CellData;
import com.google.api.services.sheets.v4.model.ConditionValue;
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

            /*// Day
            cellData = new CellData();
            cellData.setUserEnteredValue(new ExtendedValue().setNumberValue(
                    (double)Integer.parseInt(DateHelper.getJustDay(expense.getDateTime()))));
            cellData.setDataValidation(new DataValidationRule()
                    .setCondition(new BooleanCondition().setType("ONE_OF_LIST")
                            .setValues(getDayConditionValues()))
                    .setStrict(true)
                    .setShowCustomUi(true));
            cellDataList.add(cellData);

            // Item Name
            cellData = new CellData();
            cellData.setUserEnteredValue(
                    new ExtendedValue().setStringValue(expense.getItemName()));
            cellData.setDataValidation(new DataValidationRule()
                    .setCondition(new BooleanCondition().setType("ONE_OF_LIST")
                            .setValues(getItemNameConditionValues(items)))
                    .setStrict(true)
                    .setShowCustomUi(true));
            cellDataList.add(cellData);

            // Portion
            cellData = new CellData();
            cellData.setUserEnteredValue(new ExtendedValue()
                    .setNumberValue(expense.getPortionSize().doubleValue()));
            cellData.setDataValidation(new DataValidationRule()
                    .setCondition(new BooleanCondition().setType("ONE_OF_LIST")
                            .setValues(getPortionConditionValues()))
                    .setStrict(true)
                    .setShowCustomUi(true));
            cellDataList.add(cellData);*/

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
