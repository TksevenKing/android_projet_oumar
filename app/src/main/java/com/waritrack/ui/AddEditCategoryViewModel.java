package com.waritrack.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.waritrack.data.ExpenseRepository;
import com.waritrack.data.models.Category;

public class AddEditCategoryViewModel extends ViewModel {
    private final ExpenseRepository repository;

    public AddEditCategoryViewModel(ExpenseRepository repository) {
        this.repository = repository;
    }

    public LiveData<Category> getCategory(long id) {
        return repository.getCategoryById(id);
    }

    public void insert(String name, String colorHex) {
        repository.insertCategory(name, colorHex);
    }

    public void update(long id, String name, String colorHex) {
        repository.updateCategory(id, name, colorHex);
    }

    public void isNameExists(String name, long excludeId, ExpenseRepository.BooleanCallback callback) {
        repository.isCategoryNameExists(name, excludeId, callback);
    }
}
