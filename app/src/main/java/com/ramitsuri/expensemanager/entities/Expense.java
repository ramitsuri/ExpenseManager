package com.ramitsuri.expensemanager.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;

public class Expense implements Parcelable {
    private String mRowIdentifier;
    private long mDateTime;
    private BigDecimal mAmount;
    private PaymentMethod mPaymentMethod;
    private Category mCategory;
    private String mDescription;
    private String mStore;
    private boolean mIsSynced;
    private boolean mIsFlagged;

    public Expense() {
    }

    public Expense(String rowIdentifier, String dateTime, String amount, int paymentMethod,
            int category, String description, String store,
            boolean isSynced, boolean isFlagged) {
        mRowIdentifier = rowIdentifier;
        mDateTime = Long.parseLong(dateTime);
        mAmount = new BigDecimal(amount);
        mPaymentMethod = new PaymentMethod(paymentMethod, "");
        mCategory = new Category(category, "");
        mDescription = description;
        mStore = store;
        mIsSynced = isSynced;
        mIsFlagged = isFlagged;
    }

    public Expense(String rowIdentifier, long dateTime, BigDecimal amount,
            PaymentMethod paymentMethod,
            Category category, String description, String store,
            boolean isSynced, boolean isFlagged) {
        mRowIdentifier = rowIdentifier;
        mDateTime = dateTime;
        mAmount = amount;
        mPaymentMethod = paymentMethod;
        mCategory = category;
        mDescription = description;
        mStore = store;
        mIsSynced = isSynced;
        mIsFlagged = isFlagged;
    }

    public static final Creator<Expense> CREATOR = new Creator<Expense>() {
        @Override
        public Expense createFromParcel(Parcel in) {
            return new Expense(in);
        }

        @Override
        public Expense[] newArray(int size) {
            return new Expense[size];
        }
    };

    protected Expense(Parcel in) {
        mRowIdentifier = in.readString();
        mDateTime = in.readLong();
        mAmount = new BigDecimal(in.readString());
        mPaymentMethod = in.readParcelable(PaymentMethod.class.getClassLoader());
        mCategory = in.readParcelable(Category.class.getClassLoader());
        mDescription = in.readString();
        mStore = in.readString();
        mIsSynced = in.readByte() != 0;
        mIsFlagged = in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mRowIdentifier);
        parcel.writeLong(mDateTime);
        parcel.writeString(String.valueOf(mAmount));
        parcel.writeParcelable(mPaymentMethod, i);
        parcel.writeParcelable(mCategory, i);
        parcel.writeString(mDescription);
        parcel.writeString(mStore);
        parcel.writeByte((byte)(mIsSynced ? 1 : 0));
        parcel.writeByte((byte)(mIsFlagged ? 1 : 0));
    }

    public String getRowIdentifier() {
        return mRowIdentifier;
    }

    public void setRowIdentifier(String rowIdentifier) {
        mRowIdentifier = rowIdentifier;
    }

    public long getDateTime() {
        return mDateTime;
    }

    public void setDateTime(long dateTime) {
        mDateTime = dateTime;
    }

    public BigDecimal getAmount() {
        return mAmount;
    }

    public void setAmount(BigDecimal amount) {
        mAmount = amount;
    }

    public PaymentMethod getPaymentMethod() {
        return mPaymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        mPaymentMethod = paymentMethod;
    }

    public Category getCategory() {
        return mCategory;
    }

    public void setCategory(Category category) {
        mCategory = category;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public boolean isSynced() {
        return mIsSynced;
    }

    public void setIsSynced(boolean syncStatus) {
        mIsSynced = syncStatus;
    }

    public boolean isFlagged() {
        return mIsFlagged;
    }

    public void setIsFlagged(boolean isFlagged) {
        mIsFlagged = isFlagged;
    }

    public String getStore() {
        return mStore;
    }

    public void setStore(String store) {
        mStore = store;
    }
}
