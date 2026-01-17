package com.waritrack.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.waritrack.data.Expense;
import com.waritrack.data.ExpenseRepository;

public class ExpenseDetailViewModel extends ViewModel {
    private final ExpenseRepository repository;

    public ExpenseDetailViewModel(ExpenseRepository repository) {
        this.repository = repository;
    }

    public LiveData<Expense> getExpense(long id) {
        return repository.getById(id);
    }

    public void deleteExpense(Expense expense) {
        repository.delete(expense);
    }
}
