package com.ramitsuri.expensemanager.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ramitsuri on 1/15/2017.
 */

public class Expense implements Parcelable{
    private String rowIdentifier;
    private long dateTime;
    private double amount;
    private String paymentMode;
    private Category category;
    private String notes;
    private boolean syncStatus;

    public Expense() {
    }

    public Expense(String rowIdentifier, long dateTime, double amount, String paymentMode, Category category, String notes, boolean syncStatus) {
        this.rowIdentifier = rowIdentifier;
        this.dateTime = dateTime;
        this.amount = amount;
        this.paymentMode = paymentMode;
        this.category = category;
        this.notes = notes;
        this.syncStatus = syncStatus;
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
        rowIdentifier = in.readString();
        dateTime = in.readLong();
        amount = in.readDouble();
        paymentMode = in.readString();
        category = in.readParcelable(Category.class.getClassLoader());
        notes = in.readString();
        syncStatus = in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(rowIdentifier);
        parcel.writeLong(dateTime);
        parcel.writeDouble(amount);
        parcel.writeString(paymentMode);
        parcel.writeParcelable(category, i);
        parcel.writeString(notes);
        parcel.writeByte((byte) (syncStatus ? 1 : 0));
    }

    public String getRowIdentifier() {
        return rowIdentifier;
    }

    public void setRowIdentifier(String rowIdentifier) {
        this.rowIdentifier = rowIdentifier;
    }

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(boolean syncStatus) {
        this.syncStatus = syncStatus;
    }
}
