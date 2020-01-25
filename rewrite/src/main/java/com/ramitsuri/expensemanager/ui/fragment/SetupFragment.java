package com.ramitsuri.expensemanager.ui.fragment;

import android.accounts.Account;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;
import com.ramitsuri.expensemanager.Constants;
import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.utils.AppHelper;
import com.ramitsuri.expensemanager.utils.PrefHelper;
import com.ramitsuri.expensemanager.viewModel.SetupViewModel;
import com.ramitsuri.sheetscore.consumerResponse.EntitiesConsumerResponse;
import com.ramitsuri.sheetscore.googleSignIn.AccountManager;
import com.ramitsuri.sheetscore.googleSignIn.SignInResponse;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import timber.log.Timber;

public class SetupFragment extends BaseFragment {

    private SetupViewModel mViewModel;

    private Button mBtnSetup;
    private EditText mEditSpreadsheetId;

    public SetupFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_setup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(SetupViewModel.class);

        mEditSpreadsheetId = view.findViewById(R.id.edit_text_spreadsheet_id);

        mBtnSetup = view.findViewById(R.id.btn_setup);
        mBtnSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleSetupClicked();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        hideActionBar();
    }

    private void handleSetupClicked() {
        if (!TextUtils.isEmpty(getSpreadsheetId())) {
            signIn();
        } else {
            Snackbar.make(mBtnSetup, R.string.setup_spreadsheet_id_empty, Snackbar.LENGTH_SHORT)
                    .show();
        }
    }

    private void signIn() {
        String accountName = AppHelper.getAccountName();
        String accountType = AppHelper.getAccountType();
        if (accountName != null && accountType != null) {
            setupEntities(accountName, accountType);
            return;
        } else {
            Timber.w("AccountType or Name null. Name " + accountName + ", Type " + accountType);
        }

        AccountManager accountManager = new AccountManager();
        SignInResponse response =
                accountManager.prepareSignIn(MainApplication.getInstance(), Constants.SCOPES);
        if (response.getGoogleSignInAccount() != null) {
            Account account = response.getGoogleSignInAccount().getAccount();
            if (account != null) {
                saveAccountDetailsIfNecessary(account);
                setupEntities(account.name, account.type);
            } else {
                Timber.w("SignIn() -> Account is null");
            }
        } else if (response.getGoogleSignInIntent() != null) {
            // request account access
            startActivityForResult(response.getGoogleSignInIntent(),
                    Constants.RequestCode.GOOGLE_SIGN_IN);
        }
    }

    private void setupEntities(String accountName, String accountType) {
        Timber.i("Initiating setup");

        // Init Sheet Repo
        mViewModel.initSheetRepository(accountName, accountType, getSpreadsheetId());

        // Get Categories and PaymentMethods from Google Sheet
        LiveData<EntitiesConsumerResponse> response = mViewModel.getEntitiesFromSheets();
        if (response != null) {
            response.observe(getViewLifecycleOwner(), new Observer<EntitiesConsumerResponse>() {
                @Override
                public void onChanged(EntitiesConsumerResponse response) {
                    Timber.i(response.toString());
                    if (response.getStringLists() != null) {
                        onSaveDataRequested(response.getStringLists());
                    }
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.RequestCode.GOOGLE_SIGN_IN) {
            Account account = AccountManager.getSignInAccountFromIntent(data);
            if (account != null) {
                saveAccountDetailsIfNecessary(account);
                setupEntities(account.name, account.type);
            } else {
                Timber.i("Sign-in failed.");
            }
        }
    }

    private void onSaveDataRequested(List<List<String>> stringsList) {
        mViewModel.saveEntities(stringsList);
        AppHelper.setSpreadsheetId(getSpreadsheetId());

        NavDirections action = SetupFragmentDirections.navActionSetupDone();
        Navigation.findNavController(mBtnSetup).popBackStack();
        Navigation.findNavController(mBtnSetup).navigate(action);
    }

    private void saveAccountDetailsIfNecessary(Account account) {
        String accountName = AppHelper.getAccountName();
        String accountType = AppHelper.getAccountType();
        if (accountName == null && accountType == null) {
            AppHelper.setAccountName(account.name);
            AppHelper.setAccountType(account.type);
        }
    }

    private String getSpreadsheetId() {
        if (mEditSpreadsheetId != null) {
            return mEditSpreadsheetId.getText().toString().trim();
        }
        return null;
    }
}
