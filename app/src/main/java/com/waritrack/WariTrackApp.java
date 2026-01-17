package com.waritrack;

import android.app.Application;

import com.waritrack.data.AppDatabase;
import com.waritrack.data.ExpenseRepository;

public class WariTrackApp extends Application {
    private ExpenseRepository repository;

    @Override
    public void onCreate() {
        super.onCreate();
        repository = new ExpenseRepository(AppDatabase.getInstance(this).expenseDao());
    }

    public ExpenseRepository getRepository() {
        return repository;
    }
}
