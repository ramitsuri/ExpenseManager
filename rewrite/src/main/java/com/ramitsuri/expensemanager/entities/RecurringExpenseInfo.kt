package com.ramitsuri.expensemanager.entities

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.ramitsuri.expensemanager.constants.intDefs.RecurType
import timber.log.Timber
import java.lang.Exception

@Entity(indices = [Index(value = ["identifier"], unique = true)])
data class RecurringExpenseInfo(
        @ColumnInfo(name = DB.COL_IDENTIFIER)
        var identifier: String,

        @ColumnInfo(name = DB.COL_LAST_OCCUR)
        var lastOccur: Long,

        @ColumnInfo(name = DB.COL_RECUR_TYPE)
        @RecurType
        var recurType: String): Parcelable {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    constructor(parcel: Parcel): this(
            parcel.readString() ?: "",
            parcel.readLong(),
            parcel.readString() ?: RecurType.NONE) {
        id = parcel.readInt()
    }

    constructor(expense: Expense, @RecurType
    recurType: String): this(
            expense.identifier,
            expense.dateTime,
            recurType
    )

    constructor(): this("",
            0L,
            RecurType.MONTHLY)

    constructor(objects: List<Any>): this() {
        try {
            identifier = objects[0] as String
            lastOccur = (objects[1] as String).toLong()
            recurType = objects[2] as String
        } catch (e: Exception) {
            Timber.w("Unable to convert downloaded recurring info - $e")
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(identifier)
        parcel.writeLong(lastOccur)
        parcel.writeString(recurType)
        parcel.writeInt(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    fun toStringList(): List<String> {
        val list = mutableListOf<String>()
        list.add(identifier)
        list.add(lastOccur.toString())
        list.add(recurType)
        return list
    }

    companion object CREATOR: Parcelable.Creator<RecurringExpenseInfo> {
        override fun createFromParcel(parcel: Parcel): RecurringExpenseInfo {
            return RecurringExpenseInfo(parcel)
        }

        override fun newArray(size: Int): Array<RecurringExpenseInfo?> {
            return arrayOfNulls(size)
        }
    }

    object DB {
        const val TABLE: String = "recurringexpenseinfo"
        const val COL_ID: String = "id"
        const val COL_LAST_OCCUR: String = "last_occur"
        const val COL_RECUR_TYPE: String = "recur_type"
        const val COL_IDENTIFIER: String = "identifier"
    }
}