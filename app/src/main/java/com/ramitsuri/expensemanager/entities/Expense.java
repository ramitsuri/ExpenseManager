package com.ramitsuri.expensemanager.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Expense implements Parcelable{
    private String mRowIdentifier;
    private long mDateTime;
    private double mAmount;
    private String mPaymentMode;
    private Category mCategory;
    private String mDescription;
    private boolean mIsSynced;
    private boolean mIsFlagged;

    public Expense() {
    }

    public Expense(String rowIdentifier, long dateTime, double amount, String paymentMode,
                   Category category, String description, boolean isSynced, boolean isFlagged) {
        mRowIdentifier = rowIdentifier;
        mDateTime = dateTime;
        mAmount = amount;
        mPaymentMode = paymentMode;
        mCategory = category;
        mDescription = description;
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
        mAmount = in.readDouble();
        mPaymentMode = in.readString();
        mCategory = in.readParcelable(Category.class.getClassLoader());
        mDescription = in.readString();
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
        parcel.writeDouble(mAmount);
        parcel.writeString(mPaymentMode);
        parcel.writeParcelable(mCategory, i);
        parcel.writeString(mDescription);
        parcel.writeByte((byte) (mIsSynced ? 1 : 0));
        parcel.writeByte((byte) (mIsFlagged ? 1 : 0));
    }

    public String getRowIdentifier() {
        return mRowIdentifier;
    }

    public void setRowIdentifier(String rowIdentifier) {
        this.mRowIdentifier = rowIdentifier;
    }

    public long getDateTime() {
        return mDateTime;
    }

    public void setDateTime(long dateTime) {
        this.mDateTime = dateTime;
    }

    public double getAmount() {
        return mAmount;
    }

    public void setAmount(double amount) {
        this.mAmount = amount;
    }

    public String getPaymentMode() {
        return mPaymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.mPaymentMode = paymentMode;
    }

    public Category getCategory() {
        return mCategory;
    }

    public void setCategory(Category category) {
        this.mCategory = category;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public boolean isSyncStatus() {
        return mIsSynced;
    }

    public void setSyncStatus(boolean syncStatus) {
        this.mIsSynced = syncStatus;
    }

    public boolean isFlagged(){
        return mIsFlagged;
    }

    public void setIsFlagged(boolean isFlagged){
        mIsFlagged = isFlagged;
    }
}
