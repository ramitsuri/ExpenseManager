package com.ramitsuri.expensemanagerrewrite.ui.fragment;

import android.app.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class BaseFragment extends Fragment {

    protected void showActionBar() {
        Activity activity = getActivity();
        ActionBar actionBar = null;
        if (activity != null) {
            actionBar = ((AppCompatActivity)activity).getSupportActionBar();
        }
        if (actionBar != null) {
            actionBar.show();
        }
    }

    protected void hideActionBar() {
        Activity activity = getActivity();
        ActionBar actionBar = null;
        if (activity != null) {
            actionBar = ((AppCompatActivity)activity).getSupportActionBar();
        }
        if (actionBar != null) {
            actionBar.hide();
        }
    }
}
