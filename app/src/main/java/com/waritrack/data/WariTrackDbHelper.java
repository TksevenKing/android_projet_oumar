package com.waritrack.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WariTrackDbHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "waritrack.db";
    public static final int DB_VERSION = 3;

    public WariTrackDbHelper(Context context) {
        super(context.getApplicationContext(), DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS expenses(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "amount REAL NOT NULL," +
                "category TEXT NOT NULL," +
                "date INTEGER NOT NULL," +
                "note TEXT NOT NULL DEFAULT ''" +
                ");");
        createCategoriesTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE expenses ADD COLUMN note TEXT NOT NULL DEFAULT ''");
        }
        if (oldVersion < 3) {
            createCategoriesTable(db);
            seedCategories(db);
        }
    }

    private void createCategoriesTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS categories(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL UNIQUE," +
                "colorHex TEXT NOT NULL DEFAULT '#2196F3'" +
                ");");
    }

    private void seedCategories(SQLiteDatabase db) {
        db.execSQL("INSERT OR IGNORE INTO categories(name) " +
                "SELECT DISTINCT category FROM expenses WHERE category IS NOT NULL AND category != ''");
    }
}
