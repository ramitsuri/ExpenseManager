package com.ramitsuri.expensemanager.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.ui.adapter.MetadataAdapter;
import com.ramitsuri.expensemanager.viewModel.MetadataViewModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

public class MetadataFragment extends BaseFragment {

    // Data
    private MetadataViewModel mViewModel;

    // Views
    private ImageView mBtnClose;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_metadata, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        hideActionBar();
    }

    @Override
    public void onStop() {
        super.onStop();
        showActionBar();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(MetadataViewModel.class);

        setupViews(view);
    }

    private void setupViews(View view) {
        // Logs
        final RecyclerView listLogs = view.findViewById(R.id.list_logs);
        listLogs.setLayoutManager(new LinearLayoutManager(getActivity()));

        final MetadataAdapter logsAdapter = new MetadataAdapter();
        listLogs.setAdapter(logsAdapter);

        mViewModel.getLogs().observe(getViewLifecycleOwner(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                Timber.i("Refreshing logs");
                logsAdapter.setMetadataItems(strings);
            }
        });

        // Sheets
        final RecyclerView listSheets = view.findViewById(R.id.list_sheets);
        listSheets.setLayoutManager(new LinearLayoutManager(getActivity()));

        final MetadataAdapter sheetsAdapter = new MetadataAdapter();
        listSheets.setAdapter(sheetsAdapter);

        // Button delete logs
        Button btnDeleteLogs = view.findViewById(R.id.btn_delete_logs);
        btnDeleteLogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.deleteLogs();
            }
        });

        // Button fetch sheets
        Button btnFetchSheets = view.findViewById(R.id.btn_fetch_sheets);
        btnFetchSheets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.getSheets().observe(getViewLifecycleOwner(),
                        new Observer<List<String>>() {
                            @Override
                            public void onChanged(List<String> strings) {
                                Timber.i("Refreshing sheets");
                                sheetsAdapter.setMetadataItems(strings);
                            }
                        });
            }
        });

        // Close
        mBtnClose = view.findViewById(R.id.btn_close);
        mBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitToUp();
            }
        });
    }
}
