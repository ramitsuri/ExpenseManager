package com.ramitsuri.expensemanager.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ramitsuri.expensemanager.IntDefs.ListItemType;
import com.ramitsuri.expensemanager.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

public class MetadataAdapter extends RecyclerView.Adapter<MetadataAdapter.ViewHolder> {

    @Nullable
    private List<String> mMetadataItems;

    public void setMetadataItems(List<String> metadataItems) {
        if (mMetadataItems != null) {
            mMetadataItems.clear();
            mMetadataItems.addAll(metadataItems);
        } else {
            mMetadataItems = metadataItems;
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
            @ListItemType int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.metadata_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (mMetadataItems != null) {
            holder.bind(mMetadataItems.get(position));
        } else {
            Timber.w("mMetadataItems is null");
        }
    }

    @Override
    public int getItemCount() {
        if (mMetadataItems == null) {
            Timber.w("mMetadataItems is null");
            return 0;
        }
        return mMetadataItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtInfo;

        ViewHolder(View itemView) {
            super(itemView);

            txtInfo = itemView.findViewById(R.id.txt_info);
        }

        private void bind(String info) {
            txtInfo.setText(info);
        }
    }
}
