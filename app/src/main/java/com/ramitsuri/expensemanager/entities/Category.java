package com.ramitsuri.expensemanager.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ramitsuri on 1/15/2017.
 */

public class Category implements Parcelable {
    private int id;
    private String name;
    private int parentID;

    protected Category(Parcel in) {
        id = in.readInt();
        name = in.readString();
        parentID = in.readInt();
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

    public Category(int id, String name, int parentID) {
        this.id = id;
        this.name = name;
        this.parentID = parentID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getParentID() {
        return parentID;
    }

    public void setParentID(int parentID) {
        this.parentID = parentID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeInt(parentID);
    }
}
