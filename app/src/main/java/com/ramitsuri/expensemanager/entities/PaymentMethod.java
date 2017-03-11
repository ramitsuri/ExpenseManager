package com.ramitsuri.expensemanager.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class PaymentMethod implements Parcelable{

    private int mId;
    private String mName;

    public  PaymentMethod(){

    }

    public PaymentMethod(int id, String name){
        mId = id;
        mName = name;
    }

    protected PaymentMethod(Parcel in) {
        mId = in.readInt();
        mName = in.readString();
    }

    public static final Creator<PaymentMethod> CREATOR = new Creator<PaymentMethod>() {
        @Override
        public PaymentMethod createFromParcel(Parcel in) {
            return new PaymentMethod(in);
        }

        @Override
        public PaymentMethod[] newArray(int size) {
            return new PaymentMethod[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mId);
        parcel.writeString(mName);
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
        mName = name;
    }
}
