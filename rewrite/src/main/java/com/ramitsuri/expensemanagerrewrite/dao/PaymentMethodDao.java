package com.ramitsuri.expensemanagerrewrite.dao;

import com.ramitsuri.expensemanagerrewrite.entities.PaymentMethod;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface PaymentMethodDao {
    @Query("SELECT * FROM paymentmethod")
    List<PaymentMethod> getAll();

    @Insert
    void insertAll(List<PaymentMethod> paymentMethods);

    @Query("DELETE FROM paymentmethod")
    void deleteAll();
}
