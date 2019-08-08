package com.ramitsuri.expensemanagerrewrite.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ramitsuri.expensemanagerrewrite.R;

import androidx.fragment.app.Fragment;

public class AddExpenseFragment extends Fragment {
    public AddExpenseFragment() {
        // Required empty public constructor
    }
    public static AddExpenseFragment newInstance(String param1, String param2) {
        AddExpenseFragment fragment = new AddExpenseFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_expense, container, false);
    }

}
