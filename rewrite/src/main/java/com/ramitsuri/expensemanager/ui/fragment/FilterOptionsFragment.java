package com.ramitsuri.expensemanager.ui.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import timber.log.Timber;

public class FilterOptionsFragment extends BaseBottomSheetFragment {

    static final String TAG = FilterOptionsFragment.class.getName();

    static FilterOptionsFragment newInstance() {
        return new FilterOptionsFragment();
    }

    @Nonnull
    private FilterOptionsViewModel mViewModel;

    private FilterAdapter mYearAdapter, mMonthAdapter, mPaymentsAdapter, mCategoriesAdapter,
            mFlagAdapter, mRecordTypeAdapter;

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
        if (mYearAdapter != null && mViewModel.getYears() != null) {
            mYearAdapter.setValues(mViewModel.getYears());
        }
        if (mMonthAdapter != null) {
            mMonthAdapter.setValues(mViewModel.getMonths());
        }
        if (mPaymentsAdapter != null) {
            mPaymentsAdapter.setValues(mViewModel.getPayments());
        }
        if (mCategoriesAdapter != null) {
            mCategoriesAdapter.setValues(mViewModel.getCategories());
        }
        if (mFlagAdapter != null) {
            mFlagAdapter.setValues(mViewModel.getFlagStatuses());
        }
        if (mRecordTypeAdapter != null) {
            mRecordTypeAdapter.setValues(mViewModel.getRecordTypes());
        }
        apply(filter);
    }

    private void setupViews(@Nonnull View view) {
        setupYears(view);
        setupMonths(view);
        setupCategories(view);
        setupPaymentMethods(view);
        setupFlagStatus(view);
        setupRecordType(view);
    }

    private void setupYears(@NonNull final View view) {
        // Years
        final RecyclerView list = view.findViewById(R.id.list_years);
        list.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        list.setHasFixedSize(true);
        mYearAdapter = new FilterAdapter();
        mYearAdapter.setCallback(new FilterAdapter.Callback() {
            @Override
            public void onSelected(FilterWrapper value) {
                if (value == null) {
                    return;
                }
                mViewModel.onAddYear(value);
                List<FilterWrapper> years = mViewModel.getYears();
                if (years != null) {
                    mYearAdapter.setValues(years);
                }
                apply(mViewModel.get());
            }

            @Override
            public void onUnselected(FilterWrapper value) {
                if (value == null) {
                    return;
                }
                mViewModel.onRemoveYear(value);
                List<FilterWrapper> years = mViewModel.getYears();
                if (years != null) {
                    mYearAdapter.setValues(years);
                }
                apply(mViewModel.get());
            }
        });
        list.setAdapter(mYearAdapter);
        mViewModel.areYearsAvailable().observe(getViewLifecycleOwner(),
                new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean available) {
                        Timber.i("Years available %s", available);
                        List<FilterWrapper> values = mViewModel.getYears();
                        if (values != null) {
                            mYearAdapter.setValues(values);
                        } else {
                            list.setVisibility(View.GONE);
                            view.findViewById(R.id.text_header_year).setVisibility(View.GONE);
                        }
                    }
                });
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
        list.scrollToPosition(mViewModel.getSelectedMonthPosition());
        mMonthAdapter.setCallback(new FilterAdapter.Callback() {
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
        mCategoriesAdapter.setCallback(new FilterAdapter.Callback() {
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
        mPaymentsAdapter.setCallback(new FilterAdapter.Callback() {
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

    private void setupFlagStatus(@NonNull View view) {
        // Flag
        mFlagAdapter = new FilterAdapter();
        mFlagAdapter.setValues(mViewModel.getFlagStatuses());
        mFlagAdapter.setCallback(new FilterAdapter.Callback() {
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

    private void setupRecordType(@NonNull View view) {
        // Record Type
        mRecordTypeAdapter = new FilterAdapter();
        mRecordTypeAdapter.setValues(mViewModel.getRecordTypes());
        mRecordTypeAdapter.setCallback(new FilterAdapter.Callback() {
            @Override
            public void onSelected(FilterWrapper value) {
                if (value == null || mRecordTypeAdapter.getValues() == null) {
                    return;
                }
                mViewModel.onAddRecordType(mRecordTypeAdapter.getValues(), value);
                mRecordTypeAdapter.setValues(mViewModel.getRecordTypes());
                apply(mViewModel.get());
            }

            @Override
            public void onUnselected(FilterWrapper value) {
                if (value == null || mRecordTypeAdapter.getValues() == null) {
                    return;
                }
                mViewModel.onRemoveRecordType(mRecordTypeAdapter.getValues(), value);
                mRecordTypeAdapter.setValues(mViewModel.getRecordTypes());
                apply(mViewModel.get());
            }
        });

        final RecyclerView list = view.findViewById(R.id.list_record_type);
        list.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        list.setHasFixedSize(true);
        list.setAdapter(mRecordTypeAdapter);
    }

    private void apply(@Nonnull Filter filter) {
        if (mCallback != null) {
            mCallback.onFilterRequested(filter);
        }
    }
}
