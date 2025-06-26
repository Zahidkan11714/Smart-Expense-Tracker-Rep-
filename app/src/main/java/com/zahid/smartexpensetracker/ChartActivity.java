package com.zahid.smartexpensetracker;

import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChartActivity extends AppCompatActivity {

    PieChart pieChart;
    ExpenseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        pieChart = findViewById(R.id.pieChart);
        database = ExpenseDatabase.getInstance(this);

        loadChartData();
    }

    private void loadChartData() {
        new Thread(() -> {
            List<Expense> allExpenses = database.expenseDao().getAllExpenses(); // Make sure you have this method

            Map<String, Double> categoryTotals = new HashMap<>();
            for (Expense expense : allExpenses) {
                String category = expense.getCategory();
                double amount = expense.getAmount();

                if (!categoryTotals.containsKey(category)) {
                    categoryTotals.put(category, amount);
                } else {
                    categoryTotals.put(category, categoryTotals.get(category) + amount);
                }
            }

            List<PieEntry> entries = new ArrayList<>();
            for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
                entries.add(new PieEntry(entry.getValue().floatValue(), entry.getKey()));
            }

            PieDataSet dataSet = new PieDataSet(entries, "Expense by Category");
            dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
            dataSet.setValueTextSize(14f);

            PieData pieData = new PieData(dataSet);

            runOnUiThread(() -> {
                pieChart.setData(pieData);
                pieChart.invalidate(); // refresh
                pieChart.setCenterText("Spending by Category");
                pieChart.setEntryLabelColor(Color.BLACK);
                pieChart.animateY(1000);
            });
        }).start();
    }
}
