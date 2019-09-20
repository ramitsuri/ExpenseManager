package com.ramitsuri.expensemanagerlegacy.helper;

import com.ramitsuri.expensemanagerlegacy.MainApplication;
import com.ramitsuri.expensemanagerlegacy.db.PaymentMethodDB;
import com.ramitsuri.expensemanagerlegacy.entities.PaymentMethod;

import java.util.List;

public class PaymentMethodHelper {

    private static PaymentMethodDB getDB() {
        return new PaymentMethodDB(MainApplication.getInstance());
    }

    public static List<PaymentMethod> getAllPaymentMethods() {
        return getDB().getAllPaymentMethods();
    }

    public static boolean addPaymentMethod(String paymentMethodName) {
        return getDB().setPaymentMethod(paymentMethodName);
    }

    public static boolean updatePaymentMethodName(int id, String name) {
        return getDB().setPaymentMethodName(id, name);
    }

    public static PaymentMethod getFirstPaymentMethod() {
        return getDB().getFirstPaymentMethod();
    }
}
