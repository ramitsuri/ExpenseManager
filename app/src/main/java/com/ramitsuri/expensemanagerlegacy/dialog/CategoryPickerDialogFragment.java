package com.ramitsuri.expensemanagerlegacy.dialog;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ramitsuri.expensemanagerlegacy.R;
import com.ramitsuri.expensemanagerlegacy.adapter.ListPickerAdapter;
import com.ramitsuri.expensemanagerlegacy.constants.Others;
import com.ramitsuri.expensemanagerlegacy.entities.Category;
import com.ramitsuri.expensemanagerlegacy.helper.CategoryHelper;

public class CategoryPickerDialogFragment extends DialogFragment
        implements ListPickerAdapter.ListPickerAdapterCallbacks {

    public static String TAG = CategoryPickerDialogFragment.class.getName();

    private CategoryPickerCallbacks mCallbacks;

    public interface CategoryPickerCallbacks {
        void onCategoryPicked(Category category);
    }

    public static CategoryPickerDialogFragment newInstance() {
        return new CategoryPickerDialogFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_fragment_list_picker, container, false);
        TextView title = (TextView)v.findViewById(R.id.title);
        title.setText(getString(R.string.category_picker_title));
        ListPickerAdapter<Category> adapter =
                new ListPickerAdapter<>(this, CategoryHelper.getAllCategories(),
                        (Category)getArguments().getParcelable(Others.CATEGORY_PICKER_CATEGORY));
        RecyclerView categoriesRecyclerView = (RecyclerView)v.findViewById(R.id.values);
        categoriesRecyclerView.setHasFixedSize(false);
        RecyclerView.LayoutManager recyclerViewLManager = new LinearLayoutManager(getContext());
        categoriesRecyclerView.setLayoutManager(recyclerViewLManager);
        categoriesRecyclerView.setAdapter(adapter);
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (CategoryPickerCallbacks)context;
    }

    @Override
    public void onItemSelected(Object item) {
        dismiss();
        mCallbacks.onCategoryPicked((Category)item);
    }
}
