package com.zahid.smartexpensetracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

public class ParsedItemAdapter extends RecyclerView.Adapter<ParsedItemAdapter.ViewHolder> {

    private final List<ParsedItem> itemList;
    private final Context context;
    private final String[] categories = {"Food", "Clothing", "Transport", "Other"};

    public ParsedItemAdapter(Context context, List<ParsedItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    public List<ParsedItem> getUpdatedList() {
        return itemList;
    }

    @NonNull
    @Override
    public ParsedItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_parsed_expense, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParsedItemAdapter.ViewHolder holder, int position) {
        ParsedItem item = itemList.get(position);
        holder.editName.setText(item.getName());
        holder.editAmount.setText(String.valueOf(item.getAmount()));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spinner.setAdapter(adapter);

        int index = Arrays.asList(categories).indexOf(item.getCategory());
        holder.spinner.setSelection(index >= 0 ? index : 0);

        holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                item.setCategory(categories[pos]); // ✔ Only updates current item
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });


        holder.editName.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) item.setName(holder.editName.getText().toString());
        });
        holder.editAmount.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                try {
                    item.setAmount(Double.parseDouble(holder.editAmount.getText().toString()));
                } catch (Exception ignored) {}
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        EditText editName, editAmount;
        Spinner spinner;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            editName = itemView.findViewById(R.id.edit_item_name);
            editAmount = itemView.findViewById(R.id.edit_item_amount);
            spinner = itemView.findViewById(R.id.spinner_category);
        }
    }
}

