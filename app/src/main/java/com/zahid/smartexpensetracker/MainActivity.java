package com.zahid.smartexpensetracker;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;


import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import com.itextpdf.text.Document;


public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ExpenseAdapter adapter;
    private ExpenseDatabase database;
    private Button buttonAdd, buttonScan, pieChartBtn, exportPdfBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_expenses);
        buttonAdd = findViewById(R.id.button_add_expense);
        buttonScan = findViewById(R.id.button_add_expense_auto);
        pieChartBtn = findViewById(R.id.pieChartBtn);
        exportPdfBtn = findViewById(R.id.exportPdfBtn);

        adapter = new ExpenseAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        database = ExpenseDatabase.getInstance(this);

//        new Thread(() -> {
//            database.expenseDao().deleteAll();
//            runOnUiThread(() ->
//                    Toast.makeText(this, "All expenses deleted", Toast.LENGTH_SHORT).show()
//            );
//        }).start();

        buttonAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
            startActivity(intent);
        });

        buttonScan.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ScanReceiptActivity.class);
            startActivity(intent);
        });

        pieChartBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ChartActivity.class);
            startActivity(intent);
        });

        exportPdfBtn.setOnClickListener(v -> {
            new Thread(() -> {
                List<Expense> expenses = ExpenseDatabase.getInstance(this).expenseDao().getAllExpenses();
                runOnUiThread(() -> exportExpensesToPDF(expenses));
            }).start();
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadExpenses();
    }

    private void loadExpenses() {
        new Thread(() -> {
            List<Expense> list = database.expenseDao().getAllExpenses();
            runOnUiThread(() -> adapter.setExpenseList(list));
        }).start();
    }

    private void exportExpensesToPDF(List<Expense> expenseList) {
        Document document = new Document();

        try {
            File pdfDir = new File(getExternalFilesDir(null), "exports");
            if (!pdfDir.exists()) {
                pdfDir.mkdirs();
            }

            File file = new File(pdfDir, "ExpensesReport.pdf");

            FileOutputStream outputStream = new FileOutputStream(file);
            PdfWriter.getInstance(document, outputStream);
            document.open();

            // Title
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("Expense Report\n\n", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            // Table
            PdfPTable table = new PdfPTable(4); // 4 columns: Category, Date, Amount, Note
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);

            table.addCell("Category");
            table.addCell("Date");
            table.addCell("Amount");
            table.addCell("Note");

            for (Expense e : expenseList) {
                table.addCell(e.getCategory());
                table.addCell(e.getDate());
                table.addCell(String.valueOf(e.getAmount()));
                table.addCell(e.getNote());
            }

            document.add(table);
            document.close();

            Toast.makeText(this, "PDF exported to: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error exporting PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


}
