package com.ramitsuri.expensemanagerrewrite.ui.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ramitsuri.expensemanagerrewrite.IntDefs.ListItemType;
import com.ramitsuri.expensemanagerrewrite.R;
import com.ramitsuri.expensemanagerrewrite.entities.Expense;
import com.ramitsuri.expensemanagerrewrite.entities.ExpenseWrapper;
import com.ramitsuri.expensemanagerrewrite.ui.decoration.StickyHeaderItemDecoration;
import com.ramitsuri.expensemanagerrewrite.utils.DateHelper;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

public class ExpenseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        StickyHeaderItemDecoration.StickyHeaderInterface {

    public interface ItemClickListener {
        void onItemClicked(@NonNull ExpenseWrapper expense);
    }

    @Nullable
    private List<ExpenseWrapper> mExpenses;
    @Nullable
    private ItemClickListener mCallback;

    public void setExpenses(List<ExpenseWrapper> expenses) {
        if (mExpenses != null) {
            /*ExpenseDiffCallback callback = new ExpenseDiffCallback(mExpenses, expenses);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(callback);*/

            mExpenses.clear();
            mExpenses.addAll(expenses);
            //diffResult.dispatchUpdatesTo(this);
        } else {
            // first initialization
            mExpenses = expenses;
        }
        notifyDataSetChanged();
    }

    public void setCallback(@Nullable ItemClickListener callback) {
        mCallback = callback;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
            @ListItemType int viewType) {
        View view;
        if (viewType == ListItemType.HEADER) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.expense_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.expense_item, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (mExpenses != null) {
            ExpenseWrapper wrapper = getItem(position);
            if (wrapper == null) {
                return;
            }
            if (holder instanceof ItemViewHolder) {
                ((ItemViewHolder)holder).bind(wrapper.getExpense());
            } else {
                ((HeaderViewHolder)holder).bind(wrapper.getDate());
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mExpenses == null) {
            return 0;
        }
        return mExpenses.size();
    }

    @Override
    @ListItemType
    public int getItemViewType(int position) {
        if (mExpenses == null) {
            return ListItemType.ITEM;
        }
        ExpenseWrapper wrapper = getItem(position);
        if (wrapper == null) {
            return ListItemType.ITEM;
        }
        return wrapper.getItemType();
    }

    @Nullable
    private ExpenseWrapper getItem(int position) {
        if (mExpenses == null) {
            Timber.w("getItem() mWrappers is null");
            return null;
        }
        if (position < 0 || position >= mExpenses.size()) {
            Timber.e("getItem() invalid position");
            return null;
        }
        return mExpenses.get(position);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ViewGroup container;
        private TextView txtDate, txtDescription, txtAmount, txtDetail1, txtDetail2, txtDetail3;

        ItemViewHolder(View itemView) {
            super(itemView);

            container = itemView.findViewById(R.id.container);
            container.setOnClickListener(this);

            txtDescription = itemView.findViewById(R.id.txt_expense_description);
            txtAmount = itemView.findViewById(R.id.txt_expense_amount);
            txtDate = itemView.findViewById(R.id.text_expense_date);
            txtDetail1 = itemView.findViewById(R.id.text_expense_detail_1);
            txtDetail2 = itemView.findViewById(R.id.text_expense_detail_2);
            txtDetail3 = itemView.findViewById(R.id.text_expense_detail_3);
        }

        private void bind(final Expense expense) {
            if (TextUtils.isEmpty(expense.getStore())) {
                txtDetail1.setVisibility(View.GONE);
            } else {
                txtDetail1.setText(expense.getStore());
            }
            txtDetail2.setText(expense.getCategory());
            txtDescription.setText(expense.getDescription());
            txtAmount.setText(txtAmount.getContext()
                    .getString(R.string.amount_with_currency, String.valueOf(expense.getAmount())));
            txtDate.setText(DateHelper.getFriendlyDate(expense.getDateTime()));
            txtDetail3.setText(expense.getPaymentMethod());



       /* if (expense.isSynced()) {
            txtAmount.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
        } else {
            txtAmount.setTextColor(ContextCompat.getColor(mContext, R.color.red));
        }*/
        }

        @Override
        public void onClick(View view) {
            if (view == container) {
                if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                    if (mCallback != null) {
                        if (mExpenses != null) {
                            ExpenseWrapper wrapper = getItem(getAdapterPosition());
                            if (wrapper == null) {
                                Timber.w("onClick() -> clicked wrapper is null");
                            } else {
                                mCallback.onItemClicked(wrapper);
                            }
                        } else {
                            Timber.w("mExpenses is null");
                        }
                    } else {
                        Timber.w("mCallback is null");
                    }
                    notifyDataSetChanged();
                } else {
                    Timber.w("getAdapterPosition returned -1");
                }
            }
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        private TextView txtDate;

        HeaderViewHolder(View itemView) {
            super(itemView);
            txtDate = itemView.findViewById(R.id.text_header);
        }

        private void bind(String date) {
            txtDate.setText(date);
        }
    }

    class ExpenseDiffCallback extends DiffUtil.Callback {

        private final List<ExpenseWrapper> oldExpenses, newExpenses;

        ExpenseDiffCallback(List<ExpenseWrapper> oldExpenses, List<ExpenseWrapper> newExpenses) {
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
            ExpenseWrapper oldWrapper = oldExpenses.get(oldItemPosition);
            ExpenseWrapper newWrapper = newExpenses.get(newItemPosition);

            if (oldWrapper.getDate() != null && newWrapper.getDate() != null) {
                return oldWrapper.getDate().equals(newWrapper.getDate());
            } else if (oldWrapper.getExpense() != null && newWrapper.getExpense() != null) {
                return oldWrapper.getExpense().getId() == newWrapper.getExpense().getId();
            } else {
                return false;
            }
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldExpenses.get(oldItemPosition).equals(newExpenses.get(newItemPosition));
        }
    }

    @Override
    public int getHeaderPositionForItem(int itemPosition) {
        while (itemPosition >= 0) {
            ExpenseWrapper wrapper = getItem(itemPosition);
            if (wrapper != null && wrapper.getItemType() == ListItemType.HEADER) {
                break;
            }
            itemPosition--;
        }
        return itemPosition;
    }

    @Override
    public int getHeaderLayout() {
        return R.layout.expense_header;
    }

    @Override
    public void bindHeaderData(View header, int headerPosition) {
        TextView textView = header.findViewById(R.id.text_header);
        if (textView != null) {
            ExpenseWrapper wrapper = getItem(headerPosition);
            if (wrapper != null) {
                textView.setText(wrapper.getDate());
            }
        }
    }

    @Override
    public boolean isHeader(int itemPosition) {
        return (getItemViewType(itemPosition) == ListItemType.HEADER);
    }
}
