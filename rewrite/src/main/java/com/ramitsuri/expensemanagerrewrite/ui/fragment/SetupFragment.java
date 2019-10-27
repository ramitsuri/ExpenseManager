package com.ramitsuri.expensemanagerrewrite.ui.fragment;

import android.accounts.Account;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;
import com.ramitsuri.expensemanagerrewrite.Constants;
import com.ramitsuri.expensemanagerrewrite.MainApplication;
import com.ramitsuri.expensemanagerrewrite.R;
import com.ramitsuri.expensemanagerrewrite.utils.PrefHelper;
import com.ramitsuri.expensemanagerrewrite.viewModel.SetupViewModel;
import com.ramitsuri.sheetscore.consumerResponse.EntitiesConsumerResponse;
import com.ramitsuri.sheetscore.googleSignIn.AccountManager;
import com.ramitsuri.sheetscore.googleSignIn.SignInResponse;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
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

    private void handleSetupClicked() {
        if (!TextUtils.isEmpty(getSpreadsheetId())) {
            signIn();
        } else {
            Snackbar.make(mBtnSetup, R.string.setup_spreadsheet_id_empty, Snackbar.LENGTH_SHORT)
                    .show();
        }
    }

    private void signIn() {
        String accountName = PrefHelper.get(getString(R.string.settings_key_account_name), null);
        String accountType = PrefHelper.get(getString(R.string.settings_key_account_type), null);
        if (accountName != null && accountType != null) {
            setupEntities(accountName, accountType);
            return;
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

    private void onSaveDataRequested(List<List<String>> stringsList) {
        mViewModel.saveEntities(stringsList);
    }

    private void saveAccountDetailsIfNecessary(Account account) {
        String accountName = PrefHelper.get(getString(R.string.settings_key_account_name), null);
        String accountType = PrefHelper.get(getString(R.string.settings_key_account_type), null);
        if (accountName == null && accountType == null) {
            PrefHelper.set(getString(R.string.settings_key_account_name), account.name);
            PrefHelper.set(getString(R.string.settings_key_account_type), account.type);
        }
    }

    private String getSpreadsheetId() {
        if (mEditSpreadsheetId != null) {
            return mEditSpreadsheetId.getText().toString().trim();
        }
        return null;
    }
}
