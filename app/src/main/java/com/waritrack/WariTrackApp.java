package com.waritrack;

import android.app.Application;

import com.waritrack.data.ExpenseRepository;
import com.waritrack.data.ExpenseSqliteDao;
import com.waritrack.data.WariTrackDbHelper;

public class WariTrackApp extends Application {
    private ExpenseRepository repository;

    @Override
    public void onCreate() {
        super.onCreate();
        WariTrackDbHelper helper = new WariTrackDbHelper(this);
        ExpenseSqliteDao dao = new ExpenseSqliteDao(helper);
        repository = new ExpenseRepository(dao);
    }

    public ExpenseRepository getRepository() {
        return repository;
    }
}
