package com.ramitsuri.expensemanager.data;

import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.data.converter.BigDecimalConverter;
import com.ramitsuri.expensemanager.data.dao.CategoryDao;
import com.ramitsuri.expensemanager.data.dao.ExpenseDao;
import com.ramitsuri.expensemanager.data.dao.PaymentMethodDao;
import com.ramitsuri.expensemanager.entities.Category;
import com.ramitsuri.expensemanager.entities.Expense;
import com.ramitsuri.expensemanager.entities.PaymentMethod;

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
