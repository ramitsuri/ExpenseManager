package com.ramitsuri.expensemanagerrewrite.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import timber.log.Timber;

public class BaseFragment extends Fragment {

    void showActionBar() {
        Activity activity = getActivity();
        ActionBar actionBar = null;
        if (activity != null) {
            actionBar = ((AppCompatActivity)activity).getSupportActionBar();
        }
        if (actionBar != null) {
            actionBar.show();
        }
    }

    void hideActionBar() {
        Activity activity = getActivity();
        ActionBar actionBar = null;
        if (activity != null) {
            actionBar = ((AppCompatActivity)activity).getSupportActionBar();
        }
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Timber.i(this.getClass().getSimpleName() + " OnAttach");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.i(this.getClass().getSimpleName() + " OnCreate");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        Timber.i(this.getClass().getSimpleName() + " OnCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Timber.i(this.getClass().getSimpleName() + " OnViewCreated");
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Timber.i(this.getClass().getSimpleName() + " OnViewStateRestored");
    }

    @Override
    public void onStart() {
        super.onStart();
        Timber.i(this.getClass().getSimpleName() + " OnAttach");
    }

    @Override
    public void onResume() {
        super.onResume();
        Timber.i(this.getClass().getSimpleName() + " OnResume");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Timber.i(this.getClass().getSimpleName() + " OnSaveInstanceState");
    }

    @Override
    public void onPause() {
        super.onPause();
        Timber.i(this.getClass().getSimpleName() + " OnPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Timber.i(this.getClass().getSimpleName() + " OnStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Timber.i(this.getClass().getSimpleName() + " OnDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Timber.i(this.getClass().getSimpleName() + " OnDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Timber.i(this.getClass().getSimpleName() + " OnDetach");
    }
}
