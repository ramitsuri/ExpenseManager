package com.ramitsuri.expensemanager.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.ramitsuri.expensemanager.utils.DateHelper;

import java.math.BigDecimal;
import java.util.List;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import static com.ramitsuri.expensemanager.Constants.Sheets.FLAG;

@Entity
public class Expense implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int mId;

    @ColumnInfo(name = "date_time")
    private long mDateTime;

    @ColumnInfo(name = "amount")
    private BigDecimal mAmount;

    @ColumnInfo(name = "payment_method")
    private String mPaymentMethod;

    @ColumnInfo(name = "category")
    private String mCategory;

    @ColumnInfo(name = "description")
    private String mDescription;

    @ColumnInfo(name = "store")
    private String mStore;

    @ColumnInfo(name = "is_synced")
    private boolean mIsSynced;

    @ColumnInfo(name = "is_starred")
    private boolean mIsStarred;

    @ColumnInfo(name = "sheet_id")
    private int mSheetId;

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

    public Expense() {
    }

    protected Expense(Parcel in) {
        mId = in.readInt();
        mDateTime = in.readLong();
        mAmount = new BigDecimal(in.readString());
        mPaymentMethod = in.readString();
        mCategory = in.readString();
        mDescription = in.readString();
        mStore = in.readString();
        mIsSynced = in.readByte() != 0;
        mIsStarred = in.readByte() != 0;
        mSheetId = in.readInt();
    }

    public Expense(List<Object> objects, int sheetId) {
        mDateTime = DateHelper.fromSheetsDate(((BigDecimal)objects.get(0)).intValue());
        mDescription = (String)objects.get(1);
        mStore = (String)objects.get(2);
        mAmount = (BigDecimal)objects.get(3);
        mPaymentMethod = (String)objects.get(4);
        mCategory = (String)objects.get(5);
        if (objects.size() >= 7) {
            mIsStarred = objects.get(6).equals(FLAG);
        }
        mIsSynced = true;
        mSheetId = sheetId;
    }

    public Expense(Expense expense) {
        mDateTime = expense.getDateTime();
        mDescription = expense.getDescription();
        mStore = expense.getStore();
        mAmount = expense.getAmount();
        mPaymentMethod = expense.getPaymentMethod();
        mCategory = expense.getCategory();
        mIsStarred = expense.isStarred();
        mIsSynced = expense.isSynced();
        mSheetId = expense.getSheetId();
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
        parcel.writeString(mPaymentMethod);
        parcel.writeString(mCategory);
        parcel.writeString(mDescription);
        parcel.writeString(mStore);
        parcel.writeByte((byte)(mIsSynced ? 1 : 0));
        parcel.writeByte((byte)(mIsStarred ? 1 : 0));
        parcel.writeInt(mSheetId);
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

    public String getPaymentMethod() {
        return mPaymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        mPaymentMethod = paymentMethod;
    }

    public String getCategory() {
        return mCategory;
    }

    public void setCategory(String category) {
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

    public boolean isStarred() {
        return mIsStarred;
    }

    public void setIsStarred(boolean isFlagged) {
        mIsStarred = isFlagged;
    }

    public String getStore() {
        return mStore;
    }

    public void setStore(String store) {
        mStore = store;
    }

    public int getSheetId() {
        return mSheetId;
    }

    public void setSheetId(int sheetId) {
        mSheetId = sheetId;
    }

    @Override
    public String toString() {
        return "Expense { " +
                "mId = " + mId +
                ", mDateTime = " + mDateTime +
                ", mAmount = " + mAmount +
                ", mPaymentMethod = '" + mPaymentMethod + '\'' +
                ", mCategory = '" + mCategory + '\'' +
                ", mDescription = '" + mDescription + '\'' +
                ", mStore = '" + mStore + '\'' +
                ", mIsSynced = " + mIsSynced +
                ", mIsStarred = " + mIsStarred +
                ", mSheetId = " + mSheetId +
                " }\n";
    }
}
