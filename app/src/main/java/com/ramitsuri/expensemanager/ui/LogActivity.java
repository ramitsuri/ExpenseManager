package com.ramitsuri.expensemanager.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.async.SheetsDetailsLoader;
import com.ramitsuri.expensemanager.constants.LoaderIDs;
import com.ramitsuri.expensemanager.entities.SheetDto;
import com.ramitsuri.expensemanager.helper.AppHelper;

import java.util.List;

public class LogActivity extends AppCompatActivity implements View.OnClickListener,
        LoaderManager.LoaderCallbacks<List<SheetDto>> {

    private Button mButton;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        setupActionBar();
        setupViews();
        setTitle(getString(R.string.nav_menu_log));
    }

    private void setupViews() {
        mButton = findViewById(R.id.button);
        mButton.setOnClickListener(this);

        mTextView = findViewById(R.id.text);
    }

    private void setupActionBar() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View v) {
        if (v == mButton) {
            onSheetsDetailsRequested();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onSheetsDetailsRequested() {
        getSupportLoaderManager().restartLoader(LoaderIDs.SHEETS_DETAILS, null, this).forceLoad();
    }

    @NonNull
    @Override
    public Loader<List<SheetDto>> onCreateLoader(int id, @Nullable Bundle args) {
        return new SheetsDetailsLoader(this, AppHelper.getAccountName());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<SheetDto>> loader, List<SheetDto> data) {
        StringBuilder sb = new StringBuilder();
        for (SheetDto sheetDto : data) {
            sb.append(sheetDto.toString());
        }
        mTextView.setText(sb.toString());
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<SheetDto>> loader) {

    }
}
