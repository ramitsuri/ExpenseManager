package com.ramitsuri.expensemanager.helper;

import com.google.api.services.sheets.v4.model.*;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.ramitsuri.expensemanager.db.DBConstants;

import java.util.ArrayList;
import java.util.List;

public class SheetsHelper {

    private static String SPREADSHEET_TITLE = "Expense Manager Expenses";
    private static String LOCALE = "en";

    public static Spreadsheet getNewSpreadsheet(){
        Spreadsheet spreadsheet = new Spreadsheet();
        SpreadsheetProperties properties = new SpreadsheetProperties();
        properties.setTitle(SPREADSHEET_TITLE);
        properties.setLocale(LOCALE);
        spreadsheet.setProperties(properties);

        Sheet sheet1 = new Sheet();
        sheet1.setProperties(getSheetProperties(3, 3, DBConstants.TABLE_CATEGORIES));
        List<GridData> data = new ArrayList<>();
        GridData gridData = new GridData();
        gridData.setStartColumn(0);
        gridData.setStartRow(0);
        gridData.setRowData(getRowDataCategories());
        data.add(gridData);
        sheet1.setData(data);

        Sheet sheet2 = new Sheet();
        sheet2.setProperties(getSheetProperties(2, 2, DBConstants.TABLE_PAYMENT_METHOD));
        data = new ArrayList<>();
        gridData = new GridData();
        gridData.setStartColumn(0);
        gridData.setStartRow(0);
        gridData.setRowData(getRowDataPaymentMethods());
        data.add(gridData);
        sheet2.setData(data);

        Sheet sheet3 = new Sheet();
        sheet3.setProperties(getSheetProperties(1, 1, DBConstants.TABLE_EXPENSES));
        data = new ArrayList<>();
        gridData = new GridData();
        gridData.setStartColumn(0);
        gridData.setStartRow(0);
        gridData.setRowData(getRowDataExpenses());
        data.add(gridData);
        sheet3.setData(data);

        Sheet sheet4 = new Sheet();
        sheet4.setProperties(getSheetProperties(4, 4, DBConstants.TABLE_BUDGET));
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

    private static SheetProperties getSheetProperties(int id, int index, String title){
        SheetProperties sheetProperties = new SheetProperties();
        sheetProperties.setSheetId(id);
        sheetProperties.setTitle(title);
        sheetProperties.setIndex(index);
        return sheetProperties;
    }

    private static Color getColor(float r, float b, float g){
        Color backgroundColor = new Color();
        backgroundColor.setRed(r);
        backgroundColor.setBlue(b);
        backgroundColor.setGreen(g);
        return backgroundColor;
    }

    private static CellFormat getCellFormat(){
        CellFormat cellFormat = new CellFormat();
        cellFormat.setBackgroundColor(getColor(223F, 103F, 50F));
        TextFormat textFormat = new TextFormat();
        textFormat.setBold(true);
        textFormat.setForegroundColor(getColor(1F, 1F, 1F));
        cellFormat.setTextFormat(textFormat);
        return cellFormat;
    }

    private static CellData getCellData(String columnName){
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
        return new ArrayList<RowData>(){{add(rowData);}};
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
        return new ArrayList<RowData>(){{add(rowData);}};
    }

    private static List<RowData> getRowDataCategories() {
        final RowData rowData = new RowData();
        List<CellData> cellData = new ArrayList<>();
        cellData.add(getCellData(DBConstants.COLUMN_CATEGORIES_ID));
        cellData.add(getCellData(DBConstants.COLUMN_CATEGORIES_NAME));
        rowData.setValues(cellData);
        return new ArrayList<RowData>(){{add(rowData);}};
    }

    private static List<RowData> getRowDataBudget() {
        final RowData rowData = new RowData();
        List<CellData> cellData = new ArrayList<>();
        cellData.add(getCellData(DBConstants.COLUMN_BUDGET_ID));
        cellData.add(getCellData(DBConstants.COLUMN_BUDGET_CATEGORY_IDS));
        cellData.add(getCellData(DBConstants.COLUMN_BUDGET_AMOUNT));
        rowData.setValues(cellData);
        return new ArrayList<RowData>(){{add(rowData);}};
    }
}
