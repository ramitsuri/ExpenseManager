package com.ramitsuri.expensemanager.ui;

import android.Manifest;
import android.accounts.AccountManager;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.constants.Others;
import com.ramitsuri.expensemanager.helper.AppHelper;

import java.util.Arrays;

import pub.devrel.easypermissions.EasyPermissions;

public class BaseNavigationViewActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener{

    protected DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private Toolbar mToolbar;
    private NavigationView mNavigationView;
    private LinearLayout mAccount;
    private TextView mAccountText;
    private GoogleAccountCredential mCredential;
    private GoogleApiClient mGoogleApiClient;

    @Override
    public void setContentView(int layoutResID) {
        mDrawerLayout = (DrawerLayout)
                getLayoutInflater().inflate(R.layout.activity_base_navigation_view, null);
        super.setContentView(mDrawerLayout);

        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(Others.SCOPES))
                .setBackOff(new ExponentialBackOff());

        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
                R.string.drawer_open, R.string.drawer_closed);

        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();

        mNavigationView = (NavigationView)findViewById(R.id.nav_view);
        if (mNavigationView != null) {
            setupDrawerContent(mNavigationView);
        }
        mNavigationView.getMenu().getItem(0).setChecked(true);
        View headerLayout = mNavigationView.getHeaderView(0);
        mAccount = (LinearLayout)headerLayout.findViewById(R.id.header);
        mAccountText = (TextView)headerLayout.findViewById(R.id.account);
        mAccountText.setText(
                AppHelper.getAccountName() == null ? getString(R.string.connect_account) :
                        AppHelper.getAccountName());
        mAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermission();
            }
        });

        FrameLayout activityContainer =
                (FrameLayout)mDrawerLayout.findViewById(R.id.content_frame);
        getLayoutInflater().inflate(layoutResID, activityContainer, true);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        //menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        switch (menuItem.getItemId()) {
                            case R.id.nav_expenses:
                                startExpensesActivity();
                                break;
                            case R.id.nav_all_expenses:
                                startAllExpenseActivity();
                                break;
                            case R.id.nav_categories:
                                startCategoriesActivity();
                                break;
                            case R.id.nav_payment_methods:
                                startPaymentMethodsActivity();
                                break;
                            case R.id.nav_settings:
                                startSettingsActivity();
                                break;
                        }
                        return onOptionsItemSelected(menuItem);
                    }
                });
    }

    private void startSettingsActivity() {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    private void startAllExpenseActivity() {
        startActivity(new Intent(this, AllExpensesActivity.class));
    }

    private void startExpensesActivity() {
        /*if(mSelectedMenuItem == null || mSelectedMenuItem.getItemId() != R.id.nav_expenses) {
            Intent intent = new Intent(this, MainActivity.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            //mSelectedMenuItem = null;
            finish();
            //startActivity(intent);
        }*/
    }

    private void startCategoriesActivity() {
        Intent intent = new Intent(this, CategoriesActivity.class);
        startActivity(intent);
    }

    private void startPaymentMethodsActivity() {
        Intent intent = new Intent(this, PaymentMethodsActivity.class);
        startActivity(intent);
    }

    private void requestPermission() {
        if (AppHelper.getAccountName() != null) {
            return;
        } else if (EasyPermissions.hasPermissions(this, Manifest.permission.GET_ACCOUNTS)) {
            startActivityForResult(mCredential.newChooseAccountIntent(),
                    Others.REQUEST_ACCOUNT_PICKER);
        } else {
            EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.account_access_permission_text),
                    Others.REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Others.REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        AppHelper.setAccountName(accountName);
                        mAccountText.setText(accountName);
                        mCredential.setSelectedAccountName(accountName);
                        requestSheetsAccess();
                    }
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        if (EasyPermissions.hasPermissions(this, Manifest.permission.GET_ACCOUNTS)) {
            startActivityForResult(mCredential.newChooseAccountIntent(),
                    Others.REQUEST_ACCOUNT_PICKER);
        }
    }

    private void requestSheetsAccess() {
        GoogleSignInOptions gso =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestScopes(new Scope(Others.SCOPES[0]), new Scope(Others.SCOPES[1]))
                        .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, Others.REQUEST_AUTHORIZATION);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
