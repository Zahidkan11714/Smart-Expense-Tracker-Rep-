package com.zahid.smartexpensetracker;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ExpenseDao {

    @Insert
    void insert(Expense expense);

    @Delete
    void delete(Expense expense);

    @Query("DELETE FROM expense_table")
    void deleteAll();

    @Query("SELECT * FROM expense_table ORDER BY id DESC")
    List<Expense> getAllExpenses();

}

