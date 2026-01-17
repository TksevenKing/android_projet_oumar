package com.waritrack.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.waritrack.R;
import com.waritrack.WariTrackApp;
import com.waritrack.data.CategoryTotal;
import com.waritrack.data.Expense;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ExpenseAdapter.ExpenseListener {
    public static final String EXTRA_EXPENSE_ID = "extra_expense_id";

    private MainViewModel viewModel;
    private ExpenseAdapter adapter;
    private Expense lastDeletedExpense;

    private TextView totalMonthText;
    private TextView totalAllText;
    private TextView topCategoriesText;
    private Spinner categorySpinner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewModelFactory factory = new ViewModelFactory(((WariTrackApp) getApplication()).getRepository());
        viewModel = new ViewModelProvider(this, factory).get(MainViewModel.class);

        RecyclerView recyclerView = findViewById(R.id.recycler_expenses);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ExpenseAdapter(this);
        recyclerView.setAdapter(adapter);

        totalMonthText = findViewById(R.id.text_total_month);
        totalAllText = findViewById(R.id.text_total_all);
        topCategoriesText = findViewById(R.id.text_top_categories);
        categorySpinner = findViewById(R.id.spinner_category);
        EditText searchInput = findViewById(R.id.edit_search);
        FloatingActionButton fab = findViewById(R.id.fab_add);

        fab.setOnClickListener(v -> startActivity(new Intent(this, AddEditExpenseActivity.class)));

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.updateSearchQuery(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        viewModel.getExpenses().observe(this, adapter::submitList);

        viewModel.getCategories().observe(this, categories -> {
            List<String> spinnerItems = new ArrayList<>();
            spinnerItems.add(getString(R.string.filter_all));
            if (categories != null) {
                spinnerItems.addAll(categories);
            }
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_spinner_item,
                    spinnerItems
            );
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            categorySpinner.setAdapter(spinnerAdapter);
        });

        categorySpinner.setOnItemSelectedListener(new SimpleItemSelectedListener(position -> {
            String selected = (String) categorySpinner.getItemAtPosition(position);
            if (getString(R.string.filter_all).equals(selected)) {
                viewModel.updateCategoryFilter(null);
            } else {
                viewModel.updateCategoryFilter(selected);
            }
        }));

        viewModel.getTotalMonth().observe(this,
                total -> totalMonthText.setText(getString(R.string.total_month_format, total)));
        viewModel.getTotalAll().observe(this,
                total -> totalAllText.setText(getString(R.string.total_all_format, total)));
        viewModel.getTopCategories().observe(this, this::renderTopCategories);
    }

    private void renderTopCategories(List<CategoryTotal> categories) {
        if (categories == null || categories.isEmpty()) {
            topCategoriesText.setText(getString(R.string.no_top_categories));
            return;
        }
        StringBuilder builder = new StringBuilder();
        int rank = 1;
        for (CategoryTotal total : categories) {
            builder.append(rank)
                    .append(". ")
                    .append(total.getCategory())
                    .append(" (")
                    .append(getString(R.string.amount_format, total.getTotal()))
                    .append(")\n");
            rank++;
        }
        topCategoriesText.setText(builder.toString().trim());
    }

    @Override
    public void onEdit(Expense expense) {
        Intent intent = new Intent(this, AddEditExpenseActivity.class);
        intent.putExtra(EXTRA_EXPENSE_ID, expense.getId());
        startActivity(intent);
    }

    @Override
    public void onDelete(Expense expense) {
        lastDeletedExpense = expense;
        viewModel.deleteExpense(expense);
        Snackbar.make(findViewById(R.id.root_layout), R.string.expense_deleted, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo, v -> {
                    if (lastDeletedExpense != null) {
                        viewModel.insertExpense(lastDeletedExpense);
                        lastDeletedExpense = null;
                    }
                })
                .show();
    }
}
