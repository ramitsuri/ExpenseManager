package com.ramitsuri.expensemanager.ui.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.entities.Filter;
import com.ramitsuri.expensemanager.ui.adapter.ListPickerAdapter;
import com.ramitsuri.expensemanager.viewModel.FilterOptionsViewModel;

import java.util.Arrays;

import javax.annotation.Nonnull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
        RecyclerView listSheets = view.findViewById(R.id.list_months);
        listSheets.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        listSheets.setHasFixedSize(true);

        final ListPickerAdapter adapter = new ListPickerAdapter();
        adapter.setValues(Arrays.asList(getMonths()), null);
        listSheets.setAdapter(adapter);
        adapter.setCallback(new ListPickerAdapter.ListPickerAdapterCallback() {
            @Override
            public void onItemPicked(String month) {
                dismiss();
                onMonthPicked(month);
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

    private String[] getMonths() {
        return getResources().getStringArray(R.array.months);
    }
}
