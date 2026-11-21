package com.example.smartpantrymanagerjava.ui;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanagerjava.R;
import com.example.smartpantrymanagerjava.adapter.RecipeIngredientAdapter;
import com.example.smartpantrymanagerjava.database.DatabaseHelper;
import com.example.smartpantrymanagerjava.database.PantryDao;
import com.example.smartpantrymanagerjava.database.RecipeDao;
import com.example.smartpantrymanagerjava.model.MatchResult;
import com.example.smartpantrymanagerjava.model.PantryItem;
import com.example.smartpantrymanagerjava.model.Recipe;
import com.example.smartpantrymanagerjava.model.RecipeIngredient;
import com.example.smartpantrymanagerjava.util.IngredientNormalizer;
import com.example.smartpantrymanagerjava.util.RecipeMatcher;
import com.example.smartpantrymanagerjava.util.UnitConverter;
import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.Locale;

public class RecipeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID = "extra_recipe_id";

    private RecipeDao recipeDao;
    private PantryDao pantryDao;

    private Recipe currentRecipe;
    private MatchResult currentMatchResult;

    private TextView tvTitle, tvBadge, tvDescription, tvPrepTime, tvCookTime, tvCategory, tvInstructions;
    private RecipeIngredientAdapter ingredientAdapter;
    private MaterialButton btnCook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.recipe_detail_title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);
        recipeDao = new RecipeDao(dbHelper);
        pantryDao = new PantryDao(dbHelper);

        long recipeId = getIntent() != null ? getIntent().getLongExtra(EXTRA_RECIPE_ID, -1) : -1;
        if (recipeId == -1) {
            finish();
            return;
        }

        initViews();

        RecyclerView rvIngredients = findViewById(R.id.rv_recipe_ingredients);
        rvIngredients.setLayoutManager(new LinearLayoutManager(this));
        ingredientAdapter = new RecipeIngredientAdapter(this);
        rvIngredients.setAdapter(ingredientAdapter);

        loadRecipeData(recipeId);

        btnCook.setOnClickListener(v -> cookRecipe());
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tv_detail_title);
        tvBadge = findViewById(R.id.tv_detail_match_badge);
        tvDescription = findViewById(R.id.tv_detail_description);
        tvPrepTime = findViewById(R.id.tv_detail_prep_time);
        tvCookTime = findViewById(R.id.tv_detail_cook_time);
        tvCategory = findViewById(R.id.tv_detail_category);
        tvInstructions = findViewById(R.id.tv_detail_instructions);
        btnCook = findViewById(R.id.btn_cook_recipe);
    }

    private void loadRecipeData(long recipeId) {
        currentRecipe = recipeDao.getRecipeById(recipeId);
        if (currentRecipe == null) {
            finish();
            return;
        }

        List<PantryItem> pantryItems = pantryDao.getAllPantryItems();
        currentMatchResult = RecipeMatcher.checkRecipeMatch(currentRecipe, pantryItems);

        tvTitle.setText(currentRecipe.getTitle());
        tvDescription.setText(currentRecipe.getDescription());
        tvCategory.setText(currentRecipe.getCategory());
        tvPrepTime.setText(String.format(Locale.US, "Prep: %d min", currentRecipe.getPrepTimeMinutes()));
        tvCookTime.setText(String.format(Locale.US, "Cook: %d min", currentRecipe.getCookTimeMinutes()));
        tvInstructions.setText(currentRecipe.getInstructions());

        if (currentMatchResult.isCanCook()) {
            tvBadge.setText(R.string.badge_ready);
            tvBadge.setBackgroundResource(R.drawable.bg_badge_success);
            tvBadge.setTextColor(ContextCompat.getColor(this, R.color.can_cook_green));
            btnCook.setEnabled(true);
            btnCook.setAlpha(1.0f);
        } else {
            tvBadge.setText(R.string.badge_missing);
            tvBadge.setBackgroundResource(R.drawable.bg_badge_warning);
            tvBadge.setTextColor(ContextCompat.getColor(this, R.color.missing_red));
            btnCook.setEnabled(false);
            btnCook.setAlpha(0.5f);
        }

        ingredientAdapter.setStatuses(currentMatchResult.getIngredientStatuses());
    }

    private void cookRecipe() {
        if (currentMatchResult == null || !currentMatchResult.isCanCook()) {
            Toast.makeText(this, R.string.cannot_cook_toast, Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Cook Recipe?")
                .setMessage("Deduct required ingredient quantities from your pantry?")
                .setPositiveButton("Cook", (dialog, which) -> deductIngredientsFromPantry())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deductIngredientsFromPantry() {
        List<PantryItem> pantryItems = pantryDao.getAllPantryItems();

        for (RecipeIngredient req : currentRecipe.getIngredients()) {
            String reqNorm = IngredientNormalizer.normalize(req.getName());
            double remainingRequired = req.getQuantity();

            for (PantryItem item : pantryItems) {
                if (remainingRequired <= 0.0001) break;

                String itemNorm = IngredientNormalizer.normalize(item.getName());
                if (itemNorm.equals(reqNorm) && item.getQuantity() > 0) {
                    double convertedQty = UnitConverter.convert(item.getQuantity(), item.getUnit(), req.getUnit());
                    if (Double.isNaN(convertedQty)) {
                        convertedQty = item.getQuantity();
                    }

                    if (convertedQty >= remainingRequired) {
                        double remainingInPantryInReqUnit = convertedQty - remainingRequired;
                        double updatedPantryQty = UnitConverter.convert(remainingInPantryInReqUnit, req.getUnit(), item.getUnit());
                        if (Double.isNaN(updatedPantryQty)) {
                            updatedPantryQty = remainingInPantryInReqUnit;
                        }
                        item.setQuantity(Math.max(0, updatedPantryQty));
                        pantryDao.updatePantryItem(item);
                        remainingRequired = 0;
                    } else {
                        remainingRequired -= convertedQty;
                        item.setQuantity(0);
                        pantryDao.updatePantryItem(item);
                    }
                }
            }
        }

        Toast.makeText(this, R.string.cook_success, Toast.LENGTH_LONG).show();
        loadRecipeData(currentRecipe.getId());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
