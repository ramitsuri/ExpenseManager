package com.ramitsuri.expensemanager.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.utils.CurrencyHelper;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

public class BudgetSetupAdapter extends RecyclerView.Adapter<BudgetSetupAdapter.ViewHolder> {

    @Nullable
    private List<BudgetSetupWrapper> mValues;
    @Nullable
    private BudgetSetupAdapterCallback mCallback;
    private String mSelectedValue;

    public interface BudgetSetupAdapterCallback {
        void onBudgetPicked(BudgetSetupWrapper value);
    }

    public BudgetSetupAdapter() {
    }

    public void setValues(@NonNull List<BudgetSetupWrapper> values) {
        mValues = values;
        notifyDataSetChanged();
    }

    public void setCallback(@NonNull BudgetSetupAdapterCallback callback) {
        mCallback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.budget_setup_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (mValues != null) {
            holder.bind(mValues.get(position));
        } else {
            Timber.w("mValues is null");
        }
    }

    @Override
    public int getItemCount() {
        if (mValues != null) {
            return mValues.size();
        } else {
            Timber.w("mValues is null");
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        EditText name, amount;
        RecyclerView categories;
        ListDeleteItemAdapter adapter;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.edit_text_name);
            amount = itemView.findViewById(R.id.edit_text_amount);
            categories = itemView.findViewById(R.id.list_categories);
            categories.setLayoutManager(
                    new LinearLayoutManager(categories.getContext(), RecyclerView.HORIZONTAL,
                            false));
            if (adapter == null) {
                adapter = new ListDeleteItemAdapter();
                adapter.setShowDelete(true);
            }
            categories.setAdapter(adapter);
        }

        private void bind(final BudgetSetupWrapper value) {
            name.setText(value.getBudget().getName());
            amount.setText(
                    CurrencyHelper.formatForDisplay(false, value.getBudget().getAmount(), true));
            adapter.setValues(value.getBudget().getCategories());
        }

        @Override
        public void onClick(View view) {
            if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                if (mValues != null) {

                } else {
                    Timber.w("mValues is null");
                }
            } else {
                Timber.w("getAdapterPosition returned -1");
            }
        }
    }
}
