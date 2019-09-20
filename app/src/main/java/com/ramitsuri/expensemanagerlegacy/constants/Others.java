package com.ramitsuri.expensemanagerlegacy.constants;

import com.google.api.services.sheets.v4.SheetsScopes;

public class Others {
    public static final String EXPENSE_VIEW_TYPE = "expense_view_type";

    public static final String DATE_PICKER_YEAR = "date_picker_year";

    public static final String DATE_PICKER_MONTH = "date_picker_month";

    public static final String DATE_PICKER_DAY = "date_picker_day";

    public static final String PAYMENT_METHOD_PICKER_METHOD = "payment_method_picker_method";

    public static final String CATEGORY_PICKER_CATEGORY = "category_picker_category";

    public static final String DEFAULT_CURRENCY = "USD - $";

    public static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    public static final int REQUEST_ACCOUNT_PICKER = 1000;
    public static final int REQUEST_AUTHORIZATION = 1001;
    public static final String[] SCOPES = { SheetsScopes.SPREADSHEETS, SheetsScopes.DRIVE};
}
