package com.ramitsuri.expensemanager.backup

sealed class WorkResult<T> {
    data class Success<T>(val data: T): WorkResult<T>()

    data class Failure<T>(val data: T): WorkResult<T>()

    companion object {
        fun <T> success(data: T) = Success(data)

        fun <T> failure(data: T) = Failure(data)
    }
}