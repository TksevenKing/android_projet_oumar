package com.waritrack;

import android.app.Application;

import com.waritrack.data.ExpenseRepository;
import com.waritrack.data.ExpenseSqliteDao;
import com.waritrack.data.WariTrackDbHelper;
import com.waritrack.data.CategorySqliteDao;

public class WariTrackApp extends Application {
    private ExpenseRepository repository;

    @Override
    public void onCreate() {
        super.onCreate();
        WariTrackDbHelper helper = new WariTrackDbHelper(this);
        ExpenseSqliteDao dao = new ExpenseSqliteDao(helper);
        CategorySqliteDao categoryDao = new CategorySqliteDao(helper);
        repository = new ExpenseRepository(dao, categoryDao);
    }

    public ExpenseRepository getRepository() {
        return repository;
    }
}
