package com.ramitsuri.expensemanager.ui;

import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ramitsuri.expensemanager.Constants;
import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.utils.AppHelper;
import com.ramitsuri.expensemanager.utils.PrefHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
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

        final NavController navController =
                Navigation.findNavController(this, R.id.nav_host_fragment);

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

        final BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        if (navController.getCurrentDestination() != null) {
                            if (navController.getCurrentDestination().getId() == item.getItemId()) {
                                return false;
                            }
                        }
                        navController.navigate(item.getItemId());
                        return true;
                    }
                });
        //NavigationUI.setupWithNavController(bottomNavigationView, navController);

        navController.addOnDestinationChangedListener(
                new NavController.OnDestinationChangedListener() {
                    @Override
                    public void onDestinationChanged(@NonNull NavController controller,
                            @NonNull NavDestination destination, @Nullable Bundle arguments) {
                        if (destination.getId() == R.id.fragment_expenses ||
                                destination.getId() == R.id.fragment_all_expenses ||
                                destination.getId() == R.id.fragment_miscellaneous) {
                            bottomNavigationView.setVisibility(View.VISIBLE);
                        } else {
                            bottomNavigationView.setVisibility(View.GONE);
                        }
                        // Manually setting checked item because there being an issue where selected
                        // fragment and bottom nav menu item are mismatched on pressing
                        // back button from non main view fragments
                        MenuItem item =
                                bottomNavigationView.getMenu().findItem(destination.getId());
                        if (item != null) {
                            item.setChecked(true);
                        }
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        return Navigation.findNavController(this, R.id.nav_host_fragment).navigateUp();
    }
}
