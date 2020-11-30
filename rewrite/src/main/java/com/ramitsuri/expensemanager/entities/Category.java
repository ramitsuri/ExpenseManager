package com.ramitsuri.expensemanager.entities;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.ramitsuri.expensemanager.constants.intDefs.RecordType;
import com.ramitsuri.expensemanager.ui.adapter.ListEqualizer;

import javax.annotation.Nonnull;

@Entity
public class Category implements Parcelable, ListEqualizer {

    @PrimaryKey(autoGenerate = true)
    private int mId;

    @ColumnInfo(name = "name")
    private String mName;

    @ColumnInfo(name = "record_type", defaultValue = "MONTHLY")
    @RecordType
    @NonNull
    private String mRecordType;

    @Ignore
    protected Category(Parcel in) {
        mId = in.readInt();
        mName = in.readString();
        String recordType = in.readString();
        if (TextUtils.isEmpty(recordType)) {
            recordType = RecordType.MONTHLY;
        }
        mRecordType = recordType;
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    public Category() {
        mRecordType = RecordType.MONTHLY;
    }

    public Category(String name, @Nonnull @RecordType String recordType) {
        mName = name;
        mRecordType = recordType;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    @RecordType
    @NonNull
    public String getRecordType() {
        return mRecordType;
    }

    public void setRecordType(@RecordType @Nonnull String recordType) {
        if (TextUtils.isEmpty(recordType)) {
            recordType = RecordType.MONTHLY;
        }
        mRecordType = recordType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mId);
        parcel.writeString(mName);
        parcel.writeString(mRecordType);
    }

    @Override
    @NonNull
    public String toString() {
        return "Category { " +
                "mId = " + mId +
                ", mName = '" + mName + '\'' +
                ", mRecordType = '" + mRecordType + '\'' +
                " }";
    }

    @Override
    public String getValue() {
        return mName;
    }
}
