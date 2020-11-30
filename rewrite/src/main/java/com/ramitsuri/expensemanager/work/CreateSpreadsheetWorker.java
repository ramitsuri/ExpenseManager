package com.ramitsuri.expensemanager.work;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;

import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.constants.Constants;
import com.ramitsuri.expensemanager.data.ExpenseManagerDatabase;
import com.ramitsuri.expensemanager.data.repository.SheetRepository;
import com.ramitsuri.expensemanager.entities.Budget;
import com.ramitsuri.expensemanager.entities.Category;
import com.ramitsuri.expensemanager.entities.PaymentMethod;
import com.ramitsuri.expensemanager.entities.SheetInfo;
import com.ramitsuri.expensemanager.utils.AppHelper;
import com.ramitsuri.expensemanager.utils.DateHelper;
import com.ramitsuri.sheetscore.consumerResponse.CreateSpreadsheetConsumerResponse;
import com.ramitsuri.sheetscore.consumerResponse.SheetMetadata;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.work.WorkerParameters;

public class CreateSpreadsheetWorker extends BaseWorker {

    public CreateSpreadsheetWorker(@NonNull Context context,
            @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String workType = getInputData().getString(Constants.Work.TYPE);

        SheetRepository repository = MainApplication.getInstance().getSheetRepository();
        if (repository == null) {
            onFailure(workType, "Sheet repo is null");
            return Result.failure();
        }

        // Entities
        List<Category> categories =
                ExpenseManagerDatabase.getInstance().categoryDao().getAll();
        List<PaymentMethod> paymentMethods =
                ExpenseManagerDatabase.getInstance().paymentMethodDao().getAll();
        List<Budget> budgets =
                ExpenseManagerDatabase.getInstance().budgetDao().getAll();
        if (categories == null || categories.size() == 0 ||
                paymentMethods == null || paymentMethods.size() == 0) {
            onFailure(workType, "Categories, or payment methods are empty. This shouldn't happen");
            return Result.failure();
        }
        List<String> months = AppHelper.getMonths();

        // Spreadsheet Name
        LocalDate localDate = DateHelper.getLocalDate(new Date(System.currentTimeMillis()));
        int year = DateHelper.getYearFromDate(localDate);
        String spreadsheetName = resources().getString(R.string.spreadsheet_title_format, year);

        // Entities Sheet Name
        String entitiesSheetName = resources().getString(R.string.spreadsheet_entities_sheet_title);

        // Create spreadsheet
        CreateSpreadsheetConsumerResponse response = repository
                .getCreateSpreadsheetResponse(spreadsheetName, entitiesSheetName,
                        paymentMethods, categories, months, budgets);
        if (!TextUtils.isEmpty(response.getSpreadsheetId()) &&
                response.getSheetMetadataList() != null) {
            AppHelper.setSpreadsheetId(response.getSpreadsheetId());
            List<SheetInfo> sheetInfos = new ArrayList<>();
            for (SheetMetadata sheetMetadata : response.getSheetMetadataList()) {
                sheetInfos.add(new SheetInfo(sheetMetadata));
            }
            ExpenseManagerDatabase.getInstance().sheetDao().setAll(sheetInfos);
            onSuccess(workType, "Spreadsheet created successfully, id and sheets info saved");
            return Result.success();
        } else if (response.getException() != null) {
            onFailure(workType, response.getException().getMessage());
            return Result.failure();
        }

        onFailure(workType, "Unknown reason");
        return Result.failure();
    }

    private Resources resources() {
        return MainApplication.getInstance().getResources();
    }
}
