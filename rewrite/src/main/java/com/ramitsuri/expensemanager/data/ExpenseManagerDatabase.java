package com.ramitsuri.expensemanager.data;

import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.data.converter.BigDecimalConverter;
import com.ramitsuri.expensemanager.data.converter.ListConverter;
import com.ramitsuri.expensemanager.data.dao.BudgetDao;
import com.ramitsuri.expensemanager.data.dao.CategoryDao;
import com.ramitsuri.expensemanager.data.dao.EditedSheetDao;
import com.ramitsuri.expensemanager.data.dao.ExpenseDao;
import com.ramitsuri.expensemanager.data.dao.LogDao;
import com.ramitsuri.expensemanager.data.dao.PaymentMethodDao;
import com.ramitsuri.expensemanager.data.dao.SheetDao;
import com.ramitsuri.expensemanager.entities.Budget;
import com.ramitsuri.expensemanager.entities.Category;
import com.ramitsuri.expensemanager.entities.EditedSheet;
import com.ramitsuri.expensemanager.entities.Expense;
import com.ramitsuri.expensemanager.entities.Log;
import com.ramitsuri.expensemanager.entities.PaymentMethod;
import com.ramitsuri.expensemanager.entities.SheetInfo;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {Category.class, Expense.class, PaymentMethod.class,
        Log.class, SheetInfo.class, Budget.class, EditedSheet.class},
        version = 8, exportSchema = true)
@TypeConverters({BigDecimalConverter.class, ListConverter.class})
public abstract class ExpenseManagerDatabase extends RoomDatabase {

    private static volatile ExpenseManagerDatabase INSTANCE;
    private static final String DB_NAME = "expense_manager_db";

    public static ExpenseManagerDatabase getInstance() {
        if (INSTANCE == null) {
            synchronized (ExpenseManagerDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(MainApplication.getInstance(),
                            ExpenseManagerDatabase.class, DB_NAME)
                            .addMigrations(DatabaseMigration.MIGRATION_1_2)
                            .addMigrations(DatabaseMigration.MIGRATION_2_3)
                            .addMigrations(DatabaseMigration.MIGRATION_3_4)
                            .addMigrations(DatabaseMigration.MIGRATION_4_5)
                            .addMigrations(DatabaseMigration.MIGRATION_5_6)
                            .addMigrations(DatabaseMigration.MIGRATION_6_7)
                            .addMigrations(DatabaseMigration.MIGRATION_7_8)
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

    public abstract SheetDao sheetDao();

    public abstract BudgetDao budgetDao();

    public abstract EditedSheetDao editedSheetDao();
}
