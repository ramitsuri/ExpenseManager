package com.ramitsuri.expensemanager.ui.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.ui.adapter.ListPickerMultiSelectionAdapter;
import com.ramitsuri.expensemanager.utils.DialogHelper;
import com.ramitsuri.expensemanager.viewModel.SetupViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import timber.log.Timber;

public class SetupFragment extends BaseFragment {

    private SetupViewModel mViewModel;

    // Views
    private RecyclerView mListItems;
    private Button mBtnAdd, mBtnNext, mBtnPrevious;
    private TextView mTxtHeader;

    public SetupFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_setup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(SetupViewModel.class);

        setupViews(view);

        mViewModel.getCurrentStepLive().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer currentStep) {
                if (currentStep != null) {
                    onCurrentStepChanged(currentStep);
                }
            }
        });
    }

    private void onCurrentStepChanged(@SetupCurrentStep int currentStep) {
        switch (currentStep) {
            case SetupCurrentStep.CATEGORIES:
                mTxtHeader.setText(R.string.setup_edit_categories);
                StaggeredGridLayoutManager manager =
                        new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.HORIZONTAL);
                mListItems.setLayoutManager(manager);
                final ListPickerMultiSelectionAdapter categoryAdapter =
                        new ListPickerMultiSelectionAdapter();
                categoryAdapter.setCallback(
                        new ListPickerMultiSelectionAdapter.ListPickerMultiSelectionCallback() {
                            @Override
                            public void onItemsChanged(List<String> values) {
                                mViewModel.setCategories(values);
                            }
                        });
                mListItems.setAdapter(categoryAdapter);
                mViewModel.getCategoriesLive()
                        .observe(getViewLifecycleOwner(), new Observer<List<String>>() {
                            @Override
                            public void onChanged(List<String> strings) {
                                categoryAdapter.setValues(strings);
                            }
                        });
                break;

            case SetupCurrentStep.PAYMENT_METHODS:
                mTxtHeader.setText(R.string.setup_edit_payment_methods);
                manager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.HORIZONTAL);
                mListItems.setLayoutManager(manager);
                final ListPickerMultiSelectionAdapter paymentAdapter =
                        new ListPickerMultiSelectionAdapter();
                paymentAdapter.setCallback(
                        new ListPickerMultiSelectionAdapter.ListPickerMultiSelectionCallback() {
                            @Override
                            public void onItemsChanged(List<String> values) {
                                mViewModel.setPaymentMethods(values);
                            }
                        });
                mListItems.setAdapter(paymentAdapter);
                mViewModel.getPaymentMethodsLive()
                        .observe(getViewLifecycleOwner(), new Observer<List<String>>() {
                            @Override
                            public void onChanged(List<String> strings) {
                                paymentAdapter.setValues(strings);
                            }
                        });
                break;

            case SetupCurrentStep.BUDGETS:
                mTxtHeader.setText(R.string.setup_edit_budgets);
                break;
        }

        mBtnPrevious.setEnabled(mViewModel.canGoPrevious());

        if (mViewModel.canGoNext()) {
            mBtnNext.setText(R.string.common_next);
            mBtnNext.setEnabled(true);
        } else {
            mBtnNext.setText(R.string.common_done);
        }
    }

    private void setupViews(@Nonnull View view) {
        mTxtHeader = view.findViewById(R.id.text_current_item);

        mBtnAdd = view.findViewById(R.id.btn_add);
        mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddEntityDialog();
            }
        });

        mBtnNext = view.findViewById(R.id.btn_next);
        mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mViewModel.canGoNext()) {
                    exitToUp();
                } else {
                    mViewModel.goNext();
                }
            }
        });

        mBtnPrevious = view.findViewById(R.id.btn_previous);
        mBtnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.goPrevious();
            }
        });

        mListItems = view.findViewById(R.id.list_items);
    }

    private void addCategory(String newValue) {
        Timber.i("Add new category %s", newValue);
        if (mListItems == null || mListItems.getAdapter() == null) {
            Timber.i("Failed to add new. RecyclerView or its adapter null");
            return;
        }
        if (mListItems.getAdapter() instanceof ListPickerMultiSelectionAdapter) {
            ListPickerMultiSelectionAdapter adapter =
                    (ListPickerMultiSelectionAdapter)mListItems.getAdapter();
            List<String> values = adapter.getValues();
            if (values == null) {
                values = new ArrayList<>();
            }
            values.add(newValue);
            mViewModel.setCategories(values);
        } else {
            Timber.i("Not a valid adapter for categories");
        }
    }

    private void addBudget(String newValue) {
        Timber.i("Add new budget %s", newValue);
    }

    private void addPaymentMethod(String newValue) {
        Timber.i("Add new payment method %s", newValue);
        if (mListItems == null || mListItems.getAdapter() == null) {
            Timber.i("Failed to add new. RecyclerView or its adapter null");
            return;
        }
        if (mListItems.getAdapter() instanceof ListPickerMultiSelectionAdapter) {
            ListPickerMultiSelectionAdapter adapter =
                    (ListPickerMultiSelectionAdapter)mListItems.getAdapter();
            List<String> values = adapter.getValues();
            if (values == null) {
                values = new ArrayList<>();
            }
            values.add(newValue);
            mViewModel.setPaymentMethods(values);
        } else {
            Timber.i("Not a valid adapter for categories");
        }
    }

    private void showAddEntityDialog() {
        Context context = getContext();
        if (context == null) {
            return;
        }
        final int currentStep = mViewModel.getCurrentStep();
        final EditText input = new EditText(context);
        DialogInterface.OnClickListener positiveListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (TextUtils.isEmpty(input.getText().toString())) {
                            return;
                        }
                        if (currentStep == SetupCurrentStep.CATEGORIES) {
                            addCategory(input.getText().toString());
                        } else if (currentStep == SetupCurrentStep.BUDGETS) {
                            addBudget(input.getText().toString());
                        } else if (currentStep == SetupCurrentStep.PAYMENT_METHODS) {
                            addPaymentMethod(input.getText().toString());
                        }
                    }
                };

        DialogHelper.showAlertWithInput(context,
                input,
                R.string.setup_add_new,
                R.string.common_ok, positiveListener,
                R.string.common_cancel, null);
    }
}
