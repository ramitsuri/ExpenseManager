package com.ramitsuri.expensemanager.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class CategoryPickerDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        List<String> values = getCategories();
        final CharSequence[] items = values.toArray(new CharSequence[values.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select Category")
                .setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getContext(), items[which], Toast.LENGTH_SHORT).show();
                    }
                });
        return builder.create();
    }

    public List<String> getCategories() {
        List mCategories = new ArrayList<>();
        mCategories.add("Food");
        mCategories.add("Travel");
        mCategories.add("Entertainment");
        mCategories.add("Utilities");
        mCategories.add("Rent");
        mCategories.add("Home");
        mCategories.add("Groceries");
        mCategories.add("Tech");
        mCategories.add("Miscellaneous");
        mCategories.add("Fun");
        mCategories.add("Personal");
        mCategories.add("Shopping");
        return mCategories;
    }
}
