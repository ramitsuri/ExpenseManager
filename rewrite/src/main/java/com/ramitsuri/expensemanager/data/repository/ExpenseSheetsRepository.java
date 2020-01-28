package com.ramitsuri.expensemanager.data.repository;

import com.ramitsuri.expensemanager.AppExecutors;
import com.ramitsuri.expensemanager.data.ExpenseManagerDatabase;
import com.ramitsuri.expensemanager.entities.Expense;
import com.ramitsuri.expensemanager.entities.SheetInfo;
import com.ramitsuri.sheetscore.consumerResponse.RangeConsumerResponse;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import static com.ramitsuri.expensemanager.Constants.Sheets.EXPENSE_RANGE;

public class ExpenseSheetsRepository extends ExpenseRepository {

    private SheetRepository mSheetRepository;

    public ExpenseSheetsRepository(AppExecutors executors, ExpenseManagerDatabase database,
            SheetRepository sheetRepository) {
        super(executors, database);
        mSheetRepository = sheetRepository;
    }

    public LiveData<List<Expense>> getExpensesFromSheet(@Nonnull final SheetInfo sheetInfo) {
        final MutableLiveData<List<Expense>> expenses = new MutableLiveData<>();
        mExecutors.networkIO().execute(new Runnable() {
            @Override
            public void run() {
                List<Expense> values = new ArrayList<>();
                String range = sheetInfo.getSheetName() + EXPENSE_RANGE;
                RangeConsumerResponse response = mSheetRepository.getRangeDataResponse(range);
                for (List<Object> objects : response.getObjectLists()) {
                    if (objects.size() >= 6) {
                        Expense expense = new Expense(objects, sheetInfo.getSheetId());
                        values.add(expense);
                    }
                }
                expenses.postValue(values);
            }
        });
        return expenses;
    }
}
