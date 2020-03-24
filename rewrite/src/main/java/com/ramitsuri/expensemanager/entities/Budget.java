package com.ramitsuri.expensemanager.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import static com.ramitsuri.expensemanager.constants.Constants.Basic.EMPTY_BUDGET;

@Entity
public class Budget implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int mId;

    @ColumnInfo(name = "name")
    private String mName;

    @ColumnInfo(name = "amount")
    private BigDecimal mAmount;

    @ColumnInfo(name = "categories")
    private List<String> mCategories;

    public Budget() {
    }

    protected Budget(Parcel in) {
        mId = in.readInt();
        mName = in.readString();
        mAmount = new BigDecimal(in.readString());
        mCategories = new ArrayList<>();
        in.readList(mCategories, String.class.getClassLoader());
    }

    public Budget(List<String> strings) {
        mName = strings.get(0);
        mAmount = new BigDecimal(strings.get(1));
        mCategories = new ArrayList<>();
        for (int index = 2; index < strings.size(); index++) {
            String string = strings.get(index);
            if (string.equals(EMPTY_BUDGET)) {
                continue;
            }
            mCategories.add(strings.get(index));
        }
    }

    public static final Creator<Budget> CREATOR = new Creator<Budget>() {
        @Override
        public Budget createFromParcel(Parcel in) {
            return new Budget(in);
        }

        @Override
        public Budget[] newArray(int size) {
            return new Budget[size];
        }
    };

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

    public BigDecimal getAmount() {
        return mAmount;
    }

    public void setAmount(BigDecimal amount) {
        mAmount = amount;
    }

    public List<String> getCategories() {
        return mCategories;
    }

    public void setCategories(List<String> categories) {
        mCategories = categories;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mName);
        dest.writeString(String.valueOf(mAmount));
        dest.writeList(mCategories);
    }

    @Nonnull
    @Override
    public String toString() {
        return "Budget{" +
                "mId=" + mId +
                ", mName='" + mName + '\'' +
                ", mAmount=" + mAmount +
                ", mCategories=" + mCategories +
                "}";
    }
}
