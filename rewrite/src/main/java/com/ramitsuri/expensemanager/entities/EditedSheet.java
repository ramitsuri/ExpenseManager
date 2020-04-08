package com.ramitsuri.expensemanager.entities;

import android.os.Parcel;
import android.os.Parcelable;

import javax.annotation.Nonnull;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index(value = {"sheet_id"}, unique = true)})
public class EditedSheet implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int mId;

    // This is actually the month index (0-11) the expense belongs to that was edited.
    // This was done so I don't have to modufy the database
    @ColumnInfo(name = "sheet_id")
    private int mSheetId;

    public static final Creator<EditedSheet> CREATOR = new Creator<EditedSheet>() {
        @Override
        public EditedSheet createFromParcel(Parcel in) {
            return new EditedSheet(in);
        }

        @Override
        public EditedSheet[] newArray(int size) {
            return new EditedSheet[size];
        }
    };

    public EditedSheet(int sheetId) {
        mSheetId = sheetId;
    }

    protected EditedSheet(Parcel in) {
        mId = in.readInt();
        mSheetId = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mId);
        parcel.writeInt(mSheetId);
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public int getSheetId() {
        return mSheetId;
    }

    public void setSheetId(int sheetId) {
        mSheetId = sheetId;
    }

    @Override
    @Nonnull
    public String toString() {
        return "EditedSheets{" +
                "mId=" + mId +
                ", mSheetId=" + mSheetId +
                '}';
    }
}
