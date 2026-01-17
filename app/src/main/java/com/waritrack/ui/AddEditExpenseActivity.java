package com.waritrack.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.waritrack.R;
import com.waritrack.WariTrackApp;
import com.waritrack.data.Expense;
import com.waritrack.util.DateUtils;

import java.util.Calendar;

public class AddEditExpenseActivity extends AppCompatActivity {
    private AddEditViewModel viewModel;
    private EditText amountInput;
    private EditText categoryInput;
    private EditText noteInput;
    private TextView dateText;
    private long selectedDate;
    private long editingExpenseId = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        ViewModelFactory factory = new ViewModelFactory(((WariTrackApp) getApplication()).getRepository());
        viewModel = new ViewModelProvider(this, factory).get(AddEditViewModel.class);

        amountInput = findViewById(R.id.edit_amount);
        categoryInput = findViewById(R.id.edit_category);
        noteInput = findViewById(R.id.edit_note);
        dateText = findViewById(R.id.text_date_value);
        Button dateButton = findViewById(R.id.button_pick_date);
        Button saveButton = findViewById(R.id.button_save);

        selectedDate = System.currentTimeMillis();
        updateDateLabel();

        dateButton.setOnClickListener(v -> openDatePicker());
        saveButton.setOnClickListener(v -> saveExpense());

        if (getIntent() != null && getIntent().hasExtra(MainActivity.EXTRA_EXPENSE_ID)) {
            editingExpenseId = getIntent().getLongExtra(MainActivity.EXTRA_EXPENSE_ID, -1);
            viewModel.getExpense(editingExpenseId).observe(this, expense -> {
                if (expense != null) {
                    amountInput.setText(String.valueOf(expense.getAmount()));
                    categoryInput.setText(expense.getCategory());
                    noteInput.setText(expense.getNote());
                    selectedDate = expense.getDate();
                    updateDateLabel();
                }
            });
            setTitle(R.string.title_edit_expense);
        } else {
            setTitle(R.string.title_add_expense);
        }
    }

    private void openDatePicker() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(selectedDate);
        new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    Calendar selected = Calendar.getInstance();
                    selected.set(year, month, dayOfMonth, 12, 0, 0);
                    selectedDate = selected.getTimeInMillis();
                    updateDateLabel();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    private void updateDateLabel() {
        dateText.setText(DateUtils.formatDate(selectedDate));
    }

    private void saveExpense() {
        String amountText = amountInput.getText().toString().trim();
        String categoryText = categoryInput.getText().toString().trim();
        String noteText = noteInput.getText().toString().trim();

        if (TextUtils.isEmpty(amountText)) {
            amountInput.setError(getString(R.string.error_amount_required));
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            amountInput.setError(getString(R.string.error_amount_invalid));
            return;
        }

        if (amount <= 0) {
            amountInput.setError(getString(R.string.error_amount_invalid));
            return;
        }

        if (TextUtils.isEmpty(categoryText)) {
            categoryInput.setError(getString(R.string.error_category_required));
            return;
        }

        Expense expense = new Expense(amount, categoryText, selectedDate, noteText);
        if (editingExpenseId > 0) {
            expense.setId(editingExpenseId);
            viewModel.update(expense);
        } else {
            viewModel.insert(expense);
        }

        Snackbar.make(findViewById(R.id.add_edit_root), R.string.expense_saved, Snackbar.LENGTH_SHORT)
                .show();
        finish();
    }
}
