package com.ramitsuri.expensemanager.constants;

import com.google.api.services.drive.DriveScopes;
import com.google.api.services.sheets.v4.SheetsScopes;

public class Constants {

    public static final String[] SCOPES = {SheetsScopes.SPREADSHEETS, SheetsScopes.DRIVE};
    public static final String[] SCOPES_LIMITED = {DriveScopes.DRIVE_FILE};

    public class BundleKeys {
        public static final String PICKER_VALUES = "picker_values";
        public static final String PICKED_ITEM = "picked_item";
        public static final String DATE_PICKER_YEAR = "date_picker_year";
        public static final String DATE_PICKER_MONTH = "date_picker_month";
        public static final String DATE_PICKER_DAY = "date_picker_day";
        public static final String SELECTED_EXPENSE = "selected_expense";
        public static final String SELECTED_BUDGET = "selected_budget";
        public static final String SELECTED_ENTITY = "selected_entity";
        public static final String ALL_CATEGORIES = "all_categories";
        public static final String SHEET_INFOS = "sheet_infos";
        public static final String SELECTED_SHEET_ID = "selected_sheet_id";
        public static final String ANALYSIS_EXPENSES = "analysis_expenses";
    }

    public class RequestCode {
        public static final int GOOGLE_SIGN_IN = 100;
    }

    public class Work {
        public static final String APP_NAME = "app_name";
        public static final String ACCOUNT_NAME = "account_name";
        public static final String ACCOUNT_TYPE = "account_type";
        public static final String SPREADSHEET_ID = "spreadsheet_id";
        public static final String SHEET_ID = "sheet_id";
        public static final String TYPE = "type";
    }

    public class Tag {
        public static final String ONE_TIME_BACKUP = "one_time_backup";
        public static final String ONE_TIME_ENTITIES_BACKUP = "one_time_entities_backup";
        public static final String SCHEDULED_BACKUP_LEGACY = "scheduled_backup";
        public static final String PERIODIC_BACKUP = "periodic_backup";
        public static final String PERIODIC_ENTITIES_BACKUP = "periodic_entities_backup";
        public static final String ONE_TIME_SYNC = "one_time_sync";
        public static final String ONE_TIME_EXPENSE_SYNC = "one_time_expense_sync";
        public static final String ONE_TIME_CREATE_SPREADSHEET = "one_time_create_spreadsheet";
    }

    public class Range {
        public static final String CATEGORIES = SheetNames.ENTITIES + "!C1:C20";
        public static final String USER_ENTERED_CATEGORIES = "=" + CATEGORIES;
        public static final String PAYMENT_METHODS = SheetNames.ENTITIES + "!A1:A20";
        public static final String USER_ENTERED_PAYMENT_METHODS = "=" + PAYMENT_METHODS;
        public static final String CATEGORIES_PAYMENT_METHODS = SheetNames.ENTITIES + "!A1:C20";
        public static final String BUDGETS = SheetNames.ENTITIES + "!G1:M20";
    }

    public class SheetNames {
        public static final String ENTITIES = "Entities";
        public static final String TEMPLATE = "Template";
        public static final String CALCULATOR = "Calculator";
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
    }

    public class Sheets {
        public static final String DATE = "DATE";
        public static final String DATE_PATTERN = "M/d/yyyy";
        public static final String DATE_IS_VALID = "DATE_IS_VALID";
        public static final String ONE_OF_LIST = "ONE_OF_LIST";
        public static final String ONE_OF_RANGE = "ONE_OF_RANGE";
        public static final String FLAG = "FLAG";
        public static final String EXPENSE_RANGE = "!A:G";
    }

    public class Basic {
        public static final int UNDEFINED = -1;
        public static final String EMPTY_BUDGET = "-";
        public static final int BUDGET_CATEGORY_COUNT = 5;
        public static final String CALCULATOR_ALL = "CALCULATOR_ALL";
    }
}
