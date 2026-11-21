package com.example.smartpantrymanagerjava.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Result structure representing strict matching analysis for a recipe against pantry items.
 */
public class MatchResult {
    private final Recipe recipe;
    private final boolean canCook; // true ONLY if EVERY ingredient is present in sufficient quantity
    private final List<RecipeIngredientStatus> ingredientStatuses;
    private final List<String> missingIngredientNames;
    private final List<String> insufficientIngredientDetails;

    public MatchResult(Recipe recipe, boolean canCook,
                       List<RecipeIngredientStatus> ingredientStatuses,
                       List<String> missingIngredientNames,
                       List<String> insufficientIngredientDetails) {
        this.recipe = recipe;
        this.canCook = canCook;
        this.ingredientStatuses = ingredientStatuses != null ? ingredientStatuses : new ArrayList<>();
        this.missingIngredientNames = missingIngredientNames != null ? missingIngredientNames : new ArrayList<>();
        this.insufficientIngredientDetails = insufficientIngredientDetails != null ? insufficientIngredientDetails : new ArrayList<>();
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public boolean isCanCook() {
        return canCook;
    }

    public List<RecipeIngredientStatus> getIngredientStatuses() {
        return ingredientStatuses;
    }

    public List<String> getMissingIngredientNames() {
        return missingIngredientNames;
    }

    public List<String> getInsufficientIngredientDetails() {
        return insufficientIngredientDetails;
    }

    /**
     * Details for each required ingredient in the recipe match comparison.
     */
    public static class RecipeIngredientStatus {
        private final RecipeIngredient requiredIngredient;
        private final double availableQuantityInRequiredUnit;
        private final boolean isPresent;
        private final boolean isSufficient;
        private final String message;

        public RecipeIngredientStatus(RecipeIngredient requiredIngredient,
                                      double availableQuantityInRequiredUnit,
                                      boolean isPresent,
                                      boolean isSufficient,
                                      String message) {
            this.requiredIngredient = requiredIngredient;
            this.availableQuantityInRequiredUnit = availableQuantityInRequiredUnit;
            this.isPresent = isPresent;
            this.isSufficient = isSufficient;
            this.message = message;
        }

        public RecipeIngredient getRequiredIngredient() {
            return requiredIngredient;
        }

        public double getAvailableQuantityInRequiredUnit() {
            return availableQuantityInRequiredUnit;
        }

        public boolean isPresent() {
            return isPresent;
        }

        public boolean isSufficient() {
            return isSufficient;
        }

        public String getMessage() {
            return message;
        }
    }
}
