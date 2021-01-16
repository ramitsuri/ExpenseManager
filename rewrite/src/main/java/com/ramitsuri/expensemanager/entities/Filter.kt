package com.ramitsuri.expensemanager.entities

import android.os.Parcel
import android.os.Parcelable
import androidx.sqlite.db.SimpleSQLiteQuery
import com.ramitsuri.expensemanager.constants.intDefs.RecordType
import com.ramitsuri.expensemanager.data.utils.SqlBuilder
import com.ramitsuri.expensemanager.utils.AppHelper
import com.ramitsuri.expensemanager.utils.DateHelper
import timber.log.Timber
import java.time.YearMonth
import java.time.ZoneId
import java.util.*
import kotlin.collections.ArrayList

class Filter(
        var years: MutableSet<Int>? = null,
        var months: MutableSet<Int>? = null,
        var categories: MutableList<String>? = null,
        var paymentMethods: MutableList<String>? = null,
        @RecordType
        var recordType: String? = null,
        var isStarred: Boolean? = null,
        private var timeZone: TimeZone = AppHelper.getTimeZone()
): Parcelable {
    constructor(parcel: Parcel): this() {
        val yearArray = parcel.createIntArray()
        yearArray?.let {
            years = it.toMutableSet()
        }

        val monthArray = parcel.createIntArray()
        monthArray?.let {
            months = it.toMutableSet()
        }

        categories = parcel.createStringArrayList()

        paymentMethods = parcel.createStringArrayList()

        recordType = parcel.readString()

        val starred: String? = parcel.readString()
        if (starred != null) {
            isStarred = starred == TRUE
        }

        timeZone = TimeZone.getTimeZone(ZoneId.of(parcel.readString()))
    }

    constructor(timeZone: TimeZone): this() {
        this.timeZone = timeZone
    }

    fun getDefault(): Filter {
        apply {
            addCurrentMonth()
        }
        return this
    }

    fun clear(): Filter {
        years = null
        months = null
        categories = null
        paymentMethods = null
        isStarred = null
        recordType = RecordType.MONTHLY
        return this
    }

    fun addYear(year: Int): Filter {
        if (years == null) {
            years = mutableSetOf()
        }
        years?.add(year)
        return this
    }

    fun removeYear(year: Int): Filter {
        years?.remove(year)
        return this
    }

    fun addMonth(month: Int): Filter {
        if (months == null) {
            months = mutableSetOf()
        }
        months?.add(month)
        return this
    }

    fun removeMonth(month: Int): Filter {
        months?.remove(month)
        return this
    }

    private fun addCurrentMonth(): Filter {
        val yearMonth = YearMonth.now(timeZone.toZoneId())
        addYear(yearMonth.year)
        addMonth(yearMonth.month.value)
        return this
    }

    fun addCategory(category: String): Filter {
        if (categories == null) {
            categories = ArrayList()
        }
        categories?.add(category)
        return this
    }

    fun removeCategory(category: String): Filter {
        categories?.remove(category)
        return this
    }

    fun addPaymentMethod(paymentMethod: String): Filter {
        if (paymentMethods == null) {
            paymentMethods = ArrayList()
        }
        paymentMethods?.add(paymentMethod)
        return this
    }

    fun removePaymentMethod(paymentMethod: String): Filter {
        paymentMethods?.remove(paymentMethod)
        return this
    }

    fun toFriendlyString(): String? {
        var friendlyString: String? = null
        val periods = getPeriods()
        if (periods.size == 1) {
            friendlyString = DateHelper.getMonthAndYear(periods[0].start, timeZone)
        }
        return if (isStarred != null ||
                categories != null && categories!!.size > 0 ||
                paymentMethods != null && paymentMethods!!.size > 0 ||
                recordType == null || recordType == RecordType.ANNUAL) {
            null
        } else friendlyString
    }

    fun toQuery(): SimpleSQLiteQuery {
        val builder = SqlBuilder()
        builder.select("*")
                .from(Expense.DB.TABLE)
        // Date ranges
        val periods = getPeriods()
        periods.let {
            if (it.isNotEmpty()) {
                builder.whereOrAnd()
                        .betweenPeriods(Expense.DB.COL_DATE_TIME, it)
            }
        }
        // Categories
        categories?.let {
            if (it.isNotEmpty()) {
                builder.whereOrAnd()
                        .column(Expense.DB.COL_CATEGORY)
                        .`in`<String>(it)
            }
        }
        // Payment Methods
        paymentMethods?.let {
            if (it.isNotEmpty()) {
                builder.whereOrAnd()
                        .column(Expense.DB.COL_PAYMENT)
                        .`in`<String>(it)
            }
        }
        // Starred
        isStarred?.let {
            builder.whereOrAnd()
                    .column(Expense.DB.COL_STARRED)
                    .equal(if (it) 1 else 0)
        }

        // Record Type
        recordType?.let {
            builder.whereOrAnd()
                    .column(Expense.DB.COL_RECORD_TYPE)
                    .equal(it)
        }
        val query = SimpleSQLiteQuery(builder.toString(), builder.args.toTypedArray())
        Timber.i("Generated query is: [%s]", query.sql)
        return query
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        var yearArray: IntArray? = null
        years?.let {
            yearArray = it.toIntArray()
        }
        dest.writeIntArray(yearArray)

        var monthArray: IntArray? = null
        months?.let {
            monthArray = it.toIntArray()
        }
        dest.writeIntArray(monthArray)

        categories?.let {
            dest.writeStringList(it)
        } ?: run {
            dest.writeStringList(null)
        }

        paymentMethods?.let {
            dest.writeStringList(it)
        } ?: run {
            dest.writeStringList(null)
        }

        recordType?.let {
            dest.writeString(it)
        } ?: run {
            dest.writeString(null)
        }

        isStarred?.let {
            if (it) {
                dest.writeString(TRUE)
            } else {
                dest.writeString(FALSE)
            }
        } ?: run {
            dest.writeString(null)
        }

        dest.writeString(timeZone.toZoneId().id)
    }

    fun getPeriods(): List<Period> {
        val periods = mutableListOf<Period>()
        years?.let {years ->
            months?.let {months ->
                for (year in years) {
                    for (month in months) {
                        periods.add(Period.fromYearAndMonth(year, month, timeZone))
                    }
                }
            }
        }
        return periods
    }

    companion object CREATOR: Parcelable.Creator<Filter> {
        override fun createFromParcel(parcel: Parcel): Filter {
            return Filter(parcel)
        }

        override fun newArray(size: Int): Array<Filter?> {
            return arrayOfNulls(size)
        }

        private const val TRUE = "true"
        private const val FALSE = "false"
    }
}