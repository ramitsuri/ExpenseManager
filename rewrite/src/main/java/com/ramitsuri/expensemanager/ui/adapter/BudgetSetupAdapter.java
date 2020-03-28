package com.ramitsuri.expensemanager.ui.adapter;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ramitsuri.expensemanager.MainApplication;
import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.entities.Budget;
import com.ramitsuri.expensemanager.utils.CurrencyHelper;

import java.util.List;

import javax.annotation.Nonnull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

public class BudgetSetupAdapter extends RecyclerView.Adapter<BudgetSetupAdapter.ViewHolder> {

    @Nullable
    private List<Budget> mValues;
    @Nullable
    private BudgetSetupAdapterCallback mCallback;

    public interface BudgetSetupAdapterCallback {
        void onItemDeleteRequested(@Nonnull Budget value);

        void onItemEditRequested(@Nonnull Budget value);
    }

    public BudgetSetupAdapter() {
    }

    public void setValues(@NonNull List<Budget> values) {
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, amount, categories;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.txt_name);
            amount = itemView.findViewById(R.id.txt_amount);
            categories = itemView.findViewById(R.id.txt_categories);

            Button btnEdit = itemView.findViewById(R.id.btn_edit);
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                        if (mValues != null) {
                            Budget budget = mValues.get(getAdapterPosition());
                            if (budget != null) {
                                if (mCallback != null) {
                                    mCallback.onItemEditRequested(budget);
                                } else {
                                    Timber.i("mCallbacks is null");
                                }
                            } else {
                                Timber.i("Budget or budget is null");
                            }
                        } else {
                            Timber.i("mValues is null");
                        }
                    } else {
                        Timber.w("getAdapterPosition returned -1");
                    }
                }
            });

            Button btnDelete = itemView.findViewById(R.id.btn_delete);
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                        if (mValues != null) {
                            Budget budget = mValues.get(getAdapterPosition());
                            if (budget != null) {
                                if (mCallback != null) {
                                    mCallback.onItemDeleteRequested(budget);
                                } else {
                                    Timber.i("mCallbacks is null");
                                }
                            } else {
                                Timber.i("Budget or wrapper is null");
                            }
                        } else {
                            Timber.i("mValues is null");
                        }
                    } else {
                        Timber.w("getAdapterPosition returned -1");
                    }
                }
            });
        }

        private void bind(final Budget value) {
            name.setText(value.getName());
            amount.setText(getResources().getString(R.string.setup_budget_limit,
                    CurrencyHelper.formatForDisplay(true, value.getAmount(), true)));
            categories.setText(getResources().getString(R.string.setup_budget_categories,
                    getCategoriesString(value.getCategories())));
        }

        private String getCategoriesString(List<String> categories) {
            if (categories == null) {
                return "None";
            }
            StringBuilder sb = new StringBuilder();
            int index = 0;
            for (String category : categories) {
                if (index != 0) {
                    sb.append(getResources().getString(R.string.setup_budget_separator));
                }
                sb.append(category);
                index = index + 1;
            }
            return sb.toString();
        }

        private Resources getResources() {
            return MainApplication.getInstance().getResources();
        }
    }
}
