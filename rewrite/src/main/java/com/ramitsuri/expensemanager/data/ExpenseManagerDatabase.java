package com.ramitsuri.expensemanager.data;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.data.converter.BigDecimalConverter;
import com.ramitsuri.expensemanager.data.converter.ListConverter;
import com.ramitsuri.expensemanager.data.dao.BudgetDao;
import com.ramitsuri.expensemanager.data.dao.CategoryDao;
import com.ramitsuri.expensemanager.data.dao.ExpenseDao;
import com.ramitsuri.expensemanager.data.dao.LogDao;
import com.ramitsuri.expensemanager.data.dao.PaymentMethodDao;
import com.ramitsuri.expensemanager.data.dao.RecurringExpenseInfoDao;
import com.ramitsuri.expensemanager.entities.Budget;
import com.ramitsuri.expensemanager.entities.Category;
import com.ramitsuri.expensemanager.entities.Expense;
import com.ramitsuri.expensemanager.entities.Log;
import com.ramitsuri.expensemanager.entities.PaymentMethod;
import com.ramitsuri.expensemanager.entities.RecurringExpenseInfo;

@Database(entities = {Category.class, Expense.class, PaymentMethod.class,
        Log.class, Budget.class, RecurringExpenseInfo.class},
        version = 12, exportSchema = true)
@TypeConverters({BigDecimalConverter.class, ListConverter.class})
public abstract class ExpenseManagerDatabase extends RoomDatabase {

    private static volatile ExpenseManagerDatabase INSTANCE;
    private static final String DB_NAME = "expense_manager_db";

    @NonNull
    public static ExpenseManagerDatabase getInstance() {
        if (INSTANCE == null) {
            synchronized (ExpenseManagerDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(MainApplication.getInstance(),
                            ExpenseManagerDatabase.class, DB_NAME)
                            .addMigrations(
                                    DatabaseMigration.MIGRATION_1_2,
                                    DatabaseMigration.MIGRATION_2_3,
                                    DatabaseMigration.MIGRATION_3_4,
                                    DatabaseMigration.MIGRATION_4_5,
                                    DatabaseMigration.MIGRATION_5_6,
                                    DatabaseMigration.MIGRATION_6_7,
                                    DatabaseMigration.MIGRATION_7_8,
                                    DatabaseMigration.MIGRATION_8_9,
                                    DatabaseMigration.MIGRATION_9_10,
                                    DatabaseMigration.MIGRATION_10_11,
                                    DatabaseMigration.MIGRATION_11_12)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract CategoryDao categoryDao();

    public abstract ExpenseDao expenseDao();

    public abstract PaymentMethodDao paymentMethodDao();

    public abstract LogDao logDao();

    public abstract BudgetDao budgetDao();

    public abstract RecurringExpenseInfoDao recurringExpenseDao();
}
