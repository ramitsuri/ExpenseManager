package com.ramitsuri.expensemanagerlegacy.ui;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ramitsuri.expensemanagerlegacy.R;
import com.ramitsuri.expensemanagerlegacy.async.SheetsDetailsLoader;
import com.ramitsuri.expensemanagerlegacy.constants.LoaderIDs;
import com.ramitsuri.expensemanagerlegacy.entities.SheetDto;
import com.ramitsuri.expensemanagerlegacy.helper.AppHelper;

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
