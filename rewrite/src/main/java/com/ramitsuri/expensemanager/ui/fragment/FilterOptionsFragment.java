package com.ramitsuri.expensemanager.ui.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.entities.Filter;
import com.ramitsuri.expensemanager.ui.adapter.MonthPickerAdapter;
import com.ramitsuri.expensemanager.viewModel.FilterOptionsViewModel;

import java.util.Arrays;
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
        setupViews(view);
    }

    private void setupViews(@NonNull View view) {
        // Months
        RecyclerView listMonths = view.findViewById(R.id.list_months);
        listMonths.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        listMonths.setHasFixedSize(true);

        final MonthPickerAdapter adapter = new MonthPickerAdapter();
        adapter.setValues(Arrays.asList(getMonths()));
        listMonths.setAdapter(adapter);
        adapter.setCallback(new MonthPickerAdapter.MonthPickerAdapterCallback() {
            @Override
            public void onValuePicked(String value) {
                onMonthPicked(value);
                dismiss();
            }
        });

        // Categories
        final RecyclerView listCategories = view.findViewById(R.id.list_categories);
        listCategories.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        listCategories.setHasFixedSize(true);
        final MonthPickerAdapter categoriesAdapter = new MonthPickerAdapter();
        categoriesAdapter.setCallback(new MonthPickerAdapter.MonthPickerAdapterCallback() {
            @Override
            public void onValuePicked(String value) {
                onCategoryPicked(value);
                dismiss();
            }
        });
        listCategories.setAdapter(categoriesAdapter);
        mViewModel.getCategories().observe(getViewLifecycleOwner(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> categories) {
                Timber.i("Categories received %s", categories);
                categoriesAdapter.setValues(categories);
            }
        });

        // Payment Methods
        final RecyclerView listPaymentMethods = view.findViewById(R.id.list_payment_methods);
        listPaymentMethods.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        listPaymentMethods.setHasFixedSize(true);
        final MonthPickerAdapter paymentsAdapter = new MonthPickerAdapter();
        paymentsAdapter.setCallback(new MonthPickerAdapter.MonthPickerAdapterCallback() {
            @Override
            public void onValuePicked(String value) {
                onPaymentMethodPicked(value);
                dismiss();
            }
        });
        listPaymentMethods.setAdapter(paymentsAdapter);
        mViewModel.getPaymentMethods()
                .observe(getViewLifecycleOwner(), new Observer<List<String>>() {
                    @Override
                    public void onChanged(List<String> paymentMethods) {
                        Timber.i("Payment Methods received %s", paymentMethods);
                        paymentsAdapter.setValues(paymentMethods);
                    }
                });

        // Income
        Button btnIncome = view.findViewById(R.id.btn_get_income);
        btnIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Filter filter = mViewModel.onGetIncome();
                if (mCallback != null) {
                    mCallback.onFilterRequested(filter);
                }
                dismiss();
            }
        });
        if (mViewModel.isIncomeAvailable()) {
            btnIncome.setVisibility(View.VISIBLE);
        } else {
            btnIncome.setVisibility(View.GONE);
        }

        // Starred
        Button btnStarred = view.findViewById(R.id.btn_get_starred);
        btnStarred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Filter filter = mViewModel.onGetStarred();
                if (mCallback != null) {
                    mCallback.onFilterRequested(filter);
                }
                dismiss();
            }
        });
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
}
