package com.ramitsuri.expensemanagerrewrite.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.ramitsuri.expensemanagerrewrite.Constants;
import com.ramitsuri.expensemanagerrewrite.R;
import com.ramitsuri.expensemanagerrewrite.ui.adapter.ListPickerAdapter;
import com.ramitsuri.expensemanagerrewrite.ui.dialog.DatePickerDialog;
import com.ramitsuri.expensemanagerrewrite.viewModel.AddExpenseViewModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import timber.log.Timber;

public class AddExpenseFragment extends BaseFragment implements View.OnClickListener,
        DatePickerDialog.DatePickerDialogCallback {

    // Data
    private AddExpenseViewModel mViewModel;

    // Views
    private ImageView mBtnClose;
    private EditText mEditStore, mEditAmount, mEditDescription;
    private ViewGroup mLayoutDate;

    public AddExpenseFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_expense, container, false);
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

        mViewModel = ViewModelProviders.of(this).get(AddExpenseViewModel.class);

        setupViews(view);

        setupRecyclerViews(view);
    }

    private void setupViews(View view) {
        // Close
        mBtnClose = view.findViewById(R.id.btn_close);
        mBtnClose.setOnClickListener(this);

        // EditTexts
        mEditStore = view.findViewById(R.id.edit_text_store);
        mEditAmount = view.findViewById(R.id.edit_text_amount);
        mEditDescription = view.findViewById(R.id.edit_text_description);

        // ViewGroups
        mLayoutDate = view.findViewById(R.id.layout_date);
        mLayoutDate.setOnClickListener(this);
    }

    private void setupRecyclerViews(View view) {
        // Categories
        RecyclerView listCategories = view.findViewById(R.id.list_categories);
        listCategories.setLayoutManager(new StaggeredGridLayoutManager(
                getResources().getInteger(R.integer.values_grid_view_rows),
                StaggeredGridLayoutManager.HORIZONTAL));
        listCategories.setHasFixedSize(true);
        final ListPickerAdapter categoriesAdapter = new ListPickerAdapter();
        categoriesAdapter.setCallback(new ListPickerAdapter.ListPickerAdapterCallback() {
            @Override
            public void onItemPicked(String value) {
                onCategoryPicked(value);
            }
        });
        listCategories.setAdapter(categoriesAdapter);
        mViewModel.getCategories().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> categories) {
                Timber.i("Categories received " + categories);
                categoriesAdapter.setValues(categories);
            }
        });

        // Payment Methods
        RecyclerView listPaymentMethods = view.findViewById(R.id.list_payment_methods);
        listPaymentMethods.setLayoutManager(new StaggeredGridLayoutManager(
                getResources().getInteger(R.integer.values_grid_view_rows),
                StaggeredGridLayoutManager.HORIZONTAL));
        listPaymentMethods.setHasFixedSize(true);
        final ListPickerAdapter paymentMethodsAdapter = new ListPickerAdapter();
        paymentMethodsAdapter.setCallback(new ListPickerAdapter.ListPickerAdapterCallback() {
            @Override
            public void onItemPicked(String value) {
                onPaymentMethodPicked(value);
            }
        });
        listPaymentMethods.setAdapter(paymentMethodsAdapter);
        mViewModel.getPaymentMethods().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> paymentMethods) {
                Timber.i("Payment Methods received " + paymentMethods);
                paymentMethodsAdapter.setValues(paymentMethods);
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == mLayoutDate) {
            handleDateClicked();
        } else if (view == mBtnClose) {
            handleCloseFragmentClicked();
        }
    }

    private void handleCloseFragmentClicked() {
        Activity activity = getActivity();
        if (activity != null) {
            ((AppCompatActivity)activity).onSupportNavigateUp();
        } else {
            Timber.i("handleCloseFragmentClicked() -> Activity is null");
        }
    }

    private void handleDateClicked() {
        DatePickerDialog dialog = DatePickerDialog.newInstance();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.BundleKeys.DATE_PICKER_YEAR, 2019);
        bundle.putInt(Constants.BundleKeys.DATE_PICKER_MONTH, 7);
        bundle.putInt(Constants.BundleKeys.DATE_PICKER_DAY, 22);
        dialog.setArguments(bundle);
        dialog.setCallback(this);
        if (getActivity() != null) {
            dialog.show(getActivity().getSupportFragmentManager(), DatePickerDialog.TAG);
        } else {
            Timber.e("handleDateClicked -> getActivity() " +
                    "returned null when showing date picker dialog");
        }
    }

    private void onCategoryPicked(String value) {
        Timber.i("Category picked: %s", value);
    }

    private void onPaymentMethodPicked(String value) {
        Timber.i("Payment method picked: %s", value);
    }

    @Override
    public void onDatePicked(int year, int month, int day) {
        Timber.i("Date picked: %s/%s/%s", month + 1, day, year);
    }
}
