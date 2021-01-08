package com.ramitsuri.expensemanager.work


import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.ramitsuri.expensemanager.constants.Constants
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class RecurringExpensesWorkerTest: BaseWorkerTest() {
    private lateinit var workManager: WorkManager
    private val input = workDataOf(Constants.Work.TYPE to "RecurringExpenseWorker")

    @Before
    override fun setup() {
        super.setup()
        workManager = WorkManager.getInstance(context)
    }

    @Test
    fun testRecurringExpenseWorker_shouldFail_ifNoRecurringExpenses() {
        val request = OneTimeWorkRequestBuilder<RecurringExpensesWorker>()
                .setInputData(input)
                .build()
        // This runs the Worker synchronously because we are using a SynchronousExecutor
        workManager.enqueue(request).result.get()

        val workInfo = workManager.getWorkInfoById(request.id).get()

        // Assert
        assertThat(workInfo.state, `is`(WorkInfo.State.FAILED))
    }
}