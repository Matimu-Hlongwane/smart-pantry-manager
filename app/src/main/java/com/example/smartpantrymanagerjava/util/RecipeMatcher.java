package com.example.smartpantrymanagerjava.util;

import com.example.smartpantrymanagerjava.model.MatchResult;
import com.example.smartpantrymanagerjava.model.PantryItem;
import com.example.smartpantrymanagerjava.model.Recipe;
import com.example.smartpantrymanagerjava.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Core engine for strict recipe matching against available pantry items.
 */
public class RecipeMatcher {

    /**
     * Checks if a recipe can be cooked given the list of pantry items.
     * Strict rule: Returns match details with canCook = true ONLY if EVERY required ingredient
     * is present in the pantry in sufficient quantity (with unit conversion handled).
     */
    public static MatchResult checkRecipeMatch(Recipe recipe, List<PantryItem> pantryItems) {
        if (recipe == null || recipe.getIngredients() == null || recipe.getIngredients().isEmpty()) {
            return new MatchResult(recipe, false, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        }

        List<PantryItem> validPantry = pantryItems != null ? pantryItems : new ArrayList<>();
        List<MatchResult.RecipeIngredientStatus> statuses = new ArrayList<>();
        List<String> missingIngredients = new ArrayList<>();
        List<String> insufficientDetails = new ArrayList<>();

        boolean allIngredientsSufficient = true;

        for (RecipeIngredient req : recipe.getIngredients()) {
            String reqNormName = IngredientNormalizer.normalize(req.getName());
            double reqQty = req.getQuantity();
            String reqUnit = req.getUnit();

            double totalAvailableConvertedQty = 0.0;
            boolean foundAnyPantryMatch = false;

            for (PantryItem item : validPantry) {
                if (item.getQuantity() <= 0) {
                    continue; // Zero quantity counts as unavailable
                }
                String pantryNormName = IngredientNormalizer.normalize(item.getName());

                if (pantryNormName.equals(reqNormName)) {
                    foundAnyPantryMatch = true;
                    double converted = UnitConverter.convert(item.getQuantity(), item.getUnit(), reqUnit);
                    if (!Double.isNaN(converted)) {
                        totalAvailableConvertedQty += converted;
                    } else if (item.getUnit() == null || item.getUnit().trim().isEmpty() ||
                            reqUnit == null || reqUnit.trim().isEmpty()) {
                        // If one or both units are unspecified, treat quantities as 1:1 match
                        totalAvailableConvertedQty += item.getQuantity();
                    }
                }
            }

            boolean isPresent = foundAnyPantryMatch && totalAvailableConvertedQty > 0;
            boolean isSufficient = isPresent && totalAvailableConvertedQty >= (reqQty - 0.0001); // Epsilon for float rounding

            String msg;
            if (isSufficient) {
                msg = String.format(Locale.US, "Available (Need: %.1f %s, Have: %.1f %s)",
                        reqQty, reqUnit, totalAvailableConvertedQty, reqUnit);
            } else if (isPresent) {
                msg = String.format(Locale.US, "Insufficient (Need: %.1f %s, Have: %.1f %s)",
                        reqQty, reqUnit, totalAvailableConvertedQty, reqUnit);
                insufficientDetails.add(String.format(Locale.US, "%s: Need %.1f %s, Have %.1f %s",
                        req.getName(), reqQty, reqUnit, totalAvailableConvertedQty, reqUnit));
                allIngredientsSufficient = false;
            } else {
                msg = String.format(Locale.US, "Missing (Need: %.1f %s)", reqQty, reqUnit);
                missingIngredients.add(req.getName());
                allIngredientsSufficient = false;
            }

            statuses.add(new MatchResult.RecipeIngredientStatus(
                    req,
                    totalAvailableConvertedQty,
                    isPresent,
                    isSufficient,
                    msg
            ));
        }

        return new MatchResult(
                recipe,
                allIngredientsSufficient,
                statuses,
                missingIngredients,
                insufficientDetails
        );
    }

    /**
     * Filters a list of recipes and returns only those that can be cooked right now.
     */
    public static List<MatchResult> filterCanCookRecipes(List<Recipe> recipes, List<PantryItem> pantryItems) {
        List<MatchResult> resultList = new ArrayList<>();
        if (recipes == null) return resultList;

        for (Recipe recipe : recipes) {
            MatchResult matchResult = checkRecipeMatch(recipe, pantryItems);
            if (matchResult.isCanCook()) {
                resultList.add(matchResult);
            }
        }
        return resultList;
    }

    /**
     * Analyzes all recipes and returns MatchResults for all, sorted with cookable recipes first.
     */
    public static List<MatchResult> evaluateAllRecipes(List<Recipe> recipes, List<PantryItem> pantryItems) {
        List<MatchResult> resultList = new ArrayList<>();
        if (recipes == null) return resultList;

        for (Recipe recipe : recipes) {
            resultList.add(checkRecipeMatch(recipe, pantryItems));
        }

        // Sort: canCook = true first, then alphabetically by title
        resultList.sort((r1, r2) -> {
            if (r1.isCanCook() != r2.isCanCook()) {
                return r1.isCanCook() ? -1 : 1;
            }
            return r1.getRecipe().getTitle().compareToIgnoreCase(r2.getRecipe().getTitle());
        });

        return resultList;
    }
}
