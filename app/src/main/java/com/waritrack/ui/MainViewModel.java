package com.waritrack.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.waritrack.data.CategoryTotal;
import com.waritrack.data.Expense;
import com.waritrack.data.ExpenseRepository;

import java.util.Calendar;
import java.util.List;

public class MainViewModel extends ViewModel {
    private final ExpenseRepository repository;
    private final MutableLiveData<FilterState> filterState = new MutableLiveData<>();

    private final LiveData<List<Expense>> expenses = Transformations.switchMap(
            filterState,
            state -> repository.getFiltered(state.searchQuery, state.category)
    );

    private final LiveData<List<String>> categories = repository.getCategories();
    private final LiveData<Double> totalAll = repository.getTotalAll();
    private final LiveData<Double> totalMonth;
    private final LiveData<List<CategoryTotal>> topCategories = repository.getTopCategories();

    public MainViewModel(ExpenseRepository repository) {
        this.repository = repository;
        filterState.setValue(new FilterState("", null));
        long[] range = getCurrentMonthRange();
        totalMonth = repository.getTotalBetween(range[0], range[1]);
    }

    public LiveData<List<Expense>> getExpenses() {
        return expenses;
    }

    public LiveData<List<String>> getCategories() {
        return categories;
    }

    public LiveData<Double> getTotalAll() {
        return totalAll;
    }

    public LiveData<Double> getTotalMonth() {
        return totalMonth;
    }

    public LiveData<List<CategoryTotal>> getTopCategories() {
        return topCategories;
    }

    public void updateSearchQuery(String query) {
        FilterState current = filterState.getValue();
        String category = current == null ? null : current.category;
        filterState.setValue(new FilterState(query, category));
    }

    public void updateCategoryFilter(String category) {
        FilterState current = filterState.getValue();
        String query = current == null ? "" : current.searchQuery;
        filterState.setValue(new FilterState(query, category));
    }

    public void deleteExpense(Expense expense) {
        repository.delete(expense);
    }

    public void insertExpense(Expense expense) {
        repository.insert(expense);
    }

    private long[] getCurrentMonthRange() {
        Calendar start = Calendar.getInstance();
        start.set(Calendar.DAY_OF_MONTH, 1);
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);

        Calendar end = (Calendar) start.clone();
        end.add(Calendar.MONTH, 1);
        end.add(Calendar.MILLISECOND, -1);

        return new long[]{start.getTimeInMillis(), end.getTimeInMillis()};
    }

    private static class FilterState {
        private final String searchQuery;
        private final String category;

        private FilterState(String searchQuery, String category) {
            this.searchQuery = searchQuery == null ? "" : searchQuery;
            this.category = category;
        }
    }
}
