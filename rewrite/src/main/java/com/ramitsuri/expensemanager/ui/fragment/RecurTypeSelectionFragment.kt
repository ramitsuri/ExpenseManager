package com.ramitsuri.expensemanager.ui.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.ramitsuri.expensemanager.R
import com.ramitsuri.expensemanager.constants.Constants
import com.ramitsuri.expensemanager.constants.intDefs.RecurType

class RecurTypeSelectionFragment: BaseBottomSheetFragment() {

    var onSelected: ((String) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        val view: View? = inflater.inflate(R.layout.fragment_recur_type_selection, container, false)
        setSystemUiVisibility(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var selectedRecurType = RecurType.NONE
        arguments?.let {
            selectedRecurType = it.getString(Constants.BundleKeys.RECUR_TYPE) ?: RecurType.NONE
        }
        setupViews(view, selectedRecurType)
    }

    private fun setupViews(view: View, @RecurType defaultSelection: String) {
        val radioGroup = view.findViewById<RadioGroup>(R.id.radio_recur_types)
        val selectedRadioButtonId = when (defaultSelection) {
            RecurType.DAILY -> {
                R.id.radio_recur_type_daily
            }
            RecurType.WEEKLY -> {
                R.id.radio_recur_type_weekly
            }
            RecurType.MONTHLY -> {
                R.id.radio_recur_type_monthly
            }
            else -> {
                R.id.radio_recur_type_none
            }
        }
        view.findViewById<RadioButton>(selectedRadioButtonId)?.isChecked = true
        radioGroup.setOnCheckedChangeListener {_, checkedId ->
            when (checkedId) {
                R.id.radio_recur_type_daily -> {
                    onSelected?.invoke(RecurType.DAILY)
                }
                R.id.radio_recur_type_weekly -> {
                    onSelected?.invoke(RecurType.WEEKLY)
                }
                R.id.radio_recur_type_monthly -> {
                    onSelected?.invoke(RecurType.MONTHLY)
                }
                else -> {
                    onSelected?.invoke(RecurType.NONE)
                }
            }
            dismiss()
        }
    }

    companion object {
        @JvmField
        val TAG = RecurTypeSelectionFragment::class.simpleName
        fun newInstance() = RecurTypeSelectionFragment()
    }
}