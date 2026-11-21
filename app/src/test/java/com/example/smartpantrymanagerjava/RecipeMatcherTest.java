package com.example.smartpantrymanagerjava;

import com.example.smartpantrymanagerjava.model.MatchResult;
import com.example.smartpantrymanagerjava.model.PantryItem;
import com.example.smartpantrymanagerjava.model.Recipe;
import com.example.smartpantrymanagerjava.model.RecipeIngredient;
import com.example.smartpantrymanagerjava.util.RecipeMatcher;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class RecipeMatcherTest {

    private Recipe createPancakeRecipe() {
        List<RecipeIngredient> ingredients = new ArrayList<>();
        ingredients.add(new RecipeIngredient("Flour", 200, "g"));
        ingredients.add(new RecipeIngredient("Milk", 300, "ml"));
        ingredients.add(new RecipeIngredient("Egg", 2, "pcs"));
        ingredients.add(new RecipeIngredient("Sugar", 30, "g"));

        return new Recipe("Classic Pancakes", "Delicious breakfast pancakes", "Breakfast",
                10, 15, "Mix ingredients and cook on skillet", ingredients);
    }

    @Test
    public void testStrictMatching_AllSufficientWithUnitConversion_ReturnsCanCook() {
        Recipe recipe = createPancakeRecipe();

        List<PantryItem> pantry = new ArrayList<>();
        pantry.add(new PantryItem("Flour", 1.0, "kg", "2025-12-31")); // 1000g >= 200g
        pantry.add(new PantryItem("Milk", 0.5, "l", "2025-12-31"));   // 500ml >= 300ml
        pantry.add(new PantryItem("Eggs", 6, "pcs", "2025-12-31"));   // 6pcs >= 2pcs (plural matches)
        pantry.add(new PantryItem("Sugar", 100, "g", "2025-12-31"));  // 100g >= 30g

        MatchResult result = RecipeMatcher.checkRecipeMatch(recipe, pantry);

        assertTrue(result.isCanCook());
        assertTrue(result.getMissingIngredientNames().isEmpty());
        assertTrue(result.getInsufficientIngredientDetails().isEmpty());
    }

    @Test
    public void testStrictMatching_InsufficientQuantity_ReturnsCannotCook() {
        Recipe recipe = createPancakeRecipe();

        List<PantryItem> pantry = new ArrayList<>();
        pantry.add(new PantryItem("Flour", 100, "g", "2025-12-31")); // Need 200g, have 100g
        pantry.add(new PantryItem("Milk", 500, "ml", "2025-12-31"));
        pantry.add(new PantryItem("Eggs", 6, "pcs", "2025-12-31"));
        pantry.add(new PantryItem("Sugar", 100, "g", "2025-12-31"));

        MatchResult result = RecipeMatcher.checkRecipeMatch(recipe, pantry);

        assertFalse("Should fail strict match due to insufficient flour", result.isCanCook());
        assertEquals(1, result.getInsufficientIngredientDetails().size());
        assertTrue(result.getInsufficientIngredientDetails().get(0).contains("Flour"));
    }

    @Test
    public void testStrictMatching_MissingIngredient_ReturnsCannotCook() {
        Recipe recipe = createPancakeRecipe();

        List<PantryItem> pantry = new ArrayList<>();
        pantry.add(new PantryItem("Flour", 500, "g", "2025-12-31"));
        pantry.add(new PantryItem("Milk", 500, "ml", "2025-12-31"));
        // Sugar and Eggs missing!

        MatchResult result = RecipeMatcher.checkRecipeMatch(recipe, pantry);

        assertFalse("Should fail strict match due to missing ingredients", result.isCanCook());
        assertEquals(2, result.getMissingIngredientNames().size());
        assertTrue(result.getMissingIngredientNames().contains("Egg"));
        assertTrue(result.getMissingIngredientNames().contains("Sugar"));
    }

    @Test
    public void testStrictMatching_ZeroQuantityPantryItem_Ignored() {
        Recipe recipe = createPancakeRecipe();

        List<PantryItem> pantry = new ArrayList<>();
        pantry.add(new PantryItem("Flour", 500, "g", "2025-12-31"));
        pantry.add(new PantryItem("Milk", 500, "ml", "2025-12-31"));
        pantry.add(new PantryItem("Eggs", 0, "pcs", "2025-12-31")); // Zero quantity
        pantry.add(new PantryItem("Sugar", 100, "g", "2025-12-31"));

        MatchResult result = RecipeMatcher.checkRecipeMatch(recipe, pantry);

        assertFalse("Zero quantity should be treated as missing", result.isCanCook());
        assertTrue(result.getMissingIngredientNames().contains("Egg"));
    }
}
