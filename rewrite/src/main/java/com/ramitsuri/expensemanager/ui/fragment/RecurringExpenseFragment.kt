package com.ramitsuri.expensemanager.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import com.ramitsuri.expensemanager.R
import com.ramitsuri.expensemanager.constants.intDefs.RecurType
import com.ramitsuri.expensemanager.entities.RecurringExpenseInfo
import com.ramitsuri.expensemanager.viewModel.RecurringExpenseViewModel
import java.time.Instant

class RecurringExpenseFragment : BaseFragment(), View.OnClickListener {

    // Data
    private lateinit var mViewModel: RecurringExpenseViewModel

    // Views
    private lateinit var mTxtExpenses: TextView
    private lateinit var mBtnDelete: Button
    private lateinit var mBtnAdd: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recurring_expense, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                exitToUp()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel = ViewModelProvider(this).get(RecurringExpenseViewModel::class.java)

        setupViews(view)
    }

    fun setupViews(view: View) {
        mBtnDelete = view.findViewById(R.id.btn_delete)
        mBtnDelete.setOnClickListener(this)

        mBtnAdd = view.findViewById(R.id.btn_add)
        mBtnAdd.setOnClickListener(this)

        mTxtExpenses = view.findViewById(R.id.text_expenses)

        mViewModel.getRecurringExpenses().observe(viewLifecycleOwner, {
            val sb = StringBuilder()
            for (expense in it) {
                sb.append(expense.toString())
                sb.append("\n")
            }
            mTxtExpenses.text = sb.toString()
        })
    }

    var identifier = 0
    override fun onClick(v: View) {
        when (v) {
            mBtnAdd -> {
                val info = RecurringExpenseInfo(
                        identifier.toString(),
                        Instant.now().toEpochMilli(),
                        RecurType.MONTHLY)
                mViewModel.add(info)
                identifier++
            }
            mBtnDelete -> {
                mViewModel.delete(identifier.toString())
            }
        }
    }
}