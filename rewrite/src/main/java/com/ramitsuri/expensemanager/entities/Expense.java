package com.ramitsuri.expensemanager.entities;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.ramitsuri.expensemanager.constants.intDefs.RecordType;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import timber.log.Timber;

import static com.ramitsuri.expensemanager.constants.Constants.Sheets.FLAG;
import static com.ramitsuri.expensemanager.constants.Constants.Sheets.INCOME;

@Entity
public class Expense implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int mId;

    @ColumnInfo(name = COL_DATE_TIME)
    private long mDateTime;

    @ColumnInfo(name = COL_AMOUNT)
    private BigDecimal mAmount;

    @ColumnInfo(name = COL_PAYMENT)
    private String mPaymentMethod;

    @ColumnInfo(name = COL_CATEGORY)
    private String mCategory;

    @ColumnInfo(name = COL_DESCRIPTION)
    private String mDescription;

    @ColumnInfo(name = COL_STORE)
    private String mStore;

    @ColumnInfo(name = COL_SYNCED)
    private boolean mIsSynced;

    @ColumnInfo(name = COL_STARRED)
    private boolean mIsStarred;

    @ColumnInfo(name = COL_SHEET_ID)
    private int mSheetId;

    @ColumnInfo(name = COL_INCOME)
    private boolean mIsIncome;

    @ColumnInfo(name = COL_RECORD_TYPE, defaultValue = "MONTHLY")
    @RecordType
    @NonNull
    private String mRecordType;

    @ColumnInfo(name = COL_IDENTIFIER)
    @Nonnull
    private String mIdentifier;

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
        mRecordType = RecordType.MONTHLY;
        generateIdentifier();
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
        mIsIncome = in.readByte() != 0;
        String recordType = in.readString();
        if (TextUtils.isEmpty(recordType)) {
            recordType = RecordType.MONTHLY;
        }
        mRecordType = recordType;
        String identifier = in.readString();
        if (TextUtils.isEmpty(identifier)) {
            generateIdentifier();
        } else {
            setIdentifier(identifier);
        }
    }

    public Expense(List<Object> objects) throws Exception {
        try {
            mDateTime = Long.parseLong((String)objects.get(0));
            mDescription = (String)objects.get(1);
            mStore = (String)objects.get(2);
            mAmount = new BigDecimal((String)objects.get(3));
            mPaymentMethod = (String)objects.get(4);
            mCategory = (String)objects.get(5);
            mRecordType = (String)objects.get(6);
            mIdentifier = (String)objects.get(7);
            if (objects.size() >= 9) {
                mIsStarred = objects.get(8).equals(FLAG);
            }
            mIsSynced = true;
            if (objects.size() >= 10) {
                mIsIncome = objects.get(9).equals(INCOME);
            }
        } catch (Exception e) {
            Timber.w("Unable to convert downloaded expense");
            throw e;
        }
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
        mIsIncome = expense.isIncome();
        mRecordType = expense.getRecordType();
        mIdentifier = expense.getIdentifier();
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
        parcel.writeByte((byte)(mIsIncome ? 1 : 0));
        parcel.writeString(mRecordType);
        parcel.writeString(mIdentifier);
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

    public boolean isIncome() {
        return mIsIncome;
    }

    public void setIsIncome(boolean income) {
        mIsIncome = income;
    }

    @RecordType
    @NonNull
    public String getRecordType() {
        return mRecordType;
    }

    public void setRecordType(@RecordType @Nullable String recordType) {
        if (TextUtils.isEmpty(recordType)) {
            recordType = RecordType.MONTHLY;
        }
        mRecordType = recordType;
    }

    @Nonnull
    public String getIdentifier() {
        return mIdentifier;
    }

    public void setIdentifier(@Nonnull String identifier) {
        mIdentifier = identifier;
    }

    public void generateIdentifier() {
        mIdentifier = UUID.randomUUID().toString();
    }

    @NonNull
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
                ", mIsIncome = " + mIsIncome +
                ", mRecordType = " + mRecordType +
                ", mIdentifier = " + mIdentifier +
                " }\n";
    }

    public static final String TABLE = "expense";
    public static final String COL_DATE_TIME = "date_time";
    public static final String COL_AMOUNT = "amount";
    public static final String COL_PAYMENT = "payment_method";
    public static final String COL_CATEGORY = "category";
    public static final String COL_DESCRIPTION = "description";
    public static final String COL_STORE = "store";
    public static final String COL_SYNCED = "is_synced";
    public static final String COL_STARRED = "is_starred";
    public static final String COL_SHEET_ID = "sheet_id";
    public static final String COL_INCOME = "is_income";
    public static final String COL_RECORD_TYPE = "record_type";
    public static final String COL_IDENTIFIER = "identifier";
}
