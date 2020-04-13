package com.ramitsuri.expensemanager.data.dummy;

import com.ramitsuri.expensemanager.entities.SheetInfo;
import com.ramitsuri.sheetscore.consumerResponse.SheetMetadata;

import java.util.ArrayList;
import java.util.List;

public class SheetInfos {
    public static List<SheetInfo> getSheetInfos() {
        List<SheetInfo> sheetInfos = new ArrayList<>();
        sheetInfos.add(new SheetInfo(new SheetMetadata(12342, "Jan")));
        sheetInfos.add(new SheetInfo(new SheetMetadata(12344, "Feb")));
        sheetInfos.add(new SheetInfo(new SheetMetadata(12345, "Mar")));
        sheetInfos.add(new SheetInfo(new SheetMetadata(12346, "Apr")));
        sheetInfos.add(new SheetInfo(new SheetMetadata(12347, "May")));
        sheetInfos.add(new SheetInfo(new SheetMetadata(12348, "Jun")));
        sheetInfos.add(new SheetInfo(new SheetMetadata(12349, "Jul")));
        sheetInfos.add(new SheetInfo(new SheetMetadata(12350, "Aug")));
        sheetInfos.add(new SheetInfo(new SheetMetadata(12351, "Sep")));
        sheetInfos.add(new SheetInfo(new SheetMetadata(12352, "Oct")));
        sheetInfos.add(new SheetInfo(new SheetMetadata(12353, "Nov")));
        sheetInfos.add(new SheetInfo(new SheetMetadata(12354, "Dec")));
        return sheetInfos;
    }

    public static List<String> getMonths() {
        List<String> months = new ArrayList<>();
        months.add("Jan");
        months.add("Feb");
        months.add("Mar");
        months.add("Apr");
        months.add("May");
        months.add("Jun");
        months.add("Jul");
        months.add("Aug");
        months.add("Sep");
        months.add("Oct");
        months.add("Nov");
        months.add("Dec");
        return months;
    }
}
