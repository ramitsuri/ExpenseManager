package com.ramitsuri.expensemanagerrewrite.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Expense implements Parcelable {

    @PrimaryKey
    private int mId;

    @ColumnInfo(name = "date_time")
    private long mDateTime;

    @ColumnInfo(name = "amount")
    private BigDecimal mAmount;

    @ColumnInfo(name = "payment_method")
    private PaymentMethod mPaymentMethod;

    @ColumnInfo(name = "category")
    private Category mCategory;

    @ColumnInfo(name = "description")
    private String mDescription;

    @ColumnInfo(name = "store")
    private String mStore;

    @ColumnInfo(name = "is_synced")
    private boolean mIsSynced;

    @ColumnInfo(name = "is_starred")
    private boolean mIsStarred;

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
        mId = in.readInt();
        mDateTime = in.readLong();
        mAmount = new BigDecimal(in.readString());
        mPaymentMethod = in.readParcelable(PaymentMethod.class.getClassLoader());
        mCategory = in.readParcelable(Category.class.getClassLoader());
        mDescription = in.readString();
        mStore = in.readString();
        mIsSynced = in.readByte() != 0;
        mIsStarred = in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mId);
        parcel.writeLong(mDateTime);
        parcel.writeString(String.valueOf(mAmount));
        parcel.writeParcelable(mPaymentMethod, i);
        parcel.writeParcelable(mCategory, i);
        parcel.writeString(mDescription);
        parcel.writeString(mStore);
        parcel.writeByte((byte)(mIsSynced ? 1 : 0));
        parcel.writeByte((byte)(mIsStarred ? 1 : 0));
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
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

    public void setSynced(boolean syncStatus) {
        mIsSynced = syncStatus;
    }

    public boolean isStarred() {
        return mIsStarred;
    }

    public void setStarred(boolean isFlagged) {
        mIsStarred = isFlagged;
    }

    public String getStore() {
        return mStore;
    }

    public void setStore(String store) {
        mStore = store;
    }
}
