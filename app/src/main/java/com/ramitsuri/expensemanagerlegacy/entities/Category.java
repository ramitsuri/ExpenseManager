package com.ramitsuri.expensemanagerlegacy.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Category implements Parcelable {
    private int mId;
    private String mName;

    protected Category(Parcel in) {
        mId = in.readInt();
        mName = in.readString();
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
    }

    public Category(int id, String name) {
        mId = id;
        mName = name;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mId);
        parcel.writeString(mName);
    }

    @Override
    public String toString() {
        return mName;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Category)) {
            return false;
        }
        return this.getId() == ((Category)other).getId();
    }

    @Override
    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * this.getId() + this.getName().hashCode();
        return hashCode;
    }
}
