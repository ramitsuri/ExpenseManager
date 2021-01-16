package com.ramitsuri.expensemanager.constants;

import com.google.api.services.drive.DriveScopes;
import com.google.api.services.sheets.v4.SheetsScopes;

public class Constants {

    public static final String[] SCOPES = {SheetsScopes.SPREADSHEETS, SheetsScopes.DRIVE};
    public static final String[] SCOPES_LIMITED = {DriveScopes.DRIVE_FILE};

    public class BundleKeys {
        public static final String DATE_PICKER_YEAR = "date_picker_year";
        public static final String DATE_PICKER_MONTH = "date_picker_month";
        public static final String DATE_PICKER_DAY = "date_picker_day";
        public static final String SELECTED_EXPENSE = "selected_expense";
        public static final String ENABLE_SHARED = "ENABLE_SHARED";
        public static final String SELECTED_BUDGET = "selected_budget";
        public static final String SELECTED_ENTITY = "selected_entity";
        public static final String ALL_CATEGORIES = "all_categories";
        public static final String ANALYSIS_EXPENSES = "analysis_expenses";
        public static final String FILTER = "filter";
        public static final String RECUR_TYPE = "recur_type";
    }

    public class RequestCode {
        public static final int GOOGLE_SIGN_IN = 100;
    }

    public class Work {
        public static final String TYPE = "type";
    }

    public class Tag {
        public static final String RECURRING_EXPENSES_RUNNER = "recurring_expenses_runner";
    }

    public class SystemTheme {
        public static final String LIGHT = "light";
        public static final String DARK = "dark";
        public static final String SYSTEM_DEFAULT = "system_default";
        public static final String BATTERY_SAVER = "battery_saver";
    }

    public class AddExpenseMode {
        public static final int ADD = 0;
        public static final int EDIT = 1;
    }

    public class LogResult {
        public static final String SUCCESS = "success";
        public static final String FAILURE = "failure";
        public static final String ERROR = "error";
    }

    public class Sheets {
        public static final String FLAG = "FLAG";
    }

    public class Basic {
        public static final String EMPTY_BUDGET = "-";
        public static final int BUDGET_CATEGORY_COUNT = 5;
        public static final String CALCULATOR_ALL = "CALCULATOR_ALL";
    }
}
