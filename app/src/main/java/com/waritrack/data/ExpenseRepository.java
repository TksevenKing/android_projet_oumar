package com.waritrack.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExpenseRepository {
    private final ExpenseSqliteDao dao;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final MutableLiveData<Long> changeSignal = new MutableLiveData<>(0L);

    public ExpenseRepository(ExpenseSqliteDao dao) {
        this.dao = dao;
    }

    public LiveData<List<Expense>> getFiltered(String search, String category) {
        String safeSearch = search == null ? "" : search;
        return Transformations.switchMap(changeSignal, ignored -> loadExpenses(safeSearch, category));
    }

    public LiveData<List<String>> getCategories() {
        return Transformations.switchMap(changeSignal, ignored -> loadCategories());
    }

    public LiveData<Double> getTotalAll() {
        return Transformations.switchMap(changeSignal, ignored -> loadTotalAll());
    }

    public LiveData<Double> getTotalBetween(long start, long end) {
        return Transformations.switchMap(changeSignal, ignored -> loadTotalBetween(start, end));
    }

    public LiveData<List<CategoryTotal>> getTopCategories() {
        return Transformations.switchMap(changeSignal, ignored -> loadTopCategories());
    }

    public LiveData<Expense> getById(long id) {
        return Transformations.switchMap(changeSignal, ignored -> loadExpenseById(id));
    }

    public void insert(Expense expense) {
        executorService.execute(() -> {
            dao.insert(expense);
            notifyChange();
        });
    }

    public void update(Expense expense) {
        executorService.execute(() -> {
            dao.update(expense);
            notifyChange();
        });
    }

    public void delete(Expense expense) {
        executorService.execute(() -> {
            dao.delete(expense.getId());
            notifyChange();
        });
    }

    private LiveData<List<Expense>> loadExpenses(String search, String category) {
        MutableLiveData<List<Expense>> liveData = new MutableLiveData<>();
        executorService.execute(() -> liveData.postValue(dao.getFilteredSync(search, category)));
        return liveData;
    }

    private LiveData<List<String>> loadCategories() {
        MutableLiveData<List<String>> liveData = new MutableLiveData<>();
        executorService.execute(() -> liveData.postValue(dao.getCategoriesSync()));
        return liveData;
    }

    private LiveData<Double> loadTotalAll() {
        MutableLiveData<Double> liveData = new MutableLiveData<>();
        executorService.execute(() -> liveData.postValue(dao.getTotalAllSync()));
        return liveData;
    }

    private LiveData<Double> loadTotalBetween(long start, long end) {
        MutableLiveData<Double> liveData = new MutableLiveData<>();
        executorService.execute(() -> liveData.postValue(dao.getTotalBetweenSync(start, end)));
        return liveData;
    }

    private LiveData<List<CategoryTotal>> loadTopCategories() {
        MutableLiveData<List<CategoryTotal>> liveData = new MutableLiveData<>();
        executorService.execute(() -> liveData.postValue(dao.getTopCategoriesSync()));
        return liveData;
    }

    private LiveData<Expense> loadExpenseById(long id) {
        MutableLiveData<Expense> liveData = new MutableLiveData<>();
        executorService.execute(() -> liveData.postValue(dao.getByIdSync(id)));
        return liveData;
    }

    private void notifyChange() {
        Long current = changeSignal.getValue();
        long next = current == null ? 1L : current + 1L;
        changeSignal.postValue(next);
    }
}
