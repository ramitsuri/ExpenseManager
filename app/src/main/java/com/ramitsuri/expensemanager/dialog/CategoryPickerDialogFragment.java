package com.ramitsuri.expensemanager.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.entities.Category;
import com.ramitsuri.expensemanager.helper.CategoryHelper;

import java.util.ArrayList;
import java.util.List;

public class CategoryPickerDialogFragment extends DialogFragment {

    private CategoryPickerCallbacks mCallbacks;
    private List<Category> mCategories;
    private List<String> mCategoryNames;

    public interface CategoryPickerCallbacks{
        void onCategoryPicked(Category category);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mCallbacks = (CategoryPickerCallbacks)context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        buildCategoryList();
        final CharSequence[] items =
                mCategoryNames.toArray(new CharSequence[mCategoryNames.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.category_picker_title))
                .setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mCallbacks.onCategoryPicked(mCategories.get(which));
                    }
                });
        builder.setView(R.layout.category_picker_dialog);
        return builder.create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.category_picker_dialog, container, false);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //getDialog().getWindow().setLayout(840, 1360);
    }

    public void buildCategoryList() {
        mCategories = CategoryHelper.getAllCategories();
        mCategoryNames = new ArrayList<>();
        for(Category category: mCategories){
            mCategoryNames.add(category.toString());
        }
    }
}
