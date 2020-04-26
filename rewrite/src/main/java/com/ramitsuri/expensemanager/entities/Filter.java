package com.ramitsuri.expensemanager.entities;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Pair;
import android.util.SparseArray;

import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.constants.Constants;
import com.ramitsuri.expensemanager.utils.AppHelper;

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

        if (mDateTimes != null) {
            // TODO
          /*  sb.append(MainApplication.getInstance().getResources()
                    .getString(R.string.expenses_filter_date_range_format,
                            DateHelper.getFriendlyDate(mFromDateTime),
                            DateHelper.getFriendlyDate(mToDateTime)));*/
        }
        return sb.toString();
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
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT * FROM expense");
        List<Object> args = new ArrayList<>();
        // Is income
        if (getIsIncome() != null) {
            queryBuilder.append(" WHERE is_income = ?");
            args.add(getIsIncome() ? 1 : 0);
        }

        // Date ranges
        if (getDateTimes() != null && getDateTimes().size() > 0) {
            if (queryBuilder.indexOf("WHERE") == -1) {
                queryBuilder.append(" WHERE");
            } else {
                queryBuilder.append(" AND");
            }
            queryBuilder.append(" (");
            boolean dateTimeAdded = false;
            for (int i = 0; i < getDateTimes().size(); i++) {
                Pair<Long, Long> dateTime = getDateTimes().valueAt(i);
                if (dateTime == null) {
                    continue;
                }
                if (dateTimeAdded) {
                    queryBuilder.append(" OR");
                }
                queryBuilder.append(" (date_time BETWEEN ? AND ?)");
                args.add(dateTime.first);
                args.add(dateTime.second);
                dateTimeAdded = true;
            }
            queryBuilder.append(" )");
        }

        // Categories
        if (getCategories() != null && getCategories().size() > 0) {
            if (queryBuilder.indexOf("WHERE") == -1) {
                queryBuilder.append(" WHERE");
            } else {
                queryBuilder.append(" AND");
            }
            queryBuilder.append(" category IN (");
            StringBuilder placeholders = new StringBuilder();
            for (int i = 0; i < getCategories().size(); i++) {
                if (i != 0) {
                    placeholders.append(",");
                }
                placeholders.append("?");
                args.add(getCategories().get(i));
            }
            queryBuilder.append(placeholders.toString());
            queryBuilder.append(")");
        }

        // Payment Methods
        if (getPaymentMethods() != null && getPaymentMethods().size() > 0) {
            if (queryBuilder.indexOf("WHERE") == -1) {
                queryBuilder.append(" WHERE");
            } else {
                queryBuilder.append(" AND");
            }
            queryBuilder.append(" payment_method IN (");
            StringBuilder placeholders = new StringBuilder();
            for (int i = 0; i < getPaymentMethods().size(); i++) {
                if (i != 0) {
                    placeholders.append(",");
                }
                placeholders.append("?");
                args.add(getPaymentMethods().get(i));
            }
            queryBuilder.append(placeholders.toString());
            queryBuilder.append(")");
        }

        // Synced
        if (getIsSynced() != null) {
            if (queryBuilder.indexOf("WHERE") == -1) {
                queryBuilder.append(" WHERE");
            } else {
                queryBuilder.append(" AND");
            }
            queryBuilder.append(" is_synced = ?");
            args.add(getIsSynced() ? 1 : 0);
        }

        // Starred
        if (getIsStarred() != null) {
            if (queryBuilder.indexOf("WHERE") == -1) {
                queryBuilder.append(" WHERE");
            } else {
                queryBuilder.append(" AND");
            }
            queryBuilder.append(" is_starred = ?");
            args.add(getIsStarred() ? 1 : 0);
        }

        SimpleSQLiteQuery query = new SimpleSQLiteQuery(queryBuilder.toString(), args.toArray());
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
    }

    private static final String TRUE = "true";
    private static final String FALSE = "false";
}
