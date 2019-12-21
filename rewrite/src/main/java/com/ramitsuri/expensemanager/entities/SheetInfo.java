package com.ramitsuri.expensemanager.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.ramitsuri.sheetscore.consumerResponse.SheetMetadata;

import org.jetbrains.annotations.NotNull;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class SheetInfo implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int mId;

    @ColumnInfo(name = "sheet_name")
    private String mSheetName;

    @ColumnInfo(name = "sheet_id")
    private int mSheetId;

    public SheetInfo() {
    }

    protected SheetInfo(Parcel in) {
        mId = in.readInt();
        mSheetName = in.readString();
        mSheetId = in.readInt();
    }

    public SheetInfo(SheetMetadata sheetMetadata) {
        mSheetName = sheetMetadata.getSheetName();
        mSheetId = sheetMetadata.getSheetId();
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getSheetName() {
        return mSheetName;
    }

    public void setSheetName(String sheetName) {
        mSheetName = sheetName;
    }

    public int getSheetId() {
        return mSheetId;
    }

    public void setSheetId(int sheetId) {
        mSheetId = sheetId;
    }

    public static final Creator<SheetInfo> CREATOR = new Creator<SheetInfo>() {
        @Override
        public SheetInfo createFromParcel(Parcel in) {
            return new SheetInfo(in);
        }

        @Override
        public SheetInfo[] newArray(int size) {
            return new SheetInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mSheetName);
        dest.writeInt(mSheetId);
    }

    @NotNull
    @Override
    public String toString() {
        return "SheetInfo { " +
                "mId=" + mId +
                ", mSheetName='" + mSheetName + '\'' +
                ", mSheetId=" + mSheetId +
                " }";
    }
}
