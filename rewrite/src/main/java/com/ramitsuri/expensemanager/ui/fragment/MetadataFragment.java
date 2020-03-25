package com.ramitsuri.expensemanager.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.ui.adapter.MetadataAdapter;
import com.ramitsuri.expensemanager.viewModel.MetadataViewModel;

import java.util.List;

import androidx.activity.OnBackPressedCallback;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                exitToUp();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_metadata, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(MetadataViewModel.class);

        setupViews(view);

        mViewModel.refreshLogs();
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

        final EditText editSecret = view.findViewById(R.id.edit_secret);

        // Button delete logs
        Button btnDeleteLogs = view.findViewById(R.id.btn_delete_logs);
        btnDeleteLogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.onDeleteClicked(editSecret.getText().toString());
                editSecret.setText("");
                if (getActivity() != null) { // close keyboard
                    hideKeyboardFrom(getActivity(), editSecret);
                }
            }
        });

        // Close
        // Views
        ImageView btnClose = view.findViewById(R.id.btn_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitToUp();
            }
        });
    }
}
