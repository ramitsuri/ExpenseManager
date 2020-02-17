package com.ramitsuri.expensemanager.ui.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.utils.ProgressBarAnimation;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

public class BarAdapter extends RecyclerView.Adapter<BarAdapter.ViewHolder> {

    @Nullable
    private List<BarWrapper> mItems;

    public void setItems(List<BarWrapper> items) {
        if (mItems != null) {
            mItems.clear();
            mItems.addAll(items);
        } else {
            mItems = items;
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bar_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (mItems != null) {
            holder.bind(mItems.get(position));
        } else {
            Timber.w("mItems is null");
        }
    }

    @Override
    public int getItemCount() {
        if (mItems == null) {
            Timber.w("mItems is null");
            return 0;
        }
        return mItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtTitle, txtValue1, txtValue2, txtValue3;
        private ProgressBar progressBar;

        ViewHolder(View itemView) {
            super(itemView);

            txtTitle = itemView.findViewById(R.id.txt_title);
            txtValue1 = itemView.findViewById(R.id.txt_value_1);
            txtValue2 = itemView.findViewById(R.id.txt_value_2);
            txtValue3 = itemView.findViewById(R.id.txt_value_3);
            progressBar = itemView.findViewById(R.id.progress);
        }

        private void bind(BarWrapper wrapper) {
            txtTitle.setText(wrapper.getTitle());
            txtValue1.setText(wrapper.getValue1());
            if (!TextUtils.isEmpty(wrapper.getValue2())) {
                txtValue2.setText(wrapper.getValue2());
                txtValue2.setVisibility(View.VISIBLE);
            } else {
                txtValue2.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(wrapper.getValue3())) {
                txtValue3.setText(wrapper.getValue3());
                txtValue3.setVisibility(View.VISIBLE);
            } else {
                txtValue3.setVisibility(View.GONE);
            }

            // Progress bar
            int oldProgress = progressBar.getProgress();
            int newProgress = wrapper.getProgress();
            progressBar.setProgress(newProgress, true);
            progressBar.setMax(100);
            progressBar.setProgressDrawable(ContextCompat
                    .getDrawable(progressBar.getContext(), R.drawable.progress_bar_shape));
            ProgressBarAnimation animation =
                    new ProgressBarAnimation(progressBar, oldProgress, newProgress);
            animation.setDuration(2000);
            progressBar.startAnimation(animation);
        }
    }
}
