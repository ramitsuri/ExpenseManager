package com.ramitsuri.expensemanager.ui.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ramitsuri.expensemanager.BuildConfig;
import com.ramitsuri.expensemanager.constants.intDefs.ListItemType;
import com.ramitsuri.expensemanager.R;
import com.ramitsuri.expensemanager.entities.Expense;
import com.ramitsuri.expensemanager.entities.ExpenseWrapper;
import com.ramitsuri.expensemanager.ui.decoration.StickyHeaderItemDecoration;
import com.ramitsuri.expensemanager.utils.CurrencyHelper;
import com.ramitsuri.expensemanager.utils.DateHelper;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
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
            mExpenses.clear();
            mExpenses.addAll(expenses);
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
        private View flagStatus;

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
            flagStatus = itemView.findViewById(R.id.flag_status);
        }

        private void bind(final Expense expense) {
            if (TextUtils.isEmpty(expense.getStore())) {
                txtDetail3.setVisibility(View.GONE);
            } else {
                txtDetail3.setText(expense.getStore());
            }
            txtDetail2.setText(expense.getPaymentMethod());
            txtDescription.setText(expense.getDescription());
            txtAmount.setText(CurrencyHelper.formatForDisplay(true, expense.getAmount()));
            txtDate.setText(DateHelper.getFriendlyDate(expense.getDateTime()));
            txtDetail1.setText(expense.getCategory());
            if (expense.isStarred()) {
                flagStatus.setVisibility(View.VISIBLE);
            } else {
                flagStatus.setVisibility(View.GONE);
            }
            if (BuildConfig.DEBUG) {
                if (expense.isSynced()) {
                    txtAmount.setTextColor(
                            ContextCompat.getColor(txtAmount.getContext(), R.color.color_teal));
                } else {
                    txtAmount.setTextColor(
                            ContextCompat.getColor(txtAmount.getContext(), R.color.color_red));
                }
            }
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

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {

        private TextView txtDate;

        HeaderViewHolder(View itemView) {
            super(itemView);
            txtDate = itemView.findViewById(R.id.text_header);
        }

        private void bind(String date) {
            txtDate.setText(date);
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
