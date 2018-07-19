package com.ramitsuri.expensemanager.ui;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.async.SheetsCreateLoader;
import com.ramitsuri.expensemanager.constants.LoaderIDs;
import com.ramitsuri.expensemanager.constants.Others;
import com.ramitsuri.expensemanager.entities.LoaderResponse;
import com.ramitsuri.expensemanager.helper.AppHelper;
import com.ramitsuri.expensemanager.service.BackupService;

import java.util.Arrays;

import pub.devrel.easypermissions.EasyPermissions;

public class BaseNavigationViewActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener, LoaderManager.LoaderCallbacks<LoaderResponse> {

    protected DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private Toolbar mToolbar;
    private NavigationView mNavigationView;
    private LinearLayout mAccount;
    private TextView mAccountText;
    private GoogleAccountCredential mCredential;
    private NavigationDrawerCallbacks mCallbacks;

    public interface NavigationDrawerCallbacks{
        void onSyncClicked();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        try {
            mCallbacks = (NavigationDrawerCallbacks)this;
        }
        catch (Exception e){

        }
    }

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
                //requestSheetsAccess();
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
                            case R.id.nav_sync:
                                if(mCallbacks!=null){
                                    mCallbacks.onSyncClicked();
                                }
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
                        /*getSupportLoaderManager().restartLoader(LoaderIDs.SHEETS_CREATE, null, this)
                                .forceLoad();*/
                    }
                }
                break;
            case Others.REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK && data != null) {
                    getSupportLoaderManager().restartLoader(LoaderIDs.SHEETS_CREATE, null, this)
                            .forceLoad();
                }
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public Loader<LoaderResponse> onCreateLoader(int id, Bundle args) {
        return new SheetsCreateLoader(this, AppHelper.getAccountName());
    }

    @Override
    public void onLoadFinished(Loader<LoaderResponse> loader, LoaderResponse data) {
        if (data.getResponseCode() == LoaderResponse.SUCCESS) {
            AppHelper.setSheetsId(data.getSheetId());
            AppHelper.setFirstBackupComplete(false);
            scheduleJob();
        } else if (data.getResponseCode() == LoaderResponse.REQUEST_ACCESS) {
            startActivityForResult(data.getIntent(), Others.REQUEST_AUTHORIZATION);
        }
    }

    @Override
    public void onLoaderReset(Loader<LoaderResponse> loader) {

    }

    private void scheduleJob() {
        /*ComponentName serviceComponent = new ComponentName(this, BackupService.class);
        JobInfo.Builder builder = new JobInfo.Builder(1, serviceComponent)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setRequiresCharging(true)
                .setPeriodic(86400000)
                .setPersisted(true);
        JobScheduler scheduler = (JobScheduler)getSystemService(Context.JOB_SCHEDULER_SERVICE);
        scheduler.schedule(builder.build());*/
    }
}
