package com.ramitsuri.expensemanager.ui.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.constants.Constants;
import com.ramitsuri.expensemanager.constants.intDefs.RecordType;
import com.ramitsuri.expensemanager.entities.Category;
import com.ramitsuri.expensemanager.entities.Expense;
import com.ramitsuri.expensemanager.entities.PaymentMethod;
import com.ramitsuri.expensemanager.ui.adapter.ListEqualizer;
import com.ramitsuri.expensemanager.ui.adapter.ListPickerAdapter;
import com.ramitsuri.expensemanager.ui.dialog.DatePickerDialog;
import com.ramitsuri.expensemanager.utils.DateHelper;
import com.ramitsuri.expensemanager.utils.DialogHelper;
import com.ramitsuri.expensemanager.viewModel.AddExpenseViewModel;
import com.ramitsuri.expensemanager.viewModel.ViewModelFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import javax.annotation.Nonnull;

import timber.log.Timber;

public class AddExpenseFragment extends BaseFragment implements View.OnClickListener,
        DatePickerDialog.DatePickerDialogCallback {

    // Data
    private AddExpenseViewModel mViewModel;
    private ListPickerAdapter mCategoriesAdapter;
    private ListPickerAdapter mPaymentsAdapter;

    // Views
    private Button mBtnClose;
    private EditText mEditAmount, mEditDescription;
    private AutoCompleteTextView mEditStore;
    private Button mBtnDone, mBtnDate;
    private MaterialButton mBtnFlag;
    private TabLayout mTabLayout;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                handleCloseFragmentClicked();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Expense expense = null;
        if (getArguments() != null) {
            expense = AddExpenseFragmentArgs.fromBundle(getArguments()).getExpense();
        }
        mViewModel = new ViewModelProvider(this, new ViewModelFactory(expense))
                .get(AddExpenseViewModel.class);

        setupRecyclerViews(view);

        setupViews(view);
    }

    private void setupViews(View view) {
        // Close
        mBtnClose = view.findViewById(R.id.btn_close);
        mBtnClose.setOnClickListener(this);

        // EditTexts
        mEditStore = view.findViewById(R.id.edit_text_store);
        mEditAmount = view.findViewById(R.id.edit_text_amount);
        mEditDescription = view.findViewById(R.id.edit_text_description);

        // Done
        mBtnDone = view.findViewById(R.id.btn_done);
        mBtnDone.setOnClickListener(this);

        // Change Date
        mBtnDate = view.findViewById(R.id.btn_date);
        mBtnDate.setOnClickListener(this);

        // Flag
        mBtnFlag = view.findViewById(R.id.btn_flag);
        mBtnFlag.setOnClickListener(this);
        updateExpenseFlag();

        // Income
        SwitchCompat btnIncome = view.findViewById(R.id.toggle_income);
        btnIncome.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mViewModel.setIncome(isChecked);
            }
        });
        if (mViewModel.isIncomeAvailable()) {
            btnIncome.setVisibility(View.VISIBLE);
        } else {
            btnIncome.setVisibility(View.GONE);
        }

        /*
         * Set values from expense on views
         */

        // Store
        String value = mViewModel.getStore();
        if (!TextUtils.isEmpty(value) && !value.equals(getDefaultStoreValue())) {
            mEditStore.setText(value);
            mEditStore.setSelection(value.length());
        }
        // Description
        value = mViewModel.getDescription();
        if (!TextUtils.isEmpty(value) && !value.equals(getDefaultDescriptionValue())) {
            mEditDescription.setText(value);
            mEditDescription.setSelection(value.length());
        }
        // Date
        long longValue = mViewModel.getDate();
        mBtnDate.setText(DateHelper.getFriendlyDate(longValue));

        // Amount
        if (mViewModel.getAmount() != null &&
                mViewModel.getAmount().compareTo(BigDecimal.ZERO) != 0) {
            value = mViewModel.getAmount().toString();
            mEditAmount.setText(value);
            mEditAmount.setSelection(value.length());
        }

        // Income
        btnIncome.setChecked(mViewModel.isIncome());

        mTabLayout = view.findViewById(R.id.tabs_categories);
        mTabLayout.addOnTabSelectedListener(mTabSelectedListener);

        setupStoreAutoComplete();
        setupEntitiesAutoComplete();
    }

    private void setupRecyclerViews(View view) {
        // Categories
        final RecyclerView listCategories = view.findViewById(R.id.list_categories);
        final StaggeredGridLayoutManager categoryLayout = new StaggeredGridLayoutManager(
                getResources().getInteger(R.integer.values_grid_view_rows),
                StaggeredGridLayoutManager.HORIZONTAL);
        listCategories.setLayoutManager(categoryLayout);
        listCategories.setHasFixedSize(true);
        mCategoriesAdapter = new ListPickerAdapter();
        mCategoriesAdapter.setCallback(new ListPickerAdapter.ListPickerAdapterCallback() {
            @Override
            public void onItemPicked(@Nonnull ListEqualizer value) {
                onCategoryPicked(value, true);
            }
        });
        listCategories.setAdapter(mCategoriesAdapter);
        mViewModel.getCategories().observe(getViewLifecycleOwner(), new Observer<List<Category>>() {
            @Override
            public void onChanged(List<Category> categories) {
                Timber.i("Categories received %s", categories);
                Category selectedValue = mViewModel.getCategory();
                // Not able to fix auto selection of tab. Might need to redo this fragment
                // to listen for changes on Expense. That way, would request VM to set stuff on
                // Expense, which will trigger a LiveData update and set the view state
                // selectTab(selectedValue.getRecordType());
                mCategoriesAdapter.setValues(categories, selectedValue);
                if (categories.size() <= 5) {
                    categoryLayout.setSpanCount(getResources()
                            .getInteger(R.integer.values_grid_view_rows_reduced));
                }
            }
        });

        // Payment Methods
        final RecyclerView listPaymentMethods = view.findViewById(R.id.list_payment_methods);
        final StaggeredGridLayoutManager paymentLayout = new StaggeredGridLayoutManager(
                getResources().getInteger(R.integer.values_grid_view_rows),
                StaggeredGridLayoutManager.HORIZONTAL);
        listPaymentMethods.setLayoutManager(paymentLayout);
        listPaymentMethods.setHasFixedSize(true);
        mPaymentsAdapter = new ListPickerAdapter();
        mPaymentsAdapter.setCallback(new ListPickerAdapter.ListPickerAdapterCallback() {
            @Override
            public void onItemPicked(@Nonnull ListEqualizer value) {
                onPaymentMethodPicked(value, true);
            }
        });
        listPaymentMethods.setAdapter(mPaymentsAdapter);
        mViewModel.getPaymentMethods()
                .observe(getViewLifecycleOwner(), new Observer<List<PaymentMethod>>() {
                    @Override
                    public void onChanged(List<PaymentMethod> paymentMethods) {
                        Timber.i("Payment Methods received %s", paymentMethods);
                        PaymentMethod selectedValue = mViewModel.getPaymentMethod();
                        mPaymentsAdapter.setValues(paymentMethods, selectedValue);
                        if (paymentMethods.size() <= 5) {
                            paymentLayout.setSpanCount(getResources()
                                    .getInteger(R.integer.values_grid_view_rows_reduced));
                        }
                    }
                });
    }

    private void setupStoreAutoComplete() {
        if (getContext() == null) {
            Timber.i("getContext is null, cannot setup store autocomplete adapter");
            return;
        }
        // Autocomplete on store edit text
        final ArrayAdapter<String> adapter =
                new ArrayAdapter<>(getContext(), R.layout.spinner_item);
        LiveData<List<String>> stores = mViewModel.getStores();
        if (stores != null) {
            stores.observe(getViewLifecycleOwner(), new Observer<List<String>>() {
                @Override
                public void onChanged(List<String> stores) {
                    Timber.i("Autocomplete stores: %s", stores);
                    adapter.clear();
                    adapter.addAll(stores);
                }
            });
        }
        mEditStore.setAdapter(adapter);
        mEditStore.setThreshold(1);

        mEditStore.addTextChangedListener(new TextWatcherHelper() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s != null) {
                    mViewModel.onStoreValueChanged(s.toString().trim());
                }
            }
        });
    }

    private void setupEntitiesAutoComplete() {
        if (!mViewModel.enableEntitiesAutoComplete()) {
            return;
        }
        mEditStore.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Timber.i("Store EditText has focus, skip auto selections");
                    return;
                } else {
                    Timber.i("Might apply auto selections based on store value");
                }
                String text = mEditStore.getText().toString().trim();
                if (TextUtils.isEmpty(text)) {
                    return;
                }
                mViewModel.getForStore(text).observe(getViewLifecycleOwner(),
                        new Observer<Expense>() {
                            @Override
                            public void onChanged(Expense expense) {
                                if (expense == null) {
                                    return;
                                }

                                if (!TextUtils.isEmpty(expense.getCategory())) {
                                    Category category = new Category(expense.getCategory(),
                                            expense.getRecordType());
                                    mCategoriesAdapter.setSelectedValue(category);
                                    onCategoryPicked(category, false);
                                }
                                if (!TextUtils.isEmpty(expense.getPaymentMethod())) {
                                    PaymentMethod paymentMethod = new PaymentMethod();
                                    paymentMethod.setName(expense.getPaymentMethod());
                                    mPaymentsAdapter.setSelectedValue(paymentMethod);
                                    onPaymentMethodPicked(paymentMethod, false);
                                }
                            }
                        });
            }
        });
    }

    private void onAnnualTabSelected() {
        mViewModel.onAnnualTabSelected();
    }

    private void onMonthlyTabSelected() {
        mViewModel.onMonthlyTabSelected();
    }

    @Override
    public void onClick(View view) {
        if (view == mBtnDate) {
            handleDateClicked();
        } else if (view == mBtnClose) {
            handleCloseFragmentClicked();
        } else if (view == mBtnDone) {
            handleDoneClicked();
        } else if (view == mBtnFlag) {
            handleFlagClicked();
        }
    }

    private void handleCloseFragmentClicked() {
        removeFocusAndHideKeyboard();
        if (mViewModel.isChangesMade()) {
            if (getContext() != null) {
                DialogInterface.OnClickListener negativeListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                exitToUp();
                            }
                        };
                DialogHelper.showAlert(getContext(),
                        R.string.exit_while_editing_warning_message,
                        R.string.common_keep_editing, null,
                        R.string.common_discard, negativeListener);
                return;
            }
        }
        exitToUp();
    }

    private void handleDateClicked() {
        removeFocusAndHideKeyboard();
        LocalDate localDate = DateHelper.getLocalDate(new Date(mViewModel.getDate()));
        int year = DateHelper.getYearFromDate(localDate);
        int month = DateHelper.getMonthFromDate(localDate);
        int day = DateHelper.getDayFromDate(localDate);

        DatePickerDialog dialog = DatePickerDialog.newInstance();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.BundleKeys.DATE_PICKER_YEAR, year);
        bundle.putInt(Constants.BundleKeys.DATE_PICKER_MONTH, month);
        bundle.putInt(Constants.BundleKeys.DATE_PICKER_DAY, day);
        dialog.setArguments(bundle);
        dialog.setCallback(this);
        if (getActivity() != null) {
            dialog.show(getActivity().getSupportFragmentManager(), DatePickerDialog.TAG);
        } else {
            Timber.e(
                    "handleDateClicked -> getActivity() returned null when showing date picker dialog");
        }
    }

    private void handleDoneClicked() {
        Timber.i("Attempting save");
        removeFocusAndHideKeyboard();
        mViewModel.setAmount(getExpenseAmount());
        mViewModel.setStore(getExpenseStore());
        mViewModel.setDescription(getExpenseDescription());
        if (mViewModel.getAddMode() == Constants.AddExpenseMode.ADD) {
            mViewModel.add();
        } else {
            mViewModel.edit();
        }
        exitToUp();
    }

    private void handleFlagClicked() {
        mViewModel.setFlag(!mViewModel.isFlagged());
        updateExpenseFlag();
    }

    private void updateExpenseFlag() {
        if (mViewModel.isFlagged()) {
            mBtnFlag.setIcon(
                    ContextCompat.getDrawable(mBtnFlag.getContext(), R.drawable.ic_flag_on));
        } else {
            mBtnFlag.setIcon(
                    ContextCompat.getDrawable(mBtnFlag.getContext(), R.drawable.ic_flag_off));
        }
    }

    private void onCategoryPicked(ListEqualizer value, boolean hideKeyboard) {
        Timber.i("Category picked: %s", value);
        Category category = (Category) value;
        mViewModel.setCategory(category);
        if (hideKeyboard) {
            removeFocusAndHideKeyboard();
        }
    }

    private final TabLayout.OnTabSelectedListener mTabSelectedListener =
            new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    AddExpenseFragment.this.onTabSelected(tab);
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            };

    private void onTabSelected(@Nonnull TabLayout.Tab tab) {
        if (tab.getPosition() == 0) { // Monthly
            onMonthlyTabSelected();
        } else if (tab.getPosition() == 1) { // Annual
            onAnnualTabSelected();
        }
    }

    private void selectTab(@RecordType @Nonnull String recordType) {
        Timber.i("Select tab invoked");
        int index = 0;
        if (recordType.equals(RecordType.ANNUAL)) {
            index = 1;
        }
        TabLayout.Tab tab = mTabLayout.getTabAt(index);
        if (tab != null) {
            mTabLayout.setScrollPosition(index, 0f, true);
            if (mTabLayout.getSelectedTabPosition() != tab.getPosition()) {
                onTabSelected(tab);
            }
        }
    }

    private void onPaymentMethodPicked(ListEqualizer value, boolean hideKeyboard) {
        Timber.i("Payment method picked: %s", value);
        mViewModel.setPaymentMethod((PaymentMethod) value);
        if (hideKeyboard) {
            removeFocusAndHideKeyboard();
        }
    }

    @Override
    public void onDatePicked(int year, int month, int day) {
        Timber.i("Date picked: %s/%s/%s", month + 1, day, year);
        long pickedDate = DateHelper.getDateFromYearMonthDay(year, month, day);
        mBtnDate.setText(DateHelper.getFriendlyDate(pickedDate));
        mViewModel.setDate(pickedDate);
    }

    private String getExpenseAmount() {
        if (mEditAmount != null) {
            String text = mEditAmount.getText().toString().trim();
            if (!TextUtils.isEmpty(text)) {
                return text;
            }
        }
        return "0.00";
    }

    private String getExpenseStore() {
        if (mEditStore != null) {
            String text = mEditStore.getText().toString().trim();
            if (!TextUtils.isEmpty(text)) {
                return text;
            }
        }
        return getDefaultStoreValue();
    }

    private String getExpenseDescription() {
        if (mEditDescription != null) {
            String text = mEditDescription.getText().toString().trim();
            if (!TextUtils.isEmpty(text)) {
                return text;
            }
        }
        return getDefaultDescriptionValue();
    }

    private void removeFocusAndHideKeyboard() {
        // Remove from Amount field
        if (mEditAmount.hasFocus()) {
            mEditAmount.clearFocus();
            hideKeyboardFrom(mEditAmount);
        }
        // Remove from Store field
        if (mEditStore.hasFocus()) {
            mEditStore.clearFocus();
            hideKeyboardFrom(mEditStore);
        }

        // Remove from Description field
        if (mEditDescription.hasFocus()) {
            mEditDescription.clearFocus();
            hideKeyboardFrom(mEditDescription);
        }
    }

    private String getDefaultStoreValue() {
        return getString(R.string.common_store);
    }

    private String getDefaultDescriptionValue() {
        return getString(R.string.common_expense);
    }
}
