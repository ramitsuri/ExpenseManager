package com.ramitsuri.expensemanager.ui;

import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.ramitsuri.expensemanager.BuildConfig;
import com.ramitsuri.expensemanager.Constants;
import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.utils.AppHelper;
import com.ramitsuri.expensemanager.utils.PrefHelper;
import com.ramitsuri.expensemanager.utils.WorkHelper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    private long mLastPressTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        setupNavigation();

        // Set Default Theme
        if (TextUtils.isEmpty(AppHelper.getCurrentTheme())) {
            Timber.i("Setting default theme");
            AppHelper.setCurrentTheme(Constants.SystemTheme.SYSTEM_DEFAULT);
        }
        mLastPressTime = 0;

        migrateCurrentToDefaultSheetId();

        // Set dark nav and status bar if light theme
        if ((getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_NO) {
            Timber.i("Light theme, setting status and nav bar light flag");
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR |
                    View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }
    }

    private void migrateCurrentToDefaultSheetId() {
        // Delete current sheet id and migrate it to default sheet id
        // After which current sheet id is deleted
        String currentSheetId = AppHelper.getCurrentSheetId();
        if (TextUtils.isEmpty(currentSheetId)) {
            return;
        }
        try {
            int defaultSheetId = Integer.parseInt(currentSheetId);
            AppHelper.setDefaultSheetId(defaultSheetId);
            PrefHelper.remove(getString(R.string.settings_key_sheet_id));
        } catch (Exception ex) {
            Timber.i("Unable to migrate current sheet id to default sheet id");
        }
    }

    private void setupNavigation() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        NavInflater navInflater = navController.getNavInflater();
        NavGraph graph = navInflater.inflate(R.navigation.nav_graph);

        if (TextUtils.isEmpty(AppHelper.getSpreadsheetId())) {
            // First time setup
            graph.setStartDestination(R.id.fragment_setup);
        } else { // First time setup already done
            graph.setStartDestination(R.id.fragment_expenses);
        }
        navController.setGraph(graph);

        NavigationUI.setupWithNavController(toolbar, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return Navigation.findNavController(this, R.id.nav_host_fragment).navigateUp();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Hide Delete All
        if (!BuildConfig.DEBUG) {
            MenuItem menuItem = menu.findItem(R.id.menu_delete_all);
            if (menuItem != null) {
                menuItem.setVisible(false);
            }
        }

        // Hide Sheet metadata
        if (!enableHidden()) {
            MenuItem menuItem = menu.findItem(R.id.fragment_metadata);
            if (menuItem != null) {
                menuItem.setVisible(false);
            }
        }
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_backup_now:
                initiateBackup();
                return true;

            case R.id.menu_delete_all:
                deleteExpenses();
                return true;

            case R.id.menu_sync:
                syncDataFromSheet();
                return true;
        }
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.onNavDestinationSelected(item, navController);
        return super.onOptionsItemSelected(item);
    }

    private void deleteExpenses() {
        Timber.i("Deleting expenses");

        long currentTime = System.currentTimeMillis();
        if (currentTime - mLastPressTime <= 2000) {
            if (BuildConfig.DEBUG) { // Extra protection, only in debug
                MainApplication.getInstance().getExpenseRepo().deleteExpenses();
            }
            mLastPressTime = 0;
        } else {
            mLastPressTime = currentTime;
        }
    }

    private void initiateBackup() {
        Timber.i("Initiating backup");

        WorkHelper.enqueueOneTimeBackup();
    }

    private void syncDataFromSheet() {
        Timber.i("Initiating sync");

        WorkHelper.enqueueOneTimeSync();
    }

    private boolean enableHidden() {
        String accountName = AppHelper.getAccountName();
        return (AppHelper.isDebugOptionEnabled() || BuildConfig.DEBUG) ||
                (!TextUtils.isEmpty(accountName) && accountName.toLowerCase().contains("jess"));
    }
}
