package com.example.smartpantrymanagerjava.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanagerjava.R;
import com.example.smartpantrymanagerjava.adapter.PantryAdapter;
import com.example.smartpantrymanagerjava.database.DatabaseHelper;
import com.example.smartpantrymanagerjava.database.PantryDao;
import com.example.smartpantrymanagerjava.model.PantryItem;
import com.example.smartpantrymanagerjava.util.DateUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PantryActivity extends AppCompatActivity implements PantryAdapter.OnPantryItemClickListener {

    private PantryDao pantryDao;
    private PantryAdapter adapter;

    private RecyclerView rvPantry;
    private LinearLayout layoutEmpty;
    private EditText etSearch;

    private static final String[] STANDARD_UNITS = new String[]{
            "g", "kg", "ml", "l", "pcs", "cup", "tbsp", "tsp", "cloves", "slices"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.pantry_title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        pantryDao = new PantryDao(DatabaseHelper.getInstance(this));

        rvPantry = findViewById(R.id.rv_pantry);
        layoutEmpty = findViewById(R.id.layout_empty_pantry);
        etSearch = findViewById(R.id.et_search_pantry);
        FloatingActionButton fabAdd = findViewById(R.id.fab_add_pantry);

        rvPantry.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PantryAdapter(this, this);
        rvPantry.setAdapter(adapter);

        fabAdd.setOnClickListener(v -> showAddEditDialog(null));

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterPantryItems(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        loadPantryItems();
    }

    private void loadPantryItems() {
        List<PantryItem> items = pantryDao.getAllPantryItems();
        updateUI(items);
    }

    private void filterPantryItems(String query) {
        if (query == null || query.trim().isEmpty()) {
            loadPantryItems();
        } else {
            List<PantryItem> filtered = pantryDao.searchPantryItems(query);
            updateUI(filtered);
        }
    }

    private void updateUI(List<PantryItem> items) {
        if (items == null || items.isEmpty()) {
            rvPantry.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            rvPantry.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
            adapter.setItems(items);
        }
    }

    private void showAddEditDialog(PantryItem existingItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_pantry, null);
        builder.setView(dialogView);

        TextView tvTitle = dialogView.findViewById(R.id.tv_dialog_title);
        TextInputEditText etName = dialogView.findViewById(R.id.et_name);
        TextInputEditText etQuantity = dialogView.findViewById(R.id.et_quantity);
        AutoCompleteTextView actUnit = dialogView.findViewById(R.id.act_unit);
        TextInputEditText etExpiry = dialogView.findViewById(R.id.et_expiry);

        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, STANDARD_UNITS);
        actUnit.setAdapter(unitAdapter);

        if (existingItem != null) {
            tvTitle.setText(R.string.edit_pantry_item);
            etName.setText(existingItem.getName());
            etQuantity.setText(String.format(Locale.US, "%.1f", existingItem.getQuantity()));
            actUnit.setText(existingItem.getUnit(), false);
            etExpiry.setText(existingItem.getExpiryDate() != null ? existingItem.getExpiryDate() : "");
        } else {
            tvTitle.setText(R.string.add_pantry_item);
            actUnit.setText("g", false);
        }

        etExpiry.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            if (existingItem != null && existingItem.getExpiryDate() != null) {
                java.util.Date d = DateUtils.parseDate(existingItem.getExpiryDate());
                if (d != null) calendar.setTime(d);
            }
            DatePickerDialog datePicker = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        String selectedDate = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                        etExpiry.setText(selectedDate);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            datePicker.show();
        });

        builder.setPositiveButton(R.string.save, null);
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = etName.getText() != null ? etName.getText().toString().trim() : "";
            String qtyStr = etQuantity.getText() != null ? etQuantity.getText().toString().trim() : "";
            String unit = actUnit.getText() != null ? actUnit.getText().toString().trim() : "";
            String expiry = etExpiry.getText() != null ? etExpiry.getText().toString().trim() : "";

            if (name.isEmpty()) {
                etName.setError(getString(R.string.err_empty_name));
                return;
            }

            double quantity;
            try {
                quantity = Double.parseDouble(qtyStr);
                if (quantity <= 0) {
                    etQuantity.setError(getString(R.string.err_invalid_qty));
                    return;
                }
            } catch (NumberFormatException e) {
                etQuantity.setError(getString(R.string.err_invalid_qty));
                return;
            }

            if (unit.isEmpty()) {
                actUnit.setError(getString(R.string.err_empty_unit));
                return;
            }

            if (!expiry.isEmpty() && !DateUtils.isValidDate(expiry)) {
                etExpiry.setError(getString(R.string.err_invalid_date));
                return;
            }

            if (existingItem != null) {
                existingItem.setName(name);
                existingItem.setQuantity(quantity);
                existingItem.setUnit(unit);
                existingItem.setExpiryDate(expiry);
                pantryDao.updatePantryItem(existingItem);
            } else {
                PantryItem newItem = new PantryItem(name, quantity, unit, expiry);
                pantryDao.insertPantryItem(newItem);
            }

            dialog.dismiss();
            loadPantryItems();
        });
    }

    @Override
    public void onEditClick(PantryItem item) {
        showAddEditDialog(item);
    }

    @Override
    public void onDeleteClick(PantryItem item) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete)
                .setMessage(R.string.delete_confirm)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    pantryDao.deletePantryItem(item.getId());
                    loadPantryItems();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
