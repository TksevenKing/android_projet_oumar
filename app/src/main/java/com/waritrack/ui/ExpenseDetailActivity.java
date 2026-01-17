package com.waritrack.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.waritrack.R;
import com.waritrack.WariTrackApp;
import com.waritrack.data.Expense;
import com.waritrack.util.DateUtils;
import com.waritrack.util.PreferenceUtils;

public class ExpenseDetailActivity extends AppCompatActivity {
    public static final String EXTRA_EXPENSE_ID = "expenseId";

    private ExpenseDetailViewModel viewModel;
    private Expense currentExpense;

    private TextView amountText;
    private TextView categoryText;
    private TextView dateText;
    private TextView noteText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_detail);

        ViewModelFactory factory = new ViewModelFactory(((WariTrackApp) getApplication()).getRepository());
        viewModel = new ViewModelProvider(this, factory).get(ExpenseDetailViewModel.class);

        amountText = findViewById(R.id.text_amount);
        categoryText = findViewById(R.id.text_category);
        dateText = findViewById(R.id.text_date);
        noteText = findViewById(R.id.text_note);
        Button editButton = findViewById(R.id.button_edit);
        Button deleteButton = findViewById(R.id.button_delete);
        Button shareButton = findViewById(R.id.button_share);

        setTitle(R.string.title_expense_detail);

        long expenseId = getIntent().getLongExtra(EXTRA_EXPENSE_ID, -1);
        if (expenseId <= 0) {
            Toast.makeText(this, R.string.expense_deleted, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        viewModel.getExpense(expenseId).observe(this, expense -> {
            if (expense == null) {
                Toast.makeText(this, R.string.expense_deleted, Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            currentExpense = expense;
            bindExpense(expense);
        });

        editButton.setOnClickListener(v -> {
            if (currentExpense == null) {
                return;
            }
            Intent intent = new Intent(this, AddEditExpenseActivity.class);
            intent.putExtra(MainActivity.EXTRA_EXPENSE_ID, currentExpense.getId());
            startActivity(intent);
        });

        deleteButton.setOnClickListener(v -> confirmDelete());

        shareButton.setOnClickListener(v -> shareExpense());
    }

    private void bindExpense(Expense expense) {
        String currency = PreferenceUtils.getCurrency(this);
        String amount = getString(R.string.amount_currency_format, expense.getAmount(), currency);
        amountText.setText(amount);
        categoryText.setText(expense.getCategory());
        String formattedDate = DateUtils.formatDate(expense.getDate(), PreferenceUtils.getDateFormat(this));
        dateText.setText(formattedDate);
        noteText.setText(expense.getNote().isEmpty() ? getString(R.string.no_note) : expense.getNote());
    }

    private void confirmDelete() {
        if (currentExpense == null) {
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_delete_title)
                .setMessage(R.string.confirm_delete_message)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    viewModel.deleteExpense(currentExpense);
                    finish();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void shareExpense() {
        if (currentExpense == null) {
            return;
        }
        String currency = PreferenceUtils.getCurrency(this);
        String formattedDate = DateUtils.formatDate(currentExpense.getDate(), PreferenceUtils.getDateFormat(this));
        String note = currentExpense.getNote().isEmpty() ? getString(R.string.no_note) : currentExpense.getNote();
        String formattedAmount = getString(R.string.amount_currency_format, currentExpense.getAmount(), currency);
        String shareText = "Dépense: " + formattedAmount + "\n" +
                "Catégorie: " + currentExpense.getCategory() + "\n" +
                "Date: " + formattedDate + "\n" +
                "Note: " + note;

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(intent, getString(R.string.share)));
    }
}
