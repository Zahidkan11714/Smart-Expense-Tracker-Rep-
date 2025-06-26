package com.zahid.smartexpensetracker;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "expense_table")
public class Expense {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String category;
    private String date;
    private double amount;
    private String note;

    // Constructor
    public Expense(String category, String date, double amount, String note) {
        this.category = category;
        this.date = date;
        this.amount = amount;
        this.note = note;
    }

    // Setters and Getters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCategory() { return category; }
    public String getDate() { return date; }
    public double getAmount() { return amount; }
    public String getNote() { return note; }
}

