package com.ramitsuri.expensemanager.ui.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.constants.Constants;
import com.ramitsuri.expensemanager.entities.Expense;
import com.ramitsuri.expensemanager.entities.SheetInfo;
import com.ramitsuri.expensemanager.ui.adapter.ListPickerAdapter;
import com.ramitsuri.expensemanager.ui.adapter.SheetPickerAdapter;
import com.ramitsuri.expensemanager.ui.dialog.DatePickerDialog;
import com.ramitsuri.expensemanager.utils.DateHelper;
import com.ramitsuri.expensemanager.utils.DialogHelper;
import com.ramitsuri.expensemanager.viewModel.AddExpenseViewModel;
import com.ramitsuri.expensemanager.viewModel.ViewModelFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import timber.log.Timber;

public class AddExpenseFragment extends BaseFragment implements View.OnClickListener,
        DatePickerDialog.DatePickerDialogCallback {

    // Data
    private AddExpenseViewModel mViewModel;

    // Views
    private ViewGroup mContainerSheets, mContainerSheetName, mContainerSheetNameSecondary;
    private ImageView mBtnClose;
    private EditText mEditStore, mEditAmount, mEditDescription;
    private Button mBtnDone, mBtnDate;
    private MaterialButton mBtnFlag, mBtnSplit;
    private RecyclerView mListSheets;
    private TextView mTxtSheetName, mTxtSheetNameSecondary;
    private View mBackground;

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

        setupViews(view);

        setupRecyclerViews(view);
    }

    private void setupViews(View view) {
        mContainerSheets = view.findViewById(R.id.container_sheets);
        mContainerSheetName = view.findViewById(R.id.container_sheet_name);
        mContainerSheetName.setOnClickListener(this);
        mContainerSheetNameSecondary = view.findViewById(R.id.container_sheet_name_secondary);
        mContainerSheetNameSecondary.setOnClickListener(this);
        mBackground = view.findViewById(R.id.background);
        mBackground.setOnClickListener(this);

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

        // Split
        mBtnSplit = view.findViewById(R.id.btn_split);
        mBtnSplit.setOnClickListener(this);

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

        // Sheet Name
        mTxtSheetName = view.findViewById(R.id.txt_sheet_name);
        mTxtSheetName.setOnClickListener(this);
        mTxtSheetNameSecondary = view.findViewById(R.id.txt_sheet_name_secondary);
        mTxtSheetNameSecondary.setOnClickListener(this);
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
        mViewModel.getCategories().observe(getViewLifecycleOwner(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> categories) {
                Timber.i("Categories received %s", categories);
                String selectedValue = null;
                selectedValue = mViewModel.getCategory();
                categoriesAdapter.setValues(categories, selectedValue);
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
        mViewModel.getPaymentMethods()
                .observe(getViewLifecycleOwner(), new Observer<List<String>>() {
                    @Override
                    public void onChanged(List<String> paymentMethods) {
                        Timber.i("Payment Methods received %s", paymentMethods);
                        String selectedValue = null;
                        selectedValue = mViewModel.getPaymentMethod();
                        paymentMethodsAdapter.setValues(paymentMethods, selectedValue);
                        if (mViewModel.isSplitAvailable()) {
                            mBtnSplit.setVisibility(View.VISIBLE);
                        } else {
                            mBtnSplit.setVisibility(View.GONE);
                        }
                    }
                });

        // Sheets
        mListSheets = view.findViewById(R.id.list_sheets);
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 3);
        manager.setOrientation(RecyclerView.VERTICAL);
        mListSheets.setLayoutManager(manager);
        mListSheets.setHasFixedSize(true);

        final SheetPickerAdapter sheetsAdapter = new SheetPickerAdapter();
        sheetsAdapter.setCallback(new SheetPickerAdapter.SheetPickerAdapterCallback() {
            @Override
            public void onItemPicked(SheetInfo value) {
                onSheetPicked(value);
            }
        });
        mListSheets.setAdapter(sheetsAdapter);
        LiveData<List<SheetInfo>> sheetInfos = mViewModel.getSheetInfos();
        if (sheetInfos != null) {
            sheetInfos.observe(getViewLifecycleOwner(), new Observer<List<SheetInfo>>() {
                @Override
                public void onChanged(List<SheetInfo> sheetInfos) {
                    Timber.i("SheetInfos received %s", sheetInfos);
                    int selectedValue = mViewModel.getSheetId();
                    for (SheetInfo info : sheetInfos) {
                        if (selectedValue == info.getSheetId()) {
                            onSheetPicked(info);
                            break;
                        }
                    }
                    sheetsAdapter.setValues(sheetInfos, selectedValue);
                }
            });
        }
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
        } else if (view == mBtnSplit) {
            handleSplitClicked();
        } else if (view == mTxtSheetName || view == mContainerSheetName) {
            handleChangeSheetClicked();
        } else if (view == mTxtSheetNameSecondary || view == mContainerSheetNameSecondary) {
            collapseContainerView();
        } else if (view == mBackground) {
            collapseContainerView();
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

    private void handleChangeSheetClicked() {
        expandContainerView();
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

    private void handleSplitClicked() {
        mViewModel.setSplit();
        if (mViewModel.isSplit()) {
            mBtnSplit.setIcon(
                    ContextCompat.getDrawable(mBtnSplit.getContext(), R.drawable.ic_split_on));
        } else {
            mBtnSplit.setIcon(
                    ContextCompat.getDrawable(mBtnSplit.getContext(), R.drawable.ic_split_off));
        }
    }

    private void onCategoryPicked(String value) {
        Timber.i("Category picked: %s", value);
        mViewModel.setCategory(value);
        removeFocusAndHideKeyboard();
    }

    private void onPaymentMethodPicked(String value) {
        Timber.i("Payment method picked: %s", value);
        mViewModel.setPaymentMethod(value);
        removeFocusAndHideKeyboard();
    }

    private void onSheetPicked(SheetInfo value) {
        Timber.i("Sheet picked: %s", value.toString());
        mTxtSheetName.setText(value.getSheetName());
        mTxtSheetNameSecondary.setText(value.getSheetName());
        mViewModel.setSheet(value);
        removeFocusAndHideKeyboard();
        collapseContainerView();
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

    private void collapseContainerView() {
        Timber.i("Collapsing");
        ViewGroup.LayoutParams params = mContainerSheets.getLayoutParams();
        params.height = 0;
        mContainerSheets.setLayoutParams(params);
        if (mListSheets != null && mListSheets.getAdapter() != null) {
            mListSheets.getAdapter().notifyDataSetChanged();
        }
        if (mBackground != null) {
            mBackground.setClickable(false);
        }
    }

    private void expandContainerView() {
        Timber.i("Expanding");
        ViewGroup.LayoutParams params = mContainerSheets.getLayoutParams();
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        mContainerSheets.setLayoutParams(params);
        if (mListSheets != null && mListSheets.getAdapter() != null) {
            mListSheets.getAdapter().notifyDataSetChanged();
        }
        if (mBackground != null) {
            mBackground.setClickable(true);
        }
    }
}
