package com.waritrack.ui;

import android.graphics.Color;
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
import com.waritrack.data.models.Category;

public class CategoryAdapter extends ListAdapter<Category, CategoryAdapter.CategoryViewHolder> {
    interface CategoryListener {
        void onItemClick(Category category);
        void onDelete(Category category);
    }

    private final CategoryListener listener;

    public CategoryAdapter(CategoryListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Category> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Category>() {
                @Override
                public boolean areItemsTheSame(@NonNull Category oldItem, @NonNull Category newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull Category oldItem, @NonNull Category newItem) {
                    return oldItem.getName().equals(newItem.getName())
                            && oldItem.getColorHex().equals(newItem.getColorHex());
                }
            };

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameText;
        private final View colorView;
        private final ImageButton deleteButton;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.text_category_name);
            colorView = itemView.findViewById(R.id.view_color);
            deleteButton = itemView.findViewById(R.id.button_delete_category);
        }

        void bind(Category category) {
            nameText.setText(category.getName());
            colorView.setBackgroundColor(Color.parseColor(category.getColorHex()));

            itemView.setOnClickListener(v -> listener.onItemClick(category));
            deleteButton.setOnClickListener(v -> listener.onDelete(category));
        }
    }
}
