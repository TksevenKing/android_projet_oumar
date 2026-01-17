package com.waritrack.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.waritrack.data.models.Category;

import java.util.ArrayList;
import java.util.List;

public class CategorySqliteDao {
    private final WariTrackDbHelper helper;

    public CategorySqliteDao(WariTrackDbHelper helper) {
        this.helper = helper;
    }

    public List<Category> getAllSync() {
        SQLiteDatabase db = helper.getReadableDatabase();
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT id, name, colorHex FROM categories ORDER BY name ASC";
        try (Cursor cursor = db.rawQuery(sql, null)) {
            while (cursor.moveToNext()) {
                categories.add(mapCategory(cursor));
            }
        }
        return categories;
    }

    public Category getByIdSync(long id) {
        SQLiteDatabase db = helper.getReadableDatabase();
        String sql = "SELECT id, name, colorHex FROM categories WHERE id = ?";
        try (Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(id)})) {
            if (cursor.moveToFirst()) {
                return mapCategory(cursor);
            }
        }
        return null;
    }

    public long insertSync(String name, String colorHex) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("colorHex", colorHex);
        return db.insert("categories", null, values);
    }

    public int updateSync(long id, String name, String colorHex) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("colorHex", colorHex);
        return db.update("categories", values, "id = ?", new String[]{String.valueOf(id)});
    }

    public int deleteByIdSync(long id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.delete("categories", "id = ?", new String[]{String.valueOf(id)});
    }

    public boolean isNameExistsSync(String name, long excludeId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        String sql = "SELECT COUNT(*) FROM categories WHERE name = ?";
        List<String> args = new ArrayList<>();
        args.add(name);
        if (excludeId > 0) {
            sql += " AND id != ?";
            args.add(String.valueOf(excludeId));
        }
        try (Cursor cursor = db.rawQuery(sql, args.toArray(new String[0]))) {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0) > 0;
            }
        }
        return false;
    }

    public int countExpensesUsingCategorySync(String name) {
        SQLiteDatabase db = helper.getReadableDatabase();
        String sql = "SELECT COUNT(*) FROM expenses WHERE category = ?";
        try (Cursor cursor = db.rawQuery(sql, new String[]{name})) {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        }
        return 0;
    }

    public int replaceCategoryInExpensesSync(String oldName, String newName) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("category", newName);
        return db.update("expenses", values, "category = ?", new String[]{oldName});
    }

    private Category mapCategory(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
        String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        String colorHex = cursor.getString(cursor.getColumnIndexOrThrow("colorHex"));
        return new Category(id, name, colorHex);
    }
}
