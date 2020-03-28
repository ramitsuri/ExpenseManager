package com.ramitsuri.expensemanager.ui.adapter;

import android.os.Parcel;
import android.os.Parcelable;

import javax.annotation.Nonnull;

public class BudgetCategoryWrapper implements Parcelable {

    private String mCategory;
    private boolean mIsAvailable;
    private boolean mIsSelected;

    public BudgetCategoryWrapper(String category) {
        mCategory = category;
    }

    protected BudgetCategoryWrapper(Parcel in) {
        mCategory = in.readString();
        mIsAvailable = in.readByte() != 0;
        mIsSelected = in.readByte() != 0;
    }

    public static final Creator<BudgetCategoryWrapper> CREATOR =
            new Creator<BudgetCategoryWrapper>() {
                @Override
                public BudgetCategoryWrapper createFromParcel(Parcel in) {
                    return new BudgetCategoryWrapper(in);
                }

                @Override
                public BudgetCategoryWrapper[] newArray(int size) {
                    return new BudgetCategoryWrapper[size];
                }
            };

    public String getCategory() {
        return mCategory;
    }

    public void setCategory(String category) {
        mCategory = category;
    }

    public boolean isAvailable() {
        return mIsAvailable;
    }

    public void setAvailable(boolean available) {
        mIsAvailable = available;
    }

    public boolean isSelected() {
        return mIsSelected;
    }

    public void setSelected(boolean selected) {
        mIsSelected = selected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mCategory);
        dest.writeByte((byte)(mIsAvailable ? 1 : 0));
        dest.writeByte((byte)(mIsSelected ? 1 : 0));
    }

    @Override
    @Nonnull
    public String toString() {
        return "BudgetCategoryWrapper{" +
                "mCategory='" + mCategory + '\'' +
                ", mIsAvailable=" + mIsAvailable +
                ", mIsSelected=" + mIsSelected +
                "}\n";
    }
}
