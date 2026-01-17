package com.waritrack.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.waritrack.R;
import com.waritrack.WariTrackApp;
import com.waritrack.data.models.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoriesActivity extends AppCompatActivity implements CategoryAdapter.CategoryListener {
    public static final String EXTRA_CATEGORY_ID = "categoryId";

    private CategoriesViewModel viewModel;
    private CategoryAdapter adapter;
    private List<Category> currentCategories = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        setTitle(R.string.categories_title);

        ViewModelFactory factory = new ViewModelFactory(((WariTrackApp) getApplication()).getRepository());
        viewModel = new ViewModelProvider(this, factory).get(CategoriesViewModel.class);

        RecyclerView recyclerView = findViewById(R.id.recycler_categories);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CategoryAdapter(this);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab_add_category);
        fab.setOnClickListener(v -> startActivity(new android.content.Intent(this, AddEditCategoryActivity.class)));

        viewModel.getCategories().observe(this, categories -> {
            currentCategories = categories == null ? new ArrayList<>() : new ArrayList<>(categories);
            adapter.submitList(new ArrayList<>(currentCategories));
        });
    }

    @Override
    public void onItemClick(Category category) {
        android.content.Intent intent = new android.content.Intent(this, AddEditCategoryActivity.class);
        intent.putExtra(EXTRA_CATEGORY_ID, category.getId());
        startActivity(intent);
    }

    @Override
    public void onDelete(Category category) {
        viewModel.countExpensesUsingCategory(category.getName(), count -> {
            if (count > 0) {
                showReplaceDialog(category);
            } else {
                confirmDelete(category);
            }
        });
    }

    private void confirmDelete(Category category) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_delete_title)
                .setMessage(R.string.confirm_delete_message)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    viewModel.deleteCategory(category.getId());
                    Toast.makeText(this, R.string.category_deleted, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void showReplaceDialog(Category category) {
        if (currentCategories.size() <= 1) {
            Toast.makeText(this, R.string.no_categories_available, Toast.LENGTH_SHORT).show();
            return;
        }

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_replace_category, null);
        Spinner spinner = dialogView.findViewById(R.id.spinner_replace_category);

        List<String> options = new ArrayList<>();
        for (Category item : currentCategories) {
            if (item.getId() != category.getId()) {
                options.add(item.getName());
            }
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                options
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        new AlertDialog.Builder(this)
                .setTitle(R.string.category_in_use)
                .setView(dialogView)
                .setPositiveButton(R.string.replace_category, (dialog, which) -> {
                    String selected = (String) spinner.getSelectedItem();
                    if (selected == null) {
                        return;
                    }
                    viewModel.replaceCategory(category.getName(), selected, () -> {
                        viewModel.deleteCategory(category.getId());
                        Toast.makeText(this, R.string.category_deleted, Toast.LENGTH_SHORT).show();
                    });
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }
}
