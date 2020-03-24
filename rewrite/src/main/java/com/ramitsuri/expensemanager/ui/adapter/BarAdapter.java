package com.ramitsuri.expensemanager.ui.adapter;

import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ScaleDrawable;
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

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtTitle, txtValue1, txtValue2;
        private ProgressBar progressBar;

        ViewHolder(View itemView) {
            super(itemView);

            txtTitle = itemView.findViewById(R.id.txt_title);
            txtValue1 = itemView.findViewById(R.id.txt_value_1);
            txtValue2 = itemView.findViewById(R.id.txt_value_2);
            progressBar = itemView.findViewById(R.id.progress);
        }

        private void bind(BarWrapper wrapper) {
            txtTitle.setText(wrapper.getTitle());
            txtValue1.setText(wrapper.getValueUsed());
            if (!TextUtils.isEmpty(wrapper.getValueRemaining())) {
                txtValue2.setText(wrapper.getValueRemaining());
                txtValue2.setVisibility(View.VISIBLE);
            } else {
                txtValue2.setVisibility(View.GONE);
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
            animation.setDuration(1500);
            progressBar.startAnimation(animation);

            LayerDrawable layerDrawable = (LayerDrawable)progressBar.getProgressDrawable();
            layerDrawable.mutate();

            ScaleDrawable scaleDrawable = (ScaleDrawable)layerDrawable.getDrawable(1);
            GradientDrawable progressLayer = (GradientDrawable)scaleDrawable.getDrawable();
            if (progressLayer != null) {
                if (newProgress > 100) {
                    progressLayer.setColor(
                            ContextCompat.getColor(progressBar.getContext(), R.color.color_red));
                } else {
                    progressLayer.setColor(
                            ContextCompat.getColor(progressBar.getContext(), R.color.color_teal));
                }
            }
        }
    }
}
