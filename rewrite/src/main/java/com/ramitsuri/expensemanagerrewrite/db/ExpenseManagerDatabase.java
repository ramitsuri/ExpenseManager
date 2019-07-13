package com.ramitsuri.expensemanagerrewrite.db;

import com.ramitsuri.expensemanagerrewrite.converter.BigDecimalConverter;
import com.ramitsuri.expensemanagerrewrite.dao.CategoryDao;
import com.ramitsuri.expensemanagerrewrite.dao.ExpenseDao;
import com.ramitsuri.expensemanagerrewrite.dao.PaymentMethodDao;
import com.ramitsuri.expensemanagerrewrite.entities.Category;
import com.ramitsuri.expensemanagerrewrite.entities.Expense;
import com.ramitsuri.expensemanagerrewrite.entities.PaymentMethod;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {Category.class, Expense.class, PaymentMethod.class}, version = 1)
@TypeConverters({BigDecimalConverter.class})
public abstract class ExpenseManagerDatabase extends RoomDatabase {

    public abstract CategoryDao categoryDao();

    public abstract ExpenseDao expenseDao();

    public abstract PaymentMethodDao paymentMethodDao();
}
