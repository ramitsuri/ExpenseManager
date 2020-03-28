package com.ramitsuri.expensemanager.ui.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.material.snackbar.Snackbar;
import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.ui.adapter.ListOptionsItemAdapter;
import com.ramitsuri.expensemanager.utils.DialogHelper;
import com.ramitsuri.expensemanager.viewModel.SetupPaymentMethodsViewModel;

import java.util.List;

import javax.annotation.Nonnull;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

public class SetupPaymentMethodsFragment extends BaseFragment {

    private SetupPaymentMethodsViewModel mViewModel;

    public SetupPaymentMethodsFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_setup_payment_methods, container, false);
    }

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(SetupPaymentMethodsViewModel.class);

        setupViews(view);
    }

    private void setupViews(@Nonnull View view) {
        // Close
        ImageView btnClose = view.findViewById(R.id.btn_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitToUp();
            }
        });

        // Done
        Button btnDone = view.findViewById(R.id.btn_done);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.save();
                exitToUp();
            }
        });

        // Add new
        Button btnAdd = view.findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timber.i("Add requested");
                showAddEntityDialog(null);
            }
        });

        // List
        final RecyclerView listItems = view.findViewById(R.id.list_items);
        if (getContext() != null) {
            DividerItemDecoration divider =
                    new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
            Drawable dividerDrawable =
                    ContextCompat.getDrawable(getContext(), R.drawable.recycler_view_divider);
            if (dividerDrawable != null) {
                divider.setDrawable(dividerDrawable);
                listItems.addItemDecoration(divider);
            }
        }
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        listItems.setLayoutManager(manager);
        final ListOptionsItemAdapter adapter = new ListOptionsItemAdapter();
        adapter.setCallback(
                new ListOptionsItemAdapter.ListOptionsItemCallback() {
                    @Override
                    public void onItemDeleteRequested(@Nonnull String value) {
                        Timber.i("Delete requested: %s", value);
                        if (mViewModel.delete(value)) {
                            Timber.i("Delete succeeded");
                            if (getView() != null) {
                                Snackbar.make(getView(), R.string.setup_payment_method_deleted,
                                        Snackbar.LENGTH_SHORT).show();
                            }
                        } else {
                            Timber.i("Delete failed");
                            Snackbar.make(listItems, R.string.setup_at_least_one,
                                    Snackbar.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onItemEditRequested(@Nonnull String value) {
                        Timber.i("Edit requested %s", value);
                        showAddEntityDialog(value);
                    }
                });
        listItems.setAdapter(adapter);
        mViewModel.getValuesLive()
                .observe(getViewLifecycleOwner(), new Observer<List<String>>() {
                    @Override
                    public void onChanged(List<String> strings) {
                        adapter.setValues(strings);
                    }
                });
    }

    private void showAddEntityDialog(@Nullable final String value) {
        Context context = getContext();
        if (context == null) {
            return;
        }
        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        if (value != null) {
            input.setText(value);
            input.setSelection(value.length());
        }
        DialogInterface.OnClickListener positiveListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newValue = input.getText().toString().trim();
                        if (TextUtils.isEmpty(newValue)) {
                            return;
                        }
                        if (value == null) {
                            if (mViewModel.add(newValue)) {
                                Timber.i("Add succeeded");
                                if (getView() != null) {
                                    Snackbar.make(getView(), R.string.setup_payment_method_added,
                                            Snackbar.LENGTH_SHORT).show();
                                }
                            } else {
                                Timber.i("Add failed");
                                if (getView() != null) {
                                    Snackbar.make(getView(), R.string.setup_payment_method_exists,
                                            Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            if (mViewModel.edit(value, newValue)) {
                                Timber.i("Edit succeeded");
                                if (getView() != null) {
                                    Snackbar.make(getView(), R.string.setup_payment_method_edited,
                                            Snackbar.LENGTH_SHORT).show();
                                }
                            } else {
                                Timber.i("Edit failed");
                                if (getView() != null) {
                                    Snackbar.make(getView(), R.string.setup_payment_method_exists,
                                            Snackbar.LENGTH_SHORT).show();
                                }
                            }
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
