package com.ramitsuri.expensemanager.ui.fragment;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;

@IntDef({SetupCurrentStep.CATEGORIES, SetupCurrentStep.PAYMENT_METHODS, SetupCurrentStep.BUDGETS})
@Retention(RetentionPolicy.SOURCE)
public @interface SetupCurrentStep {
    int CATEGORIES = 0;
    int PAYMENT_METHODS = 1;
    int BUDGETS = 2;
}
