package com.ramitsuri.expensemanager.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CurrencyHelper {

    private static DecimalFormat sCurrencyFormatter;
    private static DecimalFormat sCurrencyFormatterNoSymbol;
    private static int sMaxCurrencyPrecision;
    private static int sRoundingStyle;

    private static final Locale CURRENCY_LOCALE = Locale.US;

    static {
        sCurrencyFormatter = (DecimalFormat)NumberFormat.getCurrencyInstance(CURRENCY_LOCALE);
        String currencySymbol = "";
        if (sCurrencyFormatter.getCurrency() != null) {
            currencySymbol = sCurrencyFormatter.getCurrency().getSymbol();
        }
        sCurrencyFormatter.setNegativePrefix("(" + currencySymbol);
        sCurrencyFormatter.setNegativeSuffix(")");

        sCurrencyFormatterNoSymbol =
                (DecimalFormat)NumberFormat.getCurrencyInstance(CURRENCY_LOCALE);
        sCurrencyFormatterNoSymbol.setNegativePrefix("(");
        sCurrencyFormatterNoSymbol.setNegativeSuffix(")");
        DecimalFormatSymbols noSymbolFormatSymbol = sCurrencyFormatter.getDecimalFormatSymbols();
        noSymbolFormatSymbol.setCurrencySymbol("");
        sCurrencyFormatterNoSymbol.setDecimalFormatSymbols(noSymbolFormatSymbol);

        sMaxCurrencyPrecision = sCurrencyFormatter.getMaximumFractionDigits();
        sRoundingStyle = BigDecimal.ROUND_HALF_EVEN;
    }

    private static BigDecimal roundForCalculation(BigDecimal amount) {
        if (amount == null) {
            return null;
        }
        return amount.setScale(sMaxCurrencyPrecision, sRoundingStyle);
    }

    @Nullable
    public static String formatForDisplay(boolean showSymbol, BigDecimal amount) {
        if (amount == null) {
            amount = BigDecimal.ZERO;
        }
        if (showSymbol) {
            return sCurrencyFormatter.format(roundForCalculation(amount));
        } else {
            return sCurrencyFormatterNoSymbol.format(roundForCalculation(amount));
        }
    }

    @NonNull
    public static BigDecimal string2Decimal(String amountString) {
        if (amountString.trim().startsWith("(") && amountString.trim().endsWith(")")) {
            return new BigDecimal(clearFormatting(amountString)).negate();
        } else {
            return new BigDecimal(clearFormatting(amountString));
        }
    }

    @NonNull
    public static String clearFormatting(String amountString) {
        return amountString.replaceAll("[^\\d.-]", "");
    }
}
