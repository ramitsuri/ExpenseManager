package com.ramitsuri.expensemanager.entities;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Pair;
import android.util.SparseArray;

import com.ramitsuri.expensemanager.constants.Constants;
import com.ramitsuri.expensemanager.constants.intDefs.RecordType;
import com.ramitsuri.expensemanager.data.utils.SqlBuilder;
import com.ramitsuri.expensemanager.utils.AppHelper;
import com.ramitsuri.expensemanager.utils.DateHelper;
import com.ramitsuri.expensemanager.utils.ObjectHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import androidx.sqlite.db.SimpleSQLiteQuery;

import timber.log.Timber;

public class Filter implements Parcelable {
    private SparseArray<Pair<Long, Long>> mDateTimes;
    private Boolean mIsIncome;
    private List<String> mCategories;
    private List<String> mPaymentMethods;
    private Boolean mIsSynced;
    private Boolean mIsStarred;
    @RecordType
    private String mRecordType;

    public Filter() {
    }

    protected Filter(Parcel in) {
        int[] months = in.createIntArray();
        if (months != null) {
            for (int monthIndex : months) {
                if (monthIndex == Constants.Basic.UNDEFINED) {
                    continue;
                }
                addMonthIndex(monthIndex);
            }
        }
        String isIncome = in.readString();
        if (isIncome != null) {
            mIsIncome = isIncome.equals(TRUE);
        }
        mCategories = in.createStringArrayList();
        mPaymentMethods = in.createStringArrayList();
        String isSynced = in.readString();
        if (isSynced != null) {
            mIsSynced = isSynced.equals(TRUE);
        }
        String isStarred = in.readString();
        if (isStarred != null) {
            mIsStarred = isStarred.equals(TRUE);
        }
        mRecordType = in.readString();
    }

    public static final Creator<Filter> CREATOR = new Creator<Filter>() {
        @Override
        public Filter createFromParcel(Parcel in) {
            return new Filter(in);
        }

        @Override
        public Filter[] newArray(int size) {
            return new Filter[size];
        }
    };

    public Filter getDefault() {
        return setIsIncome(false)
                .addCurrentMonth(AppHelper.getTimeZone());
    }

    public Filter clear() {
        mDateTimes = null;
        mIsIncome = null;
        mCategories = null;
        mPaymentMethods = null;
        mIsStarred = null;
        mIsSynced = null;
        mRecordType = RecordType.MONTHLY;

        return this;
    }

    public Filter addMonthIndex(int monthIndex) {
        return addMonthIndex(monthIndex, AppHelper.getTimeZone());
    }

    public Filter addMonthIndex(int monthIndex, TimeZone timeZone) {
        if (mDateTimes == null) {
            mDateTimes = new SparseArray<>();
        }
        onMonthAddRequested(monthIndex, timeZone);
        return this;
    }

    public Filter addCurrentMonth(TimeZone timeZone) {
        addMonthIndex(Constants.Basic.UNDEFINED, timeZone);
        return this;
    }

    public Filter removeMonthIndex(int monthIndex) {
        onMonthRemoveRequested(monthIndex);
        return this;
    }

    @Nullable
    public SparseArray<Pair<Long, Long>> getDateTimes() {
        return mDateTimes;
    }

    @Nullable
    public Boolean getIsIncome() {
        return mIsIncome;
    }

    public Filter setIsIncome(Boolean isIncome) {
        mIsIncome = isIncome;
        return this;
    }

    public List<String> getCategories() {
        return mCategories;
    }

    public Filter addCategory(String category) {
        if (mCategories == null) {
            mCategories = new ArrayList<>();
        }
        mCategories.add(category);
        return this;
    }

    public Filter removeCategory(String category) {
        if (mCategories != null) {
            mCategories.remove(category);
        }
        return this;
    }

    public List<String> getPaymentMethods() {
        return mPaymentMethods;
    }

    public Filter addPaymentMethod(String paymentMethod) {
        if (mPaymentMethods == null) {
            mPaymentMethods = new ArrayList<>();
        }
        mPaymentMethods.add(paymentMethod);
        return this;
    }

    public Filter removePaymentMethod(String paymentMethod) {
        if (mPaymentMethods != null) {
            mPaymentMethods.remove(paymentMethod);
        }
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

    public Filter setIsStarred(Boolean starred) {
        mIsStarred = starred;
        return this;
    }

    @RecordType
    public String getRecordType() {
        return mRecordType;
    }

    public Filter setRecordType(@RecordType String recordType) {
        mRecordType = recordType;
        return this;
    }

    @Nonnull
    @Override
    public String toString() {
        return "Filter{" +
                "mDateTimes=" + mDateTimes +
                ", mIsIncome=" + mIsIncome +
                ", mCategories=" + mCategories +
                ", mPaymentMethods=" + mPaymentMethods +
                ", mIsSynced=" + mIsSynced +
                ", mIsStarred=" + mIsStarred +
                ", mRecordType=" + mRecordType +
                '}';
    }

    @Nullable
    public String toFriendlyString() {
        String friendlyString = null;
        if (mDateTimes != null && mDateTimes.size() == 1) {
            friendlyString = DateHelper.getMonth(mDateTimes.valueAt(0).first);
        }
        if (mIsSynced != null || mIsStarred != null ||
                (mCategories != null && mCategories.size() > 0) ||
                (mPaymentMethods != null && mPaymentMethods.size() > 0) ||
                (mRecordType == null || mRecordType.equals(RecordType.ANNUAL))) {
            return null;
        }
        return friendlyString;
    }

    private void onMonthAddRequested(int monthIndex, TimeZone timeZone) {
        Calendar cal = Calendar.getInstance(timeZone);
        if (monthIndex == Constants.Basic.UNDEFINED) {
            monthIndex = cal.get(Calendar.MONTH);
        }
        if (monthIndex < 0 || monthIndex > 11) {
            Timber.i("Invalid month index");
            return;
        }
        if (mDateTimes.get(monthIndex) != null) {
            Timber.i("Month already contained in filter");
            return;
        }

        cal.set(Calendar.MONTH, monthIndex);

        // First day of month at 00:00:00 001ms
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long fromDateTime = cal.getTimeInMillis();

        // Last Day of month at 23:59:59 999ms
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        long toDateTime = cal.getTimeInMillis();
        Pair<Long, Long> dateTime = new Pair<>(fromDateTime, toDateTime);

        mDateTimes.put(monthIndex, dateTime);
    }

    private void onMonthRemoveRequested(int monthIndex) {
        if (mDateTimes == null) {
            Timber.i("mDateTimes null");
            return;
        }
        if (monthIndex < 0 || monthIndex > 11) {
            Timber.i("Invalid month index");
            return;
        }

        mDateTimes.remove(monthIndex);
    }

    public SimpleSQLiteQuery toQuery() {
        SqlBuilder builder = new SqlBuilder();
        builder.select("*")
                .from(Expense.DB.TABLE);
        // Is income
        if (getIsIncome() != null) {
            builder.where()
                    .column(Expense.DB.COL_INCOME)
                    .equal(getIsIncome() ? 1 : 0);
        }
        // Date ranges
        if (getDateTimes() != null && getDateTimes().size() > 0) {
            builder.whereOrAnd()
                    .between(Expense.DB.COL_DATE_TIME,
                            ObjectHelper.sparseArrayToList(getDateTimes()));
        }
        // Categories
        if (getCategories() != null && getCategories().size() > 0) {
            builder.whereOrAnd()
                    .column(Expense.DB.COL_CATEGORY)
                    .in(getCategories());
        }

        // Payment Methods
        if (getPaymentMethods() != null && getPaymentMethods().size() > 0) {
            builder.whereOrAnd()
                    .column(Expense.DB.COL_PAYMENT)
                    .in(getPaymentMethods());
        }

        // Synced
        if (getIsSynced() != null) {
            builder.whereOrAnd()
                    .column(Expense.DB.COL_SYNCED)
                    .equal(getIsSynced() ? 1 : 0);
        }

        // Starred
        if (getIsStarred() != null) {
            builder.whereOrAnd()
                    .column(Expense.DB.COL_STARRED)
                    .equal(getIsStarred() ? 1 : 0);
        }

        // Record Type
        if (getRecordType() != null) {
            builder.whereOrAnd()
                    .column(Expense.DB.COL_RECORD_TYPE)
                    .equal(getRecordType());
        }
        SimpleSQLiteQuery query =
                new SimpleSQLiteQuery(builder.toString(), builder.getArgs().toArray());
        Timber.i("Generated query is: [%s]", query.getSql());
        return query;
    }

    public SimpleSQLiteQuery toUpdateSyncedQuery() {
        SqlBuilder builder = new SqlBuilder();
        builder.update(Expense.DB.TABLE)
                .setEqualTo(Expense.DB.COL_SYNCED, 0);

        // Date ranges
        if (getDateTimes() != null && getDateTimes().size() > 0) {
            builder.whereOrAnd()
                    .between(Expense.DB.COL_DATE_TIME,
                            ObjectHelper.sparseArrayToList(getDateTimes()));
        }
        SimpleSQLiteQuery query =
                new SimpleSQLiteQuery(builder.toString(), builder.getArgs().toArray());
        Timber.i("Generated query is: [%s]", query.getSql());
        return query;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (mDateTimes != null) {
            int[] array = new int[mDateTimes.size()];
            for (int i = 0; i < mDateTimes.size(); i++) {
                if (mDateTimes.get(mDateTimes.keyAt(i)) == null) {
                    array[i] = Constants.Basic.UNDEFINED;
                } else {
                    array[i] = mDateTimes.keyAt(i);
                }
            }
            dest.writeIntArray(array);
        } else {
            dest.writeIntArray(null);
        }
        if (mIsIncome != null) {
            if (mIsIncome) {
                dest.writeString(TRUE);
            } else {
                dest.writeString(FALSE);
            }
        } else {
            dest.writeString(null);
        }
        if (mCategories != null) {
            dest.writeStringList(mCategories);
        } else {
            dest.writeStringList(null);
        }
        if (mPaymentMethods != null) {
            dest.writeStringList(mPaymentMethods);
        } else {
            dest.writeStringList(null);
        }
        if (mIsSynced != null) {
            if (mIsSynced) {
                dest.writeString(TRUE);
            } else {
                dest.writeString(FALSE);
            }
        } else {
            dest.writeString(null);
        }
        if (mIsStarred != null) {
            if (mIsStarred) {
                dest.writeString(TRUE);
            } else {
                dest.writeString(FALSE);
            }
        } else {
            dest.writeString(null);
        }
        if (mRecordType != null) {
            dest.writeString(mRecordType);
        } else {
            dest.writeString(null);
        }
    }

    private static final String TRUE = "true";
    private static final String FALSE = "false";
}
