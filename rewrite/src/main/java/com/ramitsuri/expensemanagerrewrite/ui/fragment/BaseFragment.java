package com.ramitsuri.expensemanagerrewrite.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.ramitsuri.expensemanagerrewrite.MainApplication;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
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
        Timber.i("%s OnAttach", this.getClass().getSimpleName());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.i("%s OnCreate", this.getClass().getSimpleName());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        Timber.i("%s OnCreateView", this.getClass().getSimpleName());
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Timber.i("%s OnViewCreated", this.getClass().getSimpleName());
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Timber.i("%s OnViewStateRestored", this.getClass().getSimpleName());
    }

    @Override
    public void onStart() {
        super.onStart();
        Timber.i("%s OnAttach", this.getClass().getSimpleName());
    }

    @Override
    public void onResume() {
        super.onResume();
        Timber.i("%s OnResume", this.getClass().getSimpleName());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Timber.i("%s OnSaveInstanceState", this.getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        Timber.i("%s OnPause", this.getClass().getSimpleName());
    }

    @Override
    public void onStop() {
        super.onStop();
        Timber.i("%s OnStop", this.getClass().getSimpleName());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Timber.i("%s OnDestroyView", this.getClass().getSimpleName());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Timber.i("%s OnDestroy", this.getClass().getSimpleName());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Timber.i("%s OnDetach", this.getClass().getSimpleName());
    }

    public void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm =
                (InputMethodManager)context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    protected void logWorkStatus(String workTag) {
        WorkManager.getInstance(MainApplication.getInstance()).getWorkInfosByTagLiveData(workTag)
                .observe(this, new Observer<List<WorkInfo>>() {
                    @Override
                    public void onChanged(List<WorkInfo> workInfos) {
                        if (workInfos != null && !workInfos.isEmpty()) {
                            Timber.i("Work status %s", workInfos.get(0).toString());
                        }
                    }
                });
    }
}
