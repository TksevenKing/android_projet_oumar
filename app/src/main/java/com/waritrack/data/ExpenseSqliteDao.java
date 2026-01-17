package com.waritrack.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class ExpenseSqliteDao {
    private final WariTrackDbHelper helper;

    public ExpenseSqliteDao(WariTrackDbHelper helper) {
        this.helper = helper;
    }

    public long insert(Expense expense) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("amount", expense.getAmount());
        values.put("category", expense.getCategory());
        values.put("date", expense.getDate());
        values.put("note", expense.getNote());
        return db.insert("expenses", null, values);
    }

    public int update(Expense expense) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("amount", expense.getAmount());
        values.put("category", expense.getCategory());
        values.put("date", expense.getDate());
        values.put("note", expense.getNote());
        return db.update("expenses", values, "id = ?", new String[]{String.valueOf(expense.getId())});
    }

    public int delete(long id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.delete("expenses", "id = ?", new String[]{String.valueOf(id)});
    }

    public Expense getByIdSync(long id) {
        SQLiteDatabase db = helper.getReadableDatabase();
        String sql = "SELECT id, amount, category, date, note FROM expenses WHERE id = ?";
        try (Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(id)})) {
            if (cursor.moveToFirst()) {
                return mapExpense(cursor);
            }
        }
        return null;
    }

    public List<Expense> getFilteredSync(String search, String category) {
        SQLiteDatabase db = helper.getReadableDatabase();
        String safeSearch = search == null ? "" : search;
        String likeArg = "%" + safeSearch + "%";

        StringBuilder selection = new StringBuilder("(note LIKE ? OR category LIKE ?)");
        List<String> args = new ArrayList<>();
        args.add(likeArg);
        args.add(likeArg);

        if (category != null && !"ALL".equalsIgnoreCase(category)) {
            selection.append(" AND category = ?");
            args.add(category);
        }

        String sql = "SELECT id, amount, category, date, note FROM expenses WHERE " + selection
                + " ORDER BY date DESC";
        List<Expense> expenses = new ArrayList<>();
        try (Cursor cursor = db.rawQuery(sql, args.toArray(new String[0]))) {
            while (cursor.moveToNext()) {
                expenses.add(mapExpense(cursor));
            }
        }
        return expenses;
    }

    public List<String> getCategoriesSync() {
        SQLiteDatabase db = helper.getReadableDatabase();
        List<String> categories = new ArrayList<>();
        try (Cursor cursor = db.rawQuery("SELECT DISTINCT category FROM expenses ORDER BY category ASC", null)) {
            while (cursor.moveToNext()) {
                categories.add(cursor.getString(0));
            }
        }
        return categories;
    }

    public double getTotalAllSync() {
        SQLiteDatabase db = helper.getReadableDatabase();
        try (Cursor cursor = db.rawQuery("SELECT IFNULL(SUM(amount), 0) FROM expenses", null)) {
            if (cursor.moveToFirst()) {
                return cursor.getDouble(0);
            }
        }
        return 0;
    }

    public double getTotalBetweenSync(long start, long end) {
        SQLiteDatabase db = helper.getReadableDatabase();
        String sql = "SELECT IFNULL(SUM(amount), 0) FROM expenses WHERE date BETWEEN ? AND ?";
        String[] args = {String.valueOf(start), String.valueOf(end)};
        try (Cursor cursor = db.rawQuery(sql, args)) {
            if (cursor.moveToFirst()) {
                return cursor.getDouble(0);
            }
        }
        return 0;
    }

    public List<CategoryTotal> getTopCategoriesSync() {
        SQLiteDatabase db = helper.getReadableDatabase();
        String sql = "SELECT category, SUM(amount) AS total FROM expenses " +
                "GROUP BY category ORDER BY total DESC LIMIT 3";
        List<CategoryTotal> totals = new ArrayList<>();
        try (Cursor cursor = db.rawQuery(sql, null)) {
            while (cursor.moveToNext()) {
                String category = cursor.getString(0);
                double total = cursor.getDouble(1);
                totals.add(new CategoryTotal(category, total));
            }
        }
        return totals;
    }

    public List<Expense> getExpensesBetweenSync(long start, long end) {
        SQLiteDatabase db = helper.getReadableDatabase();
        String sql = "SELECT id, amount, category, date, note FROM expenses WHERE date BETWEEN ? AND ? ORDER BY date DESC";
        String[] args = {String.valueOf(start), String.valueOf(end)};
        List<Expense> expenses = new ArrayList<>();
        try (Cursor cursor = db.rawQuery(sql, args)) {
            while (cursor.moveToNext()) {
                expenses.add(mapExpense(cursor));
            }
        }
        return expenses;
    }

    private Expense mapExpense(Cursor cursor) {
        Expense expense = new Expense(
                cursor.getDouble(cursor.getColumnIndexOrThrow("amount")),
                cursor.getString(cursor.getColumnIndexOrThrow("category")),
                cursor.getLong(cursor.getColumnIndexOrThrow("date")),
                cursor.getString(cursor.getColumnIndexOrThrow("note"))
        );
        expense.setId(cursor.getLong(cursor.getColumnIndexOrThrow("id")));
        return expense;
    }
}
