package com.ramitsuri.expensemanager.ui;

import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.constants.Constants;
import com.ramitsuri.expensemanager.utils.AppHelper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.Navigation;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.BaseTheme);
        super.onCreate(savedInstanceState);
        Timber.i("onCreate");

        setContentView(R.layout.activity_main);

        // Set Default Theme
        String theme = AppHelper.getCurrentTheme();
        if (TextUtils.isEmpty(theme)) {
            Timber.i("Setting default theme");
            theme = Constants.SystemTheme.SYSTEM_DEFAULT;
        }
        AppHelper.setCurrentTheme(theme);

        // Set dark nav and status bar if light theme
        if ((getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_NO) {
            Timber.i("Light theme, setting status and nav bar light flag");
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR |
                    View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }

        setupNavigation(savedInstanceState);
    }

    private void setupNavigation(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            final NavController navController =
                    Navigation.findNavController(this, R.id.nav_host_fragment);

            NavInflater navInflater = navController.getNavInflater();
            NavGraph graph = navInflater.inflate(R.navigation.nav_graph);
            navController.setGraph(graph);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return Navigation.findNavController(this, R.id.nav_host_fragment).navigateUp();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Timber.i("onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Timber.i("onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Timber.i("onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Timber.i("onDestroy");
    }
}
