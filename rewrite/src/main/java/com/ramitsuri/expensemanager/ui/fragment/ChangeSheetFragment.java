package com.ramitsuri.expensemanager.ui.fragment;

import android.app.Dialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.ramitsuri.expensemanager.Constants;
import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.entities.SheetInfo;
import com.ramitsuri.expensemanager.ui.adapter.SheetPickerAdapter;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

public class ChangeSheetFragment extends BottomSheetDialogFragment {

    static final String TAG = ChangeSheetFragment.class.getName();

    static ChangeSheetFragment newInstance() {
        return new ChangeSheetFragment();
    }

    private ChangeSheetFragmentCallback mCallback;

    public interface ChangeSheetFragmentCallback {
        void onChangeSheetRequested(@NonNull SheetInfo sheetInfo);
    }

    public void setCallback(@NonNull ChangeSheetFragmentCallback callback) {
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
        View view = inflater.inflate(R.layout.fragment_change_sheet, container, false);
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
        if (getArguments() != null) {
            List<SheetInfo> sheetInfoList =
                    getArguments().getParcelableArrayList(Constants.BundleKeys.SHEET_INFOS);
            int selectedSheetId = getArguments().getInt(Constants.BundleKeys.SELECTED_SHEET_ID);
            if (sheetInfoList != null) {
                Timber.i("Showing %d sheets", sheetInfoList.size());
                setupViews(view, sheetInfoList, selectedSheetId);
            }
        }
    }

    private void setupViews(@NonNull View view, @NonNull final List<SheetInfo> sheetInfoList,
            int selectedSheetId) {
        RecyclerView listSheets = view.findViewById(R.id.list_sheets);
        int numberOfColumns = getResources().getInteger(R.integer.sheets_grid_columns);
        GridLayoutManager manager = new GridLayoutManager(getActivity(), numberOfColumns);
        manager.setOrientation(RecyclerView.VERTICAL);
        listSheets.setLayoutManager(manager);
        listSheets.setHasFixedSize(true);

        SheetPickerAdapter adapter = new SheetPickerAdapter();
        listSheets.setAdapter(adapter);
        adapter.setCallback(new SheetPickerAdapter.SheetPickerAdapterCallback() {
            @Override
            public void onItemPicked(SheetInfo value) {
                dismiss();
                if (mCallback != null) {
                    mCallback.onChangeSheetRequested(value);
                }
            }
        });
        adapter.setValues(sheetInfoList, selectedSheetId, false);
    }
}
