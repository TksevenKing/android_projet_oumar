package com.waritrack.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Expense expense);

    @Update
    void update(Expense expense);

    @Delete
    void delete(Expense expense);

    @Query("SELECT * FROM expenses WHERE id = :id")
    LiveData<Expense> getById(long id);

    @Query("SELECT * FROM expenses " +
            "WHERE (:category IS NULL OR category = :category) " +
            "AND (note LIKE '%' || :search || '%' OR category LIKE '%' || :search || '%') " +
            "ORDER BY date DESC")
    LiveData<List<Expense>> getFiltered(String search, String category);

    @Query("SELECT DISTINCT category FROM expenses ORDER BY category ASC")
    LiveData<List<String>> getCategories();

    @Query("SELECT IFNULL(SUM(amount), 0) FROM expenses")
    LiveData<Double> getTotalAll();

    @Query("SELECT IFNULL(SUM(amount), 0) FROM expenses WHERE date BETWEEN :start AND :end")
    LiveData<Double> getTotalBetween(long start, long end);

    @Query("SELECT category, SUM(amount) AS total FROM expenses GROUP BY category ORDER BY total DESC LIMIT 3")
    LiveData<List<CategoryTotal>> getTopCategories();
}
