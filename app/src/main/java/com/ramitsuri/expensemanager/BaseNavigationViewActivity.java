package com.ramitsuri.expensemanager;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.ramitsuri.expensemanager.constants.IntentExtras;
import com.ramitsuri.expensemanager.constants.RecyclerViewValuesType;

public class BaseNavigationViewActivity extends AppCompatActivity{

    protected DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private Toolbar mToolbar;
    private NavigationView mNavigationView;

    @Override
    public void setContentView(int layoutResID)
    {
        mDrawerLayout = (DrawerLayout)
                getLayoutInflater().inflate(R.layout.activity_base_navigation_view, null);

        super.setContentView(mDrawerLayout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
                R.string.drawer_open, R.string.drawer_closed);
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        if (mNavigationView != null) {
            setupDrawerContent(mNavigationView);
        }

        mNavigationView.getMenu().getItem(0).setChecked(true);
        View headerLayout = mNavigationView.getHeaderView(0);

        FrameLayout activityContainer =
                (FrameLayout) mDrawerLayout.findViewById(R.id.content_frame);
        getLayoutInflater().inflate(layoutResID, activityContainer, true);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        switch (menuItem.getItemId()){
                            case R.id.nav_expenses:
                                startExpensesActivity();
                                break;
                            case R.id.nav_all_expenses:
                                break;
                            case R.id.nav_categories:
                                startRecyclerViewActivity(
                                        RecyclerViewValuesType.RECYCLER_VIEW_CATEGORIES);
                                break;
                            case R.id.nav_payment_methods:
                                startRecyclerViewActivity(
                                        RecyclerViewValuesType.RECYCLER_VIEW_PAYMENT_METHODS);
                                break;
                            case R.id.nav_settings:
                                break;
                        }
                        return onOptionsItemSelected(menuItem);
                    }
                });
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

    private void startRecyclerViewActivity(int recyclerViewMode) {
        Intent intent = new Intent(this, RecyclerViewActivity.class);
        intent.putExtra(IntentExtras.INTENT_EXTRA_RECYCLER_VIEW_ACTIVITY_MODE, recyclerViewMode);
        startActivity(intent);
    }
}
