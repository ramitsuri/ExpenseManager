package com.ramitsuri.expensemanagerrewrite.data;

import android.content.Context;

import com.ramitsuri.expensemanagerrewrite.MainApplication;
import com.ramitsuri.expensemanagerrewrite.data.converter.BigDecimalConverter;
import com.ramitsuri.expensemanagerrewrite.data.dao.CategoryDao;
import com.ramitsuri.expensemanagerrewrite.data.dao.ExpenseDao;
import com.ramitsuri.expensemanagerrewrite.data.dao.PaymentMethodDao;
import com.ramitsuri.expensemanagerrewrite.entities.Category;
import com.ramitsuri.expensemanagerrewrite.entities.Expense;
import com.ramitsuri.expensemanagerrewrite.entities.PaymentMethod;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {Category.class, Expense.class, PaymentMethod.class}, version = 1)
@TypeConverters({BigDecimalConverter.class})
public abstract class ExpenseManagerDatabase extends RoomDatabase {

    private static volatile ExpenseManagerDatabase INSTANCE;
    private static final String DB_NAME = "expense_manager_db";

    public static ExpenseManagerDatabase getInstance(){
        if (INSTANCE == null) {
            synchronized (ExpenseManagerDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(MainApplication.getInstance(),
                            ExpenseManagerDatabase.class, DB_NAME)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract CategoryDao categoryDao();

    public abstract ExpenseDao expenseDao();

    public abstract PaymentMethodDao paymentMethodDao();
}
