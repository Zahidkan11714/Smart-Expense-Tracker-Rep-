package com.zahid.smartexpensetracker;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ConfirmParsedItemsActivity extends AppCompatActivity {

    LinearLayout container;
    Button saveAllBtn;
    List<ParsedItem> parsedItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_parsed_items);

        container = findViewById(R.id.parsed_items_container);
        saveAllBtn = findViewById(R.id.button_save_all);

        parsedItems = (List<ParsedItem>) getIntent().getSerializableExtra("parsedItems");

        for (ParsedItem item : parsedItems) {
            TextView view = new TextView(this);
            view.setText("Item: " + item.name + "\nAmount: Rs. " + item.amount + "\nCategory: " + item.category);
            view.setPadding(16, 16, 16, 16);
            container.addView(view);
        }

        saveAllBtn.setOnClickListener(v -> {
            new Thread(() -> {
                ExpenseDao dao = ExpenseDatabase.getInstance(this).expenseDao();
                for (ParsedItem item : parsedItems) {
                    Expense expense = new Expense(item.category, getCurrentDate(), item.amount, item.name);
                    dao.insert(expense);
                }
                runOnUiThread(() -> {
                    Toast.makeText(this, "All items saved to database", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }).start();
        });
    }

    private String getCurrentDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }
}
