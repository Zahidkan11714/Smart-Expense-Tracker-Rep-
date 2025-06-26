package com.zahid.smartexpensetracker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScanReceiptActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private ImageView imageView;
    private Bitmap receiptBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_receipt);

        imageView = findViewById(R.id.imageView);
        Button buttonCapture = findViewById(R.id.button_capture);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, 101);
        }

        buttonCapture.setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null) {
                Toast.makeText(this, "Let's capture the receipt!", Toast.LENGTH_SHORT).show();
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                //receiptBitmap = (Bitmap) extras.get("data");
                receiptBitmap = (Bitmap) data.getExtras().get("data");

                imageView.setImageBitmap(receiptBitmap);
                recognizeText(receiptBitmap);
            } else {
                Toast.makeText(this, "No image data found", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Camera canceled or failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void recognizeText(Bitmap bitmap) {
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                .process(image)
                .addOnSuccessListener(result -> {
                    StringBuilder fullTextBuilder = new StringBuilder();
                    for (Text.TextBlock block : result.getTextBlocks()) {
                        fullTextBuilder.append(block.getText()).append("\n");
                    }

                    String fullText = fullTextBuilder.toString();
                    Log.d("OCR_OUTPUT", fullText);

                    String[] lines = fullText.split("\n");
                    List<String> cleanedLines = new ArrayList<>();

                    // Remove header labels like "Category", "Rs", "Note", etc.
                    for (String line : lines) {
                        line = line.trim();
                        if (line.equalsIgnoreCase("Category") ||
                                line.equalsIgnoreCase("Rs") ||
                                line.equalsIgnoreCase("Note") ||
                                line.equalsIgnoreCase("ID") ||
                                line.isEmpty()) {
                            continue;
                        }
                        cleanedLines.add(line);
                    }

                    List<ParsedItem> parsedItems = new ArrayList<>();
                    List<String> categories = new ArrayList<>();
                    List<String> noteBuffer = new ArrayList<>();
                    List<Double> amountBuffer = new ArrayList<>();

                    // Classify lines
                    for (String line : cleanedLines) {
                        if (line.matches("(?i)(Food|Clothing|Grocery|Drinks|Other)")) {
                            categories.add(line);
                        } else if (line.matches("\\d{2,6}")) {
                            amountBuffer.add(Double.parseDouble(line));
                        } else if (line.matches("\\d{2,6}\\s+[A-Za-z\\-\\s]+")) {
                            // e.g., "50 Onion" or "120 Juice"
                            String[] parts = line.split("\\s+", 2);
                            double amount = Double.parseDouble(parts[0]);
                            String note = parts[1];
                            amountBuffer.add(amount);
                            noteBuffer.add(note);
                        } else {
                            noteBuffer.add(line);
                        }
                    }

                    int totalItems = Math.max(categories.size(),
                            Math.max(noteBuffer.size(), amountBuffer.size()));

                    for (int i = 0; i < totalItems; i++) {
                        String category = i < categories.size() ? categories.get(i) : "Other";
                        String note = i < noteBuffer.size() ? noteBuffer.get(i) : "Unknown";
                        double amount = i < amountBuffer.size() ? amountBuffer.get(i) : 0.0;

                        parsedItems.add(new ParsedItem(note, amount, category));
                    }

                    if (parsedItems.isEmpty()) {
                        Toast.makeText(this, "❌ No items detected from receipt. Try again.", Toast.LENGTH_LONG).show();
                    } else {
                        Intent intent = new Intent(this, ConfirmParsedItemsActivity.class);
                        intent.putExtra("parsedItems", (Serializable) parsedItems);
                        startActivity(intent);
                    }

                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "OCR failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
