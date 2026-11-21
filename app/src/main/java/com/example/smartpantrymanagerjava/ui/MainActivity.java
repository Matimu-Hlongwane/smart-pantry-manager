package com.example.smartpantrymanagerjava.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantrymanagerjava.R;
import com.example.smartpantrymanagerjava.database.DatabaseHelper;
import com.example.smartpantrymanagerjava.database.PantryDao;
import com.example.smartpantrymanagerjava.database.RecipeDao;
import com.example.smartpantrymanagerjava.model.MatchResult;
import com.example.smartpantrymanagerjava.model.PantryItem;
import com.example.smartpantrymanagerjava.model.Recipe;
import com.example.smartpantrymanagerjava.util.DateUtils;
import com.example.smartpantrymanagerjava.util.RecipeMatcher;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView tvPantryCount;
    private TextView tvCanCookCount;
    private TextView tvExpiringCount;

    private PantryDao pantryDao;
    private RecipeDao recipeDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);
        pantryDao = new PantryDao(dbHelper);
        recipeDao = new RecipeDao(dbHelper);

        initViews();
        setupNavigation();
    }

    private void initViews() {
        tvPantryCount = findViewById(R.id.tv_stat_pantry_count);
        tvCanCookCount = findViewById(R.id.tv_stat_can_cook_count);
        tvExpiringCount = findViewById(R.id.tv_stat_expiring_count);
    }

    private void setupNavigation() {
        MaterialButton btnPantry = findViewById(R.id.btn_nav_pantry);
        MaterialButton btnSuggested = findViewById(R.id.btn_nav_suggested_recipes);
        MaterialButton btnAllRecipes = findViewById(R.id.btn_nav_all_recipes);
        MaterialButton btnSettings = findViewById(R.id.btn_nav_settings);

        btnPantry.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, PantryActivity.class)));

        btnSuggested.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RecipesActivity.class);
            intent.putExtra(RecipesActivity.EXTRA_SHOW_CAN_COOK_ONLY, true);
            startActivity(intent);
        });

        btnAllRecipes.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RecipesActivity.class);
            intent.putExtra(RecipesActivity.EXTRA_SHOW_CAN_COOK_ONLY, false);
            startActivity(intent);
        });

        btnSettings.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SettingsActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDashboardStats();
    }

    private void updateDashboardStats() {
        List<PantryItem> pantryItems = pantryDao.getAllPantryItems();
        List<Recipe> recipes = recipeDao.getAllRecipes();

        int totalPantry = pantryItems.size();

        List<MatchResult> canCookList = RecipeMatcher.filterCanCookRecipes(recipes, pantryItems);
        int canCookCount = canCookList.size();

        int expiringCount = 0;
        for (PantryItem item : pantryItems) {
            if (DateUtils.isExpiringSoon(item.getExpiryDate(), 3) || DateUtils.isExpired(item.getExpiryDate())) {
                expiringCount++;
            }
        }

        tvPantryCount.setText(String.valueOf(totalPantry));
        tvCanCookCount.setText(String.valueOf(canCookCount));
        tvExpiringCount.setText(String.valueOf(expiringCount));
    }
}
