package com.waritrack.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.waritrack.data.ExpenseRepository;
import com.waritrack.data.models.Category;

import java.util.List;

public class CategoriesViewModel extends ViewModel {
    private final ExpenseRepository repository;

    public CategoriesViewModel(ExpenseRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<Category>> getCategories() {
        return repository.getAllCategories();
    }

    public void deleteCategory(long id) {
        repository.deleteCategory(id);
    }

    public void countExpensesUsingCategory(String name, ExpenseRepository.IntCallback callback) {
        repository.countExpensesUsingCategory(name, callback);
    }

    public void replaceCategory(String oldName, String newName, Runnable onComplete) {
        repository.replaceCategoryInExpenses(oldName, newName, onComplete);
    }
}
