package com.ramitsuri.expensemanager;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.ramitsuri.expensemanager.constants.ExpenseViewType;
import com.ramitsuri.expensemanager.constants.Others;
import com.ramitsuri.expensemanager.entities.Category;
import com.ramitsuri.expensemanager.helper.AppHelper;
import com.ramitsuri.expensemanager.helper.CategoryHelper;
import com.ramitsuri.expensemanager.helper.ExpenseHelper;
import com.ramitsuri.expensemanager.entities.Expense;
import com.ramitsuri.expensemanager.fragments.SelectedExpensesFragment;
import com.ramitsuri.expensemanager.helper.PaymentMethodHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.ramitsuri.expensemanager.db.SQLHelper.DATABASE_NAME;

public class MainActivity extends BaseNavigationViewActivity
        implements EasyPermissions.PermissionCallbacks,
        SelectedExpensesFragment.OnFragmentInteractionListener, View.OnClickListener{
    GoogleAccountCredential mCredential;
    private TextView mOutputText;
    private Button mCallApiButton;
    ProgressDialog mProgress;
    private SelectedExpensesFragment mTodayFragment, mWeekFragment, mMonthFragment;
    private DrawerLayout mDrawerLayout;
    private FloatingActionButton mFabAddExpense;
    private Toolbar mToolbar;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String BUTTON_TEXT = "Call Google Sheets API";
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { SheetsScopes.SPREADSHEETS };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_main, contentFrameLayout);*/

        setupViews();

        setupFragments();

        switchFragments(R.id.tab_today);

        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
        //getResultsFromApi();


        debug();
    }

    private void debug() {
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

            List<Category> categories = CategoryHelper.getAllCategories();

            PaymentMethodHelper.addPaymentMethod("Discover");
            PaymentMethodHelper.addPaymentMethod("Cash");
            PaymentMethodHelper.addPaymentMethod("WF Checking");
            PaymentMethodHelper.addPaymentMethod("WF Savings");
            PaymentMethodHelper.addPaymentMethod("Amazon");

            ExpenseHelper.addExpense(new Expense("", "20170108", "45.18", 1, 2, "Budget", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170108", "21", 2, 3, "Dubai", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170108", "30", 2, 4, "Internet", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170108", "600", 2, 5, "Rent", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170108", "0", 2, 5, "Security", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170108", "43.66", 1, 6, "Walmart", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170109", "2", 2, 2, "Bus", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170109", "6.45", 3, 2, "Lyft", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170109", "12.09", 1, 7, "Patel brothers", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170109", "6.3", 3, 2, "Uber", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170110", "2.55", 1, 1, "McD", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170110", "8.11", 3, 2, "Uber", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170110", "7.51", 3, 2, "Uber", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170110", "28.42", 1, 6, "Walmart", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170111", "9.42", 1, 6, "Dollar Tree", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170111", "8.09", 3, 2, "Uber", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170111", "7.57", 3, 2, "Uber", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170112", "7.45", 3, 2, "Lyft", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170112", "6.87", 3, 2, "Uber", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170113", "3", 2, 2, "Bus", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170113", "8.22", 3, 2, "Uber", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170113", "6.47", 3, 2, "Uber", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170113", "5.35", 1, 8, "USB  C cables ", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170116", "90.93", 3, 8, "Philips Hue", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170116", "8.13", 3, 2, "Uber", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170116", "6.91", 1, 2, "Uber", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170117", "14.39", 1, 8, "BT earphones ", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170117", "7.07", 3, 2, "Uber", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170117", "6.72", 3, 2, "Uber", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170118", "4.6", 1, 1, "KFC", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170118", "13.98", 1, 7, "Patel brothers", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170118", "41.19", 1, 6, "Walmart", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170119", "6.93", 3, 2, "Uber", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170119", "7.06", 3, 2, "Uber", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170120", "1.07", 1, 1, "Coke", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170120", "9.62", 1, 1, "Dominos", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170120", "34.19", 1, 7, "Patel brothers", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170120", "6.84", 3, 2, "Uber", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170121", "269.74", 1, 8, "TV", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170123", "7.3", 3, 2, "Uber", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170123", "6.63", 3, 2, "Uber", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170124", "7.12", 3, 2, "Uber", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170124", "10.09", 3, 2, "Uber", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170125", "50", 3, 5, "Bentley Green", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170125", "4.21", 1, 6, "Dollar Tree", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170125", "14.44", 1, 6, "Target", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170125", "6.3", 3, 2, "Uber", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170126", "7.58", 3, 2, "Uber", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170126", "6.87", 3, 2, "Uber", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170127", "9.22", 3, 2, "Uber", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170127", "9.1", 3, 2, "Uber", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170130", "9.4", 3, 2, "Uber", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170130", "8.86", 3, 2, "Uber", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170130", "39", 1, 10, "AirBnb", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170131", "10.85", 3, 2, "Uber", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170131", "1.5", 2, 2, "Bus", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170131", "10", 2, 7, "Costco", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170201", "6.53", 3, 2, "Uber", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170201", "200.89", 3, 5, "Rent", "<EMPTY>", true, true));
            ExpenseHelper.addExpense(new Expense("", "20170201", "5.22", 1, 1, "McD", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170202", "78", 1, 10, "AirBnb", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170202", "56.25", 3, 9, "Driver License", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170202", "13.71", 3, 2, "Uber", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170202", "8.73", 3, 2, "Uber", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170202", "6.43", 3, 2, "Uber", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170203", "8.96", 3, 2, "Uber", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170203", "6.3", 3, 2, "Uber", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170206", "61.6", 1, 2, "Enterprise", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170206", "5.36", 1, 7, "Patel brothers", "Patel Brothers", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170206", "6.98", 1, 7, "Patel brothers", "Patel Brothers", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170207", "6.3", 3, 2, "Uber", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170207", "9.02", 3, 2, "Uber", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170207", "7.79", 1, 8, "Camera lens", "Amazon", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170208", "9.63", 3, 2, "Uber", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170208", "7.86", 3, 2, "Uber", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170208", "550", 2, 5, "Rent", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170208", "0.99", 5, 8, "Pixel screen guard", "Amazon", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170208", "27.49", 5, 8, "Table", "Amazon", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170209", "11.97", 5, 12, "Beard oil", "Amazon", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170210", "13.2", 5, 8, "Screw driver", "Amazon", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170209", "27.85", 3, 8, "mi band", "AliExpress", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170209", "6.75", 2, 9, "Driver License", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170209", "1.79", 1, 8, "Car mount", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170209", "21", 1, 10, "Greyhound", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170210", "24.97", 1, 7, "Publix", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170210", "6.71", 1, 2, "Uber", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170210", "3.96", 5, 8, "Pixel case", "Amazon", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170211", "5", 3, 10, "Parking", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170211", "13.24", 1, 2, "Uber", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170211", "1.61", 1, 10, "Water", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170211", "1.79", 1, 10, "Water", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170211", "9.59", 1, 7, "Walmart", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170211", "20.06", 1, 10, "Bawarchi", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170212", "10.68", 1, 10, "Clearwater", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170212", "38", 1, 10, "Clearwater Food", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170212", "12.24", 5, 10, "Gas", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170212", "10", 2, 10, "Parking", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170212", "8.52", 1, 10, "MCD", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170213", "17.09", 1, 10, "Apna restaurant", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170214", "11.95", 5, 12, "Beard brush", "Amazon", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170215", "9.75", 1, 2, "Uber", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170215", "0", 1, 8, "Extension cord", "Aamzon", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170214", "4.25", 1, 6, "Chopping board", "Walmart", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170214", "3.97", 1, 7, "green chillies, maggi", "Patel Brothers", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170215", "8.45", 3, 2, "Lyft", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170216", "9.15", 1, 2, "Uber", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170216", "8.07", 1, 2, "Uber", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170217", "9.24", 1, 2, "Uber", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170217", "6.58", 3, 2, "Lyft", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170217", "4.14", 3, 7, "Ice cream, tomatoes", "Publix", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170218", "25.98", 3, 7, "Rice, Apples", "Costco", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170220", "9.18", 1, 2, "Uber", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170220", "7.88", 1, 2, "Uber", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170221", "11.31", 1, 2, "Uber", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170221", "1.5", 2, 2, "Bus", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170221", "8.72", 1, 7, "Onions, tomatoes, cucumber", "Patel Brothers", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170222", "13.7", 1, 2, "Uber", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170222", "10", 1, 8, "Google Voice", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170222", "0", 1, 2, "Uber", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170223", "7.38", 3, 2, "Lyft", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170223", "9.2", 1, 2, "Uber", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170223", "11.71", 1, 7, "oil, tomato canned, chicken, salt", "Publix", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170223", "2.5", 1, 8, "Play Music", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170224", "0", 1, 2, "Uber", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170224", "8.55", 1, 11, "Socks", "Ross", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170225", "3", 2, 2, "Bus", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170224", "6.75", 3, 2, "Lyft", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170227", "9.42", 3, 2, "Lyft", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170227", "50", 2, 5, "New apartment security", "<EMPTY>", true, true));
            ExpenseHelper.addExpense(new Expense("", "20170227", "7.2", 1, 7, "Onions, sambhar powder, chat masala, potatoes", "Patel Brothers", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170227", "3.69", 1, 7, "Milk", "Publix", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170227", "6.74", 1, 2, "Lyft", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170228", "8.99", 1, 2, "Lyft", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170228", "2.14", 1, 6, "Bowls, plates", "Dollar Tree", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170228", "6.65", 1, 2, "Lyft", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170228", "1.5", 2, 2, "Bus", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170301", "9.88", 1, 2, "Uber", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170301", "3.21", 1, 6, "Bathroom cleaning stuff", "Dollar Tree", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170301", "85.2", 3, 9, "Regalia", "UF", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170302", "9.48", 1, 2, "Lyft", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170303", "1.5", 1, 2, "Bus", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170302", "4", 1, 3, "Logan", "Fandango", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170302", "30.82", 1, 11, "T shirts", "JC Penny", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170302", "1.89", 1, 2, "Uber", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170303", "9.17", 1, 2, "Lyft", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170303", "6.5", 1, 2, "Lyft", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170303", "10.81", 1, 7, "curd, tomatoes", "Patel Brothers", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170304", "1.5", 1, 2, "Bus", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170306", "9.05", 1, 2, "Lyft", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170306", "1.5", 1, 2, "Bus", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170306", "4", 1, 3, "The Great Wall", "Fandango", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170306", "7.22", 1, 1, "Sub", "Subway", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170306", "5.4", 1, 2, "Lyft", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170307", "8.85", 1, 2, "Lyft", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170307", "1.5", 1, 2, "Bus", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170307", "0.67", 1, 7, "Cucumber", "Publix", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170307", "6.82", 1, 2, "Lyft", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170308", "9.16", 1, 2, "Lyft", "<EMPTY>", false, false));
            ExpenseHelper.addExpense(new Expense("", "20170308", "44", 1, 2, "Rental for moving", "Budget Car Rental", true, true));
            ExpenseHelper.addExpense(new Expense("", "20170308", "14", 2, 7, "Curd, Paneer (old)", "<EMPTY>", false, false));

            AppHelper.setFirstRunComplete();
        }
        /*if(isStoragePermissionGranted()){
            getDb();
        }*/
    }

    private void setupViews() {
       /* mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);*/

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        mFabAddExpense = (FloatingActionButton)findViewById(R.id.fab_add);
        mFabAddExpense.setOnClickListener(this);

        BottomNavigationView bottomNavigation =
                (BottomNavigationView)findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
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

    private void getResultsFromApi() {
        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (! isDeviceOnline()) {
            //mOutputText.setText("No network connection available.");
        } else {
            new MakeRequestTask(mCredential).execute();
        }
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    mOutputText.setText(
                            "This app requires Google Play Services. Please install " +
                                    "Google Play Services on your device and relaunch this app.");
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
        getDb();
    }

    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                MainActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onClick(View view) {
        if(view == mFabAddExpense){
            startActivity(new Intent(this, ExpenseDetailActivity.class));
        }
    }

    public void getDb() {
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
    }

    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
        private com.google.api.services.sheets.v4.Sheets mService = null;
        private Exception mLastError = null;

        MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.sheets.v4.Sheets.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Google Sheets API Android Quickstart")
                    .build();
        }

        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        private List<String> getDataFromApi() throws IOException {
            String spreadsheetId = "1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms";
            //String spreadsheetId = "1pzDFprwHn6pbh6lRC8_emFEvQSa3MDnnU6baOAPwgcQ";
            String range = "Class Data!A2:E";
            List<String> results = new ArrayList<String>();
            ValueRange response = this.mService.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute();
            List<List<Object>> values = response.getValues();
            if (values != null) {
                results.add("Name, Major");
                for (List row : values) {
                    results.add(row.get(0) + ", " + row.get(4));
                }
            }
            return results;
        }



        @Override
        protected void onPreExecute() {
            /*mOutputText.setText("");
            mProgress.show();*/
        }

        @Override
        protected void onPostExecute(List<String> output) {
            //mProgress.hide();
            if (output == null || output.size() == 0) {
                //mOutputText.setText("No results returned.");
            } else {
                output.add(0, "Data retrieved using the Google Sheets API:");
                //mOutputText.setText(TextUtils.join("\n", output));
            }
        }

        @Override
        protected void onCancelled() {
            //mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            MainActivity.REQUEST_AUTHORIZATION);
                } else {
                    /*mOutputText.setText("The following error occurred:\n"
                            + mLastError.getMessage());*/
                }
            } else {
                //mOutputText.setText("Request cancelled.");
            }
        }
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
*/
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }
}
