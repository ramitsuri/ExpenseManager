package com.ramitsuri.expensemanager.entities;

import java.util.Date;

import javax.annotation.Nonnull;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Log {

    @PrimaryKey(autoGenerate = true)
    private int mId;

    @ColumnInfo(name = "time")
    private long mTime;

    @ColumnInfo(name = "type")
    private String mType;

    @ColumnInfo(name = "result")
    private String mResult;

    @ColumnInfo(name = "message")
    private String mMessage;

    @ColumnInfo(name = "acknowledged")
    private boolean mIsAcknowledged;

    public Log() {
    }

    public Log(long time, String type, String result, String message) {
        mTime = time;
        mType = type;
        mResult = result;
        mMessage = message;
        mIsAcknowledged = false;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public String getResult() {
        return mResult;
    }

    public void setResult(String result) {
        mResult = result;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public boolean isAcknowledged() {
        return mIsAcknowledged;
    }

    public void setIsAcknowledged(boolean acknowledged) {
        mIsAcknowledged = acknowledged;
    }

    @Nonnull
    @Override
    public String toString() {
        return "Log{" +
                "mId=" + mId +
                ", mTime=" + new Date(mTime) +
                ", mType='" + mType + '\'' +
                ", mResult='" + mResult + '\'' +
                ", mMessage='" + mMessage + '\'' +
                ", mIsAcknowledged=" + (mIsAcknowledged ? "TRUE" : "FALSE") +
                '}';
    }
}
