package com.waritrack.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.waritrack.data.ExpenseRepository;

public class ViewModelFactory implements ViewModelProvider.Factory {
    private final ExpenseRepository repository;

    public ViewModelFactory(ExpenseRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainViewModel.class)) {
            return (T) new MainViewModel(repository);
        }
        if (modelClass.isAssignableFrom(AddEditViewModel.class)) {
            return (T) new AddEditViewModel(repository);
        }
        if (modelClass.isAssignableFrom(ExpenseDetailViewModel.class)) {
            return (T) new ExpenseDetailViewModel(repository);
        }
        if (modelClass.isAssignableFrom(CategoriesViewModel.class)) {
            return (T) new CategoriesViewModel(repository);
        }
        if (modelClass.isAssignableFrom(AddEditCategoryViewModel.class)) {
            return (T) new AddEditCategoryViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
