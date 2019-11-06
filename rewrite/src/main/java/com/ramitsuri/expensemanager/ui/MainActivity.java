package com.ramitsuri.expensemanager.ui;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.ramitsuri.expensemanager.BuildConfig;
import com.ramitsuri.expensemanager.Constants;
import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.entities.Log;
import com.ramitsuri.expensemanager.utils.AppHelper;
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
        // Backup now menu item always visible for now
        /*boolean enableMenuItem = AppHelper.isAutoBackupEnabled();
        MenuItem menuItem = menu.findItem(R.id.menu_backup_now);
        if (menuItem != null) {
            menuItem.setVisible(enableMenuItem);
        }*/
        if (!BuildConfig.DEBUG) {
            MenuItem menuItem = menu.findItem(R.id.menu_delete_all);
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
        }
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.onNavDestinationSelected(item, navController);
        return super.onOptionsItemSelected(item);
    }

    private void deleteExpenses() {
        Timber.i("Deleting expenses");

        MainApplication.getInstance().getExpenseRepo().deleteExpenses();
    }

    private void initiateBackup() {
        Timber.i("Initiating backup");

        WorkHelper.enqueueOneTimeBackup();
    }
}
