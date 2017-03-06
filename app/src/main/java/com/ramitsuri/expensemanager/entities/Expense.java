package com.ramitsuri.expensemanager.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Expense implements Parcelable{
    private String mRowIdentifier;
    private long mDateTime;
    private String mAmount;
    private String mPaymentMode;
    private Category mCategory;
    private String mDescription;
    private String mStore;
    private boolean mIsSynced;
    private boolean mIsFlagged;

    public Expense() {
    }

    public Expense(String rowIdentifier, long dateTime, String amount, String paymentMode,
                   Category category, String description, String store,
                   boolean isSynced, boolean isFlagged) {
        mRowIdentifier = rowIdentifier;
        mDateTime = dateTime;
        mAmount = amount;
        mPaymentMode = paymentMode;
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
        mAmount = in.readString();
        mPaymentMode = in.readString();
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
        parcel.writeString(mAmount);
        parcel.writeString(mPaymentMode);
        parcel.writeParcelable(mCategory, i);
        parcel.writeString(mDescription);
        parcel.writeString(mStore);
        parcel.writeByte((byte) (mIsSynced ? 1 : 0));
        parcel.writeByte((byte) (mIsFlagged ? 1 : 0));
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

    public String getAmount() {
        return mAmount;
    }

    public void setAmount(String amount) {
        mAmount = amount;
    }

    public String getPaymentMode() {
        return mPaymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        mPaymentMode = paymentMode;
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

    public boolean isFlagged(){
        return mIsFlagged;
    }

    public void setIsFlagged(boolean isFlagged){
        mIsFlagged = isFlagged;
    }

    public String getStore() {
        return mStore;
    }

    public void setStore(String store) {
        mStore = store;
    }
}
