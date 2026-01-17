package com.waritrack.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.waritrack.data.models.Category;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExpenseRepository {
    private final ExpenseSqliteDao dao;
    private final CategorySqliteDao categoryDao;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Executor mainExecutor;
    private final MutableLiveData<Long> changeSignal = new MutableLiveData<>(0L);

    public interface CategoryCallback {
        void onResult(Category category);
    }

    public interface CategoryListCallback {
        void onResult(List<Category> categories);
    }

    public interface ExpenseListCallback {
        void onResult(List<Expense> expenses);
    }

    public interface BooleanCallback {
        void onResult(boolean exists);
    }

    public interface IntCallback {
        void onResult(int value);
    }

    public ExpenseRepository(ExpenseSqliteDao dao, CategorySqliteDao categoryDao) {
        this.dao = dao;
        this.categoryDao = categoryDao;
        this.mainExecutor = new MainThreadExecutor();
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

    public LiveData<List<Category>> getAllCategories() {
        return Transformations.switchMap(changeSignal, ignored -> loadCategoriesList());
    }

    public LiveData<Category> getCategoryById(long id) {
        return Transformations.switchMap(changeSignal, ignored -> loadCategoryById(id));
    }

    public void insertCategory(String name, String colorHex) {
        executorService.execute(() -> {
            categoryDao.insertSync(name, colorHex);
            notifyChange();
        });
    }

    public void updateCategory(long id, String name, String colorHex) {
        executorService.execute(() -> {
            categoryDao.updateSync(id, name, colorHex);
            notifyChange();
        });
    }

    public void deleteCategory(long id) {
        executorService.execute(() -> {
            categoryDao.deleteByIdSync(id);
            notifyChange();
        });
    }

    public void isCategoryNameExists(String name, long excludeId, BooleanCallback callback) {
        executorService.execute(() -> {
            boolean exists = categoryDao.isNameExistsSync(name, excludeId);
            mainExecutor.execute(() -> callback.onResult(exists));
        });
    }

    public void countExpensesUsingCategory(String name, IntCallback callback) {
        executorService.execute(() -> {
            int count = categoryDao.countExpensesUsingCategorySync(name);
            mainExecutor.execute(() -> callback.onResult(count));
        });
    }

    public void replaceCategoryInExpenses(String oldName, String newName, Runnable onComplete) {
        executorService.execute(() -> {
            categoryDao.replaceCategoryInExpensesSync(oldName, newName);
            notifyChange();
            mainExecutor.execute(onComplete);
        });
    }

    public void getAllExpenses(ExpenseListCallback callback) {
        executorService.execute(() -> {
            List<Expense> expenses = dao.getFilteredSync("", null);
            mainExecutor.execute(() -> callback.onResult(expenses));
        });
    }

    public void getExpensesBetween(long start, long end, ExpenseListCallback callback) {
        executorService.execute(() -> {
            List<Expense> expenses = dao.getExpensesBetweenSync(start, end);
            mainExecutor.execute(() -> callback.onResult(expenses));
        });
    }

    private LiveData<List<Expense>> loadExpenses(String search, String category) {
        MutableLiveData<List<Expense>> liveData = new MutableLiveData<>();
        executorService.execute(() -> liveData.postValue(dao.getFilteredSync(search, category)));
        return liveData;
    }

    private LiveData<List<String>> loadCategories() {
        MutableLiveData<List<String>> liveData = new MutableLiveData<>();
        executorService.execute(() -> {
            List<Category> categories = categoryDao.getAllSync();
            java.util.List<String> names = new java.util.ArrayList<>();
            for (Category category : categories) {
                names.add(category.getName());
            }
            liveData.postValue(names);
        });
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

    private LiveData<List<Category>> loadCategoriesList() {
        MutableLiveData<List<Category>> liveData = new MutableLiveData<>();
        executorService.execute(() -> liveData.postValue(categoryDao.getAllSync()));
        return liveData;
    }

    private LiveData<Category> loadCategoryById(long id) {
        MutableLiveData<Category> liveData = new MutableLiveData<>();
        executorService.execute(() -> liveData.postValue(categoryDao.getByIdSync(id)));
        return liveData;
    }

    private void notifyChange() {
        Long current = changeSignal.getValue();
        long next = current == null ? 1L : current + 1L;
        changeSignal.postValue(next);
    }

    private static class MainThreadExecutor implements Executor {
        private final android.os.Handler handler = new android.os.Handler(android.os.Looper.getMainLooper());

        @Override
        public void execute(Runnable command) {
            handler.post(command);
        }
    }
}
