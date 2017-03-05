package com.ramitsuri.expensemanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

public class BaseNavigationViewActivity extends AppCompatActivity{

    protected DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private Toolbar mToolbar;
    private MenuItem mSelectedMenuItem;
    private NavigationView mNavigationView;

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_navigation_view);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.common_open_on_phone, R.string.app_name);
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        View headerLayout = navigationView.getHeaderView(0);
    }*/

    @Override
    public void setContentView(int layoutResID)
    {
        mDrawerLayout = (DrawerLayout)
                getLayoutInflater().inflate(R.layout.activity_base_navigation_view, null);

        super.setContentView(mDrawerLayout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.common_open_on_phone, R.string.app_name);
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        if (mNavigationView != null) {
            setupDrawerContent(mNavigationView);
        }

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
                        mSelectedMenuItem = menuItem;
                        switch (menuItem.getItemId()){
                            case R.id.nav_expenses:
                                startExpensesActivity();
                                break;
                            case R.id.nav_all_expenses:
                                break;
                            case R.id.nav_categories:
                                startRecyclerViewActivity(Constants.RECYCLER_VIEW_CATEGORIES);
                                break;
                            case R.id.nav_payment_methods:
                                startRecyclerViewActivity(Constants.RECYCLER_VIEW_PAYMENT_METHODS);
                                break;
                            case R.id.nav_settings:
                                break;
                        }
                        return onOptionsItemSelected(menuItem);
                    }
                });
    }

    private void startExpensesActivity() {
        if(mSelectedMenuItem == null || mSelectedMenuItem.getItemId() != R.id.nav_expenses) {
            Intent intent = new Intent(this, MainActivity.class);
            finish();
            startActivity(intent);
        }
    }

    private void startRecyclerViewActivity(int recyclerViewMode) {
        Intent intent = new Intent(this, RecyclerViewActivity.class);
        intent.putExtra(Constants.INTENT_EXTRA_RECYCLER_VIEW_ACTIVITY_MODE, recyclerViewMode);
        startActivity(intent);
    }
}
