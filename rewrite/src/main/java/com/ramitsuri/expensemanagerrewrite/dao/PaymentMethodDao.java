package com.ramitsuri.expensemanagerrewrite.dao;

import com.ramitsuri.expensemanagerrewrite.entities.PaymentMethod;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

@Dao
public abstract class PaymentMethodDao {
    @Query("SELECT * FROM paymentmethod")
    public abstract List<PaymentMethod> getAll();

    @Transaction
    public void setAll(List<PaymentMethod> paymentMethods) {
        deleteAll();
        insertAll(paymentMethods);
    }

    @Insert
    public abstract void insertAll(List<PaymentMethod> paymentMethods);

    @Query("DELETE FROM paymentmethod")
    public abstract void deleteAll();
}
