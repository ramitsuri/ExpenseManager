package com.ramitsuri.expensemanager.entities

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ramitsuri.expensemanager.constants.Constants
import com.ramitsuri.expensemanager.constants.intDefs.AddType
import com.ramitsuri.expensemanager.constants.intDefs.RecordType
import timber.log.Timber
import java.math.BigDecimal
import java.util.*

@Entity
data class Expense(
        @ColumnInfo(name = DB.COL_DATE_TIME)
        var dateTime: Long,
        @ColumnInfo(name = DB.COL_AMOUNT)
        var amount: BigDecimal,
        @ColumnInfo(name = DB.COL_PAYMENT)
        var paymentMethod: String,
        @ColumnInfo(name = DB.COL_CATEGORY)
        var category: String,
        @ColumnInfo(name = DB.COL_DESCRIPTION)
        var description: String,
        @ColumnInfo(name = DB.COL_STORE)
        var store: String,
        @ColumnInfo(name = DB.COL_SYNCED)
        var isSynced: Boolean,
        @ColumnInfo(name = DB.COL_STARRED)
        var isStarred: Boolean,
        @ColumnInfo(name = DB.COL_SHEET_ID)
        var sheetId: Int,
        @ColumnInfo(name = DB.COL_INCOME)
        var isIncome: Boolean,
        @ColumnInfo(name = DB.COL_RECORD_TYPE, defaultValue = "MONTHLY")
        @RecordType
        var recordType: String,
        @ColumnInfo(name = DB.COL_IDENTIFIER)
        var identifier: String,
        @AddType
        @ColumnInfo(name = DB.COL_ADD_TYPE, defaultValue = "MANUAL")
        var addType: String) : Parcelable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = DB.COL_ID)
    var id: Int = 0

    constructor() : this(
            dateTime = 0L,
            amount = BigDecimal("0.00"),
            paymentMethod = "",
            category = "",
            description = "",
            store = "",
            isSynced = false,
            isStarred = false,
            sheetId = 0,
            isIncome = false,
            recordType = RecordType.MONTHLY,
            identifier = UUID.randomUUID().toString(),
            addType = AddType.MANUAL)

    /**
     * Creates an expense from list of Any. The list usually comes as a result of getting the
     * expense from Sheet.
     */
    constructor(objects: List<Any>) : this() {
        try {
            dateTime = (objects[0] as String).toLong()
            description = objects[1] as String
            store = objects[2] as String
            amount = BigDecimal(objects[3] as String)
            paymentMethod = objects[4] as String
            category = objects[5] as String
            recordType = objects[6] as String
            identifier = objects[7] as String
            addType = objects[8] as String
            if (objects.size >= 10) {
                isStarred = objects[9] as String == Constants.Sheets.FLAG
            }
            if (objects.size >= 11) {
                isIncome = objects[10] as String == Constants.Sheets.INCOME
            }
            isSynced = true
        } catch (e: Exception) {
            Timber.w("Unable to convert downloaded expense")
            throw e
        }
    }

    /**
     * Creates an expense from map of String, Any. The map usually comes as a result of getting the
     * expense from Firebase database.
     *
     * Doesn't contain add type because previous expenses would be difficult and the functionality
     * is going to be reworked anyway
     */
    constructor(map: Map<String, Any>) : this() {
        dateTime = map[DB.COL_DATE_TIME] as Long
        description = map[DB.COL_DESCRIPTION] as String
        store = map[DB.COL_STORE] as String
        amount = BigDecimal(map[DB.COL_AMOUNT] as String)
        paymentMethod = map[DB.COL_PAYMENT] as String
        category = map[DB.COL_CATEGORY] as String
        recordType = map[DB.COL_RECORD_TYPE] as String
        identifier = map[DB.COL_IDENTIFIER] as String
        isStarred = map[DB.COL_STARRED] as String == "true"
    }

    constructor(expense: Expense) : this(
            dateTime = expense.dateTime,
            amount = expense.amount,
            paymentMethod = expense.paymentMethod,
            category = expense.category,
            description = expense.description,
            store = expense.store,
            isSynced = expense.isSynced,
            isStarred = expense.isStarred,
            sheetId = expense.sheetId,
            isIncome = expense.isIncome,
            recordType = expense.recordType,
            identifier = expense.identifier,
            addType = expense.addType)

    constructor(parcel: Parcel) : this(
            dateTime = parcel.readLong(),
            amount = BigDecimal(parcel.readString() ?: "0.00"),
            paymentMethod = parcel.readString() ?: "",
            category = parcel.readString() ?: "",
            description = parcel.readString() ?: "",
            store = parcel.readString() ?: "",
            isSynced = parcel.readInt() != 0,
            isStarred = parcel.readInt() != 0,
            sheetId = parcel.readInt(),
            isIncome = parcel.readInt() != 0,
            recordType = parcel.readString() ?: RecordType.MONTHLY,
            identifier = parcel.readString() ?: UUID.randomUUID().toString(),
            addType = parcel.readString() ?: AddType.MANUAL) {
        id = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(dateTime)
        parcel.writeString(amount.toString())
        parcel.writeString(paymentMethod)
        parcel.writeString(category)
        parcel.writeString(description)
        parcel.writeString(store)
        parcel.writeInt(if (isSynced) 1 else 0)
        parcel.writeInt(if (isStarred) 1 else 0)
        parcel.writeInt(sheetId)
        parcel.writeInt(if (isIncome) 1 else 0)
        parcel.writeString(recordType)
        parcel.writeString(identifier)
        parcel.writeString(addType)
        parcel.writeInt(id)
    }

    fun generateIdentifier() {
        identifier = UUID.randomUUID().toString()
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Expense> {
        override fun createFromParcel(parcel: Parcel): Expense {
            return Expense(parcel)
        }

        override fun newArray(size: Int): Array<Expense?> {
            return arrayOfNulls(size)
        }
    }

    /**
     * Creates a String list of properties which is used to backup expense data in Sheet.
     * Contains only the values that are backed up.
     */
    fun toStringList(): List<String> {
        val list = mutableListOf<String>()
        list.add(dateTime.toString())
        list.add(description)
        list.add(store)
        list.add(amount.toString())
        list.add(paymentMethod)
        list.add(category)
        list.add(recordType)
        list.add(identifier)
        list.add(addType)
        if (isStarred) {
            list.add(Constants.Sheets.FLAG)
        } else {
            list.add("")
        }
        if (isIncome) {
            list.add(Constants.Sheets.INCOME)
        } else {
            list.add("")
        }

        return list
    }

    /**
     * Creates a map of properties which is used to backup expense data in firebase database
     *
     * Doesn't contain add type because previous expenses would be difficult and the functionality
     * is going to be reworked anyway
     */
    fun toMap(): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        map[DB.COL_DATE_TIME] = dateTime.toString()
        map[DB.COL_DESCRIPTION] = description
        map[DB.COL_STORE] = store
        map[DB.COL_AMOUNT] = amount.toString()
        map[DB.COL_PAYMENT] = paymentMethod
        map[DB.COL_CATEGORY] = category
        map[DB.COL_RECORD_TYPE] = recordType
        map[DB.COL_IDENTIFIER] = identifier

        return map
    }

    object DB {
        const val TABLE = "expense"
        const val COL_ID = "mId"
        const val COL_DATE_TIME = "date_time"
        const val COL_AMOUNT = "amount"
        const val COL_PAYMENT = "payment_method"
        const val COL_CATEGORY = "category"
        const val COL_DESCRIPTION = "description"
        const val COL_STORE = "store"
        const val COL_SYNCED = "is_synced"
        const val COL_STARRED = "is_starred"
        const val COL_SHEET_ID = "sheet_id"
        const val COL_INCOME = "is_income"
        const val COL_RECORD_TYPE = "record_type"
        const val COL_IDENTIFIER = "identifier"
        const val COL_ADD_TYPE = "add_type"
    }
}