package com.ramitsuri.expensemanager.ui.fragment;

import android.app.Dialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.entities.SheetInfo;
import com.ramitsuri.expensemanager.ui.adapter.SheetPickerAdapter;
import com.ramitsuri.expensemanager.viewModel.FilterOptionsViewModel;

import java.util.List;

import javax.annotation.Nonnull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

public class FilterOptionsFragment extends BottomSheetDialogFragment {

    static final String TAG = FilterOptionsFragment.class.getName();

    static FilterOptionsFragment newInstance() {
        return new FilterOptionsFragment();
    }

    @Nonnull
    private FilterOptionsViewModel mViewModel;

    private FilterOptionsFragmentCallback mCallback;

    public interface FilterOptionsFragmentCallback {
        void onFilterRequested(@NonNull SheetInfo sheetInfo);
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
        if ((getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_NO) {
            Timber.i("Light theme, setting status and nav bar light flag");
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR |
                    View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(FilterOptionsViewModel.class);
        setupViews(view);
    }

    private void setupViews(@NonNull View view) {
        RecyclerView listSheets = view.findViewById(R.id.list_sheets);
        int numberOfColumns = getResources().getInteger(R.integer.sheets_grid_columns);
        GridLayoutManager manager = new GridLayoutManager(getActivity(), numberOfColumns);
        manager.setOrientation(RecyclerView.VERTICAL);
        listSheets.setLayoutManager(manager);
        listSheets.setHasFixedSize(true);

        final SheetPickerAdapter adapter = new SheetPickerAdapter();
        listSheets.setAdapter(adapter);
        adapter.setCallback(new SheetPickerAdapter.SheetPickerAdapterCallback() {
            @Override
            public void onItemPicked(SheetInfo value) {
                dismiss();
                if (mCallback != null) {
                    mCallback.onFilterRequested(value);
                }
            }
        });
        LiveData<List<SheetInfo>> sheetInfoList = mViewModel.getSheetInfos();
        if (sheetInfoList != null) {
            sheetInfoList.observe(getViewLifecycleOwner(),
                    new Observer<List<SheetInfo>>() {
                        @Override
                        public void onChanged(List<SheetInfo> sheetInfos) {
                            adapter.setValues(sheetInfos);
                        }
                    });
        }
    }
}
