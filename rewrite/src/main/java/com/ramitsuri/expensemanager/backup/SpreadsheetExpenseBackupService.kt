package com.ramitsuri.expensemanager.backup

import android.text.TextUtils
import androidx.annotation.WorkerThread
import com.ramitsuri.expensemanager.MainApplication
import com.ramitsuri.expensemanager.constants.Constants
import com.ramitsuri.expensemanager.data.ExpenseManagerDatabase
import com.ramitsuri.expensemanager.entities.Expense
import com.ramitsuri.expensemanager.entities.SheetInfo
import com.ramitsuri.expensemanager.utils.AppHelper
import com.ramitsuri.expensemanager.utils.ObjectHelper
import com.ramitsuri.expensemanager.utils.TransformationHelper
import timber.log.Timber
import java.util.*
import javax.annotation.Nonnull

class SpreadsheetExpenseBackupService: ExpenseBackupService {
    @WorkerThread
    override fun process(): WorkResult<String> {
        try {
            val repository = MainApplication.getInstance().sheetRepository
                    ?: return WorkResult.failure("Sheet repo null")

            // Spreadsheet Id
            val spreadsheetId = AppHelper.getSpreadsheetId()
            if (TextUtils.isEmpty(spreadsheetId)) {
                return WorkResult.failure("Spreadsheet id is empty or null")
            }
            val editedMonths = ExpenseManagerDatabase.getInstance().editedSheetDao().all

            // Expenses
            val expensesToBackup: List<Expense>?
            expensesToBackup = if (editedMonths == null || editedMonths.size == 0) {
                // All expenses will be appended
                ExpenseManagerDatabase.getInstance().expenseDao().allUnsynced
            } else {
                // Expenses might be appended (new) and sheets might be rewritten (update request)
                ExpenseManagerDatabase.getInstance().expenseDao()
                        .getAllForBackup(editedMonths, AppHelper.getTimeZone())
            }
            if (expensesToBackup == null) {
                return WorkResult.failure("Expenses to backup is null")
            }

            // Do not continue if no synced expenses were edited (resulting in a possibility of a
            // sheet now having 0 expenses, in which case expense size can be zero)
            if (expensesToBackup.isEmpty() &&
                    (editedMonths == null || editedMonths.size == 0)) {
                return WorkResult.failure("No new or edited expenses")
            }
            val sheetInfos = TransformationHelper
                    .filterSheetInfos(ExpenseManagerDatabase.getInstance().sheetDao().all)
            if (!isSheetInfosValid(sheetInfos)) {
                WorkResult.failure("No info about sheet ids to attach to expenses")
            }

            // Add Recurring sheet if necessary
            val recurringSheetName = Constants.SheetNames.RECURRING
            var recurringSheetInfo: SheetInfo? = getSheetInfo(sheetInfos, recurringSheetName)
            if (recurringSheetInfo == null) { // Add the Recurring sheet
                val response = repository
                        .getAddSheetResponse(spreadsheetId, recurringSheetName, 14)
                if (response.sheetMetadata != null) {
                    val infoList: MutableList<SheetInfo> = ArrayList()
                    recurringSheetInfo = SheetInfo(response.sheetMetadata)
                    infoList.add(recurringSheetInfo)
                    ExpenseManagerDatabase.getInstance().sheetDao().insertAll(infoList)
                }
            }
            val recurringExpenses =
                    ExpenseManagerDatabase.getInstance().recurringExpenseDao().read()
            val response = repository
                    .getInsertRangeResponse(spreadsheetId, expensesToBackup, editedMonths,
                            sheetInfos, recurringExpenses, recurringSheetInfo)
            if (response.isSuccessful) {
                ExpenseManagerDatabase.getInstance().expenseDao().updateUnsynced()
                // Delete all records of edited sheets as all expenses in them are backed up now
                ExpenseManagerDatabase.getInstance().editedSheetDao().deleteAll()
                return WorkResult.success("Backup successful")
            } else if (response.exception != null) {
                return WorkResult.failure("From operation: " + response.exception)
            }
            return WorkResult.failure("Unknown reason")
        } catch (e: Exception) {
            Timber.e(e)
            return WorkResult.failure("Unknown: $e")
        }
    }

    private fun isSheetInfosValid(@Nonnull sheetInfos: List<SheetInfo>): Boolean {
        val months = AppHelper.getMonths()
        return ObjectHelper.isSheetInfosValid(sheetInfos, months)
    }

    private fun getSheetInfo(@Nonnull sheetInfos: List<SheetInfo>,
            @Nonnull recurringSheetName: String): SheetInfo? {
        return ObjectHelper.getSheetInfo(sheetInfos, recurringSheetName)
    }
}