package com.ramitsuri.expensemanager.entities

import android.os.Parcel
import android.os.Parcelable
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZonedDateTime
import java.util.*

data class Period(val start: Long, val end: Long): Parcelable {
    companion object CREATOR: Parcelable.Creator<Period>{
        fun fromYearAndMonth(year: Int?, month: Int?, timeZone: TimeZone): Period {
            val zoneId = timeZone.toZoneId()
            var yearMonth = YearMonth.now(zoneId)
            year?.let {
                yearMonth = yearMonth.withYear(year)
            }
            month?.let {
                yearMonth = yearMonth.withMonth(month)
            }
            val startZonedDateTime = yearMonth.atDay(1).atStartOfDay(zoneId)
            val endZonedDateTime = ZonedDateTime.of(
                    yearMonth.atEndOfMonth()
                            .atTime(END_OF_DAY_TIME),
                    zoneId)
            return Period(startZonedDateTime.toInstant().toEpochMilli(),
                    endZonedDateTime.toInstant().toEpochMilli())
        }

        override fun createFromParcel(parcel: Parcel): Period {
            return Period(parcel)
        }

        override fun newArray(size: Int): Array<Period?> {
            return arrayOfNulls(size)
        }
        private val END_OF_DAY_TIME = LocalTime.of(23, 59, 59, 999000000)
    }

    constructor(parcel: Parcel): this(
            parcel.readLong(),
            parcel.readLong()) {
    }

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(start)
        dest.writeLong(end)
    }
}