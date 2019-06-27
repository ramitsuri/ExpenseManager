package com.ramitsuri.expensemanager.helper;

import com.google.api.client.json.GenericJson;
import com.google.api.services.sheets.v4.model.AddNamedRangeRequest;
import com.google.api.services.sheets.v4.model.AppendCellsRequest;
import com.google.api.services.sheets.v4.model.BooleanCondition;
import com.google.api.services.sheets.v4.model.CellData;
import com.google.api.services.sheets.v4.model.CellFormat;
import com.google.api.services.sheets.v4.model.Color;
import com.google.api.services.sheets.v4.model.ConditionValue;
import com.google.api.services.sheets.v4.model.DataValidationRule;
import com.google.api.services.sheets.v4.model.ExtendedValue;
import com.google.api.services.sheets.v4.model.GridData;
import com.google.api.services.sheets.v4.model.GridRange;
import com.google.api.services.sheets.v4.model.NamedRange;
import com.google.api.services.sheets.v4.model.NumberFormat;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.RowData;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.SheetProperties;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.SpreadsheetProperties;
import com.google.api.services.sheets.v4.model.TextFormat;
import com.ramitsuri.expensemanager.db.DBConstants;
import com.ramitsuri.expensemanager.entities.Category;
import com.ramitsuri.expensemanager.entities.Expense;
import com.ramitsuri.expensemanager.entities.PaymentMethod;

import java.util.ArrayList;
import java.util.List;

public class SheetsHelper {

    private static String SPREADSHEET_TITLE = "Expense Manager Expenses";
    private static String LOCALE = "en";
    //public static int EXPENSES_SHEET_ID = 1;

    public static int EXPENSES_SHEET_ID = 2092936052;
    public static int PAYMENT_METHOD_SHEET_ID = 2;
    public static int CATEGORIES_SHEET_ID = 3;
    public static int BUDGET_SHEET_ID = 4;
    public static String CATEGORIES_NAMED_RANGE_ID = String.valueOf(CATEGORIES_SHEET_ID);
    public static String PAYMENT_METHODS_NAMED_RANGE_ID = String.valueOf(PAYMENT_METHOD_SHEET_ID);

    public static Spreadsheet getNewSpreadsheet() {
        Spreadsheet spreadsheet = new Spreadsheet();
        SpreadsheetProperties properties = new SpreadsheetProperties();
        properties.setTitle(SPREADSHEET_TITLE);
        properties.setLocale(LOCALE);
        spreadsheet.setProperties(properties);

        Sheet sheet1 = new Sheet();
        sheet1.setProperties(getSheetProperties(CATEGORIES_SHEET_ID, CATEGORIES_SHEET_ID,
                DBConstants.TABLE_CATEGORIES));
        List<GridData> data = new ArrayList<>();
        GridData gridData = new GridData();
        gridData.setStartColumn(0);
        gridData.setStartRow(0);
        gridData.setRowData(getRowDataCategories());
        data.add(gridData);
        sheet1.setData(data);

        Sheet sheet2 = new Sheet();
        sheet2.setProperties(getSheetProperties(PAYMENT_METHOD_SHEET_ID, PAYMENT_METHOD_SHEET_ID,
                DBConstants.TABLE_PAYMENT_METHOD));
        data = new ArrayList<>();
        gridData = new GridData();
        gridData.setStartColumn(0);
        gridData.setStartRow(0);
        gridData.setRowData(getRowDataPaymentMethods());
        data.add(gridData);
        sheet2.setData(data);

        Sheet sheet3 = new Sheet();
        sheet3.setProperties(getSheetProperties(EXPENSES_SHEET_ID, EXPENSES_SHEET_ID,
                DBConstants.TABLE_EXPENSES));
        data = new ArrayList<>();
        gridData = new GridData();
        gridData.setStartColumn(0);
        gridData.setStartRow(0);
        gridData.setRowData(getRowDataExpenses());
        data.add(gridData);
        sheet3.setData(data);

        Sheet sheet4 = new Sheet();
        sheet4.setProperties(
                getSheetProperties(BUDGET_SHEET_ID, BUDGET_SHEET_ID, DBConstants.TABLE_BUDGET));
        data = new ArrayList<>();
        gridData = new GridData();
        gridData.setStartColumn(0);
        gridData.setStartRow(0);
        gridData.setRowData(getRowDataBudget());
        data.add(gridData);
        sheet4.setData(data);

        List<Sheet> sheets = new ArrayList<>();
        sheets.add(sheet1);
        sheets.add(sheet2);
        sheets.add(sheet3);
        sheets.add(sheet4);
        spreadsheet.setSheets(sheets);

        return spreadsheet;
    }

    private static SheetProperties getSheetProperties(int id, int index, String title) {
        SheetProperties sheetProperties = new SheetProperties();
        sheetProperties.setSheetId(id);
        sheetProperties.setTitle(title);
        sheetProperties.setIndex(index);
        return sheetProperties;
    }

    private static Color getColor(float r, float b, float g) {
        Color backgroundColor = new Color();
        backgroundColor.setRed(r);
        backgroundColor.setBlue(b);
        backgroundColor.setGreen(g);
        return backgroundColor;
    }

    private static CellFormat getCellFormat() {
        CellFormat cellFormat = new CellFormat();
        cellFormat.setBackgroundColor(getColor(223F, 103F, 50F));
        TextFormat textFormat = new TextFormat();
        textFormat.setBold(true);
        textFormat.setForegroundColor(getColor(1F, 1F, 1F));
        cellFormat.setTextFormat(textFormat);
        return cellFormat;
    }

    private static CellData getCellData(String columnName) {
        CellData cellData = new CellData();
        cellData.setUserEnteredValue(new ExtendedValue().setStringValue(columnName));
        cellData.setUserEnteredFormat(getCellFormat());
        return cellData;
    }

    private static List<RowData> getRowDataPaymentMethods() {
        final RowData rowData = new RowData();
        List<CellData> cellData = new ArrayList<>();
        cellData.add(getCellData(DBConstants.COLUMN_PAYMENT_METHOD_ID));
        cellData.add(getCellData(DBConstants.COLUMN_PAYMENT_METHOD_NAME));
        rowData.setValues(cellData);
        return new ArrayList<RowData>() {{
            add(rowData);
        }};
    }

    private static List<RowData> getRowDataExpenses() {
        final RowData rowData = new RowData();
        List<CellData> cellData = new ArrayList<>();
        cellData.add(getCellData(DBConstants.COLUMN_EXPENSE_ROW_ID));
        cellData.add(getCellData(DBConstants.COLUMN_EXPENSE_DATE_TIME));
        cellData.add(getCellData(DBConstants.COLUMN_EXPENSE_AMOUNT));
        cellData.add(getCellData(DBConstants.COLUMN_EXPENSE_PAYMENT_METHOD_ID));
        cellData.add(getCellData(DBConstants.COLUMN_EXPENSE_CATEGORY_ID));
        cellData.add(getCellData(DBConstants.COLUMN_EXPENSE_NOTES));
        cellData.add(getCellData(DBConstants.COLUMN_EXPENSE_STORE));
        cellData.add(getCellData(DBConstants.COLUMN_EXPENSE_FLAGGED));
        cellData.add(getCellData(DBConstants.COLUMN_EXPENSE_SYNC_STATUS));
        rowData.setValues(cellData);
        return new ArrayList<RowData>() {{
            add(rowData);
        }};
    }

    private static List<RowData> getRowDataCategories() {
        final RowData rowData = new RowData();
        List<CellData> cellData = new ArrayList<>();
        cellData.add(getCellData(DBConstants.COLUMN_CATEGORIES_ID));
        cellData.add(getCellData(DBConstants.COLUMN_CATEGORIES_NAME));
        rowData.setValues(cellData);
        return new ArrayList<RowData>() {{
            add(rowData);
        }};
    }

    private static List<RowData> getRowDataBudget() {
        final RowData rowData = new RowData();
        List<CellData> cellData = new ArrayList<>();
        cellData.add(getCellData(DBConstants.COLUMN_BUDGET_ID));
        cellData.add(getCellData(DBConstants.COLUMN_BUDGET_CATEGORY_IDS));
        cellData.add(getCellData(DBConstants.COLUMN_BUDGET_AMOUNT));
        rowData.setValues(cellData);
        return new ArrayList<RowData>() {{
            add(rowData);
        }};
    }

    public static Request getExpenseSheetsRequest(List<Expense> expensesToBackup) {
        Request request = new Request();
        AppendCellsRequest appendCellsRequest = new AppendCellsRequest();
        appendCellsRequest.setFields("*");
        int sheetsId;
        try {
            sheetsId = Integer.parseInt(AppHelper.getSheetsId());
        } catch (NumberFormatException ex) {
            sheetsId = EXPENSES_SHEET_ID;
        }
        appendCellsRequest.setSheetId(sheetsId);

        List<RowData> rows = new ArrayList<>();
        for (Expense expense : expensesToBackup) {
            RowData rowData = new RowData();
            List<CellData> row = new ArrayList<>();

            CellData cellData = new CellData();

            // Date
            cellData.setUserEnteredValue(new ExtendedValue()
                    .setNumberValue(DateHelper.getDateForSheet(expense.getDateTime())));
            cellData.setUserEnteredFormat(
                    new CellFormat().setNumberFormat(
                            new NumberFormat().setType("DATE").setPattern("M/d/yyyy")));
            cellData.setDataValidation(new DataValidationRule()
                    .setCondition(new BooleanCondition().setType("DATE_IS_VALID")));
            row.add(cellData);

            // Description
            cellData = new CellData();
            cellData.setUserEnteredValue(
                    new ExtendedValue().setStringValue(String.valueOf(expense.getDescription())));
            row.add(cellData);

            // Store
            cellData = new CellData();
            cellData.setUserEnteredValue(
                    new ExtendedValue().setStringValue(String.valueOf(expense.getStore())));
            row.add(cellData);

            // Amount
            cellData = new CellData();
            cellData.setUserEnteredValue(
                    new ExtendedValue().setNumberValue(expense.getAmount().doubleValue()));
            row.add(cellData);

            // Payment Method
            cellData = new CellData();
            cellData.setUserEnteredValue(new ExtendedValue()
                    .setStringValue(String.valueOf(expense.getPaymentMethod().getName())));
            cellData.setDataValidation(new DataValidationRule()
                    .setCondition(new BooleanCondition().setType("ONE_OF_LIST")
                            .setValues(getPaymentMethodConditionValues()))
                    .setStrict(true)
                    .setShowCustomUi(true));
            row.add(cellData);

            // Category
            cellData = new CellData();
            cellData.setUserEnteredValue(new ExtendedValue()
                    .setStringValue(String.valueOf(expense.getCategory().getName())));
            cellData.setDataValidation(new DataValidationRule()
                    .setCondition(new BooleanCondition().setType("ONE_OF_LIST")
                            .setValues(getCategoryConditionValues()))
                    .setStrict(true)
                    .setShowCustomUi(true));
            row.add(cellData);

            rowData.setValues(row);
            rows.add(rowData);
        }
        appendCellsRequest.setRows(rows);
        request.setAppendCells(appendCellsRequest);
        return request;
    }

    private static ArrayList<ConditionValue> getPaymentMethodConditionValues() {
        ArrayList<ConditionValue> conditionValues = new ArrayList<>();

        List<PaymentMethod> methods = PaymentMethodHelper.getAllPaymentMethods();

        for (PaymentMethod method : methods) {
            ConditionValue value = new ConditionValue();
            value.setUserEnteredValue(method.getName());
            conditionValues.add(value);
        }

        return conditionValues;
    }

    private static ArrayList<ConditionValue> getCategoryConditionValues() {
        ArrayList<ConditionValue> conditionValues = new ArrayList<>();

        List<Category> categories = CategoryHelper.getAllCategories();

        for (Category category : categories) {
            ConditionValue value = new ConditionValue();
            value.setUserEnteredValue(category.getName());
            conditionValues.add(value);
        }

        return conditionValues;
    }

    public static Request getDeleteRangeRequest(int sheetId) {
        Request request = new Request();
        GenericJson deleteRangeRequest = new GenericJson();
        deleteRangeRequest.set("shiftDimension", "ROWS");
        GridRange range = new GridRange();
        range.setStartColumnIndex(0);
        range.setStartRowIndex(1);
        range.setSheetId(sheetId);
        deleteRangeRequest.set("range", range);
        request.set("deleteRange", deleteRangeRequest);
        return request;
    }

    public static Request getCategoriesSheetsRequest(List<Category> categories) {
        Request request = new Request();
        AppendCellsRequest appendCellsRequest = new AppendCellsRequest();
        appendCellsRequest.setFields("*");
        appendCellsRequest.setSheetId(CATEGORIES_SHEET_ID);
        List<RowData> rows = new ArrayList<>();
        for (Category category : categories) {
            RowData rowData = new RowData();
            List<CellData> row = new ArrayList<>();

            CellData cellData = new CellData();
            cellData.setUserEnteredValue(
                    new ExtendedValue().setStringValue(String.valueOf(category.getId())));
            row.add(cellData);

            cellData = new CellData();
            cellData.setUserEnteredValue(
                    new ExtendedValue().setStringValue(category.getName()));
            row.add(cellData);

            rowData.setValues(row);
            rows.add(rowData);
        }
        appendCellsRequest.setRows(rows);
        request.setAppendCells(appendCellsRequest);
        return request;
    }

    public static Request getPaymentMethodsSheetsRequest(List<PaymentMethod> paymentMethods) {
        Request request = new Request();
        AppendCellsRequest appendCellsRequest = new AppendCellsRequest();
        appendCellsRequest.setFields("*");
        appendCellsRequest.setSheetId(PAYMENT_METHOD_SHEET_ID);
        List<RowData> rows = new ArrayList<>();
        for (PaymentMethod paymentMethod : paymentMethods) {
            RowData rowData = new RowData();
            List<CellData> row = new ArrayList<>();

            CellData cellData = new CellData();
            cellData.setUserEnteredValue(
                    new ExtendedValue().setStringValue(String.valueOf(paymentMethod.getId())));
            row.add(cellData);

            cellData = new CellData();
            cellData.setUserEnteredValue(
                    new ExtendedValue().setStringValue(paymentMethod.getName()));
            row.add(cellData);

            rowData.setValues(row);
            rows.add(rowData);
        }
        appendCellsRequest.setRows(rows);
        request.setAppendCells(appendCellsRequest);
        return request;
    }

    public static Request getAddNamedRangeRequest(int sheetId, String namedRangeId,
            String namedRangeName) {
        Request request = new Request();
        AddNamedRangeRequest addNamedRangeRequest = new AddNamedRangeRequest();
        NamedRange namedRange = new NamedRange();
        namedRange.setNamedRangeId(namedRangeId);
        namedRange.setName(namedRangeName);
        GridRange range = new GridRange();
        range.setSheetId(sheetId);
        range.setStartColumnIndex(0);
        range.setStartRowIndex(1);
        namedRange.setRange(range);
        addNamedRangeRequest.setNamedRange(namedRange);
        request.setAddNamedRange(addNamedRangeRequest);
        return request;
    }
}
