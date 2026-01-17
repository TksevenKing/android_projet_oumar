package com.waritrack.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.waritrack.data.Expense;
import com.waritrack.data.ExpenseRepository;

public class AddEditViewModel extends ViewModel {
    private final ExpenseRepository repository;

    public AddEditViewModel(ExpenseRepository repository) {
        this.repository = repository;
    }

    public LiveData<Expense> getExpense(long id) {
        return repository.getById(id);
    }

    public void insert(Expense expense) {
        repository.insert(expense);
    }

    public void update(Expense expense) {
        repository.update(expense);
    }
}
