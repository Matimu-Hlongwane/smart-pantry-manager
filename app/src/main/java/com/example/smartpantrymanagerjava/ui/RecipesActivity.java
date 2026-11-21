package com.example.smartpantrymanagerjava.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanagerjava.R;
import com.example.smartpantrymanagerjava.adapter.RecipeAdapter;
import com.example.smartpantrymanagerjava.database.DatabaseHelper;
import com.example.smartpantrymanagerjava.database.PantryDao;
import com.example.smartpantrymanagerjava.database.RecipeDao;
import com.example.smartpantrymanagerjava.model.MatchResult;
import com.example.smartpantrymanagerjava.model.PantryItem;
import com.example.smartpantrymanagerjava.model.Recipe;
import com.example.smartpantrymanagerjava.util.RecipeMatcher;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class RecipesActivity extends AppCompatActivity implements RecipeAdapter.OnRecipeClickListener {

    public static final String EXTRA_SHOW_CAN_COOK_ONLY = "extra_show_can_cook_only";

    private RecipeDao recipeDao;
    private PantryDao pantryDao;

    private RecipeAdapter adapter;
    private RecyclerView rvRecipes;
    private LinearLayout layoutEmpty;
    private TabLayout tabLayout;

    private List<MatchResult> currentMatchResults;
    private boolean showCanCookOnly = false;
    private String searchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.recipes_title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);
        recipeDao = new RecipeDao(dbHelper);
        pantryDao = new PantryDao(dbHelper);

        if (getIntent() != null) {
            showCanCookOnly = getIntent().getBooleanExtra(EXTRA_SHOW_CAN_COOK_ONLY, false);
        }

        rvRecipes = findViewById(R.id.rv_recipes);
        layoutEmpty = findViewById(R.id.layout_empty_recipes);
        tabLayout = findViewById(R.id.tab_layout_recipes);
        EditText etSearch = findViewById(R.id.et_search_recipes);

        rvRecipes.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecipeAdapter(this, this);
        rvRecipes.setAdapter(adapter);

        if (showCanCookOnly) {
            TabLayout.Tab tab = tabLayout.getTabAt(1);
            if (tab != null) tab.select();
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                showCanCookOnly = (tab.getPosition() == 1);
                applyFilters();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchQuery = s.toString().trim();
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        loadRecipes();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRecipes();
    }

    private void loadRecipes() {
        List<Recipe> recipes = recipeDao.getAllRecipes();
        List<PantryItem> pantryItems = pantryDao.getAllPantryItems();

        currentMatchResults = RecipeMatcher.evaluateAllRecipes(recipes, pantryItems);
        applyFilters();
    }

    private void applyFilters() {
        if (currentMatchResults == null) return;

        List<MatchResult> filtered = new ArrayList<>();

        for (MatchResult mr : currentMatchResults) {
            if (showCanCookOnly && !mr.isCanCook()) {
                continue;
            }

            if (!searchQuery.isEmpty()) {
                String title = mr.getRecipe().getTitle().toLowerCase();
                String desc = mr.getRecipe().getDescription().toLowerCase();
                String cat = mr.getRecipe().getCategory().toLowerCase();

                if (!title.contains(searchQuery.toLowerCase()) &&
                        !desc.contains(searchQuery.toLowerCase()) &&
                        !cat.contains(searchQuery.toLowerCase())) {
                    continue;
                }
            }

            filtered.add(mr);
        }

        if (filtered.isEmpty()) {
            rvRecipes.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            rvRecipes.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
            adapter.setMatchResults(filtered);
        }
    }

    @Override
    public void onRecipeClick(MatchResult matchResult) {
        Intent intent = new Intent(RecipesActivity.this, RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, matchResult.getRecipe().getId());
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
