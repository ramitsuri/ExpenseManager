package com.ramitsuri.expensemanager.ui.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.constants.Constants;
import com.ramitsuri.expensemanager.entities.Filter;
import com.ramitsuri.expensemanager.ui.adapter.FilterAdapter;
import com.ramitsuri.expensemanager.ui.adapter.FilterWrapper;
import com.ramitsuri.expensemanager.viewModel.FilterOptionsViewModel;
import com.ramitsuri.expensemanager.viewModel.ViewModelFactory;

import java.util.List;

import javax.annotation.Nonnull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

public class FilterOptionsFragment extends BaseBottomSheetFragment {

    static final String TAG = FilterOptionsFragment.class.getName();

    static FilterOptionsFragment newInstance() {
        return new FilterOptionsFragment();
    }

    @Nonnull
    private FilterOptionsViewModel mViewModel;

    private FilterAdapter mMonthAdapter, mPaymentsAdapter, mCategoriesAdapter, mIncomeAdapter,
            mFlagAdapter;

    @Nullable
    private FilterOptionsFragmentCallback mCallback;

    public interface FilterOptionsFragmentCallback {
        void onFilterRequested(@NonNull Filter filter);
    }

    public void setCallback(@NonNull FilterOptionsFragmentCallback callback) {
        mCallback = callback;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new BottomSheetDialog(getContext(), R.style.BottomSheetDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter_options, container, false);
        setSystemUiVisibility(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            Filter filter = getArguments().getParcelable(Constants.BundleKeys.FILTER);
            mViewModel = new ViewModelProvider(this, new ViewModelFactory(filter))
                    .get(FilterOptionsViewModel.class);
        } else {
            Timber.w("Not able to get filter from arguments, cannot proceed");
            dismiss();
        }

        setupViews(view);

        Button btnClear = view.findViewById(R.id.btn_clear);
        btnClear.setVisibility(View.GONE);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClearFilter();
            }
        });
    }

    private void onClearFilter() {
        Filter filter = mViewModel.clear();
        if (mMonthAdapter != null) {
            mMonthAdapter.setValues(mViewModel.getMonths());
        }
        if (mPaymentsAdapter != null) {
            mPaymentsAdapter.setValues(mViewModel.getPayments());
        }
        if (mCategoriesAdapter != null) {
            mCategoriesAdapter.setValues(mViewModel.getCategories());
        }
        if (mIncomeAdapter != null) {
            mIncomeAdapter.setValues(mViewModel.getIncomes());
        }
        if (mFlagAdapter != null) {
            mFlagAdapter.setValues(mViewModel.getFlagStatuses());
        }
        apply(filter);
    }

    private void setupViews(@Nonnull View view) {
        setupMonths(view);
        setupCategories(view);
        setupPaymentMethods(view);
        setupFlagStatus(view);
        setupIncomeStatus(view);
    }

    private void setupMonths(@NonNull View view) {
        // Months
        RecyclerView list = view.findViewById(R.id.list_months);
        list.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        list.setHasFixedSize(true);

        mMonthAdapter = new FilterAdapter();
        mMonthAdapter.setValues(mViewModel.getMonths());
        list.setAdapter(mMonthAdapter);
        mMonthAdapter.setCallback(new FilterAdapter.MonthPickerAdapterCallback() {
            @Override
            public void onSelected(FilterWrapper value) {
                if (value == null) {
                    return;
                }
                mViewModel.onAddMonth(value);
                mMonthAdapter.setValues(mViewModel.getMonths());
                apply(mViewModel.get());
            }

            @Override
            public void onUnselected(FilterWrapper value) {
                if (value == null) {
                    return;
                }
                mViewModel.onRemoveMonth(value);
                mMonthAdapter.setValues(mViewModel.getMonths());
                apply(mViewModel.get());
            }
        });
    }

    private void setupCategories(@NonNull final View view) {
        // Categories
        final RecyclerView list = view.findViewById(R.id.list_categories);
        list.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        list.setHasFixedSize(true);
        mCategoriesAdapter = new FilterAdapter();
        mCategoriesAdapter.setCallback(new FilterAdapter.MonthPickerAdapterCallback() {
            @Override
            public void onSelected(FilterWrapper value) {
                if (value == null) {
                    return;
                }
                mViewModel.onAddCategory(value);
                mCategoriesAdapter.setValues(mViewModel.getCategories());
                apply(mViewModel.get());
            }

            @Override
            public void onUnselected(FilterWrapper value) {
                if (value == null) {
                    return;
                }
                mViewModel.onRemoveCategory(value);
                mCategoriesAdapter.setValues(mViewModel.getCategories());
                apply(mViewModel.get());
            }
        });
        list.setAdapter(mCategoriesAdapter);
        mViewModel.areCategoriesAvailable().observe(getViewLifecycleOwner(),
                new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean available) {
                        Timber.i("Categories available %s", available);
                        List<FilterWrapper> values = mViewModel.getCategories();
                        if (values != null) {
                            mCategoriesAdapter.setValues(values);
                        } else {
                            list.setVisibility(View.GONE);
                            view.findViewById(R.id.text_header_category).setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void setupPaymentMethods(@NonNull final View view) {
        // Payment Methods
        final RecyclerView list = view.findViewById(R.id.list_payment_methods);
        list.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        list.setHasFixedSize(true);
        mPaymentsAdapter = new FilterAdapter();
        mPaymentsAdapter.setCallback(new FilterAdapter.MonthPickerAdapterCallback() {
            @Override
            public void onSelected(FilterWrapper value) {
                if (value == null) {
                    return;
                }
                mViewModel.onAddPaymentMethod(value);
                mPaymentsAdapter.setValues(mViewModel.getPayments());
                apply(mViewModel.get());
            }

            @Override
            public void onUnselected(FilterWrapper value) {
                if (value == null) {
                    return;
                }
                mViewModel.onRemovePaymentMethod(value);
                mPaymentsAdapter.setValues(mViewModel.getPayments());
                apply(mViewModel.get());
            }
        });
        list.setAdapter(mPaymentsAdapter);
        mViewModel.arePaymentsAvailable().observe(getViewLifecycleOwner(),
                new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean available) {
                        Timber.i("Payment Methods available %s", available);
                        List<FilterWrapper> values = mViewModel.getPayments();
                        if (values != null) {
                            mPaymentsAdapter.setValues(values);
                        } else {
                            list.setVisibility(View.GONE);
                            view.findViewById(R.id.text_header_payment_method)
                                    .setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void setupIncomeStatus(@NonNull View view) {
        if (!mViewModel.isIncomeAvailable()) {
            return;
        }
        // Income
        mIncomeAdapter = new FilterAdapter();
        mIncomeAdapter.setValues(mViewModel.getIncomes());
        mIncomeAdapter.setCallback(new FilterAdapter.MonthPickerAdapterCallback() {
            @Override
            public void onSelected(FilterWrapper value) {
                if (value == null || mIncomeAdapter.getValues() == null) {
                    return;
                }
                mViewModel.onAddIncome(mIncomeAdapter.getValues(), value);
                mIncomeAdapter.setValues(mViewModel.getIncomes());
                apply(mViewModel.get());
            }

            @Override
            public void onUnselected(FilterWrapper value) {
                if (value == null || mIncomeAdapter.getValues() == null) {
                    return;
                }
                mViewModel.onRemoveIncome(mIncomeAdapter.getValues(), value);
                mIncomeAdapter.setValues(mViewModel.getIncomes());
                apply(mViewModel.get());
            }
        });

        final RecyclerView list = view.findViewById(R.id.list_income_status);
        list.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        list.setHasFixedSize(true);
        list.setAdapter(mIncomeAdapter);
        list.setVisibility(View.VISIBLE);
        view.findViewById(R.id.text_header_income_status).setVisibility(View.VISIBLE);
    }

    private void setupFlagStatus(@NonNull View view) {
        // Flag
        mFlagAdapter = new FilterAdapter();
        mFlagAdapter.setValues(mViewModel.getFlagStatuses());
        mFlagAdapter.setCallback(new FilterAdapter.MonthPickerAdapterCallback() {
            @Override
            public void onSelected(FilterWrapper value) {
                if (value == null || mFlagAdapter.getValues() == null) {
                    return;
                }
                mViewModel.onAddFlag(mFlagAdapter.getValues(), value);
                mFlagAdapter.setValues(mViewModel.getFlagStatuses());
                apply(mViewModel.get());
            }

            @Override
            public void onUnselected(FilterWrapper value) {
                if (value == null || mFlagAdapter.getValues() == null) {
                    return;
                }
                mViewModel.onRemoveFlag(mFlagAdapter.getValues(), value);
                mFlagAdapter.setValues(mViewModel.getFlagStatuses());
                apply(mViewModel.get());
            }
        });

        final RecyclerView list = view.findViewById(R.id.list_flag_status);
        list.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        list.setHasFixedSize(true);
        list.setAdapter(mFlagAdapter);
    }

    private void apply(@Nonnull Filter filter) {
        if (mCallback != null) {
            mCallback.onFilterRequested(filter);
        }
    }
}
