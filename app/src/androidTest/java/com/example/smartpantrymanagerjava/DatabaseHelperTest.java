package com.example.smartpantrymanagerjava;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.smartpantrymanagerjava.database.DatabaseHelper;
import com.example.smartpantrymanagerjava.database.PantryDao;
import com.example.smartpantrymanagerjava.database.RecipeDao;
import com.example.smartpantrymanagerjava.model.MatchResult;
import com.example.smartpantrymanagerjava.model.PantryItem;
import com.example.smartpantrymanagerjava.model.Recipe;
import com.example.smartpantrymanagerjava.util.RecipeMatcher;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class DatabaseHelperTest {

    private DatabaseHelper dbHelper;
    private PantryDao pantryDao;
    private RecipeDao recipeDao;

    @Before
    public void setUp() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        context.deleteDatabase("smart_pantry_test.db");
        dbHelper = new DatabaseHelper(context) {
            @Override
            public String getDatabaseName() {
                return "smart_pantry_test.db";
            }
        };
        pantryDao = new PantryDao(dbHelper);
        recipeDao = new RecipeDao(dbHelper);
    }

    @After
    public void tearDown() {
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    @Test
    public void testPreloadedRecipesSeeded() {
        List<Recipe> recipes = recipeDao.getAllRecipes();
        assertEquals("Database should seed exactly 20 preloaded recipes", 20, recipes.size());

        Recipe pancakes = null;
        for (Recipe r : recipes) {
            if ("Classic Pancakes".equals(r.getTitle())) {
                pancakes = r;
                break;
            }
        }
        assertNotNull("Classic Pancakes recipe should exist", pancakes);
        assertFalse("Pancakes should have required ingredients", pancakes.getIngredients().isEmpty());
    }

    @Test
    public void testPantryCRUD() {
        PantryItem newItem = new PantryItem("Avocado", 3, "pcs", "2026-06-01");
        long id = pantryDao.insertPantryItem(newItem);
        assertTrue("Inserted ID should be positive", id > 0);

        PantryItem fetched = pantryDao.getPantryItemById(id);
        assertNotNull("Item should be retrievable", fetched);
        assertEquals("Avocado", fetched.getName());
        assertEquals(3.0, fetched.getQuantity(), 0.001);

        fetched.setQuantity(5.0);
        int updatedRows = pantryDao.updatePantryItem(fetched);
        assertEquals(1, updatedRows);

        PantryItem updated = pantryDao.getPantryItemById(id);
        assertEquals(5.0, updated.getQuantity(), 0.001);

        int deletedRows = pantryDao.deletePantryItem(id);
        assertEquals(1, deletedRows);
        assertNull(pantryDao.getPantryItemById(id));
    }

    @Test
    public void testDatabaseMatchingWithSeededRecipes() {
        List<Recipe> recipes = recipeDao.getAllRecipes();
        List<PantryItem> pantryItems = pantryDao.getAllPantryItems();

        List<MatchResult> matchResults = RecipeMatcher.evaluateAllRecipes(recipes, pantryItems);
        assertEquals(20, matchResults.size());

        long cookableCount = matchResults.stream().filter(MatchResult::isCanCook).count();
        assertTrue("Sample pantry items should allow cooking at least one recipe", cookableCount >= 1);
    }
}
