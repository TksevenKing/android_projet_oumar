package com.waritrack.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.waritrack.R;
import com.waritrack.data.Expense;
import com.waritrack.util.DateUtils;

public class ExpenseAdapter extends ListAdapter<Expense, ExpenseAdapter.ExpenseViewHolder> {
    interface ExpenseListener {
        void onEdit(Expense expense);
        void onDelete(Expense expense);
    }

    private final ExpenseListener listener;

    public ExpenseAdapter(ExpenseListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Expense> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Expense>() {
                @Override
                public boolean areItemsTheSame(@NonNull Expense oldItem, @NonNull Expense newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull Expense oldItem, @NonNull Expense newItem) {
                    return oldItem.getAmount() == newItem.getAmount()
                            && oldItem.getCategory().equals(newItem.getCategory())
                            && oldItem.getDate() == newItem.getDate()
                            && oldItem.getNote().equals(newItem.getNote());
                }
            };

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class ExpenseViewHolder extends RecyclerView.ViewHolder {
        private final TextView amountText;
        private final TextView categoryText;
        private final TextView dateText;
        private final TextView noteText;
        private final ImageButton editButton;
        private final ImageButton deleteButton;

        ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            amountText = itemView.findViewById(R.id.text_amount);
            categoryText = itemView.findViewById(R.id.text_category);
            dateText = itemView.findViewById(R.id.text_date);
            noteText = itemView.findViewById(R.id.text_note);
            editButton = itemView.findViewById(R.id.button_edit);
            deleteButton = itemView.findViewById(R.id.button_delete);
        }

        void bind(Expense expense) {
            String amount = itemView.getContext().getString(R.string.amount_format, expense.getAmount());
            amountText.setText(amount);
            categoryText.setText(expense.getCategory());
            dateText.setText(DateUtils.formatDate(expense.getDate()));
            noteText.setText(expense.getNote().isEmpty() ? itemView.getContext().getString(R.string.no_note) : expense.getNote());

            editButton.setOnClickListener(v -> listener.onEdit(expense));
            deleteButton.setOnClickListener(v -> listener.onDelete(expense));
        }
    }
}
