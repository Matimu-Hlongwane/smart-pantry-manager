package com.example.smartpantrymanagerjava.ui;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantrymanagerjava.R;
import com.example.smartpantrymanagerjava.database.DatabaseHelper;
import com.example.smartpantrymanagerjava.database.PantryDao;
import com.google.android.material.button.MaterialButton;

public class SettingsActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private PantryDao pantryDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.settings_title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dbHelper = DatabaseHelper.getInstance(this);
        pantryDao = new PantryDao(dbHelper);

        MaterialButton btnReseed = findViewById(R.id.btn_reseed_recipes);
        MaterialButton btnClearPantry = findViewById(R.id.btn_clear_pantry);

        btnReseed.setOnClickListener(v -> reseedRecipes());
        btnClearPantry.setOnClickListener(v -> clearPantryData());
    }

    private void reseedRecipes() {
        new AlertDialog.Builder(this)
                .setTitle("Reseed Recipes")
                .setMessage("Reset preloaded recipes to default 20 recipes?")
                .setPositiveButton("Reset", (dialog, which) -> {
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    dbHelper.resetRecipes(db);
                    Toast.makeText(this, R.string.reseed_success, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void clearPantryData() {
        new AlertDialog.Builder(this)
                .setTitle("Clear Pantry")
                .setMessage("Delete all pantry items? This action cannot be undone.")
                .setPositiveButton("Clear", (dialog, which) -> {
                    pantryDao.deleteAllPantryItems();
                    Toast.makeText(this, R.string.clear_pantry_success, Toast.LENGTH_SHORT).show();
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
