package com.ramitsuri.expensemanager.data.dummy;

import com.ramitsuri.expensemanager.entities.PaymentMethod;

import java.util.ArrayList;
import java.util.List;

public class PaymentMethods {
    public static String[] getPaymentMethods() {
        return new String[] {
                "Discover",
                "Cash",
                "Chase",
                "Amazon",
                "Chase CH",
                "Master 53",
                "Disney",
                "AMEX",
                "Card1",
                "Card2",
                "Card3",
                "Card4",
                "Card5",
        };
    }

    public static List<PaymentMethod> getAllPaymentMethods() {
        List<PaymentMethod> paymentMethods = new ArrayList<>();
        for (String paymentMethodName : getPaymentMethods()) {
            PaymentMethod paymentMethod = new PaymentMethod();
            paymentMethod.setName(paymentMethodName);
            paymentMethods.add(paymentMethod);
        }
        return paymentMethods;
    }
}
