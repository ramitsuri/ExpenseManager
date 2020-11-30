package com.ramitsuri.expensemanager.ui.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.constants.Constants;
import com.ramitsuri.expensemanager.ui.adapter.ListOptionsItemAdapter;
import com.ramitsuri.expensemanager.ui.adapter.ListItemWrapper;
import com.ramitsuri.expensemanager.viewModel.SetupCategoriesViewModel;

import javax.annotation.Nonnull;

import timber.log.Timber;

public class SetupCategoriesFragment extends BaseFragment {

    private SetupCategoriesViewModel mViewModel;
    private Button mBtnAdd;

    private ListOptionsItemAdapter mAdapter;

    public SetupCategoriesFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_setup_categories, container, false);
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

        mViewModel = new ViewModelProvider(this).get(SetupCategoriesViewModel.class);

        setupViews(view);
    }

    private void setupViews(@Nonnull View view) {
        // Close
        Button btnClose = view.findViewById(R.id.btn_close);
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
        mBtnAdd = view.findViewById(R.id.btn_add);
        mBtnAdd.setOnClickListener(new View.OnClickListener() {
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
        mAdapter = new ListOptionsItemAdapter();
        mAdapter.setCallback(
                new ListOptionsItemAdapter.ListOptionsItemCallback() {
                    @Override
                    public void onItemDeleteRequested(@Nonnull ListItemWrapper value) {
                        Timber.i("Delete requested: %s", value);
                        if (mViewModel.delete(value.getValue())) {
                            Timber.i("Delete succeeded");
                            if (getView() != null) {
                                Snackbar.make(getView(), R.string.setup_category_deleted,
                                        Snackbar.LENGTH_SHORT).show();
                            }
                        } else {
                            Timber.i("Delete failed");
                            Snackbar.make(listItems, R.string.setup_at_least_one,
                                    Snackbar.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onItemEditRequested(@Nonnull ListItemWrapper value) {
                        Timber.i("Edit requested %s", value);
                        showAddEntityDialog(value.getValue());
                    }
                });
        listItems.setAdapter(mAdapter);
        mViewModel.areValuesLoaded()
                .observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean loaded) {
                        refreshAdapter();
                    }
                });

        TabLayout tabLayout = view.findViewById(R.id.tabs);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) { // Monthly
                    onMonthlyTabSelected();
                } else if (tab.getPosition() == 1) { // Annual
                    onAnnualTabSelected();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void refreshAdapter() {
        mAdapter.setValues(mViewModel.getValues());
    }

    private void onAnnualTabSelected() {
        mBtnAdd.setText(R.string.setup_category_add_new_annual);
        mViewModel.onAnnualTabSelected();
        refreshAdapter();
    }

    private void onMonthlyTabSelected() {
        mBtnAdd.setText(R.string.setup_category_add_new_monthly);
        mViewModel.onMonthlyTabSelected();
        refreshAdapter();
    }

    private void showAddEntityDialog(@Nullable final String value) {
        if (getActivity() == null) {
            Timber.e("getActivity() returned null when showing details fragment");
            return;
        }
        Timber.i("Showing category in bottom sheet");
        AddEntityFragment fragment = AddEntityFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BundleKeys.SELECTED_ENTITY, value);
        fragment.setArguments(bundle);
        fragment.setCallback(new AddEntityFragment.AddEntityCallback() {
            @Override
            public void onChanged(@Nullable String newValue) {
                if (TextUtils.isEmpty(newValue)) {
                    Timber.i("New value is empty, ignoring");
                    return;
                }
                if (value == null) { // Add
                    if (mViewModel.add(newValue)) {
                        Timber.i("Add succeeded");
                        if (getView() != null) {
                            Snackbar.make(getView(), R.string.setup_category_added,
                                    Snackbar.LENGTH_SHORT).show();
                        }
                    } else {
                        Timber.i("Add failed");
                        if (getView() != null) {
                            Snackbar.make(getView(), R.string.setup_category_exists,
                                    Snackbar.LENGTH_SHORT).show();
                        }
                    }
                } else { // Edit
                    if (newValue.equalsIgnoreCase(value)) {
                        Timber.i("New value same as old, ignoring");
                    }
                    if (mViewModel.edit(value, newValue)) {
                        Timber.i("Edit succeeded");
                        if (getView() != null) {
                            Snackbar.make(getView(), R.string.setup_category_edited,
                                    Snackbar.LENGTH_SHORT).show();
                        }
                    } else {
                        Timber.i("Edit failed");
                        if (getView() != null) {
                            Snackbar.make(getView(), R.string.setup_category_exists,
                                    Snackbar.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
        fragment.show(getActivity().getSupportFragmentManager(), AddEntityFragment.TAG);
    }
}
