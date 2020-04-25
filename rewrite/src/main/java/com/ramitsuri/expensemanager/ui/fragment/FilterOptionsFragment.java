package com.ramitsuri.expensemanager.ui.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.entities.Filter;
import com.ramitsuri.expensemanager.ui.adapter.FilterAdapter;
import com.ramitsuri.expensemanager.ui.adapter.FilterWrapper;
import com.ramitsuri.expensemanager.viewModel.FilterOptionsViewModel;

import java.util.ArrayList;
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(FilterOptionsViewModel.class);
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

        final FilterAdapter adapter = new FilterAdapter();
        List<FilterWrapper> wrappers = new ArrayList<>();
        for (String month : getMonths()) {
            wrappers.add(new FilterWrapper(month, false));
        }
        adapter.setValues(wrappers);
        list.setAdapter(adapter);
        adapter.setCallback(new FilterAdapter.MonthPickerAdapterCallback() {
            @Override
            public void onSelected(FilterWrapper value) {
                List<FilterWrapper> values = onItemSelected(adapter.getValues(), value);
                if (values == null) {
                    return;
                }
                adapter.setValues(values);
                Timber.i("New months: %s", values);
            }

            @Override
            public void onUnselected(FilterWrapper value) {
                List<FilterWrapper> values = onItemUnselected(adapter.getValues(), value);
                if (values == null) {
                    return;
                }
                adapter.setValues(values);
                Timber.i("New months: %s", values);
            }
        });
    }

    private void setupCategories(@NonNull View view) {
        // Categories
        final RecyclerView list = view.findViewById(R.id.list_categories);
        list.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        list.setHasFixedSize(true);
        final FilterAdapter adapter = new FilterAdapter();
        adapter.setCallback(new FilterAdapter.MonthPickerAdapterCallback() {
            @Override
            public void onSelected(FilterWrapper value) {
                List<FilterWrapper> values = onItemSelected(adapter.getValues(), value);
                if (values == null) {
                    return;
                }
                adapter.setValues(values);
                Timber.i("New categories: %s", values);
            }

            @Override
            public void onUnselected(FilterWrapper value) {
                List<FilterWrapper> values = onItemUnselected(adapter.getValues(), value);
                if (values == null) {
                    return;
                }
                adapter.setValues(values);
                Timber.i("New categories: %s", values);
            }
        });
        list.setAdapter(adapter);
        mViewModel.getCategories().observe(getViewLifecycleOwner(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> categories) {
                Timber.i("Categories received %s", categories);
                List<FilterWrapper> wrappers = new ArrayList<>();
                for (String category : categories) {
                    wrappers.add(new FilterWrapper(category, false));
                }
                adapter.setValues(wrappers);
            }
        });
    }

    private void setupPaymentMethods(@NonNull View view) {
        // Payment Methods
        final RecyclerView list = view.findViewById(R.id.list_payment_methods);
        list.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        list.setHasFixedSize(true);
        final FilterAdapter adapter = new FilterAdapter();
        adapter.setCallback(new FilterAdapter.MonthPickerAdapterCallback() {
            @Override
            public void onSelected(FilterWrapper value) {
                List<FilterWrapper> values = onItemSelected(adapter.getValues(), value);
                if (values == null) {
                    return;
                }
                adapter.setValues(values);
                Timber.i("New payments: %s", values);
            }

            @Override
            public void onUnselected(FilterWrapper value) {
                List<FilterWrapper> values = onItemUnselected(adapter.getValues(), value);
                if (values == null) {
                    return;
                }
                adapter.setValues(values);
                Timber.i("New payments: %s", values);
            }
        });
        list.setAdapter(adapter);
        mViewModel.getPaymentMethods()
                .observe(getViewLifecycleOwner(), new Observer<List<String>>() {
                    @Override
                    public void onChanged(List<String> paymentMethods) {
                        Timber.i("Payment Methods received %s", paymentMethods);
                        List<FilterWrapper> wrappers = new ArrayList<>();
                        for (String paymentMethod : paymentMethods) {
                            wrappers.add(new FilterWrapper(paymentMethod, false));
                        }
                        adapter.setValues(wrappers);
                    }
                });
    }

    private void setupIncomeStatus(@NonNull View view) {
        if (!mViewModel.isIncomeAvailable()) {
            return;
        }
        // Income
        final FilterAdapter adapter = new FilterAdapter();
        List<FilterWrapper> wrappers = new ArrayList<>();
        wrappers.add(new FilterWrapper(getString(R.string.filter_options_income_incomes), false));
        wrappers.add(new FilterWrapper(getString(R.string.filter_options_income_expenses), false));
        adapter.setValues(wrappers);
        adapter.setCallback(new FilterAdapter.MonthPickerAdapterCallback() {
            @Override
            public void onSelected(FilterWrapper value) {
                List<FilterWrapper> values = onItemSelected(adapter.getValues(), value);
                if (values == null) {
                    return;
                }
                adapter.setValues(values);
                Timber.i("New incomes: %s", values);
            }

            @Override
            public void onUnselected(FilterWrapper value) {
                List<FilterWrapper> values = onItemUnselected(adapter.getValues(), value);
                if (values == null) {
                    return;
                }
                adapter.setValues(values);
                Timber.i("New incomes: %s", values);
            }
        });

        final RecyclerView list = view.findViewById(R.id.list_income_status);
        list.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        list.setHasFixedSize(true);
        list.setAdapter(adapter);
        list.setVisibility(View.VISIBLE);
        view.findViewById(R.id.text_header_income_status).setVisibility(View.VISIBLE);
    }

    private void setupFlagStatus(@NonNull View view) {
        // Flag
        final FilterAdapter adapter = new FilterAdapter();
        List<FilterWrapper> wrappers = new ArrayList<>();
        wrappers.add(new FilterWrapper(getString(R.string.filter_options_flag_flagged), false));
        wrappers.add(new FilterWrapper(getString(R.string.filter_options_flag_not_flagged), false));
        adapter.setValues(wrappers);
        adapter.setCallback(new FilterAdapter.MonthPickerAdapterCallback() {
            @Override
            public void onSelected(FilterWrapper value) {
                List<FilterWrapper> values = onItemSelected(adapter.getValues(), value);
                if (values == null) {
                    return;
                }
                adapter.setValues(values);
                Timber.i("New flag status: %s", values);
            }

            @Override
            public void onUnselected(FilterWrapper value) {
                List<FilterWrapper> values = onItemUnselected(adapter.getValues(), value);
                if (values == null) {
                    return;
                }
                adapter.setValues(values);
                Timber.i("New flag status: %s", values);
            }
        });

        final RecyclerView list = view.findViewById(R.id.list_flag_status);
        list.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        list.setHasFixedSize(true);
        list.setAdapter(adapter);
    }

    /**
     * Converts picked month into its corresponding index in the range 0 - 11
     */
    private void onMonthPicked(String pickedMonth) {
        int index = 0;
        for (String month : getMonths()) {
            if (pickedMonth.equalsIgnoreCase(month)) {
                Filter filter = mViewModel.onMonthPicked(index);
                if (mCallback != null) {
                    mCallback.onFilterRequested(filter);
                }
                break;
            }
            index = index + 1;
        }
    }

    private void onCategoryPicked(String category) {
        Filter filter = mViewModel.onCategoryPicked(category);
        if (mCallback != null) {
            mCallback.onFilterRequested(filter);
        }
    }

    private void onPaymentMethodPicked(String paymentMethod) {
        Filter filter = mViewModel.onPaymentMethodPicked(paymentMethod);
        if (mCallback != null) {
            mCallback.onFilterRequested(filter);
        }
    }

    private String[] getMonths() {
        return getResources().getStringArray(R.array.months);
    }

    @Nullable
    private List<FilterWrapper> onItemSelected(List<FilterWrapper> values, FilterWrapper item) {
        FilterWrapper newValue = new FilterWrapper(item.getValue(), true);
        if (values == null) {
            return null;
        }
        int index = values.indexOf(item);
        if (index == -1) {
            return null;
        }
        values.remove(item);
        values.add(index, newValue);
        return values;
    }

    @Nullable
    private List<FilterWrapper> onItemUnselected(List<FilterWrapper> values, FilterWrapper item) {
        FilterWrapper newValue = new FilterWrapper(item.getValue(), false);
        if (values == null) {
            return null;
        }
        int index = values.indexOf(item);
        if (index == -1) {
            return null;
        }
        values.remove(item);
        values.add(index, newValue);
        return values;
    }
}
