package com.ramitsuri.expensemanager.entities;

import android.text.TextUtils;

import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.constants.Constants;
import com.ramitsuri.expensemanager.utils.AppHelper;
import com.ramitsuri.expensemanager.utils.DateHelper;

import java.util.Calendar;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Filter {
    private Calendar mCalendar;

    private int mMonthIndex;
    private Long mFromDateTime;
    private Long mToDateTime;
    private Boolean mIsIncome;
    private List<String> mCategories;
    private List<String> mPaymentMethods;
    private Boolean mIsSynced;
    private Boolean mIsStarred;

    public Filter() {
        mIsIncome = false;
        mCalendar = Calendar.getInstance(AppHelper.getTimeZone());
    }

    public Filter setMonthIndex(int monthIndex) {
        mMonthIndex = monthIndex;
        onMonthIndexSet(monthIndex);
        return this;
    }

    public Filter setCurrentMonth() {
        mMonthIndex = Constants.Basic.UNDEFINED;
        onMonthIndexSet(mMonthIndex);
        return this;
    }

    @Nullable
    public Long getFromDateTime() {
        return mFromDateTime;
    }

    @Nullable
    public Long getToDateTime() {
        return mToDateTime;
    }

    @Nullable
    public Boolean getIsIncome() {
        return mIsIncome;
    }

    public Filter setIsIncome(boolean isIncome) {
        mIsIncome = isIncome;
        return this;
    }

    public List<String> getCategories() {
        return mCategories;
    }

    public Filter setCategories(List<String> categories) {
        mCategories = categories;
        return this;
    }

    public List<String> getPaymentMethods() {
        return mPaymentMethods;
    }

    public Filter setPaymentMethods(List<String> paymentMethods) {
        mPaymentMethods = paymentMethods;
        return this;
    }

    public Boolean getIsSynced() {
        return mIsSynced;
    }

    public Filter setSynced(Boolean synced) {
        mIsSynced = synced;
        return this;
    }

    public Boolean getIsStarred() {
        return mIsStarred;
    }

    public Filter setStarred(Boolean starred) {
        mIsStarred = starred;
        return this;
    }

    @Override
    @Nonnull
    public String toString() {
        return "Filter{" +
                "mFromDateTime=" + mFromDateTime +
                ", mToDateTime=" + mToDateTime +
                ", mIsIncome=" + mIsIncome +
                ", mCategories=" + mCategories +
                ", mPaymentMethods=" + mPaymentMethods +
                ", mIsSynced=" + mIsSynced +
                ", mIsStarred=" + mIsStarred +
                '}';
    }

    @Nullable
    public String toFriendlyString() {
        StringBuilder sb = new StringBuilder();
        if (mIsIncome != null && mIsIncome) {
            sb.append(MainApplication.getInstance().getResources()
                    .getString(R.string.expenses_filter_incomes));
        }

        if (!TextUtils.isEmpty(sb.toString())) {
            sb.append(" ");
        }

        if (mFromDateTime != null && mToDateTime != null) {
            sb.append(MainApplication.getInstance().getResources()
                    .getString(R.string.expenses_filter_date_range_format,
                            DateHelper.getFriendlyDate(mFromDateTime),
                            DateHelper.getFriendlyDate(mToDateTime)));
        }
        return sb.toString();
    }

    private void onMonthIndexSet(int monthIndex) {
        if (mMonthIndex != Constants.Basic.UNDEFINED) {
            mCalendar.set(Calendar.MONTH, monthIndex);
        }

        // First day of month - 00:00:00 001ms
        mCalendar.set(Calendar.DAY_OF_MONTH, mCalendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        mCalendar.set(Calendar.HOUR_OF_DAY, 0);
        mCalendar.set(Calendar.MINUTE, 0);
        mCalendar.set(Calendar.SECOND, 0);
        mCalendar.set(Calendar.MILLISECOND, 0);
        mFromDateTime = mCalendar.getTimeInMillis();

        // Last Day of month - 23:59:59 999ms
        mCalendar.set(Calendar.DAY_OF_MONTH, mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        mCalendar.set(Calendar.HOUR_OF_DAY, 23);
        mCalendar.set(Calendar.MINUTE, 59);
        mCalendar.set(Calendar.SECOND, 59);
        mCalendar.set(Calendar.MILLISECOND, 999);
        mToDateTime = mCalendar.getTimeInMillis();
    }
}
