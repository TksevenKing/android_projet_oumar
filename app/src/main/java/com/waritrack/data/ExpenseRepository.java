package com.waritrack.data;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExpenseRepository {
    private final ExpenseDao expenseDao;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public ExpenseRepository(ExpenseDao expenseDao) {
        this.expenseDao = expenseDao;
    }

    public LiveData<List<Expense>> getFiltered(String search, String category) {
        return expenseDao.getFiltered(search, category);
    }

    public LiveData<List<String>> getCategories() {
        return expenseDao.getCategories();
    }

    public LiveData<Double> getTotalAll() {
        return expenseDao.getTotalAll();
    }

    public LiveData<Double> getTotalBetween(long start, long end) {
        return expenseDao.getTotalBetween(start, end);
    }

    public LiveData<List<CategoryTotal>> getTopCategories() {
        return expenseDao.getTopCategories();
    }

    public LiveData<Expense> getById(long id) {
        return expenseDao.getById(id);
    }

    public void insert(Expense expense) {
        executorService.execute(() -> expenseDao.insert(expense));
    }

    public void update(Expense expense) {
        executorService.execute(() -> expenseDao.update(expense));
    }

    public void delete(Expense expense) {
        executorService.execute(() -> expenseDao.delete(expense));
    }
}
