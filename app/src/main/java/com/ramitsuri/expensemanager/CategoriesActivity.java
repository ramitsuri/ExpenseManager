package com.ramitsuri.expensemanager;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.ramitsuri.expensemanager.adapter.ListActivityAdapter;
import com.ramitsuri.expensemanager.constants.IntentExtras;
import com.ramitsuri.expensemanager.constants.Others;
import com.ramitsuri.expensemanager.constants.RecyclerViewValuesType;
import com.ramitsuri.expensemanager.dialog.EditTextDialogFragment;
import com.ramitsuri.expensemanager.entities.Category;
import com.ramitsuri.expensemanager.helper.CategoryHelper;

import java.util.ArrayList;
import java.util.List;

public class CategoriesActivity extends AppCompatActivity implements View.OnClickListener,
        ListActivityAdapter.ListActivityAdapterCallbacks,
        EditTextDialogFragment.EditTextDialogCallbacks {

    private ListActivityAdapter<Category> mAdapter;
    private List<Category> mCategories;
    private Toolbar mToolbar;
    private FloatingActionButton mFabAddValue;
    private RecyclerView mRecyclerViewValues;
    private Category mCategoryToEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);

        setupActionBar();
        setupViews();
        setTitle(getString(R.string.nav_menu_categories));
    }

    private void setupViews() {
        mFabAddValue = (FloatingActionButton)findViewById(R.id.fab_add_value);
        mFabAddValue.setOnClickListener(this);
        mRecyclerViewValues = (RecyclerView) findViewById(R.id.recycler_view_values);
        RecyclerView.LayoutManager recyclerViewLManager = new LinearLayoutManager(this);
        mCategories = CategoryHelper.getAllCategories();
        mAdapter = new ListActivityAdapter(this, mCategories);
        mRecyclerViewValues.setHasFixedSize(false);
        mRecyclerViewValues.setLayoutManager(recyclerViewLManager);
        mRecyclerViewValues.setAdapter(mAdapter);
        /*ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT){
                    @Override
                    public boolean onMove(RecyclerView recyclerView,
                                          RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                        final int position = viewHolder.getAdapterPosition();
                        final Category removedValue = mCategories.get(position);
                        mCategories.remove(position);
                        mAdapter.notifyItemRemoved(position);
                        View.OnClickListener undoClickListener = new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mCategories.add(position, removedValue);
                                mAdapter.notifyItemInserted(position);
                                mAdapter.notifyItemRangeChanged(position, mAdapter.getItemCount());
                            }
                        };
                        Snackbar.make(viewHolder.itemView, getString(R.string.category_deleted),
                                Snackbar.LENGTH_LONG).setAction(getString(R.string.undo),
                                undoClickListener).show();
                    }
                };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerViewValues);*/
    }

    private void setupActionBar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View view) {
        if(view == mFabAddValue){
            handleFabAddClicked();
        }
    }

    private void handleFabAddClicked() {
        mCategoryToEdit = new Category();
        mCategoryToEdit.setName("");
        mCategories.add(mCategoryToEdit);
        Bundle args = new Bundle();
        args.putString(Others.CATEGORY_PICKER_CATEGORY, mCategoryToEdit.toString());
        EditTextDialogFragment newFragment = EditTextDialogFragment.newInstance();
        newFragment.setArguments(args);
        newFragment.show(getSupportFragmentManager(), EditTextDialogFragment.TAG);
    }

    @Override
    public void onItemClicked(Object item) {
        mCategoryToEdit = (Category)item;
        Bundle args = new Bundle();
        args.putString(Others.CATEGORY_PICKER_CATEGORY, mCategoryToEdit.toString());
        EditTextDialogFragment newFragment = EditTextDialogFragment.newInstance();
        newFragment.setArguments(args);
        newFragment.show(getSupportFragmentManager(), EditTextDialogFragment.TAG);
    }

    @Override
    public void onItemEdited(String value) {
        mCategoryToEdit.setName(value);
        if(mCategoryToEdit.getId() == 0){
            CategoryHelper.addCategory(mCategoryToEdit.getName());
            mCategories = CategoryHelper.getAllCategories();
        } else {
            CategoryHelper.updateCategoryName(mCategoryToEdit.getId(), mCategoryToEdit.getName());
        }
        mAdapter.notifyDataSetChanged();
    }
}
