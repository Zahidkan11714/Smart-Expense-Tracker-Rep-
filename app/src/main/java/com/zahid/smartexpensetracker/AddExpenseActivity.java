package com.zahid.smartexpensetracker;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddExpenseActivity extends AppCompatActivity {

    private EditText editCategory, editDate, editAmount, editNote;
    private Button buttonSave;

    private ExpenseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        editCategory = findViewById(R.id.edit_category);
        editDate = findViewById(R.id.edit_date);
        editAmount = findViewById(R.id.edit_amount);
        editNote = findViewById(R.id.edit_note);
        buttonSave = findViewById(R.id.button_save);

        database = ExpenseDatabase.getInstance(this);

        buttonSave.setOnClickListener(v -> {
            String category = editCategory.getText().toString();
            String date = editDate.getText().toString();
            String note = editNote.getText().toString();
            String amountText = editAmount.getText().toString();

            if (category.isEmpty() || date.isEmpty() || amountText.isEmpty()) {
                Toast.makeText(this, "Fill all fields!", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount = Double.parseDouble(amountText);
            Expense expense = new Expense(category, date, amount, note);

            // Save in background thread
            new Thread(() -> {
                database.expenseDao().insert(expense);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
                    finish(); // go back
                });
            }).start();
        });
    }
}
