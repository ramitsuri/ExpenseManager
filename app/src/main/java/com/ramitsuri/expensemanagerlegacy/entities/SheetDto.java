package com.ramitsuri.expensemanagerlegacy.entities;

public class SheetDto {
    private int mSheetId;
    private String mSheetName;

    public SheetDto(int sheetId, String sheetName) {
        mSheetId = sheetId;
        mSheetName = sheetName;
    }

    @Override
    public String toString() {
        return mSheetName + " : " + String.valueOf(mSheetId) + "\n";
    }
}
