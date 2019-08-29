package com.ramitsuri.expensemanagerrewrite.ui.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ramitsuri.expensemanagerrewrite.R;
import com.ramitsuri.expensemanagerrewrite.entities.Expense;
import com.ramitsuri.expensemanagerrewrite.utils.DateHelper;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {

    @Nullable
    private List<Expense> mExpenses;
    private int mExpandedPosition = -1;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ViewGroup containerDetails, containerExpand;
        private TextView txtDate, txtDescription, txtAmount, txtDetail1, txtDetail2, txtDetail3,
                btnEdit;
        private ImageView btnExpand;

        ViewHolder(View itemView) {
            super(itemView);
            containerDetails = itemView.findViewById(R.id.container_details);
            containerExpand = itemView.findViewById(R.id.container_expand);
            containerExpand.setOnClickListener(this);

            txtDescription = itemView.findViewById(R.id.txt_expense_description);
            txtAmount = itemView.findViewById(R.id.txt_expense_amount);
            txtDate = itemView.findViewById(R.id.text_expense_date);
            txtDetail1 = itemView.findViewById(R.id.text_expense_detail_1);
            txtDetail2 = itemView.findViewById(R.id.text_expense_detail_2);
            txtDetail3 = itemView.findViewById(R.id.txt_expense_detail_3);

            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnEdit.setOnClickListener(this);

            btnExpand = itemView.findViewById(R.id.btn_expand);
        }

        private void bind(final Expense expense) {
            txtDetail1.setText(expense.getPaymentMethod());
            txtDetail2.setText(expense.getCategory());
            txtDescription.setText(expense.getDescription());
            txtAmount.setText(String.valueOf(expense.getAmount()));
            txtDate.setText(DateHelper.getFriendlyDate(expense.getDateTime()));
            if (TextUtils.isEmpty(expense.getStore())) {
                txtDetail3.setVisibility(View.GONE);
            } else {
                txtDetail3.setText(expense.getStore());
            }

            final boolean isExpanded = getAdapterPosition() == mExpandedPosition;
            if (isExpanded) {
                containerDetails.setVisibility(View.VISIBLE);
                btnExpand.setImageResource(R.drawable.ic_up);
            } else {
                containerDetails.setVisibility(View.GONE);
                btnExpand.setImageResource(R.drawable.ic_down);
            }

            btnExpand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    handleExpandClicked(isExpanded);
                }
            });
            containerExpand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    handleExpandClicked(isExpanded);
                }
            });


       /* if (expense.isSynced()) {
            txtAmount.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
        } else {
            txtAmount.setTextColor(ContextCompat.getColor(mContext, R.color.red));
        }*/
        }

        @Override
        public void onClick(View view) {
            if (view == btnEdit) {
            }
        }

        private void handleExpandClicked(boolean isExpanded) {
            mExpandedPosition = isExpanded ? -1 : getAdapterPosition();
            notifyDataSetChanged();
        }
    }

    public void setExpenses(List<Expense> expenses) {
        if (mExpenses != null) {
            ExpenseDiffCallback callback = new ExpenseDiffCallback(mExpenses, expenses);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(callback);

            mExpenses.clear();
            mExpenses.addAll(expenses);
            diffResult.dispatchUpdatesTo(this);
        } else {
            // first initialization
            mExpenses = expenses;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.expense_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (mExpenses != null) {
            Expense expense = mExpenses.get(position);
            holder.bind(expense);
        }
    }

    @Override
    public int getItemCount() {
        if (mExpenses == null) {
            return 0;
        }
        return mExpenses.size();
    }

    class ExpenseDiffCallback extends DiffUtil.Callback {

        private final List<Expense> oldExpenses, newExpenses;

        ExpenseDiffCallback(List<Expense> oldExpenses, List<Expense> newExpenses) {
            this.oldExpenses = oldExpenses;
            this.newExpenses = newExpenses;
        }

        @Override
        public int getOldListSize() {
            return oldExpenses.size();
        }

        @Override
        public int getNewListSize() {
            return newExpenses.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldExpenses.get(oldItemPosition).getId() ==
                    newExpenses.get(newItemPosition).getId();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldExpenses.get(oldItemPosition).equals(newExpenses.get(newItemPosition));
        }
    }
}
