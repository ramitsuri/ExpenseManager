package com.ramitsuri.expensemanager.utils;

public class SharedExpenseHelper {
    public static SharedExpenseManager getSharedExpenseManager(
            SharedExpenseManager.Callbacks callbacks) {
        SharedExpenseManager.Attributes attributes = new SharedExpenseManager.Attributes(
                getSharedCollectionName(),
                getThisSource()
        );
        return new SharedExpenseManager(attributes, callbacks);
    }

    public static String getSharedCollectionName() {
        return SecretMessageHelper.getSharedCollectionName();
    }

    public static String getThisSource() {
        return SecretMessageHelper.getSharedThisSource();
    }

    public static String getOtherSource() {
        return SecretMessageHelper.getSharedOtherSource();
    }
}
