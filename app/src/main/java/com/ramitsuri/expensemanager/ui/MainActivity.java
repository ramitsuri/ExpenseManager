package com.ramitsuri.expensemanager.ui;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.async.SheetsBackupTask;
import com.ramitsuri.expensemanager.backup.BackupWorker;
import com.ramitsuri.expensemanager.constants.ExpenseViewType;
import com.ramitsuri.expensemanager.constants.Others;
import com.ramitsuri.expensemanager.entities.LoaderResponse;
import com.ramitsuri.expensemanager.fragments.SelectedExpensesFragment;
import com.ramitsuri.expensemanager.helper.AppHelper;
import com.ramitsuri.expensemanager.helper.CategoryHelper;
import com.ramitsuri.expensemanager.helper.ExpenseHelper;
import com.ramitsuri.expensemanager.helper.PaymentMethodHelper;

import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkStatus;
import pub.devrel.easypermissions.EasyPermissions;

import static com.ramitsuri.expensemanager.constants.Others.REQUEST_AUTHORIZATION;

public class MainActivity extends BaseNavigationViewActivity
        implements EasyPermissions.PermissionCallbacks,
        SelectedExpensesFragment.OnFragmentInteractionListener, View.OnClickListener,
        BaseNavigationViewActivity.NavigationDrawerCallbacks {

    private SelectedExpensesFragment mTodayFragment, mWeekFragment, mMonthFragment;
    private FloatingActionButton mFabAddExpense;

    private static final String ACTION_ADD_EXPENSE = "com.ramitsuri.expensemanager.ui.ADD_EXPENSE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupViews();

        setupFragments();

        switchFragments(R.id.tab_today);

        debug();
       /* WorkManager.getInstance().cancelAllWorkByTag("Backup");
        AppHelper.setBackupWorkerEnqueued(false);*/

        if (!AppHelper.isBackupWorkerEnqueued()) {
            Constraints myConstraints = new Constraints.Builder()
                    .setRequiresCharging(false)
                    .setRequiredNetworkType(NetworkType.UNMETERED)
                    .build();

            PeriodicWorkRequest.Builder periodicWorkRequestBuilder =
                    new PeriodicWorkRequest.Builder(BackupWorker.class, 12, TimeUnit.HOURS)
                            .setConstraints(myConstraints)
                            .addTag("Backup");
            PeriodicWorkRequest request = periodicWorkRequestBuilder.build();
            WorkManager.getInstance().enqueue(request);
            AppHelper.setBackupWorkerEnqueued(true);
            Log.w("wirk", "enqueed");
        }

        WorkManager.getInstance().getStatusesByTag("Backup").observe(this,
                new Observer<List<WorkStatus>>() {
                    @Override
                    public void onChanged(@Nullable List<WorkStatus> workStatuses) {
                        if (workStatuses == null || workStatuses.isEmpty()) {
                            Log.w("wirk ", "empty");
                            return;
                        }
                        Log.w("wirk: ", workStatuses.get(0).toString());
                    }
                });
        if (ACTION_ADD_EXPENSE.equals(getIntent().getAction())) {
            // Invoked via the manifest shortcut.
            startActivity(new Intent(this, ExpenseDetailActivity.class));
        }
    }

    private void setupViews() {

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        mFabAddExpense = (FloatingActionButton)findViewById(R.id.fab_add);
        mFabAddExpense.setOnClickListener(this);
        mFabAddExpense.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                deleteAllExpenses();
                return true;
            }
        });

        BottomNavigationView bottomNavigation =
                (BottomNavigationView)findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private void deleteAllExpenses() {
        ExpenseHelper.deleteAll();
        Toast.makeText(MainActivity.this, "Expenses deleted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switchFragments(item.getItemId());
            return true;
        }
    };

    private void switchFragments(int itemId) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (itemId) {
            case R.id.tab_week:
                transaction.replace(R.id.content_container, mWeekFragment);
                break;
            case R.id.tab_month:
                transaction.replace(R.id.content_container, mMonthFragment);
                break;
            case R.id.tab_today:
                transaction.replace(R.id.content_container, mTodayFragment);
                break;
        }
        transaction.commit();
    }

    private void setupFragments() {
        Bundle args = new Bundle();
        mTodayFragment = new SelectedExpensesFragment();
        args.putInt(Others.EXPENSE_VIEW_TYPE, ExpenseViewType.TODAY);
        mTodayFragment.setArguments(args);

        args = new Bundle();
        mWeekFragment = new SelectedExpensesFragment();
        args.putInt(Others.EXPENSE_VIEW_TYPE, ExpenseViewType.WEEK);
        mWeekFragment.setArguments(args);

        args = new Bundle();
        mMonthFragment = new SelectedExpensesFragment();
        args.putInt(Others.EXPENSE_VIEW_TYPE, ExpenseViewType.MONTH);
        mMonthFragment.setArguments(args);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onClick(View view) {
        if (view == mFabAddExpense) {
            startActivity(new Intent(this, ExpenseDetailActivity.class));
        }
    }

   /* public void getDb() {
            try {
                InputStream myInput = new FileInputStream("/data/data/com.ramitsuri.expensemanager/databases/expensemanager.db");

                File file = new File(Environment.getExternalStorageDirectory().getPath()+"/"+"expensemanager.db");
                if (!file.exists()){
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        Log.i("FO","File creation failed for " + file);
                    }
                }

                OutputStream myOutput = new FileOutputStream(Environment.getExternalStorageDirectory().getPath()+"/"+"expensemanager.db");

                byte[] buffer = new byte[1024];
                int length;
                while ((length = myInput.read(buffer))>0){
                    myOutput.write(buffer, 0, length);
                }

                //Close the streams
                myOutput.flush();
                myOutput.close();
                myInput.close();
                Log.i("FO","copied");

            } catch (Exception e) {
                Log.i("FO","exception="+e);
            }
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                //Log.v(TAG,"Permission is granted");
                return true;
            } else {

                //Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            //Log.v(TAG,"Permission is granted");
            return true;
        }
    }*/

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    public static void debug() {
        if (!AppHelper.isFirstRunComplete()) {
            CategoryHelper.addCategory("Food");
            CategoryHelper.addCategory("Travel");
            CategoryHelper.addCategory("Entertainment");
            CategoryHelper.addCategory("Utilities");
            CategoryHelper.addCategory("Rent");
            CategoryHelper.addCategory("Home");
            CategoryHelper.addCategory("Groceries");
            CategoryHelper.addCategory("Tech");
            CategoryHelper.addCategory("Miscellaneous");
            CategoryHelper.addCategory("Fun");
            CategoryHelper.addCategory("Personal");
            CategoryHelper.addCategory("Shopping");
            CategoryHelper.addCategory("Car");

            PaymentMethodHelper.addPaymentMethod("Discover");
            PaymentMethodHelper.addPaymentMethod("Cash");
            PaymentMethodHelper.addPaymentMethod("Chase");
            PaymentMethodHelper.addPaymentMethod("Amazon");
            PaymentMethodHelper.addPaymentMethod("Chase CH");
            PaymentMethodHelper.addPaymentMethod("Master 53");
            PaymentMethodHelper.addPaymentMethod("Disney");
            PaymentMethodHelper.addPaymentMethod("AMEX");

            AppHelper.setFirstRunComplete();
        }
        /*if(isStoragePermissionGranted()){
            getDb();
        }*/
    }

    private void backup() {
        new SheetsBackupTask(this) {
            @Override
            protected void onPostExecute(LoaderResponse loaderResponse) {
                super.onPostExecute(loaderResponse);
                if (loaderResponse.getResponseCode() == LoaderResponse.SUCCESS) {
                    AppHelper.setLastBackupTime(System.currentTimeMillis());
                    deleteAllExpenses();
                } else if (loaderResponse.getResponseCode() == LoaderResponse.FAILURE) {
                    startActivityForResult(loaderResponse.getIntent(), REQUEST_AUTHORIZATION);
                }
            }
        }.execute();
    }

    @Override
    public void onSyncClicked() {
        backup();
    }
}
